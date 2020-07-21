package test.puzzle;

import static java.math.BigInteger.*;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

class TestMemoise {

    static final Map<Integer, Integer> factorialCache1 = new HashMap<>();
    static int factorial1(int n) { // n は非負整数
        if (n == 0)
            return 1;
        else if (factorialCache1.containsKey(n))
            return factorialCache1.get(n);
        else {
            int x = factorial1(n - 1) * n; // 再帰呼び出し
            factorialCache1.put(n, x);
            return x;
        }
    }

    static final Map<Integer, Integer> factorialCache2 = new HashMap<>();
    static int factorial2(int n) {
        if (n == 0) return 1;
        // ConcurrentModificationExceptionをスローする。
        return factorialCache2.computeIfAbsent(n, k -> factorial2(n - 1) * n);
    }

    static final Map<Integer, Integer> factorialCache3 = new HashMap<>();
    static int factorial3(int n) {
        if (n == 0) return 1;
        if (factorialCache3.containsKey(n)) return factorialCache3.get(n);
        int x = factorial3(n - 1) * n;
        factorialCache3.put(n, x);
        return x;
    }

    @Test
    public void testFactorial() {
        assertEquals(120, factorial1(5));
        try {
            assertEquals(120, factorial2(5));
            fail();
        } catch (ConcurrentModificationException e) {
        }
        assertEquals(120, factorial3(5));
    }

    public static <T, U> Function<T, U> memoize(Function<Function<T, U>, Function<T, U>> f) {
        return new Function<T, U>() {

            final Map<T, U> cache = new HashMap<>();
            final Function<T, U> body = f.apply(this);

            @Override
            public U apply(T t) {
//                return cache.computeIfAbsent(t, body);     // this throws ConcurrentModificationException
                U r = cache.get(t);
                if (r == null) cache.put(t, r = body.apply(t));
                return r;
            }
        };
    }

    static BigInteger fact(BigInteger n) {
        return n.compareTo(ZERO) <= 0 ? ONE
            : n.multiply(fact(n.subtract(ONE)));
    }

    static Function<BigInteger, BigInteger> fact =
        memoize(self -> n -> n.compareTo(ZERO) <= 0 ? ONE
            : n.multiply(self.apply(n.subtract(ONE))));

    @Test
    public void testFact() {
        for (long i = 0; i < 1000; ++i)
            System.out.println(fact.apply(BigInteger.valueOf(i)));
//         System.out.println(fact(BigInteger.valueOf(i)));
    }

    static final BigInteger TWO = BigInteger.valueOf(2);

    static BigInteger fibonacci(BigInteger n) {
        return n.equals(ZERO) ? ZERO
            : n.equals(ONE) ? ONE
            : fibonacci(n.subtract(ONE)).add(fibonacci(n.subtract(TWO)));
    }

    static Function<BigInteger, BigInteger> fibonacci =
        memoize(self -> n -> n.equals(ZERO) ? ZERO
            : n.equals(ONE) ? ONE
            : self.apply(n.subtract(ONE)).add(self.apply(n.subtract(TWO))));

    @Test
    public void testFibonacci() {
    for (long i = 0; i < 1000; ++i)
        System.out.println(fibonacci.apply(BigInteger.valueOf(i)));
//         System.out.println(fibonacci(BigInteger.valueOf(i)));
    }

    static int tarai(int x, int y, int z) {
        return x <= y ? y
            : tarai(tarai(x - 1, y, z),
                tarai(y - 1, z, x),
                tarai(z - 1, x, y));
    }

    static Function<Integer, Function<Integer, Function<Integer, Integer>>> tarai =
        memoize(fx -> x ->
            memoize(fy -> y ->
                memoize(fz -> z -> x <= y ? y
                    : fx.apply(fx.apply(x - 1).apply(y).apply(z))
                        .apply(fx.apply(y - 1).apply(z).apply(x))
                        .apply(fx.apply(z - 1).apply(x).apply(y)))));

    @Test
    public void testTarai() {
        assertEquals(12, (int) tarai.apply(12).apply(6).apply(0));
        assertEquals(13, (int) tarai.apply(13).apply(7).apply(0));
        assertEquals(14, (int) tarai.apply(14).apply(8).apply(0));
        assertEquals(15, (int) tarai.apply(15).apply(5).apply(0));
        assertEquals(20, (int) tarai.apply(20).apply(10).apply(0));
    }

