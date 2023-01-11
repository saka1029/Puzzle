package test.puzzle.core;

import static org.junit.Assert.assertEquals;
import static puzzle.core.Nonogram.availables;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import puzzle.core.Nonogram;

public class TestNonogram {

    static void printTestCaseName() {
        System.out.println(Thread.currentThread().getStackTrace()[2].getMethodName());
    }
    
    @SafeVarargs
    static <T> List<T> list(T... elements) {
        return List.of(elements);
    }

    @Test
    public void testAvailable() {
        printTestCaseName();
        assertEquals(list(list(1, -1, 1)),
            availables(new int[] {1, 1}, 3));
        assertEquals(list(list(1, -1, -1), list(-1, 1, -1), list(-1, -1, 1)),
            availables(new int[] {1}, 3));
        assertEquals(list(list(1, -1, 1, -1), list(1, -1, -1, 1), list(-1, 1, -1, 1)),
            availables(new int[] {1, 1}, 4));
        assertEquals(list(list(1, 1, -1, 1, -1), list(1, 1, -1, -1, 1), list(-1, 1, 1, -1, 1)),
            availables(new int[] {2, 1}, 5));
        assertEquals(list(list(1, 1, 1, 1, 1)),
            availables(new int[] {5}, 5));
        assertEquals(list(list(1, 1, -1, 1, 1)),
            availables(new int[] {2, 2}, 5));
    }
    
    @Test
    public void testSimple() {
        printTestCaseName();
        int[][] rows = {{1}, {3}, {1}};
        int[][] cols = {{1}, {3}, {1}};
        Nonogram.solve(rows, cols);
    }

    @Test
    public void test3x4() {
        printTestCaseName();
        int[][] rows = {{1, 1, 1}, {1, 1}, {2, 2}};
        int[][] cols = {{1, 1}, {2}, {1}, {1}, {3}};
        Nonogram.solve(rows, cols);
    }

    @Test
    public void test10x10() {
        printTestCaseName();
        int[][] rows = {{5}, {5}, {3}, {3, 2}, {4, 1}, {1, 3, 1}, {1, 1, 3, 1}, {5}, {2, 4}, {1, 4}};
        int[][] cols = {{2, 2}, {4, 1}, {2}, {1, 1}, {1}, {2, 4}, {2, 5}, {3, 4}, {4, 3}, {4, 5}};
        Nonogram.solve(rows, cols);
    }

    static int[][][] makeProblem(String answer) {
        String[] lines = answer.lines()
            .map(s -> s.replaceAll("\\s", ""))
            .toArray(String[]::new);
        System.out.println(Arrays.toString(lines));
        return null;
    }

    @Test
    public void test25x25() {
        String answer =
            "x x x x x x x x x x x x x x x\r\n"
            + "x x x x x x x x x x - - - x x\r\n"
            + "x - x x x x x x x - - - - - x\r\n"
            + "x - - - - - - - - - - - x - x\r\n"
            + "x - - - - - - - - - - - - - -\r\n"
            + "x - - - - - - x x x - - - - x\r\n"
            + "x - - - - x x x x x - - - x x\r\n"
            + "x x - - x x x x x x - - x x x\r\n"
            + "x x x - x x x x x - - - x x x\r\n"
            + "x x x x - - - - - - - x x x -\r\n"
            + "x x x x x x - x - x x x - x x\r\n"
            + "x x x x x x - x - x x x x x -\r\n"
            + "x x x x x - - - - - - x x x x\r\n"
            + "x x x x x x x x x x x x x x x\r\n"
            + "x x x x x x x x x x x x x x x";
        makeProblem(answer);
//        Nonogram.solve(rows, cols);
    }
}
