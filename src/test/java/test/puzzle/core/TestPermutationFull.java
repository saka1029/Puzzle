package test.puzzle.core;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import org.junit.Test;

public class TestPermutationFull {

    /*
     * n P k = n! / (n - k)!
     * 1 P 1 = 1! / (1 - 1)! = 1! / 0! = 1
     * 3 P 0 = 3! / (3 - 0)! = 3! / 3! = 1
     * 3 P 1 = 3! / (3 - 1)! = 3! / 2! = 3
     * 3 P 3 = 3! / (3 - 3)! = 3! / 0! = 6
     */
    static final List<List<Integer>> EXPECTED_1_1 = List.of(List.of(0));

    static final List<List<Integer>> EXPECTED_3_0 = List.of(List.of());

    static final List<List<Integer>> EXPECTED_3_1 = List.of(List.of(0), List.of(1), List.of(2));

    static final List<List<Integer>> EXPECTED_3_3 = List.of(
        List.of(0, 1, 2),
        List.of(0, 2, 1),
        List.of(1, 0, 2),
        List.of(1, 2, 0),
        List.of(2, 0, 1),
        List.of(2, 1, 0));

    static final List<List<Integer>> EXPECTED_3_4 = List.of();

    static void permutationRecursive(int n, int r, Consumer<int[]> callback) {
        new Object() {
            boolean[] used = new boolean[n];
            int[] selected = new int[r];

            void solve(int i) {
                if (i >= r)
                    callback.accept(selected);
                else {
                    for (int j = 0; j < n; ++j) {
                        if (used[j])
                            continue;
                        selected[i] = j;
                        used[j] = true;
                        solve(i + 1);
                        used[j] = false;
                    }
                }
            }
        }.solve(0);
    }

    List<List<Integer>> permutationRecursive(int n, int r) {
        List<List<Integer>> all = new ArrayList<>();
        permutationRecursive(n, r, a -> all.add(IntStream.of(a).mapToObj(i -> i).toList()));
        return all;
    }

    @Test
    public void testPermutationRecursive() {
        assertEquals(EXPECTED_1_1, permutationRecursive(1, 1));
        assertEquals(EXPECTED_3_0, permutationRecursive(3, 0));
        assertEquals(EXPECTED_3_1, permutationRecursive(3, 1));
        assertEquals(EXPECTED_3_3, permutationRecursive(3, 3));
        assertEquals(EXPECTED_3_4, permutationRecursive(3, 4));
    }

    Iterator<int[]> permutationIterator1(int n, int r) {
        return new Iterator<>() {

            int[] selected = new int[r];
            boolean[] used = new boolean[n];
            int i = 0, j = 0;
            boolean hasNext = advance();

            private boolean advance() {
                if (i >= r) {                   // ２回目以降は最後のjをやり直す。
                    if (--i < 0)                // ひとつ前からやり直し。
                        return false;           // 先頭以前のやり直しはできないので終了する。
                    j = selected[i];            // jを復元する。
                    used[j] = false;            // jの使用取り消し。
                    ++j;                        // 次のjを試す。
                }
                while (true) {
                    for (; j < n; ++j)          // 次のjを探す。
                        if (!used[j])
                            break;
                    if (j < n) {                // 見つかった
                        selected[i] = j;        // 結果を格納する。
                        used[j] = true;         // jを使用済みにする。
                        if (++i >= r)           // 最後だったら結果を返す。
                            return true;
                        j = 0;                  // 次に進むときjはゼロからスタートする。
                    } else {                    // 見つからないときは前に戻ってやり直す。
                        if (--i < 0)            // ひとつ前からやり直し。
                            return false;       // 先頭以前のやり直しはできないので終了する。
                        j = selected[i];        // jを復元する。
                        used[j] = false;        // jの使用取り消し。
                        ++j;                    // 次のjを試す。
                    }
                }
            }

            @Override
            public boolean hasNext() {
                return hasNext;
            }

            @Override
            public int[] next() {
                int[] r = selected.clone();
                hasNext = advance();
                return r;
            }
        };
    }

    List<List<Integer>> permutationIterator1List(int n, int r) {
        List<List<Integer>> all = new ArrayList<>();
        for (int[] e : (Iterable<int[]>) () -> permutationIterator1(n, r))
            all.add(IntStream.of(e).mapToObj(k -> k).toList());
        return all;
    }

    @Test
    public void testPermutationIterator1() {
        assertEquals(EXPECTED_1_1, permutationIterator1List(1, 1));
        // assertEquals(EXPECTED_3_0, permutationIterator1List(3, 0)); // !!!うまくいかない！！！
        assertEquals(EXPECTED_3_1, permutationIterator1List(3, 1));
        assertEquals(EXPECTED_3_3, permutationIterator1List(3, 3));
        assertEquals(EXPECTED_3_4, permutationIterator1List(3, 4));
    }

    Iterator<int[]> permutationIterator2(int n, int r) {
        return new Iterator<>() {

            int[] selected = new int[r];        // 選択された数
            boolean[] used = new boolean[n];    // 使用済みの数
            int i = 0;                          // 次に選択するselected上の位置
            int j = 0;                          // 次に試す数
            boolean hasNext = advance();

            private boolean advance() {
                while (true) {
                    if (i < 0)              // すべての組み合わせを試し終わった。
                        return false;
                    if (i >= r) {         // すべての数を格納した。
                        i = r - 1;          // 次回やり直す位置
                        if (i >= 0)
                            j = selected[i] + 1;// 次回やり直す数
                        return true;        // 結果を返す。
                    }                       // 格納途中
                    if (j > 0)          // 次回試す数がゼロ以外なら
                        used[selected[i]] = false;  // 前回の数を未使用にする。
                    while (j < n) {     // 未使用の数を探す。
                        if (!used[j])   // 見つかったら、
                            break;      // ループを抜ける
                        j++;
                    }
                    if (j < n) {        // 未使用の数が見つかった。(次に進む)
                        selected[i] = j;  // 見つかった数を格納する。
                        used[j] = true; // 使用済みにする。
                        j = 0;          // 次の位置はゼロから探す。
                        ++i;            // 次の位置へ
                    } else {            // 未使用の数が見つからなかった。(前に戻る)
                        --i;            // 前に戻る。
                        if (i >= 0)
                            j = selected[i] + 1;    // 次に試す数
                    }
                }
            }

            @Override
            public boolean hasNext() {
                return hasNext;
            }

            @Override
            public int[] next() {
                int[] r = selected.clone();
                hasNext = advance();
                return r;
            }
        };
    }

    List<List<Integer>> permutationIterator2List(int n, int r) {
        List<List<Integer>> all = new ArrayList<>();
        for (int[] e : (Iterable<int[]>) () -> permutationIterator2(n, r))
            all.add(IntStream.of(e).mapToObj(k -> k).toList());
        return all;
    }

    @Test
    public void testPermutationIterator2() {
        assertEquals(EXPECTED_1_1, permutationIterator2List(1, 1));
        assertEquals(EXPECTED_3_0, permutationIterator2List(3, 0));
        assertEquals(EXPECTED_3_1, permutationIterator2List(3, 1));
        assertEquals(EXPECTED_3_3, permutationIterator2List(3, 3));
        assertEquals(EXPECTED_3_4, permutationIterator2List(3, 4));
    }
}
