package test.puzzle.dikstra;

import java.util.Arrays;
import java.util.EnumSet;

import org.junit.jupiter.api.Test;

class TestDijkstra {

    enum 駅 { 東京, 渋谷, 品川, 新横浜, 小田原, 新宿, 横浜 }

    @Test
    void test() {
        駅[] all = 駅.values();
        int size = all.length;
        int[][] 所要時間 = new int[size][size];
        駅[] 経路 = new 駅[size];
        int[] 最短時間 = new int[size];
        EnumSet<駅> 訪問済 = EnumSet.noneOf(駅.class);
        new Object() {

            void set(駅 from, 駅 to, int 時間) {
                所要時間[from.ordinal()][to.ordinal()] = 時間;
                所要時間[to.ordinal()][from.ordinal()] = 時間;
            }

            void init() {
                for (駅 f : all)
                    for (駅 t : all)
                        set(f, t, -1);
                set(駅.東京, 駅.渋谷, 16);
                set(駅.東京, 駅.品川, 8);
                set(駅.東京, 駅.新宿, 15);
                set(駅.渋谷, 駅.品川, 13);
                set(駅.渋谷, 駅.新横浜, 27);
                set(駅.渋谷, 駅.小田原, 78);
                set(駅.品川, 駅.新横浜, 10);
                set(駅.品川, 駅.新宿, 21);
                set(駅.品川, 駅.横浜, 17);
                set(駅.新横浜, 駅.横浜, 13);
                set(駅.新横浜, 駅.小田原, 15);
                set(駅.新宿, 駅.横浜, 33);
                set(駅.横浜, 駅.小田原, 52);
                最短時間[駅.東京.ordinal()] = 0;
            }

            void solve() {
                init();
                for (int[] r : 所要時間)
                    System.out.println(Arrays.toString(r));
            }
        }.solve();
    }

}
