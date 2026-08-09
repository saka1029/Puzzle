package test.puzzle.matrix;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import java.util.stream.IntStream;

import org.junit.Test;

import puzzle.matrix.Matrix;
import puzzle.matrix.MatrixArray;

public class TestMatrix {

    @Test
    public void testSize() {
        int[] dimensions = {2, 3, 4};
        Matrix<Integer> m = MatrixArray.of(Integer.class, dimensions);
        assertEquals(2 * 3 * 4, m.size());
    }

    @Test
    public void testDimensions() {
        int[] dimensions = {2, 3, 4};
        Matrix<Integer> m = MatrixArray.of(Integer.class, dimensions);
        assertNotEquals(dimensions, m.dimensions());
        assertArrayEquals(dimensions, m.dimensions());
    }

    @Test
    public void testToString() {
        int[] dimensions = {2, 3, 4};
        Matrix<Integer> m = MatrixArray.of(Integer.class, dimensions);
        int v = 0;
        for (int i = 0; i < 2; ++i)
            for (int j = 0; j < 3; ++j)
                for (int k = 0; k < 4; ++k)
                    m.set(v++, i, j, k);
        assertEquals("[[[0, 1, 2, 3], [4, 5, 6, 7], [8, 9, 10, 11]], [[12, 13, 14, 15], [16, 17, 18, 19], [20, 21, 22, 23]]]", m.toString());
    }

    @Test
    public void testSetGet() {
        int[] dimensions = {2, 3, 4};
        Matrix<Integer> m = MatrixArray.of(Integer.class, dimensions);
        int v = 0;
        for (int i = 0; i < 2; ++i)
            for (int j = 0; j < 3; ++j)
                for (int k = 0; k < 4; ++k)
                    m.set(v++, i, j, k);
        v = 0;
        for (int i = 0; i < 2; ++i)
            for (int j = 0; j < 3; ++j)
                for (int k = 0; k < 4; ++k)
                    assertEquals(v++, (int)m.get(i, j, k));
    }

    @Test
    public void testStream() {
        Matrix<Integer> m = MatrixArray.of(Integer.class, 2, 3, 4);
        int v = 0;
        for (int i = 0; i < 2; ++i)
            for (int j = 0; j < 3; ++j)
                for (int k = 0; k < 4; ++k)
                    m.set(v++, i, j, k);
        int[] array = IntStream.range(0, m.size()).toArray();
        assertArrayEquals(array, m.stream().mapToInt(Integer::intValue).toArray());
    }

    @Test
    public void testSlice() {
        int[] dimensions = {2, 3};
        var m = MatrixArray.of(Integer.class, dimensions);
        int v = 0;
        for (int i = 0; i < 2; ++i)
            for (int j = 0; j < 3; ++j)
                m.set(v++, i, j);
        var m0 = m.slice(-1, 0);
        assertEquals(Integer.class, m0.elementType());
        assertEquals(2, m0.size());
        assertArrayEquals(new int[]{2}, m0.dimensions());
        assertEquals(0, (int)m0.get(0));
        assertEquals(3, (int)m0.get(1));
        assertArrayEquals(new int[]{0, 3}, m0.stream().mapToInt(Integer::intValue).toArray());
        var m1 = m.slice(-1, 1);
        assertEquals(2, m1.size());
        assertArrayEquals(new int[]{2}, m1.dimensions());
        assertEquals(1, (int)m1.get(0));
        assertEquals(4, (int)m1.get(1));
        assertArrayEquals(new int[]{1, 4}, m1.stream().mapToInt(Integer::intValue).toArray());
        try {
            m.slice(-1);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("slice", e.getMessage());
        }
    }