    interface CachedFunction<T, U> extends Function<T, U> {
        Map<T, U> cache();
    }

    public static <T, U> CachedFunction<T, U> cached(Function<Function<T, U>, Function<T, U>> f) {
        return new CachedFunction<T, U>() {
            final Map<T, U> cache = new HashMap<>();
            final Function<T, U> body = f.apply(this);

            @Override
            public U apply(T t) {
                U r = cache.get(t);
                if (r == null) cache.put(t, r = body.apply(t));
                return r;
            }

            @Override
            public Map<T, U> cache() {
                return Collections.unmodifiableMap(cache);
            }
        };
    }

    static CachedFunction<Integer, CachedFunction<Integer, CachedFunction<Integer, Integer>>> cachedTarai =
        cached(fx -> x ->
            cached(fy -> y ->
                cached(fz -> z -> x <= y ? y
                    : fx.apply(fx.apply(x - 1).apply(y).apply(z))
                        .apply(fx.apply(y - 1).apply(z).apply(x))
                        .apply(fx.apply(z - 1).apply(x).apply(y)))));

    @Test
    public void testCachedTarai() {
        assertEquals(12, (int) cachedTarai.apply(12).apply(6).apply(0));
//        assertEquals(13, (int) cachedTarai.apply(13).apply(7).apply(0));
//        assertEquals(14, (int) cachedTarai.apply(14).apply(8).apply(0));
//        assertEquals(15, (int) cachedTarai.apply(15).apply(5).apply(0));
//        assertEquals(20, (int) cachedTarai.apply(20).apply(10).apply(0));
        for (Entry<Integer, CachedFunction<Integer, CachedFunction<Integer, Integer>>> x : cachedTarai.cache().entrySet()) {
            System.out.printf("x = %s%n", x.getKey());
            for (Entry<Integer, CachedFunction<Integer, Integer>> y : x.getValue().cache().entrySet()) {
                System.out.printf("  y = %s%n", y.getKey());
                for (Entry<Integer, Integer> z : y.getValue().cache().entrySet()) {
                    System.out.printf("    z = %s -> %s%n", z.getKey(), z.getValue());
                }
            }
        }
    }

    /**
     * Stackoverflow を検索して見つけた例
     * これは再帰呼び出しには対応していない。
     *
     * lambda - Java memoization method - Stack Overflow
     * https://stackoverflow.com/questions/27549864/java-memoization-method#answer-27549948
     */
    static Map<Integer, Integer> addOneCache = new HashMap<>();

    static Integer addOne(Integer n) {
        return addOneCache.computeIfAbsent(n, x -> x + 1);
    }

    static public class Memoizer<T, U> {
        private final Map<T, U> cache = new ConcurrentHashMap<>();

        private Memoizer() {}
        private Function<T, U> doMemoize(final Function<T, U> function) {
            return input -> cache.computeIfAbsent(input, function::apply);
        }

        public static <T, U> Function<T, U> memoize(final Function<T, U> function) {
            return new Memoizer<T, U>().doMemoize(function);
        }

        public Map<T, U> cache() { return Collections.unmodifiableMap(cache); }
    }

    int square(int x) {
        return x * x;
    }

    @Test
    public void testStackOverflowMemoize() {
        assertEquals(4, addOne(3));
        assertEquals(6, addOne(5));
        assertEquals(4, addOneCache.get(3));
        assertEquals(6, addOneCache.get(5));
        Function<Integer, Integer> squareMemoized = Memoizer.memoize(this::square);
        assertEquals(9, squareMemoized.apply(3));
    }

    /**
     * Stackoverflow を検索して見つけた例
     * 上記と同じ質問に対する別回答。
     * 再帰呼び出しに対応している。
     *
     * 何やらリフレクションで再帰を横取りするようだ。
     */
    public interface MemoizedFunction<V> {
        V call(Object... args);
    }

