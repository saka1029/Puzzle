package test.puzzle.nanopico;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.Test;

public class Test_001_Mevius {

    static int[] solve(int max) {
        int[] m = new int[max + 1], p = new int[max + 1];
        for (int i = 1; i <= max; ++i) {
            m[i] = 1;
            p[i] = 0;
        }
        for (int k = 2; k <= max; ++k) {
            if (p[k] != 0)
                continue;
            for (int i = k; i <= max; i += k) {
                m[i] *= -1;
                p[i] = 1;  // 素数でないものに、１をつける
            }
            int l = k * k;
            if (l > max)
                continue;
            for (int i = l; i <= max; i += l)
                m[i] = 0;
        }
        return m;
    }

    static final int MAX = 100;
    static final boolean PRIMES[] = new boolean[MAX + 1];
    static {
        for (int i = 2; i <= MAX; ++i)
            PRIMES[i] = true;
        for (int i = 2; i <= MAX; ++i)
            for (int j = i + i; j <= MAX; j += i)
                PRIMES[j] = false;
    }

    @Test
    public void testPRIMES() {
        IntStream.rangeClosed(1, MAX)
            .filter(i -> PRIMES[i])
            .forEach(i -> System.out.print(" " + i));
    }

    static int[] factor(int n) {
        List<Integer> list = new ArrayList<>();
        for (int i = 2; i <= n; ++i) {
            if (PRIMES[i])
                while (n % i == 0) {
                    n /= i;
                    list.add(i);
                }
        }
        return list.stream()
            .mapToInt(i -> i)
            .toArray();
    }

    static int myu(int n) {
        if (n == 1)
            return 1;
        int[] f = factor(n);
        int length = f.length;
        boolean dup = IntStream.of(f).distinct().count() != length;
        if (dup)
            return 0;
        return length % 2 == 0 ? 1 : -1;
    }

    @Test
    public void testSolve() {
        int max = 100;
        int[] m = solve(max);
        for (int i = 1; i <= max; ++i) {
            int myu = myu(i);
            System.out.printf("μ(%d) = %d %d %s%n", i, m[i], myu, Arrays.toString(factor(i)));
            assertEquals(myu, m[i]);
        }
    }
    
}
