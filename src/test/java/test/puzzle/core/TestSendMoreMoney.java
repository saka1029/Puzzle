package test.puzzle.core;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class TestSendMoreMoney {

    public List<int[]> sendMoreMoney() {
        List<int[]> result = new ArrayList<>();
        new Object() {
            int size = 8, mask = (1 << 10) - 1;
            int[] n = new int[size];
            
            int number(int... is) {
                int r = 0;
                for (int i : is)
                    r = r * 10 + n[i];
                return r;
            }

            boolean check() {
                // 01234567
                // SENDMORY
                return number(0, 1, 2, 3) + number(4, 5, 6, 1) == number(4, 5, 2, 1, 7);
            }

            void solve(int i, int used) {
                if (i >= size) {
                    if (check())
                        result.add(Arrays.copyOf(n, size));
                } else {
                    for (int avail = mask & ~used, d = 0; avail != 0; avail ^= d) {
                        d = Integer.lowestOneBit(avail);
                        if ((i == 0 || i == 4) && d == 1)
                            continue;
                        n[i] = Integer.numberOfTrailingZeros(d);
                        solve(i + 1, used | d);
                    }
                }
            }
        }.solve(0, 0);
        return result;
    }

    @Test
    public void test() {
        List<int[]> result = sendMoreMoney();
        assertEquals(1, result.size());
        assertArrayEquals(new int[] {9, 5, 6, 7, 1, 0, 8, 2}, result.get(0));
    }

}
