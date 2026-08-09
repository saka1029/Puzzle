package puzzle.matrix;

import java.util.Iterator;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class Matrix<T> implements Iterable<T> {
    final int[] dimensions;

    Matrix(int... dimensions) {
        this.dimensions = dimensions.clone();
    }

    int arrayIndex(int... indexes) {
        int length = dimensions.length;
        int result = 0;
        for (int i = 0; i < length; ++i)
            result = result * dimensions[i] + indexes[i];
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

    public abstract T get(int... indexes);
    public abstract void set(T value, int... indexes);

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