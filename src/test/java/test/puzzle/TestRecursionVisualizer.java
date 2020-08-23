package test.puzzle;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class TestRecursionVisualizer {

    static class Vis<R> {
        public final String name;
        public int nest = 0;
        public int indent = 2;

        public Vis(String name) {
            this.name = name;
        }

        String indent() {
            return " ".repeat(nest * indent);
        }

        String toString(Object... args) {
            String s = Arrays.deepToString(args);
            return s.substring(1, s.length() - 1);
        }

        public void enter(Object... args) {
            System.out.println(indent() + name + "(" + toString(args) + ")");
            ++nest;
        }

        public void exit() {
            --nest;
        }

        public R exit(R result) {
            --nest;
            System.out.println(indent() + "-> " + result);
            return result;
        }
    }

    static Vis<Integer> fibonacci = new Vis<>("fibonacci");

    static int fibonacci(int n) {
        fibonacci.enter(n);
        if (n == 0)
            return fibonacci.exit(0);
        else if (n == 1)
            return fibonacci.exit(1);
        else
            return fibonacci.exit(fibonacci(n - 1) + fibonacci(n - 2));
    }

    @Test
    public void testFibonacci() {
        fibonacci(5);
    }

    static Vis<Integer> combi = new Vis<>("combination");

    static int combination(int n, int k) {
        combi.enter(n, k);
        if (k == 0 || k == n)
            return combi.exit(1);
        else
            return combi.exit(combination(n - 1, k - 1) + combination(n - 1, k));
    }

    @Test
    public void testCombi() {
        combination(4, 2);
    }

    static Vis<Void> perm = new Vis<>("perm");

    static void perm(String in, String out) {
        perm.enter(in, out);
        if (in.isEmpty())
            System.out.println("-> " + out);
        else
            for (int i = 0, max = in.length(); i < max; ++i)
                perm(in.substring(0, i) + in.substring(i + 1), out + in.charAt(i));
        perm.exit();
    }

    @Test
    public void testPerm() {
        perm("abcd", "");
    }

    static Vis<Void> combination = new Vis<>("combination");

    static List<List<String>> combination(String[] data, int k) {
        List<List<String>> result = new ArrayList<>();
        combination(data, 0, new String[k], 0, result);
        return result;
    }

    static void combination(String[] data, int di, String[] comb, int ci, List<List<String>> result) {
        combination.enter(data, di, comb, result);
        if (ci == comb.length)
            result.add(List.of(comb));
        else
            for (; di <= data.length - (comb.length - ci); di++) {
                comb[ci] = data[di];
                combination(data, di + 1, comb, ci + 1, result);
            }
        combination.exit();
    }

    @Test
    public void testCombination() {
        String[] s = {"a", "b", "c", "d"};
        // List<List<String>> result = combination(s, 2);
        // System.out.println(result);
        System.out.println(combination(s, 2));
    }

    static class Args {

        private final Object[] args;

        public Args(Object... args) {
            this.args = args.clone();
        }

        @Override
        public int hashCode() {
            return Arrays.deepHashCode(args);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            return Arrays.deepEquals(args, ((Args) obj).args);
        }

        @Override
        public String toString() {
            return Arrays.deepToString(args);
        }
    }

    @FunctionalInterface
    interface CacheFunction<R> {
        R apply(Object... args);
    }

    static class Cache<R> {

        final Map<Args, R> cache = new HashMap<>();
        final CacheFunction<R> func;
        int hit = 0, miss = 0;

        public Cache(CacheFunction<R> func) {
            this.func = func;
        }

        public R call(Object... args) {
            Args a = new Args(args);
            R cached = cache.get(a);
            if (cached != null) {
                ++hit;
                return cached;
            }
            ++miss;
            R n = func.apply(args);
            cache.put(a, n);
            return n;
        }

        @Override
        public String toString() {
            return cache.toString();
        }
    }

    @Test
    public void testArgs() {
        Object[] objs = {new int[] {1, 2}, new double[][] {{1, 2}, {3, 4}}, List.of("a", "b"), Map.of("one", 1)};
        Args args = new Args(objs);
        assertEquals("[[1, 2], [[1.0, 2.0], [3.0, 4.0]], [a, b], {one=1}]", args.toString());
        Object[] objs2 = {new int[] {1, 2}, new double[][] {{1, 2}, {3, 4}}, List.of("a", "b"), Map.of("one", 1)};
        Args args2 = new Args(objs2);
        assertEquals(args, args2);
        assertEquals(args.hashCode(), args2.hashCode());
    }

    static Cache<Integer> fib = new Cache<>(args -> fib((int) args[0]));

    static int fib(int n) {
        if (n == 0)
            return 0;
        else if (n == 1)
            return 1;
        else
            return fib.call(n - 1) + fib.call(n - 2);
    }

    @Test
    public void testCacheFib() {
        for (int i = 0; i < 30; ++i)
            System.out.println(fib.call(i));
        System.out.println(fib.cache);
    }

    static Cache<Integer> c = new Cache<>(args -> c((int) args[0], (int) args[1]));

    static int c(int n, int k) {
        if (k == 0 || k == n)
            return 1;
        else if (n - k < k)
            return c.call(n, n - k);
        else
            return c.call(n - 1, k - 1) + c.call(n - 1, k);
    }

    @Test
    public void testCacheC() {
        for (int n = 0; n <= 30; ++n)
            for (int r = 0; r <= n; ++r)
                c.call(n, r);
        System.out.println(c.cache);
        System.out.println("hit=" + c.hit + " miss=" + c.miss);
        System.out.println(c.cache.get(new Args(20, 10)));
    }

    static Vis<Integer> tarai = new Vis<>("tarai");

    static int tarai(int x, int y, int z) {
        tarai.enter(x, y, z);
        if (x <= y)
            return tarai.exit(y);
        else
            return tarai.exit(
                tarai(
                    tarai(x - 1, y, z),
                    tarai(y - 1, z, x),
                    tarai(z - 1, x, y)));
    }

    @Test
    public void testTarai() {
        tarai(6, 2, 1);
    }

    static Cache<Integer> taraic = new Cache<>(a -> taraic((int) a[0], (int) a[1], (int) a[2]));
    static Vis<Integer> taraiv = new Vis<Integer>("taraic");

    static int taraic(int x, int y, int z) {
        taraiv.enter(x, y, z);
        if (x <= y)
            return taraiv.exit(y);
        else
            return taraiv.exit(taraic.call(
                taraic.call(x - 1, y, z),
                taraic.call(y - 1, z, x),
                taraic.call(z - 1, x, y)));
    }

    @Test
    public void testTaraic() {
        taraic.call(6, 2, 1);
        System.out.println(taraic.cache.size());
        System.out.println(taraic);
    }

}
