package puzzle.matrix;

import java.util.Iterator;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class Matrix<T> implements Iterable<T>, Cloneable {
    final int[] dimensions;

    Matrix(int... dimensions) {
        this.dimensions = dimensions.clone();
    }

    public int arrayIndex(int... indexes) {
        int length = dimensions.length;
        int result = 0;
        for (int i = 0; i < length; ++i)
            result = result * dimensions[i] + indexes[i];
        return result;
    }

    public int[] matrixIndex(int index) {
        int length = dimensions.length;
        int[] result = new int[length];
        for (int i = length - 1; i >= 0; --i) {
            result[i] = index % dimensions[i];
            index /= dimensions[i];
        }
        return result;
    }

    public abstract Class<T> elementType();

    /**
     * 次元を返します。
     */
    public int[] dimensions() {
        return dimensions.clone();
    }

    /**
     * すべての要素数を返します。
     */
    public int size() {
        return IntStream.of(dimensions).reduce(1, (a, b) -> a * b);
    }

    /**
     * 指定した座標の値を返します。
     * @param indexes 位置
     */
    public abstract T get(int... indexes);

    /**
     * 指定した位置の値を変更します。
     * @param value 変更する値
     * @param indexes 位置
     */
    public abstract void set(T value, int... indexes);

    /**
     * スライスを返します。
     * スライスは元のMatarixの退化した次元のビューです。
     * @param slice スライスの位置を指定します。長さはdimensions()の長さと一致する必要があります。
     *              0以上の値を指定すると、その位置のビューを指定したことになります。
     *              0より小さい値を指定すると、その位置が指定されないビューとなります。
     *              例えば2行3列のMatrixに対して、
     *              slice(-1, 1)を指定すると1列目のビューが取得できます。
     *              slice(0, -1)を指定すると0行目のビューが取得できます。
     */
    public Matrix<T> slice(int... slice) {
        return MatrixSlice.of(this, slice);
    }

    /**
     * すべての要素を一次元配列として取得します。
     * @param index 位置を指定します。
     *              位置は0以上size()未満である必要があります。
     */
    public T at(int index) {
        return get(matrixIndex(index));
    }

    /**
     * すべての要素を一次元配列として見たときに、指定位置の値を変更します。
     * @param value 変更後の値を指定します。
     * @param index 一次元配列として見たときの、変更する位置を指定します。
     */
    public void put(T value, int index) {
        set(value, matrixIndex(index));
    }

    /**
     * すべての要素を返すイテレータです。
     */
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
                return at(i++);
            }
        };
    }

    /**
     * すべての要素を返すストリームです。
     */
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