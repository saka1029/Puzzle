package test.puzzle.core;

import static org.junit.Assert.*;

import java.util.logging.Logger;

import org.junit.Test;

import puzzle.core.Combination;
import puzzle.functions.Memoizer;

public class TestCombination {

    static final Logger logger = Logger.getLogger(TestCombination.class.getName());

    @Test
    public void testCount() {
        assertEquals(1, Combination.count(0, 0));
        assertEquals(1, Combination.count(1, 0));
        assertEquals(1, Combination.count(1, 1));
        assertEquals(1, Combination.count(2, 0));
        assertEquals(2, Combination.count(2, 1));
        assertEquals(1, Combination.count(2, 2));
        assertEquals(1, Combination.count(3, 0));
        assertEquals(3, Combination.count(3, 1));
        assertEquals(3, Combination.count(3, 2));
        assertEquals(1, Combination.count(3, 3));
        assertEquals(1, Combination.count(4, 0));
        assertEquals(4, Combination.count(4, 1));
        assertEquals(6, Combination.count(4, 2));
        assertEquals(4, Combination.count(4, 3));
        assertEquals(1, Combination.count(4, 4));
        assertEquals(1, Combination.count(5, 0));
        assertEquals(5, Combination.count(5, 1));
        assertEquals(10, Combination.count(5, 2));
        assertEquals(10, Combination.count(5, 3));
        assertEquals(5, Combination.count(5, 4));
        assertEquals(1, Combination.count(5, 5));
        assertEquals(1, Combination.count(6, 0));
        assertEquals(6, Combination.count(6, 1));
        assertEquals(15, Combination.count(6, 2));
        assertEquals(20, Combination.count(6, 3));
        assertEquals(15, Combination.count(6, 4));
        assertEquals(6, Combination.count(6, 5));
        assertEquals(1, Combination.count(6, 6));
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
                assertEquals(Combination.count(n, r), count);
            }
    }

    /**
     * 組み合わせの数(nCr)を計算するメモ化関数
     * 漸化式:
     * n C 0 = 1
     * n C n = 1;
     * n C r = (n - 1) C (r - 1) + (n - 1) C r
     */
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

}