    @Test
    public void testMatrixSlice3d() {
        var m = MatrixArray.of(Integer.class, 2, 3, 4);
        int v = 0;
        for (int i = 0; i < 2; ++i)
            for (int j = 0; j < 3; ++j)
                for (int k = 0; k < 4; ++k)
                    m.set(v++, i, j, k);
        var s00 = m.slice(0, 0, -1);
        assertEquals(4, s00.size());
        assertArrayEquals(new int[]{0, 1, 2, 3}, s00.stream().mapToInt(Integer::intValue).toArray());
        var s01 = m.slice(0, 1, -1);
        assertEquals(4, s01.size());
        assertArrayEquals(new int[]{4, 5, 6, 7}, s01.stream().mapToInt(Integer::intValue).toArray());
        var s10 = m.slice(-1, 0, 0);
        assertEquals(2, s10.size());
        assertArrayEquals(new int[]{0, 12}, s10.stream().mapToInt(Integer::intValue).toArray());
        var s0 = m.slice(0, -1, -1);
        assertEquals(12, s0.size());
        assertArrayEquals(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11}, s0.stream().mapToInt(Integer::intValue).toArray());
        var s1 = m.slice(1, -1, -1);
        assertEquals(12, s1.size());
        assertArrayEquals(new int[]{12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23}, s1.stream().mapToInt(Integer::intValue).toArray());
        var s2 = m.slice(-1, 0, -1);
        assertEquals(8, s2.size());
        assertArrayEquals(new int[]{0, 1, 2, 3, 12, 13, 14, 15}, s2.stream().mapToInt(Integer::intValue).toArray());
    }

    @Test
    public void testArrayIndex() {
        int[] dimensions = {2, 3, 3};
        var m = MatrixArray.of(Integer.class, dimensions);
        int v = 0;
        for (int i = 0; i < dimensions[0]; ++i)
            for (int j = 0; j < dimensions[1]; ++j)
                for (int k = 0; k < dimensions[2]; ++k)
                    assertEquals(v++, m.arrayIndex(i, j, k));
    }

    @Test
    public void testMatixIndex() {
        int[] dimensions = {2, 3, 3};
        var m = MatrixArray.of(Integer.class, dimensions);
        int v = 0;
        for (int i = 0; i < dimensions[0]; ++i)
            for (int j = 0; j < dimensions[1]; ++j)
                for (int k = 0; k < dimensions[2]; ++k)
                    assertArrayEquals(new int[] {i, j, k}, m.matrixIndex(v++));
    }

    @Test
    public void testCloneMatrixArray() {
        int[] dimensions = {2, 3};
        var m = MatrixArray.of(Integer.class, dimensions);
        for (int i = 0; i < dimensions[0]; ++i)
            for (int j = 0; j < dimensions[1]; ++j)
                m.set(i * 10 + j, i, j);
        var c = m.clone();
        assertEquals(c.size(), m.size());
        assertArrayEquals(c.dimensions(), m.dimensions());
        for (int i = 0; i < dimensions[0]; ++i)
            for (int j = 0; j < dimensions[1]; ++j)
                assertEquals(m.get(i, j), c.get(i, j));
        c.put(99, 5);
        assertEquals(99, (int)c.at(5));
        assertEquals(12, (int)m.at(5));
    }

    @Test
    public void testCloneMatrixSlice() {
        int[] dimensions = {2, 3};
        var m = MatrixArray.of(Integer.class, dimensions);
        var s = m.slice(-1, -1);
        for (int i = 0; i < dimensions[0]; ++i)
            for (int j = 0; j < dimensions[1]; ++j)
                s.set(i * 10 + j, i, j);
        var c = s.clone();
        assertEquals(MatrixArray.class, c.getClass());
        assertEquals(c.size(), m.size());
        assertArrayEquals(c.dimensions(), m.dimensions());
        for (int i = 0; i < dimensions[0]; ++i)
            for (int j = 0; j < dimensions[1]; ++j)
                assertEquals(s.get(i, j), c.get(i, j));
        c.put(99, 5);
        assertEquals(99, (int)c.at(5));
        assertEquals(12, (int)s.at(5));
    }
}
