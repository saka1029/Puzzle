package test.puzzle;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import puzzle.Common;

class TestNurikabe {

    static final Logger loger = Common.getLogger(TestNurikabe.class);

    static final int UNKNOWN = 0;
    static final int BLACK = -1;
    static final int WHITE = -2;

    static int r(int... p) { return p[0]; }
    static int c(int... p) { return p[1]; }
    static int[] p(int... p) { return p; }
    static int[] add(int[] p0, int[] p1) { return p(r(p0) + r(p1), c(p0) + c(p1)); }

    static int get(int[][] m, int... p) { return m[p[0]][p[1]]; }

    static boolean in(int[][] m, int... p) {
        return p[0] >= 0 && p[0] < m.length
            && p[1] >= 0 && p[1] < m[0].length;
    }

    static final int[][] N4 = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
    static final IntFunction<int[][]> TOA2 = int[][]::new;

    static Stream<int[]> nxs(int[][] m, int[][] n, int... p) {
        return Stream.of(n).map(o -> add(p, o)).filter(o -> in(m, o));
    }

    static Stream<int[]> n4s(int[][] m, int... p) { return nxs(m, N4, p); }

    static int n4c(int[][] m, int... p) { return (int)n4s(m, p).count(); }
    static int[][] n4p(int[][] m, int... p) { return n4s(m, p).toArray(TOA2); }
    static int[] n4v(int[][] m, int... p) { return n4s(m, p).mapToInt(o -> get(m, o)).toArray(); }

    static Stream<int[]> nxr(int[][] matrix, int[][] directions, Predicate<int[]> cond, int... point) {
        boolean[][] visited = new boolean[matrix.length][matrix[0].length];
        return new Object() {
            Stream<int[]> nxs(int... x) {
                visited[x[0]][x[1]] = true;
                return Stream.concat(Stream.of(x), Stream.of(directions)
                    .map(dir -> add(x, dir))
                    .filter(p -> in(matrix, p) && !visited[p[0]][p[1]] && cond.test(p))
                    .flatMap(p -> nxs(p)));
            }
        }.nxs(point);
    }

    static void print(int[][] m) {
        for (int[] row : m)
            System.out.println(Arrays.toString(row));
    }

    static final int[][] board = {
        {0, 0, 0, 0, 2, 0, 2, 0, 0, 0,},
        {2, 0, 0, 0, 0, 0, 0, 0, 2, 0,},
        {0, 0, 0, 2, 0, 0, 0, 0, 0, 0,},
        {0, 2, 0, 0, 0, 0, 2, 0, 0, 0,},
        {0, 0, 0, 0, 0, 2, 0, 2, 0, 0,},
        {0, 0, 2, 0, 0, 0, 0, 0, 0, 0,},
        {0, 0, 0, 0, 0, 2, 0, 2, 0, 0,},
        {0, 0, 2, 0, 0, 0, 0, 0, 0, 2,},
        {2, 0, 0, 0, 0, 2, 0, 0, 0, 0,},
        {0, 0, 0, 0, 0, 0, 0, 0, 2, 0,},
    };

    static void assertMatrixEquals(int[][] a, int[][] b) {
        int length = a.length;
        assertEquals(length, b.length);
        for (int i = 0; i < length; ++i)
            assertArrayEquals(a[i], b[i]);
    }

    @Test
    void testN4c() {
        assertEquals(2, n4c(board, 0, 0));
        assertEquals(4, n4c(board, 1, 1));
        assertEquals(3, n4c(board, 0, 4));
        assertEquals(2, n4c(board, 9, 9));
    }

    @Test
    void testN4p() {
        assertMatrixEquals(new int[][] {{0, 1}, {1, 0}}, n4p(board, 0, 0));
        assertMatrixEquals(new int[][] {{0, 1}, {1, 2}, {2, 1}, {1, 0}}, n4p(board, 1, 1));
        assertMatrixEquals(new int[][] {{0, 5}, {1, 4}, {0, 3}}, n4p(board, 0, 4));
        assertMatrixEquals(new int[][] {{8, 9}, {9, 8}}, n4p(board, 9, 9));
    }

    @Test
    void testN4v() {
        assertArrayEquals(new int[] {0, 2}, n4v(board, 0, 0));
        assertArrayEquals(new int[] {0, 0, 0, 2}, n4v(board, 1, 1));
        assertArrayEquals(new int[] {0, 0, 0}, n4v(board, 0, 4));
        assertArrayEquals(new int[] {0, 2}, n4v(board, 9, 9));
    }

    @Test
    void testNxr() {
        int[][] matrix = {
            {1, 0, 0, 0, 0, 0, 1},
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 1, 1, 0, 0},
            {0, 0, 1, 1, 0, 0, 0},
            {0, 0, 0, 1, 0, 0, 0},
            {0, 0, 0, 1, 0, 0, 0},
            {0, 0, 0, 1, 1, 1, 1},
        };
        int[][] f = nxr(matrix, N4, p -> matrix[p[0]][p[1]] == 1, 3, 3).toArray(TOA2);
        System.out.println(Arrays.deepToString(f));
    }

}
