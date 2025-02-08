package test.puzzle.core;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.junit.Test;

public class TestContinuation2 {

    interface Continuation<T> { void apply(T value); }
    interface DDC { void apply(double x, double y, Continuation<Double> c); }

    static <T> Continuation<T> save(Consumer<Continuation<T>> setter, Continuation<T> c) {
        setter.accept(c);
        return c;
    }

    static void multiply(double x, double y, Continuation<Double> c) { c.apply(x * y); }
    static void add(double x, double y, Continuation<Double> c) { c.apply(x + y); }
    static void sqrt(double x, Continuation<Double> c) { c.apply(Math.sqrt(x)); }

    @Test
    public void testSetter() {
        var result = new Object() { double value; };
        List<Continuation<Double>> conts = new ArrayList<>();
        DDC func = (x, y, c) ->
            multiply(x, x, save(conts::add, x2 ->
                multiply(y, y, save(conts::add, y2 ->
                    add(x2, y2, save(conts::add, x2py2 ->
                        sqrt(x2py2, c)))))));
        // sqrt(3 * 3 + 4 * 4)
        func.apply(3, 4, r -> result.value = r);
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
