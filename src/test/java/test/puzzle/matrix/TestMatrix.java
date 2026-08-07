package test.puzzle.matrix;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.stream.IntStream;

import org.junit.Test;

public class TestMatrix {

    interface Matrix<T> {
        int size();
        T get(int... index);
        void set(T value, int... index);
        int[] dimensions();

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
        MatrixArray(int[] dimensions, T... values) {
            System.out.println(values.getClass());
            Class<T> componentType = (Class<T>)values.getClass().componentType();
            if (componentType == Object.class)
                throw new RuntimeException("Specify the element type");
            System.out.println(componentType);
            this.dimensions = dimensions;
            int size = IntStream.of(dimensions).reduce(1, (a, b) -> a * b);
            this.array = (T[])Array.newInstance(componentType, size);
            this.weight = Matrix.weight(dimensions);
        }

        // public static <T> Matrix<T> of(int... index) {
        //     return new Matrix<T>(index);
        // }

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

        public int[] decodeIndex(int i) {
            if (i < 0 || i >= array.length)
                throw new IndexOutOfBoundsException(
                    "'index' must be in range 0..<%d".formatted(array.length));
            int len = dimensions.length;
            int[] result = new int[len];
            for (int j = 0; j < len; ++j)
                result[j] = i / weight[j] % dimensions[j];
            return result;
        }
    }

    @Test
    public void testMatrix() {
        // var m = Matrix.<Double>of(2, 3, 4);
        var m = new MatrixArray<Double>(new int[]{2, 3, 4});
        double v = 0;
        for (int i = 0; i < 2; ++i)
            for (int j = 0; j < 3; ++j)
                for (int k = 0; k < 4; ++k)
                    m.set(v++, i, j, k);
        System.out.println("dimensions=" + Arrays.toString(m.dimensions));
        System.out.println("array=" + Arrays.toString(m.array));
        System.out.println("encode=" + Arrays.toString(m.weight));
        for (int i = 0; i < 2; ++i)
            for (int j = 0; j < 3; ++j)
                for (int k = 0; k < 4; ++k)
                    System.out.printf(" %s", m.get(i, j, k));
        System.out.printf("%n");
        for (int i = 0, size = m.size(); i < size; ++i)
            System.out.printf("%d : %s%n", i, Arrays.toString(m.decodeIndex(i)));
    }
}