    private static class ArgList {
        public Object[] args;

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof ArgList)) {
                return false;
            }

            ArgList argList = (ArgList) o;

            // Probably incorrect - comparing Object[] arrays with Arrays.equals
            return Arrays.equals(args, argList.args);
        }

        @Override
        public int hashCode() {
            return args != null ? Arrays.hashCode(args) : 0;
        }

        @Override
        public String toString() {
            return Arrays.toString(args);
        }
    }

    public static <V> MemoizedFunction<V> memoizeFunction(Class<? super V> returnType, Method method)
            throws IllegalAccessException {
        final Map<ArgList, V> memoizedCalls = new HashMap<>();
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle methodHandle = lookup.unreflect(method)
            .asSpreader(Object[].class, method.getParameterCount());
        return args -> {
            ArgList argList = new ArgList();
            argList.args = args;
            return memoizedCalls.computeIfAbsent(argList, argList2 -> {
                try {
                    //noinspection unchecked
                    return (V) methodHandle.invoke(args);
                } catch (Throwable throwable) {
                    throw new RuntimeException(throwable);
                }
            });
        };
    }

    public static Integer factReflect(Integer n) {
        if (n <= 1)
            return 1;
        else
            return factReflect(n - 1) * n;
    }

    @Test
    public void testStackoverflowSample2() throws NoSuchMethodException, SecurityException, IllegalAccessException {
        Method method = getClass().getMethod("factReflect", Integer.class);
        MemoizedFunction<Integer> fact = memoizeFunction(Integer.class, method);
        assertEquals(1, fact.call(1));
        assertEquals(120, fact.call(5));
        assertEquals(720, fact.call(6));

    }

    /**
     * 再帰呼び出しのメモ化
     */

    static Map<Integer, Integer> factCache = new ConcurrentHashMap<>();

    static int factBase(int n) {
        if (n <= 1)
            return 1;
        else
            return factMemoized(n - 1) * n;
    }

    static int factMemoized(int n) {
        return factCache.computeIfAbsent(n, x -> factBase(x));
    }

    @Test
    public void testFactMemoized() {
        assertEquals(120, factMemoized(5));
        assertEquals(6, factMemoized(3));
        assertEquals(Map.of(1, 1, 2, 2, 3, 6, 4, 24, 5, 120), factCache);
    }

    /*
     * クラスとしてのメモ化
     */
    static abstract class MemoizedFunction2<T, R> implements Function<T, R> {

        private final Map<T, R> cache = new HashMap<>();

        protected R self(T t) {
            R r = cache.get(t);
            if (r == null) cache.put(t, r = apply(t));
            return r;
        }

        public Map<T, R> cache() {
            return Collections.unmodifiableMap(cache);
        }

    }

    static class Factorial extends MemoizedFunction2<Integer, Integer> {

        @Override
        public Integer apply(Integer n) {
            if (n <= 1)
                return 1;
            else
                return self(n - 1) * n;
        }
    }

    @Test
    public void testMemoizedFunction2() {
        Factorial factorial = new Factorial();
        for (int i = 0; i < 6; ++i)
            System.out.println(factorial.self(i));
        System.out.println(factorial.cache());
    }

    static class MemoizedFunction3<T, R> implements Function<T, R> {

        private final BiFunction<MemoizedFunction3<T, R>, T, R> function;
        private final Map<T, R> cache = new HashMap<>();

        public MemoizedFunction3(BiFunction<MemoizedFunction3<T, R>, T, R> function) {
            this.function = function;
        }

        @Override
        public R apply(T arg) {
            return function.apply(this, arg);
        }

        protected R cached(T t) {
            R r = cache.get(t);
            if (r == null) cache.put(t, r = apply(t));
            return r;
        }

        public Map<T, R> cache() {
            return Collections.unmodifiableMap(cache);
        }

        @Override
        public String toString() {
            return cache.toString();
        }
    }

    static <T, R> MemoizedFunction3<T, R> memoize(BiFunction<MemoizedFunction3<T, R>, T, R> function) {
        return new MemoizedFunction3<>(function);
    }

    @Test
    public void testMemoizedFunction3() {
        MemoizedFunction3<Integer, Integer> factorial =
            memoize((self, n) -> n <= 1 ? 1 : self.cached(n - 1) * n);
        for (int i = 0; i < 6; ++i)
            System.out.println(factorial.cached(i));
        System.out.println(factorial.cache());
    }

    @Test
    public void testMemoizedFunction3Multiply() {
        MemoizedFunction3<Integer, MemoizedFunction3<Integer, Integer>> multiply =
            memoize((self0, a) -> memoize((self2, b) -> a * b));
        System.out.println(multiply.cached(2).cached(3));
        System.out.println(multiply.cached(5).cached(7));
        System.out.println(multiply);
    }
}
