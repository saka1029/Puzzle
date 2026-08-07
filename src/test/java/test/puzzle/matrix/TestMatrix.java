package test.puzzle.matrix;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.stream.IntStream;

import org.junit.Test;

public class TestMatrix {

    public static abstract class Matrix<T> {
        public abstract int size();
        public abstract int[] dimensions();
        public abstract T get(int... index);
        public abstract void set(T value, int... index);
        public abstract Matrix<T> slice(int... index);

        public static int[] weight(int... dimensions) {
            int len = dimensions.length;
            int[] weight = new int[len];
            for (int i = len - 1, prev = 1; i >= 0; --i) {
                weight[i] = prev;
                prev *= dimensions[i];
            }
            return weight;
        }

        public static int index(int[] dimensions, int[] weight, int... index) {
            if (index.length != dimensions.length)
                throw new IndexOutOfBoundsException(
                    "'index' length must be %d but %d".formatted(dimensions.length, index.length));
            int result = 0;
            for (int i = 0, len = dimensions.length; i < len; ++i) {
                if (index[i] < 0 || index[i] > dimensions[i])
                    throw new IndexOutOfBoundsException(
                        "index must be in range 0..<%d but %d".formatted(dimensions[i], index[i]));
                result += index[i] * weight[i];
            }
            return result;
        }

        public static int[] decodeIndex(int size, int[] dimensions, int[] weight, int index) {
            if (index < 0 || index >= size)
                throw new IndexOutOfBoundsException(
                    "'index' must be in range 0..<%d".formatted(size));
            int len = dimensions.length;
            int[] result = new int[len];
            for (int i = 0; i < len; ++i)
                result[i] = index / weight[i] % dimensions[i];
            return result;
        }

        @Override
        public String toString() {
            int[] dimensions = dimensions();
            int max = dimensions.length;
            int[] index = new int[max];
            StringBuilder sb = new StringBuilder("[");
            new Object() {
                void str(boolean notFirst, int i) {
                    if (i >= max) {
                        if (notFirst)
                            sb.append(", ");
                        sb.append(get(index));
                    } else
                        for (int j = 0; j < dimensions[i]; ++j) {
                            if (i < max - 1) {
                                if (j > 0)
                                    sb.append(", ");
                                sb.append("[");
                            }
                            index[i] = j;
                            str(j > 0, i + 1);
                            if (i < max - 1)
                                sb.append("]");
                        }
                }
            }.str(false, 0);
            return sb.append("]").toString();
        }
    }

    /**
     * 型Tの多次元配列
     * @param <T> 要素の型
     */
    public static class MatrixArray<T> extends Matrix<T> {
        public final int[] dimensions;
        public final int[] weight;
        public final T[] array;

        @SuppressWarnings("unchecked")
        MatrixArray(Class<T> componentType, int... dimensions) {
            System.out.println(componentType);
            this.dimensions = dimensions;
            int size = IntStream.of(dimensions).reduce(1, (a, b) -> a * b);
            this.array = (T[])Array.newInstance(componentType, size);
            this.weight = Matrix.weight(dimensions);
        }

        public static <T> MatrixArray<T> of(Class<T> componentType, int... index) {
            return new MatrixArray<>(componentType, index);
        }

        @Override
        public int size() {
            return array.length;
        }

        @Override
        public int[] dimensions() {
            return Arrays.copyOf(dimensions, dimensions.length);
        }

        public T get(int... index) {
             return array[Matrix.index(dimensions, weight, index)];
        }

        public void set(T value, int... index) {
            array[Matrix.index(dimensions, weight, index)] = value;
        }

        @Override
        public Matrix<T> slice(int... slice) {
            return MatrixSlice.of(this, slice);
        }

    }

    public static class MatrixSlice<T> extends Matrix<T> {
        final Matrix<T> origin;
        final int[] orgDimension;
        final int[] dimension;
        final int[] slice;
        final int size;

        MatrixSlice(Matrix<T> origin, int... slice) {
            int length = slice.length;
            this.orgDimension = origin.dimensions();
            if (length != orgDimension.length)
                throw new IllegalArgumentException("slice");
            int negativeCount = (int)IntStream.of(slice).filter(i -> i < 0).count();
            this.dimension = new int[negativeCount];
            for (int i = 0, j = 0; i < length; ++i)
                if (slice[i] < 0)
                    this.dimension[j++] = this.orgDimension[i];
            this.origin = origin;
            this.slice = slice;
            this.size = IntStream.of(this.dimension).reduce(1, (a, b) -> a * b);
        }

