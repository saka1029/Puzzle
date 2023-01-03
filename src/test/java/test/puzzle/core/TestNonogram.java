package test.puzzle.core;

import org.junit.Test;

import puzzle.core.Nonogram;

public class TestNonogram {

    @Test
    public void testSimple() {
        int[][] rows = {{1}, {3}, {1}};
        int[][] cols = {{1}, {3}, {1}};
        Nonogram.solve(rows, cols);
    }

    @Test
    public void test10x10() {
        int[][] rows = {{5}, {5}, {3}, {3, 2}, {4, 1}, {1, 3, 1}, {1, 1, 3, 1}, {5}, {2, 4}, {1, 4}};
        int[][] cols = {{2, 2}, {4, 1}, {2}, {1, 1}, {1}, {2, 4}, {2, 5}, {3, 4}, {4, 3}, {4, 5}};
        Nonogram.solve(rows, cols);
    }

}
