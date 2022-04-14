package test.puzzle.core;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.junit.Test;


public class TestNimGame {

    /**
     * ハッシュテーブルのキーとしてint配列を使えるようにするための
     * ラッパークラスです。
     */
    static class SortedIntArray {
        private final int[] array;

        SortedIntArray(int... array) {
            this.array = Arrays.copyOf(array, array.length);
            Arrays.sort(this.array);
        }

        int length() {
            return array.length;
        }

        int get(int index) {
            return array[index];
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof SortedIntArray
                && Arrays.equals(array, ((SortedIntArray)obj).array);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(array);
        }

        @Override
        public String toString() {
            return Arrays.toString(array);
        }
    }

    enum Player { A, B }

    static Player opponent(Player player) {
        return player == Player.A ? Player.B : Player.A;
    }

    static Player winnerCore(Player player, int... heaps) {
        Player opponent = opponent(player);
        Player winner = opponent;
        for (int i = 0; i < heaps.length; ++i) {
            int heap = heaps[i];
            for (int move = 1; winner != player && move <= heap; ++move) {
                heaps[i] -= move;  // backup
                winner = winner(opponent, heaps);
                heaps[i] = heap;   // restore
            }
        }
//        System.out.println(Arrays.toString(heaps) + " " + player + " winner=" + winner);
        return winner;
    }

    /** 各ヒープに対して先手が勝ち(true)、負け(false)を格納します。 */
    static Map<SortedIntArray, Boolean> cache = new HashMap<>();

    static Player winner(Player player, int... heaps) {
        SortedIntArray key = new SortedIntArray(heaps);
        Boolean result = cache.get(key);
        if (result != null)
            return result ? player : opponent(player);
        Player winner = winnerCore(player, heaps);
        cache.put(key, winner == player);
        return winner;
    }

    @Test
    public void testNimGame() {
        assertEquals(Player.B, winner(Player.A, 5, 5));
        assertEquals(Player.A, winner(Player.A, 5, 7));
        assertEquals(Player.A, winner(Player.A, 2, 3, 5));
        assertEquals(Player.A, winner(Player.A, 7, 8, 9));
        assertEquals(Player.B, winner(Player.A, 2, 3, 4, 5));
        System.out.println(cache);
        // キャッシュの各エントリについてテストします。
        // ヒープのXORがゼロの時は負け、それ以外の時は勝ちであることを確認します。
        for (Entry<SortedIntArray, Boolean> e : cache.entrySet()) {
            SortedIntArray heaps = e.getKey();
            boolean result = e.getValue();
            int xor = 0;
            for (int i = 0, size = heaps.length(); i < size; ++i)
                xor ^= heaps.get(i);
            assertEquals(xor != 0, result);
        }
    }

    @Test
    public void testTreeMap() {
        Map<int[], String> map = new TreeMap<>(Arrays::compare);
        map.put(new int[] {1, 2}, "one-two");
        assertEquals("one-two", map.get(new int[] {1, 2}));
        Map<SortedIntArray, String> hm = new HashMap<>();
        hm.put(new SortedIntArray(1, 2), "ONE-TWO");
        assertEquals("ONE-TWO", hm.get(new SortedIntArray(1, 2)));
        assertEquals("ONE-TWO", hm.get(new SortedIntArray(2, 1)));
    }



}
