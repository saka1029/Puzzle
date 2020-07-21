package test.puzzle;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class TestMat {

    static class Mat<T> {

        private final T[][] mat;
        public final int width;
        public final int height;

        public Mat(int height, int width) {
            this.height = height;
            this.width = width;
            mat = (T[][])new Object[height][width];
        }

        public T get(int row, int col) {
            return mat[row][col];
        }

        public void set(int row, int col, T value) {
            mat[row][col] = value;
        }

        static final String NL = String.format("%n");

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (T[] row : mat)
                sb.append(Arrays.toString(row)).append(NL);
            return sb.toString();
        }
    }

    @Test
    void test() {
    }

}
