package test.puzzle.core;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

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
    public void testKomachi2() {
        int[] digits = {1,2,3,4,5,6,7,8,9};
        solve(digits, 100);
    }

    static final int PLUS = -100, MINUS = -99, MULT = -98, DIV = -97;

    static void solve(int[] digits, int goal) {
        new Object() {
            int[] terms = new int[digits.length * 2];
            int tsize = 0;

            void push(int value) {
                terms[tsize++] = value;
            }

            int ct = 0;
            void solve(int i, int term) {
                if (i >= digits.length) {
                    if (++ct > 25)
                        throw new RuntimeException();
                    push(term);     // the last term
                    System.out.println(
                        Arrays.toString(Arrays.copyOf(terms, tsize)));
                } else {
                    int backup = tsize;
                    if (i > 0) push(term); push(PLUS); solve(i + 1, digits[i]); tsize = backup;
                    if (i > 0) push(term); push(MINUS); solve(i + 1, digits[i]); tsize = backup;
                    if (i > 0) {
                        push(term); push(MULT); solve(i + 1, digits[i]); tsize = backup;
                        push(term); push(DIV); solve(i + 1, digits[i]); tsize = backup;
                        solve(i + 1, term * 10 + digits[i]);
                    }
                }
            }

        }.solve(0, 0);
    }

    @Test
    public void testSolve() {
        int[] digits = {1,2,3,4,5,6,7,8,9};
        solve(digits, 100);
    }

    @Test
    public void testSolveReverse() {
        int[] digits = {9,8,7,6,5,4,3,2,1};
        solve(digits, 100);
    }

}
