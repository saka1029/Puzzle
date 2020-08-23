package test.puzzle;

import static org.junit.Assert.*;

import java.util.logging.Logger;

import org.junit.Test;

import puzzle.Combination;
import puzzle.functions.Memoizer;

public class TestCombination {

    static final Logger logger = Logger.getLogger(TestCombination.class.getName());

    /** 組み合わせの数(nCr)を計算するメモ化関数 */
    static final Memoizer<Integer, Memoizer<Integer, Integer>> C =
        Memoizer.memoize(self -> n ->
            Memoizer.memoize(dummy -> r ->
                (r == 0 || r == n) ? 1 :
                    self.apply(n - 1).apply(r - 1) + self.apply(n - 1).apply(r)));

    static int C(int n, int r) {
        return C.apply(n).apply(r);
    }

    @Test
    public void testC() {
        assertEquals(1, C(2, 0));
        assertEquals(2, C(2, 1));
        assertEquals(1, C(2, 2));
        assertEquals(1, C(3, 0));
        assertEquals(3, C(3, 1));
        assertEquals(3, C(3, 2));
        assertEquals(1, C(3, 3));
        assertEquals(1, C(4, 0));
        assertEquals(4, C(4, 1));
        assertEquals(6, C(4, 2));
        assertEquals(4, C(4, 3));
        assertEquals(1, C(4, 4));
    }

    @Test
    public void testIterableCount() {
        int max = 20;
        for (int n = 0; n < max; ++n)
            for (int r = 0; r <= n; ++r) {
                int count = 0;
                for (int[] a : Combination.iterable(n, r)) {
                    ++count;
                    assertEquals(r, a.length);
                }
                // logger.info("n = " + n + " r = " + r + " count = " + count);
                assertEquals(C(n, r), count);
            }
        logger.info(C.toString());  // Cのキャッシュ
    }

}
