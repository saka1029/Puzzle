package test.puzzle.dikstra;

import java.util.Arrays;
import java.util.EnumSet;

import org.junit.Test;

public class TestDijkstra {

    enum Eki { 東京, 渋谷, 品川, 新横浜, 小田原, 新宿, 横浜 }

    @Test
    @SuppressWarnings("unused")
    public void test() {
        Eki[] all = Eki.values();
        int size = all.length;
        int[][] 所要時間 = new int[size][size];
        Eki[] 経路 = new Eki[size];
        int[] 最短時間 = new int[size];
        EnumSet<Eki> 訪問済 = EnumSet.noneOf(Eki.class);
        new Object() {

            void set(Eki from, Eki to, int 時間) {
                所要時間[from.ordinal()][to.ordinal()] = 時間;
                所要時間[to.ordinal()][from.ordinal()] = 時間;
            }

            void init() {
                for (Eki f : all)
                    for (Eki t : all)
                        set(f, t, -1);
                set(Eki.東京, Eki.渋谷, 16);
                set(Eki.東京, Eki.品川, 8);
                set(Eki.東京, Eki.新宿, 15);
                set(Eki.渋谷, Eki.品川, 13);
                set(Eki.渋谷, Eki.新横浜, 27);
                set(Eki.渋谷, Eki.小田原, 78);
                set(Eki.品川, Eki.新横浜, 10);
                set(Eki.品川, Eki.新宿, 21);
                set(Eki.品川, Eki.横浜, 17);
                set(Eki.新横浜, Eki.横浜, 13);
                set(Eki.新横浜, Eki.小田原, 15);
                set(Eki.新宿, Eki.横浜, 33);
                set(Eki.横浜, Eki.小田原, 52);
                最短時間[Eki.東京.ordinal()] = 0;
            }

            void solve() {
                init();
                for (int[] r : 所要時間)
                    System.out.println(Arrays.toString(r));
            }
        }.solve();
    }

}
