package test.puzzle.core;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.IntStream;
import org.junit.Test;

public class TestSpiralMatrixGeneral {

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
        int rows = matrix.length;
        int cols = rows > 0 ? matrix[0].length : 0;
        System.out.printf("%d x %d%n", rows, cols);
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

    static class Turtle {
        final int[][] matrix;
        int x, y, dx, dy;

        Turtle(int[][] matrix, int x, int y, int dx, int dy) {
            this.matrix = matrix;
            this.x = x;
            this.y = y;
            this.dx = dx;
            this.dy = dy;
        }

        void turn(boolean right) {
            int t = dx;
            if (right) {
                dx = dy;
                dy = -t;
            } else {
                dx = -dy;
                dy = t;
            }
        }

        void go() {
            x += dx;
            y += dy;
        }

        void set(int value) {
            matrix[x][y] = value;
        }
    }

    static int[][] spiralTurtleRight(int rows, int cols) {
        int[][] matrix = new int[rows][cols];
        int n = 0, max = rows * cols;
        int minStart = (Math.min(rows, cols) + 1) / 2;
        Turtle turtle = new Turtle(matrix, 0, 0, 0, 1);
        --rows; --cols;
        for (int s = 0; s < minStart; ++s, rows -= 2, cols -= 2) {
            turtle.x = s; turtle.y = s;
            for (int i : new int[] { cols, rows, cols, rows}) {
                for (int j = 0; j < i && n < max; ++j, turtle.go())
                    turtle.set(n++);
                turtle.turn(true);
            }
        }
        return matrix;
    }

    static int[][] spiralTurtleLeft(int rows, int cols) {
        int[][] matrix = new int[rows][cols];
        int n = 0, max = rows * cols;
        int minStart = (Math.min(rows, cols) + 1) / 2;
        Turtle turtle = new Turtle(matrix, 0, 0, 1, 0);
        --rows; --cols;
        for (int s = 0; s < minStart; ++s, rows -= 2, cols -= 2) {
            turtle.x = s; turtle.y = s;
            for (int i : new int[] { rows, cols, rows, cols }) {
                for (int j = 0; j < i && n < max; ++j, turtle.go())
                    turtle.set(n++);
                turtle.turn(false);
            }
        }
        return matrix;
    }

    @Test
    public void testSpiralTurtle() {
        print(spiralTurtleRight(9, 5));
        System.out.println("--");
        print(spiralTurtleLeft(9, 5));
    }

    /**
     * c : 四角形の水平長さ
     * r : 四角形の垂直長さ
     * s : 四角形描画の開始位置(x = s, y = sから開始する)
     * t : sの最大値(s * 2 < tでなければならない)
     * x : 現在の垂直位置
     * y : 現在の水平位置
     * z : 垂直方向の移動距離(-1, 0, 1のいずれか)
     * u : 水平方向の移動距離(-1, 0, 1のいずれか)
     * d : 回転時に使用する一時変数
     * @param h 四角形の幅
     * @param w 四角形の高さ
     * @return 左上隅から開始する螺旋上に数字をゼロから埋めた四角形の配列
     */
    static int[][] spiralRightSimple(int h, int w) {
        int[][] matrix = new int[h][w];
        for (int s = 0, n = 0, r = h - 1, c = w - 1, t = Math.min(h, w), m = h * w, z = 0, u = 1; s * 2 < t; ++s, r -= 2, c -= 2)
            for (int i = 0, x = s, y = s, p[] = {c + r == 0 ? 1 : c, r, c, r}, d = 0; i < 4; ++i, d = z, z = u, u = -d)
                for (int j = 0; j < p[i] && n < m; ++j, x += z, y += u, ++n)
                    matrix[x][y] = n;
        return matrix;
    }

