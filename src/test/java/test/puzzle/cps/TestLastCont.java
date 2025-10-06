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
            int fibonacci(int n) {
                int[] result = {0};
                fibonacci(n, i -> result[0] = i);
                return result[0];
            }
        };
        assertEquals(0, f.fibonacci(0));
        assertEquals(1, f.fibonacci(1));
        assertEquals(1, f.fibonacci(2));
        assertEquals(2, f.fibonacci(3));
        assertEquals(3, f.fibonacci(4));
        assertEquals(5, f.fibonacci(5));
        assertEquals(8, f.fibonacci(6));
        assertEquals(13, f.fibonacci(7));
    }
}
