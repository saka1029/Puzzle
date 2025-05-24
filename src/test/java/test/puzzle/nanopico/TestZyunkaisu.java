package test.puzzle.nanopico;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
import java.math.BigInteger;
import org.junit.Test;

public class TestZyunkaisu {

    static int[] intArray(String n) {
        return n.chars().map(i -> i - '0').toArray();
    }

    static int[] intArray(int n) {
        return intArray(Integer.toString(n));
    }

    static int[] intArray(BigInteger n) {
        return n.toString().chars().map(i -> i - '0').toArray();
    }

    @Test
    public void testArray() {
        assertArrayEquals(new int[] {1, 2, 0,3}, intArray(1203));
    }

    static int find(int[] b, int key, int start) {
        for (int i = start, max = b.length; i < max; ++i)
            if (b[i] == key)
                return i;
        return -1;
    }

    static boolean 巡回数(int[] a, int[] b) {
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
        assertTrue(巡回数(intArray(1203), intArray(3120)));
        assertTrue(巡回数(intArray(1203), intArray(300120)));
        assertTrue(巡回数(intArray(123), intArray(2301)));
        assertTrue(巡回数(intArray(123), intArray(23001)));
    }

    @Test
    public void test巡回数問題1() {
        for (int i = 1; i < 1000000; ++i)
            if (巡回数(intArray(i), intArray(i * 2)))
                System.out.printf("%d * 2 = %d%n", i, i * 2);
    }

    @Test
    public void test巡回数問題2() {
        for (int i = 1; i < 1000000; ++i)
            for (int j = 2; j <= 9; ++j)
                if (巡回数(intArray(i), intArray(i * j)))
                    System.out.printf("%d * %d = %d%n", i, j, i * j);
    }

    @Test
    public void test588235294117647() {
        BigInteger n = new BigInteger("588235294117647");
        for (int i = 2; i <= 16; ++i) {
            BigInteger m = n.multiply(BigInteger.valueOf(i));
            boolean b = 巡回数(intArray(n), intArray(m));
            System.out.printf("%s * %d = %s %s%n", n, i, m, b);
            assertTrue(b);
        }
    }

    @Test
    public void test434782608695652173913() {
        BigInteger n = new BigInteger("434782608695652173913");
        for (int i = 2; i <= 9; ++i) {
            BigInteger m = n.multiply(BigInteger.valueOf(i));
            System.out.printf("%s * %d = %s %s%n", n, i, m, 巡回数(intArray(n), intArray(m)));
        }
    }
}