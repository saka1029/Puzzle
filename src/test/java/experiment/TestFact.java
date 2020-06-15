package experiment;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Deque;
import java.util.LinkedList;
import java.util.function.IntUnaryOperator;

import org.junit.Test;

public class TestFact {

    static int factRec(int n) {
        int r;
        if (n <= 0)
            r = 1;
        else
            r = factRec(n - 1) * n;
        return r;
    }

    @Test
    public void testFactRec() {
        assertEquals(24, factRec(4));
    }

    static int factRep(int n) {
        Deque<Integer> stack = new LinkedList<>();
        int r;
        while (true)
            if (n <= 0) {
                r = 1;
                break;
            } else {
                stack.push(n);
                --n;
            }
        while (!stack.isEmpty())
            r = r * stack.pop();
        return r;
    }

    @Test
    public void testFactRep() {
        assertEquals(24, factRep(4));
    }

    static int factTailRec(int n, int r) {
        if (n <= 0)
            return r;
        else
            return factTailRec(n - 1, r * n);
    }

    static int factTailRec(int n) {
        return factTailRec(n, 1);
    }

    @Test
    public void testFactTailRec() {
        assertEquals(24, factTailRec(4));
    }

    static int factTailRecRep(int n) {
        int r = 1;
        while (n > 0)
            r *= n--;
        return r;
    }

    @Test
    public void testFactTailRecRep() {
        assertEquals(24, factTailRecRep(4));
    }

    static int factCont(int n, IntUnaryOperator cont) {
        if (n <= 0)
            return cont.applyAsInt(1);
        else
            return factCont(n - 1, x -> cont.applyAsInt(x * n));
    }

    @Test
    public void testFactCont() {
        assertEquals(24, factCont(4, x -> x));
    }

    static int factContRep(int n) {
        IntUnaryOperator c = IntUnaryOperator.identity();
        while (n > 0) {
            IntUnaryOperator cc = c;
            int nn = n;
            c = i -> cc.applyAsInt(i * nn);
            --n;
        }
        return c.applyAsInt(1);
    }

    @Test
    public void testFactContRep() {
        assertEquals(24, factContRep(4));
    }

    static class ContRep implements IntUnaryOperator {
        static char arg = 'a';
        final IntUnaryOperator c;
        final int n;

        ContRep(int n, IntUnaryOperator c) {
            this.n = n;
            this.c = c;
        }

        @Override
        public int applyAsInt(int i) {
            return c.applyAsInt(i * n);
        }

        @Override
        public String toString() {
            return String.format("(%s -> %s.applyAsInt(%s * %s))", arg, c, arg++, n);
        }
    }

    static int factContRep2(int n) {
        IntUnaryOperator c = new IntUnaryOperator() {
            @Override public int applyAsInt(int x) { return x; }
            @Override public String toString() { return "(x -> x)"; }
        };
        while (n > 0)
            c = new ContRep(n--, c);
        System.out.println(c);
        return c.applyAsInt(1);
    }

    @Test
    public void testFactContRep2() {
        assertEquals(24, factContRep2(4));
    }

    static int factStackCont(int n) {
        Deque<IntUnaryOperator> stack = new LinkedList<>();
        int r;
        while (true)
            if (n <= 0) {
                r = 1;
                break;
            } else {
                int nn = n;
                stack.push(i -> i * nn);
                --n;
            }
        while (!stack.isEmpty())
            r = stack.pop().applyAsInt(r);
        return r;
    }

    @Test
    public void testFactStackCont() {
        assertEquals(24, factStackCont(4));
    }

}
