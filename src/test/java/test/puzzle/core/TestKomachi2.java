package test.puzzle.core;

import org.junit.Test;

import puzzle.core.Komachi2;

public class TestKomachi2 {

    @Test
    public void testSolve() {
        int[] digits = {1,2,3,4,5,6,7,8,9};
        Komachi2.solve(digits, 100);
    }

    @Test
    public void testSolveReverse() {
        int[] digits = {9,8,7,6,5,4,3,2,1};
        Komachi2.solve(digits, 100);
    }
}
