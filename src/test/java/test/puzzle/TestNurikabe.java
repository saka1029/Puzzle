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

    static int row(int... p) { return p[0]; }
    static int col(int... p) { return p[1]; }
    static int[] point(int... p) { return p; }
    static int[] add(int[] p0, int[] p1) { return point(row(p0) + row(p1), col(p0) + col(p1)); }
    static int get(int[][] matrix, int... p) { return matrix[row(p)][col(p)]; }
    static boolean in(int[][] m, int... p) {
        return p[0] >= 0 && p[0] < m.length
            && p[1] >= 0 && p[1] < m[0].length;
    }

    static final int[][] N4 = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
    static final IntFunction<int[][]> TOA2 = int[][]::new;
    static int count(Stream<int[]> s) { return (int)s.count(); }
    static int[][] array(Stream<int[]> s) { return s.toArray(TOA2); }
    static int[] values(int[][] matrix, Stream<int[]> s) { return s.mapToInt(p -> matrix[row(p)][col(p)]).toArray(); }

    static Stream<int[]> neighbors(int[][] m, int[][] n, int... p) {
        return Stream.of(n).map(o -> add(p, o)).filter(o -> in(m, o));
    }

    static Stream<int[]> neighbors4(int[][] m, int... p) { return neighbors(m, N4, p); }

    static Stream<int[]> neighbors(int[][] matrix, int[][] directions, Predicate<int[]> includes, int... p) {
        boolean[][] visited = new boolean[matrix.length][matrix[0].length];
        return new Object() {
            Stream<int[]> stream(int... x) {
                visited[row(x)][col(x)] = true;
                return Stream.concat(Stream.of(x), Stream.of(directions)
                    .map(dir -> add(x, dir))
                    .filter(p -> in(matrix, p) && !visited[row(p)][col(p)] && includes.test(p))
                    .flatMap(p -> stream(p)));
            }
        }.stream(p);
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
        assertEquals(2, count(neighbors4(board, 0, 0)));
        assertEquals(4, count(neighbors4(board, 1, 1)));
        assertEquals(3, count(neighbors4(board, 0, 4)));
        assertEquals(2, count(neighbors4(board, 9, 9)));
    }

    @Test
    void testN4p() {
        assertMatrixEquals(new int[][] {{0, 1}, {1, 0}}, array(neighbors4(board, 0, 0)));
        assertMatrixEquals(new int[][] {{0, 1}, {1, 2}, {2, 1}, {1, 0}}, array(neighbors4(board, 1, 1)));
        assertMatrixEquals(new int[][] {{0, 5}, {1, 4}, {0, 3}}, array(neighbors4(board, 0, 4)));
        assertMatrixEquals(new int[][] {{8, 9}, {9, 8}}, array(neighbors4(board, 9, 9)));
    }

    @Test
    void testN4v() {
        assertArrayEquals(new int[] {0, 2}, values(board, neighbors4(board, 0, 0)));
        assertArrayEquals(new int[] {0, 0, 0, 2}, values(board, neighbors4(board, 1, 1)));
        assertArrayEquals(new int[] {0, 0, 0}, values(board, neighbors4(board, 0, 4)));
        assertArrayEquals(new int[] {0, 2}, values(board, neighbors4(board, 9, 9)));
    }

    @Test
    void testNxr() {
        int[][] matrix = {
            {1, 0, 0, 0, 0, 0, 1},
            {0, 0, 1, 0, 0, 0, 0},
            {0, 0, 0, 1, 1, 0, 0},
            {1, 1, 1, 1, 0, 0, 0},
            {0, 0, 0, 1, 0, 0, 0},
            {0, 0, 0, 1, 0, 0, 0},
            {0, 0, 0, 1, 1, 1, 1},
        };
        assertEquals(12, neighbors(matrix, N4, p -> matrix[p[0]][p[1]] == 1, 3, 3).count());
    }

}
