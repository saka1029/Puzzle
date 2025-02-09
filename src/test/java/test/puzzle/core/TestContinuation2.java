package test.puzzle.core;

import static org.junit.Assert.assertEquals;
import java.util.function.Consumer;
import org.junit.Test;

public class TestContinuation2 {

    interface Continuation<T> {
        void apply(T value);
    }

    static void multiply(double x, double y, Continuation<Double> c) {
        c.apply(x * y);
    }

    static void add(double x, double y, Continuation<Double> c) {
        c.apply(x + y);
    }

    static void sqrt(double x, Continuation<Double> c) {
        c.apply(Math.sqrt(x));
    }

    static void pyth(double x, double y, Continuation<Double> k) {
        multiply(x, x, x2 ->
            multiply(y, y, y2 ->
                add(x2, y2, x2py2 ->
                    sqrt(x2py2, k))));
    }

    @Test
    public void testPyth() {
        var result = new Object() {
            double value;
        };
        pyth(1, 1, v -> result.value = v);
        assertEquals(Math.sqrt(2), result.value, 1e-5);
    }

    static void equals(int x, int y, Continuation<Boolean> k) {
        k.apply(x == y);
    }

    static void addInt(int x, int y, Continuation<Integer> k) {
        k.apply(x + y);
    }

    static void subtractInt(int x, int y, Continuation<Integer> k) {
        k.apply(x - y);
    }

    static void multiplyInt(int x, int y, Continuation<Integer> k) {
        k.apply(x * y);
    }

    static void factorial(int n, Continuation<Integer> k) {
        equals(n, 0, b -> {
            if (b)
                k.apply(1);
            else
                subtractInt(n, 1, nm1 ->
                    factorial(nm1, f ->
                        multiplyInt(n, f, k)));
        });
    }

    @Test
    public void testFactorial() {
        var result = new Object() {
            int value;
        };
        factorial(6, v -> result.value = v);
        assertEquals(720, result.value);
    }

    static void factorialTailRecursion(int n, Continuation<Integer> k) {
        f_aux(n, 1, k);
    }

    static void f_aux(int n, int a, Continuation<Integer> k) {
        equals(n, 0, b -> {
            if (b)
                k.apply(a);
            else
                subtractInt(n, 1, nm1 -> multiplyInt(n, a, nta -> f_aux(nm1, nta, k)));
        });
    }

    @Test
    public void testFactorialTailRecursion() {
        var result = new Object() {
            int value;
        };
        factorialTailRecursion(6, v -> result.value = v);
        assertEquals(720, result.value);
    }

    interface DoubleDoubleContinuation {
        void apply(double x, double y, Continuation<Double> c);
    }

    static <T> Continuation<T> save(Consumer<Continuation<T>> setter, Continuation<T> c) {
        setter.accept(c);
        return c;
    }

    @Test
    public void testSetter() {
        // 結果保存領域
        var result = new Object() {
            double value;
        };
        // 途中のContinuation保存領域
        var continuations = new Object() {
            Continuation<Double> x2, y2, x2py2;
        };
        // pyth定義(Continuation版)
        DoubleDoubleContinuation pyth = (x, y, c) ->
            multiply(x, x, save(x2c -> continuations.x2 = x2c, x2 ->
                multiply(y, y, save(y2c -> continuations.y2 = y2c, y2 ->
                    add(x2, y2, save(x2py2c -> continuations.x2py2 = x2py2c, x2py2 ->
                        sqrt(x2py2, c)))))));
        // sqrt(3 * 3 + 4 * 4)
        pyth.apply(3, 4, r -> result.value = r);
        assertEquals(5.0, result.value, 1e-5);
        // sqrt(3 * 3 + 4 * 4) -> sqrt(9)
        continuations.x2py2.apply(9.0);
        assertEquals(3.0, result.value, 1e-5);
        // sqrt(3 * 3 + 4 * 4) -> sqrt(3 * 3 + 4)
        continuations.y2.apply(4.0);
        assertEquals(Math.sqrt(13), result.value, 1e-5);
        // sqrt(3 * 3 + 4 * 4) -> sqrt(1 + 4 * 4)
        continuations.x2.apply(1.0);
        assertEquals(Math.sqrt(17), result.value, 1e-5);
        // sqrt(1 + 16) -> sqrt(1 + 24)
        // x * x は既に 1 に置き換えられている点に注意する。
        continuations.y2.apply(24.0);
        // assertEquals(5D, result.value, 1e-5);
        assertEquals(5.0, result.value, 1e-5);
    }
}
