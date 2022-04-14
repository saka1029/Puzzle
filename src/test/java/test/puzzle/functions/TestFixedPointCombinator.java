package test.puzzle.functions;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.Test;

/**
 * Javaで不動点コンビネータを活用してメモ化とトレース機能を実現する - Qiita
 * https://qiita.com/saka1029/items/877e7e0d518625e47b23
 */
public class TestFixedPointCombinator {

    /*
     * 不動点コンビネータ - Wikipedia
     * https://ja.wikipedia.org/wiki/%E4%B8%8D%E5%8B%95%E7%82%B9%E3%82%B3%E3%83%
     * B3%E3%83%93%E3%83%8D%E3%83%BC%E3%82%BF#Z%E3%82%B3%E3%83%B3%E3%83%93%E3%83
     * %8D%E3%83%BC%E3%82%BF
     *
     * JavaScript ではこのように実装できる。
     *
     * function Z(f) { return (function(x) { return f(function(y) { return
     * x(x)(y); }); })(function(x) { return f(function(y) { return x(x)(y); });
     * }); }
     *
     * Z(function(f) { return function(n) { return n == 0 ? 1 : n * f(n - 1); };
     * })(5) == 120
     */

    interface RecursiveFunction<F> extends Function<RecursiveFunction<F>, F> {
    }

    static <A, B> Function<A, B> Y(Function<Function<A, B>, Function<A, B>> f) {
        RecursiveFunction<Function<A, B>> r = w -> f.apply(x -> w.apply(w).apply(x));
        return r.apply(r);
    }

    /**
     * Y combinator - Rosetta Code
     * https://rosettacode.org/wiki/Y_combinator#Java_2
     */
    @Test
    public void 不動点コンビネータのテスト() {
        Function<Integer, Integer> fib = Y(
            f -> n -> (n <= 2)
                ? 1
                : (f.apply(n - 1) + f.apply(n - 2)));
        Function<Integer, Integer> fac = Y(
            f -> n -> (n <= 1)
                ? 1
                : (n * f.apply(n - 1)));
        System.out.println("fib(10) = " + fib.apply(10));
        System.out.println("fac(10) = " + fac.apply(10));
    }

    static <T, R> Function<T, R> fixedPointCombinator(Function<Function<T, R>, Function<T, R>> f) {
        return new Function<T, R>() {
            @Override
            public R apply(T t) {
                return f.apply(this).apply(t);
            }
        };
    }

    static Function<Function<Integer, Integer>, Function<Integer, Integer>> factorial = self -> n -> n <= 0
        ? 1
        : n * self.apply(n - 1);

    @Test
    public void 単純化した不動点コンビネータのテスト() {
        System.out.println("factorial(10) = " + fixedPointCombinator(factorial).apply(10));
    }

    static <T, R> Function<T, R> memoize(Function<Function<T, R>, Function<T, R>> f) {
        return new Function<T, R>() {
            final Map<T, R> cache = new HashMap<>();

            @Override
            public R apply(T t) {
                R v = cache.get(t);
                if (v == null)
                    cache.put(t, v = f.apply(this).apply(t));
                return v;
                // 以下の実装はConcurrentModificationExceptionがスローされる。
                // return cache.computeIfAbsent(t, k -> f.apply(this).apply(k));
            }

            @Override
            public String toString() {
                return cache.toString();
            }
        };
    }

    static Function<Function<Integer, Integer>, Function<Integer, Integer>> fibonacci = self -> n -> n == 0
        ? 0
        : n == 1 ? 1 : self.apply(n - 1) + self.apply(n - 2);

    @Test
    public void メモ化のテスト() {
        Function<Integer, Integer> memoizedFibonacci = memoize(fibonacci);
        System.out.println("fibonacci(10) = " + memoizedFibonacci.apply(10));
        System.out.println(memoizedFibonacci);
    }

    static int tarai(int x, int y, int z) {
        if (x <= y)
            return y;
        else
            return tarai(tarai(x - 1, y, z),
                tarai(y - 1, z, x),
                tarai(z - 1, x, y));
    }

    static record Args(int x, int y, int z) {
    }

    static Function<Function<Args, Integer>, Function<Args, Integer>> tarai = self -> a -> a.x <= a.y
        ? a.y
        : self.apply(new Args(self.apply(new Args(a.x - 1, a.y, a.z)),
            self.apply(new Args(a.y - 1, a.z, a.x)),
            self.apply(new Args(a.z - 1, a.x, a.y))));

