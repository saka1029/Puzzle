package test.puzzle.matrix;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.stream.IntStream;

import org.junit.Test;

public class TestMatrix {

    public static class Matrix<T> {
        public final int[] dimensions;
        public final int size;
        public final T[] array;

        @SuppressWarnings("unchecked")
        Matrix(int[] dimensions, T... values) {
            System.out.println(values.getClass());
            Class<T> componentType = (Class<T>)values.getClass().componentType();
            if (componentType == Object.class)
                throw new RuntimeException("T is Object class");
            System.out.println(componentType);
            this.dimensions = dimensions;
            this.size = IntStream.of(dimensions).reduce(1, (a, b) -> a * b);
            this.array = (T[])Array.newInstance(componentType, this.size);
        }
    }

    @Test
    public void testMatrix() {
        Matrix<Double> m = new Matrix<>(new int[]{2, 3});
        System.out.println("dimensions=" + Arrays.toString(m.dimensions));
        System.out.println("array=" + Arrays.toString(m.array));
    }
}
