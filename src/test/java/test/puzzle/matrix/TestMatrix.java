package test.puzzle.matrix;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.stream.IntStream;

import org.junit.Test;

public class TestMatrix {

    interface Matrix<T> {
        int size();
        int[] dimensions();
        T get(int... index);
        void set(T value, int... index);

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
    }

    /**
     * 型Tの多次元配列
     * @param <T> 要素の型
     */
    public static class MatrixArray<T> implements Matrix<T> {
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

    }

    @Test
    public void testMatrix() {
        Matrix<Double> m = MatrixArray.of(Double.class, 2, 3, 4);
        int size = m.size();
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
        for (int i = 0; i < size; ++i)
            System.out.printf("%d : %s%n", i, Arrays.toString(
                Matrix.decodeIndex(m.size(), dimensions, weight, i)));
    }
}
