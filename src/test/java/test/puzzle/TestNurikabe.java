package test.puzzle;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.logging.Logger;

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
    static void set(int[][] matrix, int value, int... p) { matrix[row(p)][col(p)] = value; }
    static boolean get(boolean[][] matrix, int... p) { return matrix[row(p)][col(p)]; }
    static void set(boolean[][] matrix, boolean value, int... p) { matrix[row(p)][col(p)] = value; }
    static boolean in(int[][] m, int... p) {
        return p[0] >= 0 && p[0] < m.length
            && p[1] >= 0 && p[1] < m[0].length;
    }

    static final int[][] N4 = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
    static final IntFunction<int[][]> TOI2 = int[][]::new;

    static List<int[]> neighbors(int[][] matrix, int[][] directions, int... p) {
        List<int[]> result = new ArrayList<>();
        for (int[] dir : directions) {
            int[] x = add(p, dir);
            if (in(matrix, x))
                result.add(x);
        }
        return result;
    }

    static List<int[]> neighbors4(int[][] matrix, int... p) {
        return neighbors(matrix, N4, p);
    }

    static List<int[]> neighbors(int[][] matrix, int[][] directions, Predicate<int[]> includes, int... p) {
        boolean[][] visited = new boolean[matrix.length][matrix[0].length];
        List<int[]> result = new ArrayList<>();
        new Object() {
            void make(int... x) {
                result.add(x);
                set(visited, true, x);
                for (int[] dir : directions) {
                    int[] y = add(x, dir);
                    if (in(matrix, y) && !get(visited, y) && includes.test(y))
                        make(y);
                }
            }
        }.make(p);
        return result;
    }
    static List<int[]> neighbors4(int[][] matrix, Predicate<int[]> includes, int... p) {
        return neighbors(matrix, N4, includes, p);
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

    static void assertListI2Equals(List<int[]> expected, List<int[]> actual) {
        int size = expected.size();
        assertEquals("diff size", size, actual.size());
        for (int i = 0; i < size; ++i)
            assertArrayEquals("diff at " + i, expected.get(i), actual.get(i));
    }

    @Test
    void testCrawl4Size() {
        assertEquals(2, neighbors4(board, 0, 0).size());
        assertEquals(4, neighbors4(board, 1, 1).size());
        assertEquals(3, neighbors4(board, 0, 4).size());
        assertEquals(2, neighbors4(board, 9, 9).size());
    }

    @Test
    void testN4p() {
        assertArrayEquals(new int[][] {{0, 1}, {1, 0}}, neighbors4(board, 0, 0).stream().toArray(TOI2));
        assertArrayEquals(new int[][] {{0, 1}, {1, 2}, {2, 1}, {1, 0}}, neighbors4(board, 1, 1).stream().toArray(TOI2));
        assertArrayEquals(new int[][] {{0, 5}, {1, 4}, {0, 3}}, neighbors4(board, 0, 4).stream().toArray(TOI2));
        assertArrayEquals(new int[][] {{8, 9}, {9, 8}}, neighbors4(board, 9, 9).stream().toArray(TOI2));
    }

    @Test
    void testN4v() {
        assertArrayEquals(new int[] {0, 2}, neighbors4(board, 0, 0).stream().mapToInt(p -> get(board, p)).toArray());
        assertArrayEquals(new int[] {0, 0, 0, 2}, neighbors4(board, 1, 1).stream().mapToInt(p -> get(board, p)).toArray());
        assertArrayEquals(new int[] {0, 0, 0}, neighbors4(board, 0, 4).stream().mapToInt(p -> get(board, p)).toArray());
        assertArrayEquals(new int[] {0, 2}, neighbors4(board, 9, 9).stream().mapToInt(p -> get(board, p)).toArray());
    }

    @Test
    void testCrawl() {
        int[][] matrix = {
            {1, 0, 0, 0, 0, 0, 1},
            {0, 0, 1, 0, 0, 0, 0},
            {0, 0, 0, 1, 1, 0, 0},
            {1, 1, 1, 1, 0, 0, 0},
            {0, 0, 0, 1, 0, 0, 0},
            {0, 0, 0, 1, 0, 0, 0},
            {0, 0, 0, 1, 1, 1, 1},
        };
        assertEquals(12, neighbors(matrix, N4, p -> matrix[p[0]][p[1]] == 1, 3, 3).size());
    }

}
