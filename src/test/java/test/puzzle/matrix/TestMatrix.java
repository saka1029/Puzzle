package test.puzzle.matrix;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.stream.IntStream;

import org.junit.Test;

import puzzle.matrix.Matrix;
import puzzle.matrix.MatrixArray;

public class TestMatrix {

    @Test
    public void testToString() {
        int[] dimensions = {2, 3, 4};
        Matrix<Integer> m = MatrixArray.of(Integer.class, dimensions);
        int v = 0;
        for (int i = 0; i < 2; ++i)
            for (int j = 0; j < 3; ++j)
                for (int k = 0; k < 4; ++k)
                    m.set(v++, i, j, k);
        assertEquals("", m.toString());
    }

    @Test
    public void testMatrix() {
        Matrix<Double> m = MatrixArray.of(Double.class, 2, 3, 4);
        int[] dimensions = m.dimensions();
        double v = 0;
        for (int i = 0; i < 2; ++i)
            for (int j = 0; j < 3; ++j)
                for (int k = 0; k < 4; ++k)
                    m.set(v++, i, j, k);
        System.out.println("dimensions=" + Arrays.toString(dimensions));
        for (int i = 0; i < 2; ++i)
            for (int j = 0; j < 3; ++j)
                for (int k = 0; k < 4; ++k)
                    System.out.printf(" %s", m.get(i, j, k));
        System.out.printf("%n");
        System.out.println("m=" + m);
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
    public void testMatrixSlice2d() {
        var m = MatrixArray.of(Integer.class, 2, 3);
        int v = 0;
        for (int i = 0; i < 2; ++i)
            for (int j = 0; j < 3; ++j)
                m.set(v++, i, j);
        System.out.println(m);
        var m0 = m.slice(-1, 0);
        System.out.println(m0);
        System.out.println(Arrays.toString(m0.dimensions()));
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

    static int arrayIndex(int[] dimensions, int... index) {
        int length = dimensions.length;
        int result = 0;
        for (int i = 0; i < length; ++i)
            result = result * dimensions[i] + index[i];
        return result;
    }

    static int[] matrixIndex(int[] dimensions, int index) {
        int length = dimensions.length;
        int[] result = new int[length];
        for (int i = length - 1; i >= 0; --i) {
            result[i] = index % dimensions[i];
            index /= dimensions[i];
        }
        return result;
    }

    @Test
    public void testIndex() {
        int[] dimensions = {2, 3, 3};
        for (int i = 0; i < dimensions[0]; ++i)
            for (int j = 0; j < dimensions[1]; ++j)
                for (int k = 0; k < dimensions[2]; ++k)
                    System.out.printf("(%d, %d, %d) = %d%n", i, j, k, arrayIndex(dimensions, i, j, k));
        int size = IntStream.of(dimensions).reduce(1, (a, b) -> a * b);
        for (int i = 0; i < size; ++i)
            System.out.printf("%d = %s%n", i, Arrays.toString(matrixIndex(dimensions, i)));

    }
}
