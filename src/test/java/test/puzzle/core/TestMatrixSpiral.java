package test.puzzle.core;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.Test;

public class TestMatrixSpiral {

    static boolean add(List<Integer> list, int value) {
        return value > 0 ? list.add(value) : false;
    }

    static int[] steps(int rows, int cols) {
        List<Integer> r = new ArrayList<>();
        --rows;
        --cols;
        add(r, cols);
        while (add(r, rows--) && add(r, cols--))
        /* do nothing */;
        return r.stream().mapToInt(i -> i).toArray();
    }

    void testSteps(int[] expected, int rows, int cols) {
        int[] steps = steps(rows, cols);
        // System.out.printf("rows=%d cols=%d steps=%s%n", rows, cols, Arrays.toString(steps));
        assertArrayEquals(expected, steps);
        assertEquals(IntStream.of(steps).sum(), rows * cols - 1);
    }

    @Test
    public void testSteps() {
        testSteps(new int[] {1, 1, 1}, 2, 2);
        testSteps(new int[] {2, 2, 2, 1, 1}, 3, 3);
        testSteps(new int[] {3, 3, 3, 2, 2, 1, 1}, 4, 4);
        testSteps(new int[] {3}, 4, 1);
        testSteps(new int[] {3}, 1, 4);
        testSteps(new int[] {4, 2, 4, 1, 3}, 3, 5);
        testSteps(new int[] {2, 4, 2, 3, 1, 2}, 5, 3);
    }

    int[][] spiral(int rows, int cols) {
        int[][] matrix = new int[rows][cols];
        int x = 0, y = 0, n = 0;
        for (int i = 0, dx = 0, dy = 1, d = 0, s[] = steps(rows, cols); i < s.length; ++i, d =
                dx, dx = dy, dy = -d)
            for (int j = 0; j < s[i]; ++j, x += dx, y += dy)
                matrix[x][y] = n++;
        matrix[x][y] = n++;
        return matrix;
    }

    static void print(int[][] matrix) {
        int rows = matrix.length, cols = matrix[0].length;
        for (int r = 0; r < rows; ++r) {
            for (int c = 0; c < cols; ++c)
                System.out.printf("%3d", matrix[r][c]);
            System.out.println();
        }
    }

    @Test
    public void testSpiral() {
        assertArrayEquals(new int[][] {
            {0, 1, 2, 3, 4, 5},
            {25, 26, 27, 28, 29, 6},
            {24, 43, 44, 45, 30, 7},
            {23, 42, 53, 46, 31, 8},
            {22, 41, 52, 47, 32, 9},
            {21, 40, 51, 48, 33, 10},
            {20, 39, 50, 49, 34, 11},
            {19, 38, 37, 36, 35, 12},
            {18, 17, 16, 15, 14, 13}}, spiral(9, 6));
    }

    static class Spiral {
        int[][] matrix;
        int n = 0, x = 0, y = 0;
        int dx, dy;
        boolean right = true;

        boolean forward(int s) {
            if (s <= 0)
                return false;
            for (int i = 0; i < s; ++i, x += dx, y += dy)
                matrix[x][y] = n++;
            int d = dx;
            dx = right ? dy : -dy;
            dy = right ? -d : d;
            return true;
        }

        static int[][] of(int rows, int cols, boolean right) {
            Spiral spiral = new Spiral();
            spiral.matrix = new int[rows][cols];
            spiral.right = right;
            spiral.dx = right ? 0 : 1;
            spiral.dy = right ? 1 : 0;
            int r = right ? rows - 1 : cols - 1, c = right ? cols - 1 : rows - 1;
            spiral.forward(c);
            while (spiral.forward(r--) && spiral.forward(c--))
                /* do nothing */;
            spiral.matrix[spiral.x][spiral.y] = spiral.n;
            return spiral.matrix;
        }
    }

    @Test
    public void testSpiralRight() {
        assertArrayEquals(new int[][] {
            {0, 1, 2},
            {7, 8, 3},
            {6, 5, 4}}, Spiral.of(3, 3, true));
        assertArrayEquals(new int[][] {
            {0, 1, 2},
            {9, 10, 3},
            {8, 11, 4},
            {7, 6, 5}}, Spiral.of(4, 3, true));
    }

    @Test
    public void testSpiralLeft() {
        assertArrayEquals(new int[][] {
            {0, 7, 6},
            {1, 8, 5},
            {2, 3, 4}}, Spiral.of(3, 3, false));
        assertArrayEquals(new int[][] {
            {0, 9, 8},
            {1, 10, 7},
            {2, 11, 6},
            {3, 4, 5}}, Spiral.of(4, 3, false));
    }

}
