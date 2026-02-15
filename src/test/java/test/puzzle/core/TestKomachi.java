package test.puzzle.core;

import org.junit.Test;

import puzzle.core.Komachi;

public class TestKomachi {

    @Test
    public void testSolve() {
        int[] digits = {1,2,3,4,5,6,7,8,9};
        Komachi.solve(digits, 100);
    }

}
