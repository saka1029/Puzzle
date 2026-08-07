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
        final int[] weight;

        Matrix(int... dimensions) {
            this.dimensions = dimensions.clone();
            int length = dimensions.length;
            this.weight = new int[length];
            for (int i = length - 1, prev = 1; i >= 0; --i) {
                this.weight[i] = prev;
                prev *= dimensions[i];
            }
        }

        public int[] dimensions() {
            return dimensions.clone();
        }

        public int[] weight() {
            return weight.clone();
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
            return get(decodeIndex(index));
        }

        public void put(T value, int index) {
            set(value, decodeIndex(index));
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

        public int[] decodeIndex(int index) {
            int size = size();
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

        int index(int... index) {
            int length = dimensions.length;
            if (index.length != length)
                throw new IndexOutOfBoundsException(
                    "'index' length must be %d but %d".formatted(dimensions.length, index.length));
            int result = 0;
            for (int i = 0; i < length; ++i) {
                if (index[i] < 0 || index[i] > dimensions[i])
                    throw new IndexOutOfBoundsException(
                        "index must be in range 0..<%d but %d".formatted(dimensions[i], index[i]));
                result += index[i] * weight[i];
            }
            return result;
        }


        public T get(int... index) {
             return array[index(index)];
        }

        public void set(T value, int... index) {
            array[index(index)] = value;
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
    public void testMatrix() {
        Matrix<Double> m = MatrixArray.of(Double.class, 2, 3, 4);
        int[] dimensions = m.dimensions();
        int[] weight = m.weight();
        int size = m.size();
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
        System.out.println("m=" + m);
        for (int i = 0; i < size; ++i)
            System.out.printf("%d : %s%n", i, Arrays.toString(
                m.decodeIndex(i)));
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
}
