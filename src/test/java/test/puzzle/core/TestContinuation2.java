package test.puzzle.core;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.junit.Test;

public class TestContinuation2 {

    interface Continuation<T> { void apply(T value); }

    static void multiply(double x, double y, Continuation<Double> c) { c.apply(x * y); }
    static void add(double x, double y, Continuation<Double> c) { c.apply(x + y); }
    static void sqrt(double x, Continuation<Double> c) { c.apply(Math.sqrt(x)); }

    static void pyth(double x, double y, Continuation<Double> k) {
        multiply(x, x, x2 ->
            multiply(y, y, y2 ->
                add(x2, y2, x2py2 ->
                    sqrt(x2py2, k))));
    }

    @Test
    public void testPyth() {
        var result = new Object() { double value; };
        pyth(1, 1, v -> result.value = v);
        assertEquals(Math.sqrt(2), result.value, 1e-5);
    }

    static void equals(int x, int y, Continuation<Boolean> k) { k.apply(x == y); }
    static void add(int x, int y, Continuation<Integer> k) { k.apply(x + y); }
    static void subtract(int x, int y, Continuation<Integer> k) { k.apply(x - y); }
    static void multiply(int x, int y, Continuation<Integer> k) { k.apply(x * y); }

    static void factorial(int n, Continuation<Integer> k) {
        equals(n, 0, b -> {
            if (b)
                k.apply(1);
            else
                subtract(n, 1, nm1 ->
                    factorial(nm1, f ->
                        multiply(n, f, k)));
        });
    }

    @Test
    public void testFactorial() {
        var result = new Object() { int value; };
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
                subtract(n, 1, nm1 ->
                    multiply(n, a, (Integer nta) ->     // 単に「multiply(n, a, nta -> 」とするとコンパイルエラー
                        f_aux(nm1, nta, k)));
        });
    }

    @Test
    public void testFactorialTailRecursion() {
        var result = new Object() { int value; };
        factorialTailRecursion(6, v -> result.value = v);
        assertEquals(720, result.value);
    }

    interface DoubleDoubleContinuation { void apply(double x, double y, Continuation<Double> c); }

    static <T> Continuation<T> save(Consumer<Continuation<T>> setter, Continuation<T> c) {
        setter.accept(c);
        return c;
    }

    @Test
    public void testSetter() {
        var result = new Object() { double value; };
        List<Continuation<Double>> conts = new ArrayList<>();
        DoubleDoubleContinuation pyth = (x, y, c) ->
            multiply(x, x, save(conts::add, x2 ->
                multiply(y, y, save(conts::add, y2 ->
                    add(x2, y2, save(conts::add, x2py2 ->
                        sqrt(x2py2, c)))))));
        // sqrt(3 * 3 + 4 * 4)
        pyth.apply(3, 4, r -> result.value = r);
        assertEquals(5D, result.value, 1e-5);
        // sqrt(3 * 3 + 4 * 4) -> sqrt(9)
        conts.get(2).apply(9D);
        assertEquals(3D, result.value, 1e-5);
        // sqrt(3 * 3 + 4 * 4) -> sqrt(3 * 3 + 4)
        conts.get(1).apply(4D);
        assertEquals(Math.sqrt(13), result.value, 1e-5);
        // sqrt(3 * 3 + 4 * 4) -> sqrt(1 + 4 * 4)
        conts.get(0).apply(1D);
        assertEquals(Math.sqrt(17), result.value, 1e-5);
        assertEquals(6, conts.size());
    }

}
