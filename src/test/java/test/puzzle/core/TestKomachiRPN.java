package test.puzzle.core;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import puzzle.core.Cons;
import puzzle.core.KomachiRPN;
import puzzle.core.Rational;

public class TestKomachiRPN {

    @Test
    public void testSolve() {
        int[] digits = {1, 2};
        List<Cons<Integer>> list = new ArrayList<>();
        KomachiRPN.solve(digits, 3, list);
        assertEquals(
            List.of(
                Cons.of(1, 2, KomachiRPN.PLUS),
                Cons.of(1, 2, KomachiRPN.MINUS),
                Cons.of(1, 2, KomachiRPN.MULT),
                Cons.of(1, 2, KomachiRPN.DIV),
                Cons.of(12)),
            list);
        assertEquals(
            List.of("(1+2)", "(1-2)", "(1*2)", "(1/2)", "12"),
            list.stream().map(c -> KomachiRPN.tree(c).toString()).toList());
        assertEquals(
            List.of(Rational.of(3), Rational.of(-1), Rational.of(2), Rational.of(1, 2), Rational.of(12)),
            list.stream().map(c -> KomachiRPN.tree(c).value).toList());
    }

    @Test
    public void testKomachi() {
        int[] digits = {1, 2, 3, 4, 5};
        KomachiRPN.solve(digits, 10);
    }

    @Test
    public void testTicket() {
        KomachiRPN.solve(new int[] {9, 9, 9, 9}, 10);
    }
}
