package test.puzzle.cps;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestCPS {

    interface Cont { void apply(int i); }
    interface Cont0 { void apply(Cont c); }
    interface Cont1 { void apply(Cont c, int a); }
    interface Cont2 { void apply(Cont c, int a, int b); }

    Cont print = System.out::println;
    void inc(Cont c, int a) { c.apply(a + 1); }
    void add(Cont c, int a, int b) { c.apply(a + b); }
    void mult(Cont c, int a, int b) { c.apply(a * b); }

    @Test
    public void testInc() {
        inc(print, 3);
    }

    interface Unary {
        void apply(Cont c, int a);
    }

    @Test
    public void testExecute() {
        // inc(1 + 3 + 5) == 10
        Runnable execute = () ->
            add(v1 ->
                add(v2 ->
                    inc(v3 ->
                        print.apply(v3),
                        v2),
                    v1, 5),
                1, 3);
        execute.run();
    }

    @Test
    public void testLambda() {
        Cont0 execute = c ->
            add(v1 ->
                add(v2 ->
                    inc(v3 ->
                        c.apply(v3),
                        v2),
                    v1, 5),
                1, 3);
        int[] result = new int[1];
        execute.apply(c -> result[0] = c);
        assertEquals(10, result[0]);
    }

    @Test
    public void testAdd() {
        int[] r = {0};
        // 2 + 3
        add(x -> r[0] = x, 2, 3);
        assertEquals(5, r[0]);
        // (2 + 3) * 4
        add(x ->
            mult(c -> r[0] = c,
                x, 4),
            2, 3);
        assertEquals(20, r[0]);
    }

}
