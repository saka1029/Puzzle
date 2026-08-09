package puzzle.matrix;

import java.lang.reflect.Array;
import java.util.stream.IntStream;

/**
 * 型Tの多次元配列
 * @param <T> 要素の型
 */
public class MatrixArray<T> extends Matrix<T> {
    public final T[] array;

    @SuppressWarnings("unchecked")
    MatrixArray(Class<T> componentType, int... dimensions) {
        super(dimensions);
        System.out.println(componentType);
        int size = IntStream.of(dimensions).reduce(1, (a, b) -> a * b);
        this.array = (T[])Array.newInstance(componentType, size);
    }

    public static <T> MatrixArray<T> of(Class<T> componentType, int... indexes) {
        return new MatrixArray<>(componentType, indexes);
    }

    public T get(int... indexes) {
            return array[arrayIndex(indexes)];
    }

    public void set(T value, int... indexes) {
        array[arrayIndex(indexes)] = value;
    }
}