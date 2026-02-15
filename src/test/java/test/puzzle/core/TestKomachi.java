package test.puzzle.core;

import org.junit.Test;

import puzzle.core.Komachi;

public class TestKomachi {

    @Test
    public void testSolve() {
        int[] digits = {1,2,3,4,5,6,7,8,9};
        Komachi.solve(digits, 100);
    }

    @Test
    public void testSolveReverse() {
        int[] digits = {9,8,7,6,5,4,3,2,1};
        Komachi.solve(digits, 100);
    }

}