    @Test
    public void recordによる複数引数のメモ化() {
        Function<Args, Integer> memoizedTarai = memoize(tarai);
        System.out.println("tarai(3, 2, 1) = " + memoizedTarai.apply(new Args(3, 2, 1)));
        System.out.println("キャッシュの中身: " + memoizedTarai);
    }

    static String 時間測定(Supplier<String> s) {
        long start = System.currentTimeMillis();
        return s.get() + " : 所要時間 " + (System.currentTimeMillis() - start) + "ms";
    }

    @Test
    public void 通常の関数とrecordによる複数引数のメモ化の性能比較() {
        System.out.println(時間測定(() -> "通常の竹内関数           tarai(15, 7, 1) = " + tarai(15, 7, 1)));
        System.out.println(時間測定(() -> "メモ化竹内関数(record)   tarai(15, 7, 1) = "
            + memoize(tarai).apply(new Args(15, 7, 1))));
    }

    /*
     *
     * tarai -> Function<Integer, Function<Integer, Function<Integer, Integer>>>
     * tarai.apply(3) -> Function<Integer, Function<Integer, Integer>>
     * tarai.apply(3).apply(2) -> Function<Integer, Integer>
     * tarai.apply(3).apply(2).apply(1) -> Integer
     */
    @Test
    public void カリー化による複数引数のメモ化() {
        Function<Integer, Function<Integer, Function<Integer, Integer>>> tarai = memoize(
            self -> x -> memoize(selfy -> y -> memoize(selfz -> z -> x <= y ? y
                : self.apply(self.apply(x - 1).apply(y).apply(z))
                    .apply(self.apply(y - 1).apply(z).apply(x))
                    .apply(self.apply(z - 1).apply(x).apply(y)))));
        System.out.println("tarai(3, 2, 1) = " + tarai.apply(3).apply(2).apply(1));
        System.out.println("キャッシュの中身: " + tarai);
        System.out.println(
            時間測定(() -> "メモ化竹内関数(カリー化) tarai(15, 7, 1) = " + tarai.apply(15).apply(7).apply(1)));
    }

    static <T, R> Function<T, R> trace(String name, Consumer<String> output,
        Function<Function<T, R>, Function<T, R>> f) {
        return new Function<T, R>() {
            int nest = 0;

            @Override
            public R apply(T t) {
                String indent = "  ".repeat(nest);
                output.accept(indent + name + "(" + t + ")");
                ++nest;
                R r = f.apply(this).apply(t);
                --nest;
                output.accept(indent + r);
                return r;
            }
        };
    }

    @Test
    public void トレースのテスト() {
        System.out.println(
            "fibonacci(6) = " + trace("fibonacci", System.out::println, fibonacci).apply(6));
    }

    static <T, R> Function<T, R> memoizeTrace(String name, Consumer<String> output,
        Function<Function<T, R>, Function<T, R>> f) {
        return new Function<T, R>() {
            Map<T, R> cache = new HashMap<>();
            int nest = 0;

            @Override
            public R apply(T t) {
                String indent = "  ".repeat(nest);
                output.accept(indent + name + "(" + t + ")");
                ++nest;
                R result = cache.get(t);
                String from = "";
                if (result == null)
                    cache.put(t, result = f.apply(this).apply(t));
                else
                    from = " (cache)";
                --nest;
                output.accept(indent + result + from);
                return result;
            }

            @Override
            public String toString() {
                return cache.toString();
            }
        };
    }

    @Test
    public void メモ化トレース() {
        System.out.println("トレース       fibonacci(6) = "
            + trace("fibonacci", System.out::println, fibonacci).apply(6));
        System.out.println("メモ化トレース fibonacci(6) = "
            + memoizeTrace("fibonacci", System.out::println, fibonacci).apply(6));
    }

    public static double recursive(double x, double y, int n) {
        if (n == 0) {
            return x;
        }
        return recursive(x, y, n - 1) * (1 + y);
    }

    static record A(double x, double y, int n) {}
    static Function<Function<A, Double>, Function<A, Double>> recursive =
        self -> a ->
            a.n == 0 ? a.x : self.apply(new A(a.x, a.y, a.n -1)) * (1 + a.y);

    @Test
    public void testRecursive() {
        Function<A, Double> r = trace("recursive", System.out::println, recursive);
        r.apply(new A(1, 2, 3));
    }
}
