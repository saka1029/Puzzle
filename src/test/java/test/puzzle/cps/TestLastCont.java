package test.puzzle.cps;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestLastCont {

    interface Cont { void apply(int i); }

    @Test
    public void testFactorial() {
        var f = new Object() {
            void decrement(int i, Cont c) { c.apply(i - 1); }
            void multiply(int i, int j, Cont c) { c.apply(i * j); }
            void factorial(int n, Cont c) {
                if (n <= 0)
                    c.apply(1);
                else
                    decrement(n, n1 ->
                        factorial(n1, f ->
                            multiply(f, n, c)));
            }
        };
        int[] r = {0};
        Cont c = i -> r[0] = i;
        f.factorial(0, c); assertEquals(1, r[0]);
        f.factorial(1, c); assertEquals(1, r[0]);
        f.factorial(2, c); assertEquals(2, r[0]);
        f.factorial(3, c); assertEquals(6, r[0]);
    }

    @Test
    public void testFibonacci() {
        var f = new Object() {
            void add(int a, int b, Cont c) { c.apply(a + b); }
            void subtract(int a, int b, Cont c) { c.apply(a - b); }
            void fibonacci(int n, Cont c) {
                if (n <= 1)
                    c.apply(n);
                else
                    subtract(n, 1, n1 ->
                        fibonacci(n1, f1 ->
                            subtract(n, 2, n2 ->
                                fibonacci(n2, f2 ->
                                    add(f1, f2, c)))));
            }
            int result = 0;
            void save(int i) { result = i; }
        };
        f.fibonacci(0, f::save); assertEquals(0, f.result);
        f.fibonacci(1, f::save); assertEquals(1, f.result);
        f.fibonacci(2, f::save); assertEquals(1, f.result);
        f.fibonacci(3, f::save); assertEquals(2, f.result);
        f.fibonacci(4, f::save); assertEquals(3, f.result);
        f.fibonacci(5, f::save); assertEquals(5, f.result);
        f.fibonacci(6, f::save); assertEquals(8, f.result);
        f.fibonacci(7, f::save); assertEquals(13, f.result);
    }
}
