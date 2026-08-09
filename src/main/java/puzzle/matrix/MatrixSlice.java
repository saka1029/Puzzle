package puzzle.matrix;

import java.util.stream.IntStream;

public class MatrixSlice<T> extends Matrix<T> {
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

    static <T> MatrixSlice<T> of(Matrix<T> origin, int... slice) {
        return new MatrixSlice<>(origin, slice);
    }

    @Override
    public Class<T> elementType() {
        return origin.elementType();
    }

    int[] orgIndex(int... indexes) {
        int length = orgDimensions.length;
        int[] result = new int[length];
        for (int i = 0, j = 0; i < length; ++i)
            result[i] = slice[i] >= 0 ? slice[i] : indexes[j++];
        return result;
    }

    @Override
    public T get(int... indexes) {
        return origin.get(orgIndex(indexes));
    }

    @Override
    public void set(T value, int... indexes) {
        origin.set(value, orgIndex(indexes));
    }

    @Override
    public Matrix<T> clone() {
        var m = new MatrixArray<T>(origin.elementType(), dimensions);
        for (int i = 0, size = size(); i < size; ++i)
            m.put(at(i), i);
        return m;
    }
}