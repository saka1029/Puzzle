package test.puzzle.matrix;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.junit.Test;

public class TestMatrix {

    public static abstract class Matrix<T> implements Iterable<T> {
        final int[] dimensions;

        Matrix(int... dimensions) {
            this.dimensions = dimensions.clone();
        }

        int arrayIndex(int... index) {
            int length = dimensions.length;
            int result = 0;
            for (int i = 0; i < length; ++i)
                result = result * dimensions[i] + index[i];
            return result;
        }

        int[] matrixIndex(int index) {
            int length = dimensions.length;
            int[] result = new int[length];
            for (int i = length - 1; i >= 0; --i) {
                result[i] = index % dimensions[i];
                index /= dimensions[i];
            }
            return result;
        }


        public int[] dimensions() {
            return dimensions.clone();
        }

        public int size() {
            return IntStream.of(dimensions).reduce(1, (a, b) -> a * b);
        }

        public abstract T get(int... index);
        public abstract void set(T value, int... index);

        public Matrix<T> slice(int... slice) {
            return MatrixSlice.of(this, slice);
        }

        public T at(int index) {
            return get(matrixIndex(index));
        }

        public void put(T value, int index) {
            set(value, matrixIndex(index));
        }

        @Override
        public Iterator<T> iterator() {
            return new Iterator<>() {
                int size = size();
                int i = 0;

                @Override
                public boolean hasNext() {
                    return i < size;
                }

                @Override
                public T next() {
                    T result = at(i++);
                    return result;
                }
            };
        }

        public Stream<T> stream() {
            return StreamSupport.stream(spliterator(), false);
        }

        @Override
        public String toString() {
            int last = dimensions.length - 1;
            int[] indexes = new int[last + 1];
            StringBuilder sb = new StringBuilder("[");
            new Object() {
                void string(int i) {
                    for (int j = 0, max = dimensions[i]; j < max; ++j) {
                        indexes[i] = j;
                        if (j > 0)
                            sb.append(", ");
                        if (i >= last)
                            sb.append(get(indexes));
                        else {
                            sb.append("[");
                            string(i + 1);
                            sb.append("]");
                        }
                    }
                }
            }.string(0);
            return sb.append("]").toString();
        }
    }

    /**
     * 型Tの多次元配列
     * @param <T> 要素の型
     */
    public static class MatrixArray<T> extends Matrix<T> {
        public final T[] array;

        @SuppressWarnings("unchecked")
        MatrixArray(Class<T> componentType, int... dimensions) {
            super(dimensions);
            System.out.println(componentType);
            int size = IntStream.of(dimensions).reduce(1, (a, b) -> a * b);
            this.array = (T[])Array.newInstance(componentType, size);
        }

        public static <T> MatrixArray<T> of(Class<T> componentType, int... index) {
            return new MatrixArray<>(componentType, index);
        }

        public T get(int... index) {
             return array[arrayIndex(index)];
        }

        public void set(T value, int... index) {
            array[arrayIndex(index)] = value;
        }
    }

    public static class MatrixSlice<T> extends Matrix<T> {
        final Matrix<T> origin;
        final int[] orgDimensions;
        final int[] slice;

        static int[] dimensions(int[] orgDimensions, int[] slice) {
            int length = slice.length;
            int negativeCount = (int)IntStream.of(slice).filter(i -> i < 0).count();
            int[] dimensions = new int[negativeCount];
            for (int i = 0, j = 0; i < length; ++i)
                if (slice[i] < 0)
                    dimensions[j++] = orgDimensions[i];
            return dimensions;
        }

        MatrixSlice(Matrix<T> origin, int... slice) {
            super(dimensions(origin.dimensions(), slice));
            int length = slice.length;
            this.orgDimensions = origin.dimensions();
            if (length != orgDimensions.length)
                throw new IllegalArgumentException("slice");
            this.origin = origin;
            this.slice = slice;
        }

        public static <T> MatrixSlice<T> of(Matrix<T> origin, int... slice) {
            return new MatrixSlice<>(origin, slice);
        }

        int[] orgIndex(int... index) {
            int length = orgDimensions.length;
            int[] result = new int[length];
            for (int i = 0, j = 0; i < length; ++i)
                result[i] = slice[i] >= 0 ? slice[i] : index[j++];
            return result;
        }

        @Override
        public T get(int... index) {
            return origin.get(orgIndex(index));
        }

        @Override
        public void set(T value, int... index) {
            origin.set(value, orgIndex(index));
        }
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
        System.out.println(m);
    }

    @Test
    public void testMatrix() {
        Matrix<Double> m = MatrixArray.of(Double.class, 2, 3, 4);
        int[] dimensions = m.dimensions();
        int size = m.size();
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
        for (int i = 0; i < size; ++i)
            System.out.printf("%d : %s%n", i, Arrays.toString(
                m.matrixIndex(i)));
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
