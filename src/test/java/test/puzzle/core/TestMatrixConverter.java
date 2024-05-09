package test.puzzle.core;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import puzzle.core.MatrixConverter;

public class TestMatrixConverter {

    @Test
    public void testInt() {
        int[][] src = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}};
        int[][] dst = MatrixConverter.of(src).copy();
        assertArrayEquals(src, dst);
    }

    @Test
    public void testStringArray() {
        String[][] src = {
            {"1", "2"},
            {"3", "4"},
            {"5", "6"}};
        String[][] dst = MatrixConverter.of(src).copy();
        assertArrayEquals(src, dst);
    }

    @Test
    public void testStringList() {
        List<List<String>> src = List.of(
            List.of("1", "2"),
            List.of("3", "4"),
            List.of("5", "6"));
        MatrixConverter<List<List<String>>> m = MatrixConverter.of(src);
        List<List<String>> copy = m.copy();
        assertEquals(src, copy);
        List<List<String>> trans = m.transpose();
        List<List<String>> expectedTrans = List.of(
            List.of("1", "3", "5"),
            List.of("2", "4", "6"));
        assertEquals(expectedTrans, trans);
    }

    @Test
    public void testString() {
        int[][] src = {
            {1, 2},
            {3, 4},
            {5, 6}};
        assertEquals("1 2%n3 4%n5 6%n".formatted(),
            MatrixConverter.of(src).string());
        assertEquals("1, 2%n3, 4%n5, 6%n".formatted(),
            MatrixConverter.of(src).string(", "));
    }

    @Test
    public void testSubMatrix() {
        int[][] src = {
            {1, 2, 3, 4},
            {5, 6, 7, 8},
            {9, 10, 11, 12},
            {13, 14, 15, 16},
        };
        int[][] dst = MatrixConverter.of(src).subMatrix(2, 1, 2, 2);
        int[][] expected = {
            {10, 11},
            {14, 15}
        };
        assertArrayEquals(expected, dst);
        try {
            MatrixConverter.of(src).subMatrix(2, 2, 3, 2);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("rows", e.getMessage());
        }
    }

    @Test
    public void testTranspose() {
        int[][] src = {
            {1, 2},
            {3, 4},
            {5, 6}};
        int[][] dst = MatrixConverter.of(src).transpose();
        int[][] expected = {
            {1, 3, 5},
            {2, 4, 6}};
        assertArrayEquals(expected, dst);
    }

    @Test
    public void testUpsideDown() {
        int[][] src = {
            {1, 2},
            {3, 4},
            {5, 6}};
        int[][] dst = MatrixConverter.of(src).upsideDown();
        int[][] expected = {
            {5, 6},
            {3, 4},
            {1, 2}};
        assertArrayEquals(expected, dst);
    }

    @Test
    public void testMirror() {
        int[][] src = {
            {1, 2},
            {3, 4},
            {5, 6}};
        int[][] dst = MatrixConverter.of(src).mirror();
        int[][] expected = {
            {2, 1},
            {4, 3},
            {6, 5}};
        assertArrayEquals(expected, dst);
    }

    @Test
    public void testMirrorStringArray() {
        String[][] src = {
            {"1", "2"},
            {"3", "4"},
            {"5", "6"}};
        String[][] dst = MatrixConverter.of(src).mirror();
        String[][] expected = {
            {"2", "1"},
            {"4", "3"},
            {"6", "5"}};
        assertArrayEquals(expected, dst);
    }

    @Test
    public void testRotate90() {
        int[][] src = {
            {1, 2},
            {3, 4},
            {5, 6}};
        int[][] dst = MatrixConverter.of(src).rotate90();
        int[][] expected = {
            {5, 3, 1},
            {6, 4, 2}};
        assertArrayEquals(expected, dst);
    }

    @Test
    public void testRotate180() {
        int[][] src = {
            {1, 2},
            {3, 4},
            {5, 6}};
        int[][] dst = MatrixConverter.of(src).rotate180();
        int[][] expected = {
            {6, 5},
            {4, 3},
            {2, 1}};
        assertArrayEquals(expected, dst);
    }

    @Test
    public void testRotate270() {
        int[][] src = {
            {1, 2},
            {3, 4},
            {5, 6}};
        int[][] dst = MatrixConverter.of(src).rotate270();
        int[][] expected = {
            {2, 4, 6},
            {1, 3, 5}};
        assertArrayEquals(expected, dst);
    }
}
