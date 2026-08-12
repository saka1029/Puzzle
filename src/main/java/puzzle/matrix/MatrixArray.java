package puzzle.matrix;

import java.lang.reflect.Array;
import java.util.stream.IntStream;

/**
 * 型Tの多次元配列
 * @param <T> 要素の型
 */
public class MatrixArray<T> extends Matrix<T> {
    public final T[] array;
    public final Class<T> elementType;

    @SuppressWarnings("unchecked")
    MatrixArray(Class<T> elementType, int... dimensions) {
        super(dimensions);
        this.elementType = elementType;
        int size = IntStream.of(dimensions).reduce(1, (a, b) -> a * b);
        this.array = (T[])Array.newInstance(elementType, size);
    }

    public static <T> MatrixArray<T> of(Class<T> componentType, int... indexes) {
        return new MatrixArray<>(componentType, indexes);
    }

    @Override
    public Class<T> elementType() {
        return elementType;
    }

    public T get(int... indexes) {
            return array[arrayIndex(indexes)];
    }

    public void set(T value, int... indexes) {
        array[arrayIndex(indexes)] = value;
    }

    @Override
    public Matrix<T> clone() {
        var m = new MatrixArray<T>(elementType, dimensions);
        for (int i = 0, size = size(); i < size; ++i)
            m.put(at(i), i);
        return m;
    }
}