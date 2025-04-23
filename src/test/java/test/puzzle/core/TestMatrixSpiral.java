package test.puzzle.core;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.Test;

public class TestMatrixSpiral {

    static boolean add(List<Integer> list, int value) {
        return value > 0 ? list.add(value) : false;
    }

    static int[] steps(int rows, int cols) {
        List<Integer> r = new ArrayList<>();
        --rows; --cols;
        add(r, cols);
        while (add(r, rows--) && add(r, cols--))
            /* do nothing */;
        return r.stream().mapToInt(i -> i).toArray();
    }

    void testSteps(int[] expected, int rows, int cols) {
        int[] steps = steps(rows, cols);
        System.out.printf("rows=%d cols=%d steps=%s%n", rows, cols, Arrays.toString(steps));
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
        for (int i = 0, dx = 0, dy = 1, d = 0, s[] = steps(rows, cols); i < s.length; ++i, d = dx, dx = dy, dy = -d)
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
        int[][] m = spiral(9, 6);
        print(m);
    }

    static class Spiral {
        final int[][] matrix;
        int n = 0, x = 0, y = 0, dx = 0, dy = 1;

        boolean forward(int s) {
            if (s <= 0)
                return false;
            for (int i = 0; i < s; ++i, x += dx, y += dy)
                matrix[x][y] = n++;
            int d = dx;
            dx = dy;
            dy = -d;
            return true;
        }

        Spiral(int rows, int cols) {
            this.matrix = new int[rows--][cols--];
            forward(cols);
            while (forward(rows--) && forward(cols--))
                /* do nothing */;
            matrix[x][y] = n;
        }

        static int[][] right(int rows, int cols) {
            return new Spiral(rows, cols).matrix;
        }
    }

    @Test
    public void testSpiralClass() {
        assertArrayEquals(new int[][] {
            {0, 1, 2},
            {7, 8, 3},
            {6, 5, 4},
        }, Spiral.right(3, 3));
        assertArrayEquals(new int[][] {
            {0, 1, 2},
            {9, 10, 3},
            {8, 11, 4},
            {7, 6, 5},
        }, Spiral.right(4, 3));
    }

}
