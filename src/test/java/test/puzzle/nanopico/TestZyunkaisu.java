package test.puzzle.nanopico;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestZyunkaisu {

    static int[] array(int n) {
        return Integer.toString(n).chars().map(i -> i - '0').toArray();
    }

    @Test
    public void testArray() {
        assertArrayEquals(new int[] {1, 2, 0,3}, array(1203));
    }

    static int find(int[] b, int key, int start) {
        for (int i = start, max = b.length; i < max; ++i)
            if (b[i] == key)
                return i;
        return -1;
    }

    static boolean circularEquals(int[] a, int[] b) {
        int key = a[0], al = a.length, bl = b.length;
        L: for (int start = 0; start < bl; ++start) {
            int j = find(b, key, start);
            if (j == -1)
                return false;
            start = j;
            for (int i = 0, k = j, m = 0; m < bl; ++i, k = (k + 1) % bl, ++m)
                if (m >= al) {
                    if (b[k] != 0)
                        continue L;
                } else if (a[i] != b[k])
                    continue L;
            return true;
        }
        return false;
    }

    @Test
    public void testCircularEquals() {
        assertTrue(circularEquals(array(1203), array(3120)));
        assertTrue(circularEquals(array(1203), array(300120)));
        assertTrue(circularEquals(array(123), array(2301)));
        assertTrue(circularEquals(array(123), array(23001)));
    }

    @Test
    public void test巡回数問題1() {
        for (int i = 1; i < 1000000; ++i)
            if (circularEquals(array(i), array(i * 2)))
                System.out.printf("%d * 2 = %d%n", i, i * 2);
    }

    @Test
    public void test巡回数問題2() {
        for (int i = 1; i < 1000000; ++i)
            for (int j = 2; j <= 9; ++j)
                if (circularEquals(array(i), array(i * j)))
                    System.out.printf("%d * %d = %d%n", i, j, i * j);
    }
}