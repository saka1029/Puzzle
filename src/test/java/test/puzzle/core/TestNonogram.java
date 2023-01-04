package test.puzzle.core;

import org.junit.Ignore;
import org.junit.Test;

import puzzle.core.Nonogram;

public class TestNonogram {

    static void printTestCaseName() {
        System.out.println(Thread.currentThread().getStackTrace()[2].getMethodName());
    }

    @Test
    public void testSimple() {
        printTestCaseName();
        int[][] rows = {{1}, {3}, {1}};
        int[][] cols = {{1}, {3}, {1}};
        Nonogram.solve(rows, cols);
    }

    @Test
    public void test10x10() {
        printTestCaseName();
        int[][] rows = {{5}, {5}, {3}, {3, 2}, {4, 1}, {1, 3, 1}, {1, 1, 3, 1}, {5}, {2, 4}, {1, 4}};
        int[][] cols = {{2, 2}, {4, 1}, {2}, {1, 1}, {1}, {2, 4}, {2, 5}, {3, 4}, {4, 3}, {4, 5}};
        Nonogram.solve(rows, cols);
    }

    @Ignore
    @Test
    public void test25x25() {
        printTestCaseName();
        int[][] rows = {
            {1},
            {1,1},
            {1,1,1},
            {5},
            {3},
            {3,3},
            {5},
            {9},
            {5,6,3},
            {8,7,5},
            {16,2,1},
            {17,2,1,2},
            {8,8,2,2},
            {8,8,6},
            {8,7,7},
            {8,7,4},
            {8,10},
            {8,2,6},
            {8,3,2},
            {9,1,3},
            {9,4},
            {9,2},
            {7},
            {4},
            {1},
        };
        int[][] cols = {
            {5,3},
            {13},
            {15},
            {15},
            {16},
            {16},
            {16},
            {16},
            {3,7},
            {4},
            {1,6,1},
            {2,7,2},
            {15},
            {12},
            {13},
            {1,11,1},
            {2,11,2},
            {3,2,7},
            {1,4,1,6,6},
            {3,11},
            {2,2,4},
            {1,2,1,4},
            {1,3},
            {5},
            {2,1},
        };
        Nonogram.solve(rows, cols);
    }

}
