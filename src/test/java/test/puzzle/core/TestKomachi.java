package test.puzzle.core;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import puzzle.core.Komachi;

import static puzzle.core.Komachi.*;

public class TestKomachi {

    @Test
    public void testEval() throws DivisionException {
        assertEquals(579, eval(List.of(PLUS, 123, PLUS, 456)));
        assertEquals(147, eval(List.of(PLUS, 123, PLUS, 4, MULT, 6)));
        assertEquals(-99, eval(List.of(MINUS, 123, PLUS, 4, MULT, 6)));
    }

    @Test
    public void testCount() {
        int max = 2 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5;
        System.out.printf("max=%d(%s base 5)%n", max, Integer.toString(max, 5));
        System.out.printf("max - 1=%d(%s base 5)%n", max - 1, Integer.toString(max - 1, 5));
    }

    @Test
    public void testMakeTerms() {
        int[] digits = {1,2,3,4,5,6,7,8,9};
        assertEquals(List.of(-100, 123, -100, 456, -100, 789), makeTerms(digits, new int[] {0,4,4,0,4,4,0,4,4}));
    }

    @Test
    public void testIntsBase5() {
        assertArrayEquals(new int[] {0,4,4}, intsBase5(Integer.parseInt("044", 5), 3));
        assertArrayEquals(new int[] {0,1,2,3,4,1,2,3,4}, intsBase5(Integer.parseInt("012341234", 5), 9));
    }

    @Test
    public void testSolve() {
        int[] digits = {1,2,3,4,5,6,7,8,9};
        Komachi.solve(digits, 100);
    }

}
