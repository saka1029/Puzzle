package test.puzzle.matrix;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.stream.IntStream;

import org.junit.Test;

public class TestMatrix {

    public static class Matrix<T> {
        public final int[] dimensions;
        public final int[] encode;
        public final T[] array;

        @SuppressWarnings("unchecked")
        Matrix(int[] dimensions, T... values) {
            System.out.println(values.getClass());
            Class<T> componentType = (Class<T>)values.getClass().componentType();
            if (componentType == Object.class)
                throw new RuntimeException("T is Object class");
            System.out.println(componentType);
            this.dimensions = dimensions;
            int size = IntStream.of(dimensions).reduce(1, (a, b) -> a * b);
            this.array = (T[])Array.newInstance(componentType, size);
            int i = dimensions.length;
            this.encode = new int[i];
            encode[--i] = 1;
            int prev = encode[i];
            for ( ; i >= 0; --i) {
                encode[i] = prev;
                prev *= dimensions[i];
            }
        }

        public int size() {
            return array.length;
        }

        int index(int... index) {
            if (index.length != dimensions.length)
                throw new IndexOutOfBoundsException(
                    "'index' length must be %d but %d".formatted(dimensions.length, index.length));
            int result = 0;
            for (int i = 0, len = dimensions.length; i < len; ++i) {
                if (index[i] < 0 || index[i] > dimensions[i])
                    throw new IndexOutOfBoundsException(
                        "index must be in range 0..<%d but %d".formatted(dimensions[i], index[i]));
                result += index[i] * encode[i];
            }

            return result;
        }

        public T get(int... index) {
             return array[index(index)];
        }

        public void set(T value, int... index) {
            array[index(index)] = value;
        }

        public int[] decodeIndex(int i) {
            int len = dimensions.length;
            int[] result = new int[len];
            for (int j = 0; j < len; ++j)
                result[j] = i / encode[j] % dimensions[j];
            return result;
        }
    }

    @Test
    public void testMatrix() {
        Matrix<Double> m = new Matrix<>(new int[]{2, 3, 4});
        double v = 0;
        for (int i = 0; i < 2; ++i)
            for (int j = 0; j < 3; ++j)
                for (int k = 0; k < 4; ++k)
                    m.set(v++, i, j, k);
        System.out.println("dimensions=" + Arrays.toString(m.dimensions));
        System.out.println("array=" + Arrays.toString(m.array));
        System.out.println("encode=" + Arrays.toString(m.encode));
        for (int i = 0; i < 2; ++i)
            for (int j = 0; j < 3; ++j)
                for (int k = 0; k < 4; ++k)
                    System.out.printf(" %s", m.get(i, j, k));
        System.out.printf("%n");
        for (int i = 0, size = m.size(); i < size; ++i)
            System.out.printf("%d : %s%n", i, Arrays.toString(m.decodeIndex(i)));
    }
}
