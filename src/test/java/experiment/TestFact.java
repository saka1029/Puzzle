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

    static int factCont(int n) {
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
    public void testFactCont() {
        assertEquals(24, factCont(4));
    }

}