        public static <T> MatrixSlice<T> of(Matrix<T> origin, int... slice) {
            return new MatrixSlice<>(origin, slice);
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public int[] dimensions() {
            return Arrays.copyOf(dimension, dimension.length);
        }

        int[] index(int... index) {
            int length = orgDimension.length;
            int[] result = new int[length];
            for (int i = 0, j = 0; i < length; ++i)
                result[i] = slice[i] >= 0 ? slice[i] : index[j++];
            return result;
        }

        @Override
        public T get(int... index) {
            return origin.get(index(index));
        }

        @Override
        public void set(T value, int... index) {
            origin.set(value, index(index));
        }

        @Override
        public Matrix<T> slice(int... slice) {
            return MatrixSlice.of(this, slice);
        }
    }

    @Test
    public void testMatrix() {
        Matrix<Double> m = MatrixArray.of(Double.class, 2, 3, 4);
        int[] dimensions = m.dimensions();
        int[] weight = Matrix.weight(dimensions);
        double v = 0;
        for (int i = 0; i < 2; ++i)
            for (int j = 0; j < 3; ++j)
                for (int k = 0; k < 4; ++k)
                    m.set(v++, i, j, k);
        System.out.println("dimensions=" + Arrays.toString(dimensions));
        System.out.println("encode=" + Arrays.toString(weight));
        for (int i = 0; i < 2; ++i)
            for (int j = 0; j < 3; ++j)
                for (int k = 0; k < 4; ++k)
                    System.out.printf(" %s", m.get(i, j, k));
        System.out.printf("%n");
        System.out.println(m);
        // for (int i = 0; i < size; ++i)
        //     System.out.printf("%d : %s%n", i, Arrays.toString(
        //         Matrix.decodeIndex(m.size(), dimensions, weight, i)));
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
        assertEquals(0, (int)s00.get(0));
        assertEquals(1, (int)s00.get(1));
        assertEquals(2, (int)s00.get(2));
        assertEquals(3, (int)s00.get(3));
        var s01 = m.slice(0, 1, -1);
        assertEquals(4, s01.size());
        assertEquals(4, (int)s01.get(0));
        assertEquals(5, (int)s01.get(1));
        assertEquals(6, (int)s01.get(2));
        assertEquals(7, (int)s01.get(3));
        var s10 = m.slice(-1, 0, 0);
        assertEquals(2, s10.size());
        assertEquals(0, (int)s10.get(0));
        assertEquals(12, (int)s10.get(1));
        var s0 = m.slice(0, -1, -1);
        assertEquals(12, s0.size());
        assertEquals(0, (int)s0.get(0, 0));
        assertEquals(1, (int)s0.get(0, 1));
        assertEquals(2, (int)s0.get(0, 2));
        assertEquals(3, (int)s0.get(0, 3));
        assertEquals(4, (int)s0.get(1, 0));
        assertEquals(5, (int)s0.get(1, 1));
        assertEquals(6, (int)s0.get(1, 2));
        assertEquals(7, (int)s0.get(1, 3));
        assertEquals(8, (int)s0.get(2, 0));
        assertEquals(9, (int)s0.get(2, 1));
        assertEquals(10, (int)s0.get(2, 2));
        assertEquals(11, (int)s0.get(2, 3));
        var s1 = m.slice(1, -1, -1);
        assertEquals(12, s1.size());
        assertEquals(12, (int)s1.get(0, 0));
        assertEquals(13, (int)s1.get(0, 1));
        assertEquals(14, (int)s1.get(0, 2));
        assertEquals(15, (int)s1.get(0, 3));
        assertEquals(16, (int)s1.get(1, 0));
        assertEquals(17, (int)s1.get(1, 1));
        assertEquals(18, (int)s1.get(1, 2));
        assertEquals(19, (int)s1.get(1, 3));
        assertEquals(20, (int)s1.get(2, 0));
        assertEquals(21, (int)s1.get(2, 1));
        assertEquals(22, (int)s1.get(2, 2));
        assertEquals(23, (int)s1.get(2, 3));
        var s2 = m.slice(-1, 0, -1);
        assertEquals(8, s2.size());
        assertEquals(0, (int)s2.get(0, 0));
        assertEquals(1, (int)s2.get(0, 1));
        assertEquals(2, (int)s2.get(0, 2));
        assertEquals(3, (int)s2.get(0, 3));
        assertEquals(12, (int)s2.get(1, 0));
        assertEquals(13, (int)s2.get(1, 1));
        assertEquals(14, (int)s2.get(1, 2));
        assertEquals(15, (int)s2.get(1, 3));
    }
}
