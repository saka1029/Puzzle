package test.puzzle;

import static org.junit.jupiter.api.Assertions.*;

import java.util.logging.Logger;

import org.junit.Test;

import puzzle.Permutation32;

public class TestPermutation32 {

    static final Logger logger = Logger.getLogger(TestPermutation32.class.getName());

    static int countIterable(int n, int r) {
        int count = 0;
        for (int[] a : Permutation32.iterable(n, r)) {
            assertEquals(r, a.length);
            ++count;
        }
        return count;
    }

    /**
     * nPrを計算します。
     */
    static int p(int n, int r) {
        int p = 1;
        for (; r > 0; --r, --n)
            p *= n;
        return p;
    }

    @Test
    public void testIterableCount() {
        // n = 0 ... 5, r = 0 ... n
        for (int n = 0; n < 6; ++n)
            for (int r = 0; r <= n; ++r)
                assertEquals(p(n, r), countIterable(n, r));
        // n = 32 (最大値)の場合
        for (int r = 0; r < 4; ++r)
            assertEquals(p(32, r), countIterable(32, r));
    }

    /**
     * 2020-08-20T14:25:16.737 情報 15198msec.
     * 2020-08-20T14:25:45.328 情報 14597msec.
     */
//    @Test
    public void testIterablePerformance() {
        long start = System.currentTimeMillis();
        assertEquals(479001600, countIterable(12, 12));
        logger.info(System.currentTimeMillis() - start + "msec.");
    }

}