    static final int[][][] SPIRALS_SQUARE = {
        // s = 0
        {},
        // s = 1
        {{0}},
        // s = 2
        {{0, 1},
        {3, 2}},
        // s = 3
        {{0, 1, 2},
        {7, 8, 3},
        {6, 5, 4}},
        // s = 4
        {{0, 1, 2, 3},
        {11, 12, 13, 4},
        {10, 15, 14, 5},
        {9, 8, 7, 6}},
        // s = 5
        {{0, 1, 2, 3, 4},
        {15, 16, 17, 18, 5},
        {14, 23, 24, 19, 6},
        {13, 22, 21, 20, 7},
        {12, 11, 10, 9, 8}},
        // s = 6
        {{0, 1, 2, 3, 4, 5},
        {19, 20, 21, 22, 23, 6},
        {18, 31, 32, 33, 24, 7},
        {17, 30, 35, 34, 25, 8},
        {16, 29, 28, 27, 26, 9},
        {15, 14, 13, 12, 11, 10}},
        // s = 7
        {{0, 1, 2, 3, 4, 5, 6},
        {23, 24, 25, 26, 27, 28, 7},
        {22, 39, 40, 41, 42, 29, 8},
        {21, 38, 47, 48, 43, 30, 9},
        {20, 37, 46, 45, 44, 31, 10},
        {19, 36, 35, 34, 33, 32, 11},
        {18, 17, 16, 15, 14, 13, 12}},
        // s = 8
        {{0, 1, 2, 3, 4, 5, 6, 7},
        {27, 28, 29, 30, 31, 32, 33, 8},
        {26, 47, 48, 49, 50, 51, 34, 9},
        {25, 46, 59, 60, 61, 52, 35, 10},
        {24, 45, 58, 63, 62, 53, 36, 11},
        {23, 44, 57, 56, 55, 54, 37, 12},
        {22, 43, 42, 41, 40, 39, 38, 13},
        {21, 20, 19, 18, 17, 16, 15, 14}},
        // s = 9
        {{0, 1, 2, 3, 4, 5, 6, 7, 8},
        {31, 32, 33, 34, 35, 36, 37, 38, 9},
        {30, 55, 56, 57, 58, 59, 60, 39, 10},
        {29, 54, 71, 72, 73, 74, 61, 40, 11},
        {28, 53, 70, 79, 80, 75, 62, 41, 12},
        {27, 52, 69, 78, 77, 76, 63, 42, 13},
        {26, 51, 68, 67, 66, 65, 64, 43, 14},
        {25, 50, 49, 48, 47, 46, 45, 44, 15},
        {24, 23, 22, 21, 20, 19, 18, 17, 16}},
    };

    static Map<int[], int[][]> SPIRALS = Map.of(
        new int[] {1, 3},
        new int[][] {
            {0, 1, 2}},
        new int[] {3, 1},
        new int[][] {
            {0},
            {1},
            {2}},
        new int[] {4, 3},
        new int[][] {
            {0, 1, 2},
            {9, 10, 3},
            {8, 11, 4},
            {7, 6, 5}},
        new int[] {3, 4},
        new int[][] {
            {0, 1, 2, 3},
            {9, 10, 11, 4},
            {8, 7, 6, 5}}
    );

    @Test
    public void testSpiralRightSimple() {
        for (int i = 0, max = SPIRALS_SQUARE.length; i < max; ++i) {
            int[][] a = spiralRightSimple(i, i);
            print(a);
            assertArrayEquals(SPIRALS_SQUARE[i], a);
        }
        for (Entry<int[], int[][]> e : SPIRALS.entrySet()) {
            int[][] a = spiralRightSimple(e.getKey()[0], e.getKey()[1]);
            print(a);
            assertArrayEquals(e.getValue(), a);
        }
    }

    static final int VACANT = -1;
    static int[][] spiralLeftVacant(int height, int width) {
        int[][] matrix = new int[height][width];
        for (int[] row : matrix)
            Arrays.fill(row, VACANT);
        int max = height * width, x = 0, y = 0, dx = 1, dy = 0, nx, ny;
        for (int n = 0; n < max; n++, x = nx, y = ny) {
            matrix[x][y] = n;
            nx = x + dx; ny = y + dy;
            if (nx < 0 || nx >= height || ny < 0 || ny >= width || matrix[nx][ny] != VACANT) {
                int temp = dx; dx = -dy; dy = temp; // turn left
                nx = x + dx; ny = y + dy;
            }
        }
        return matrix;
    }

    static int[][] spiralRightVacant(int height, int width) {
        int[][] matrix = new int[height][width];
        for (int[] row : matrix)
            Arrays.fill(row, VACANT);
        int max = height * width, x = 0, y = 0, dx = 0, dy = 1, nx, ny;
        for (int n = 0; n < max; n++, x = nx, y = ny) {
            matrix[x][y] = n;
            nx = x + dx; ny = y + dy;
            if (nx < 0 || nx >= height || ny < 0 || ny >= width || matrix[nx][ny] != VACANT) {
                int temp = dx; dx = dy; dy = -temp; // turn right
                nx = x + dx; ny = y + dy;
            }
        }
        return matrix;
    }

    @Test
    public void testSpiralRightVacant() {
        for (int i = 0, max = SPIRALS_SQUARE.length; i < max; ++i) {
            int[][] a = spiralRightVacant(i, i);
            print(a);
            assertArrayEquals(SPIRALS_SQUARE[i], a);
        }
        for (Entry<int[], int[][]> e : SPIRALS.entrySet()) {
            int[][] a = spiralRightVacant(e.getKey()[0], e.getKey()[1]);
            print(a);
            assertArrayEquals(e.getValue(), a);
        }
    }

    @Test
    public void testSpiralLeftVacant() {
        print(spiralLeftVacant(5, 5));
    }
}
