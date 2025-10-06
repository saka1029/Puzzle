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
}
