package puzzle.core;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public interface MatrixConverter<T> {
    int rows();
    int cols();
    void move(int sr, int sc, int dr, int dc);
    void create(int rows, int cols);
    T dst();
    String string(int r, int c);

    static final String NL = System.lineSeparator();

    public default String string(String separator) {
        int rows = rows(), cols = cols();
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < rows; ++r) {
            String sep = "";
            for (int c = 0; c < cols; ++c, sep = separator)
                sb.append(sep).append(string(r, c));
            sb.append(NL);
        }
        return sb.toString();
    }

    public default String string() {
        return string(" ");
    }

    public static MatrixConverter<int[][]> of(int[][] src) {
        int rows = src.length, cols = src[0].length;
        return new MatrixConverter<>() {

            private int[][] dst = null;

            @Override
            public int rows() {
                return rows;
            }

            @Override
            public int cols() {
                return cols;
            }

            @Override
            public void move(int sr, int sc, int dr, int dc) {
                dst[dr][dc] = src[sr][sc];
            }

            @Override
            public void create(int rows, int cols) {
                dst = new int[rows][cols];
            }

            @Override
            public int[][] dst() {
                return dst;
            }

            @Override
            public String string(int r, int c) {
                return "" + src[r][c];
            }
        };
    }

    public static MatrixConverter<long[][]> of(long[][] src) {
        int rows = src.length, cols = src[0].length;
        return new MatrixConverter<>() {

            private long[][] dst = null;

            @Override
            public int rows() {
                return rows;
            }

            @Override
            public int cols() {
                return cols;
            }

            @Override
            public void move(int sr, int sc, int dr, int dc) {
                dst[dr][dc] = src[sr][sc];
            }

            @Override
            public void create(int rows, int cols) {
                dst = new long[rows][cols];
            }

            @Override
            public long[][] dst() {
                return dst;
            }

            @Override
            public String string(int r, int c) {
                return "" + src[r][c];
            }
        };
    }

    public static MatrixConverter<double[][]> of(double[][] src) {
        int rows = src.length, cols = src[0].length;
        return new MatrixConverter<>() {

            private double[][] dst = null;

            @Override
            public int rows() {
                return rows;
            }

            @Override
            public int cols() {
                return cols;
            }

            @Override
            public void move(int sr, int sc, int dr, int dc) {
                dst[dr][dc] = src[sr][sc];
            }

            @Override
            public void create(int rows, int cols) {
                dst = new double[rows][cols];
            }

            @Override
            public double[][] dst() {
                return dst;
            }

            @Override
            public String string(int r, int c) {
                return "" + src[r][c];
            }
        };
    }

    public static MatrixConverter<float[][]> of(float[][] src) {
        int rows = src.length, cols = src[0].length;
        return new MatrixConverter<>() {

            private float[][] dst = null;

            @Override
            public int rows() {
                return rows;
            }

            @Override
            public int cols() {
                return cols;
            }

            @Override
            public void move(int sr, int sc, int dr, int dc) {
                dst[dr][dc] = src[sr][sc];
            }

            @Override
            public void create(int rows, int cols) {
                dst = new float[rows][cols];
            }

            @Override
            public float[][] dst() {
                return dst;
            }

            @Override
            public String string(int r, int c) {
                return "" + src[r][c];
            }
        };
    }

    public static MatrixConverter<byte[][]> of(byte[][] src) {
        int rows = src.length, cols = src[0].length;
        return new MatrixConverter<>() {

            private byte[][] dst = null;

            @Override
            public int rows() {
                return rows;
            }

            @Override
            public int cols() {
                return cols;
            }

            @Override
            public void move(int sr, int sc, int dr, int dc) {
                dst[dr][dc] = src[sr][sc];
            }

            @Override
            public void create(int rows, int cols) {
                dst = new byte[rows][cols];
            }

            @Override
            public byte[][] dst() {
                return dst;
            }

            @Override
            public String string(int r, int c) {
                return "" + src[r][c];
            }
        };
    }

    public static MatrixConverter<short[][]> of(short[][] src) {
        int rows = src.length, cols = src[0].length;
        return new MatrixConverter<>() {

            private short[][] dst = null;

            @Override
            public int rows() {
                return rows;
            }

            @Override
            public int cols() {
                return cols;
            }

            @Override
            public void move(int sr, int sc, int dr, int dc) {
                dst[dr][dc] = src[sr][sc];
            }

            @Override
            public void create(int rows, int cols) {
                dst = new short[rows][cols];
            }

            @Override
            public short[][] dst() {
                return dst;
            }

            @Override
            public String string(int r, int c) {
                return "" + src[r][c];
            }
        };
    }

    @SuppressWarnings("unchecked")
    static <E> E[] alloc(E[] src, int size) {
        return (E[]) Array.newInstance(src.getClass().componentType(), size);
    }

    public static <E> MatrixConverter<E[][]> of(E[][] src) {
        int rows = src.length, cols = src[0].length;
        return new MatrixConverter<>() {

            private E[][] dst = null;

            @Override
            public int rows() {
                return rows;
            }

            @Override
            public int cols() {
                return cols;
            }

            @Override
            public void move(int sr, int sc, int dr, int dc) {
                dst[dr][dc] = src[sr][sc];
            }

            @Override
            public void create(int rows, int cols) {
                dst = alloc(src, rows);
                for (int r = 0; r < rows; ++r)
                    dst[r] = alloc(src[r], cols);
            }

            @Override
            public E[][] dst() {
                return dst;
            }

            @Override
            public String string(int r, int c) {
                return "" + src[r][c];
            }
        };
    }

    static <E> List<E> alloc(List<E> src, int size) {
        List<E> list = new ArrayList<>();
        for (int i = 0; i < size; ++i)
            list.add(null);
        return list;
    }

    public static <E> MatrixConverter<List<List<E>>> of(List<List<E>> src) {
        int rows = src.size(), cols = src.get(0).size();
        return new MatrixConverter<List<List<E>>>() {

            private List<List<E>> dst = null;

            @Override
            public int rows() {
                return rows;
            }

            @Override
            public int cols() {
                return cols;
            }

            @Override
            public void move(int sr, int sc, int dr, int dc) {
                dst.get(dr).set(dc, src.get(sr).get(sc));
            }

            @Override
            public void create(int rows, int cols) {
                dst = new ArrayList<>(rows);
                for (int r = 0; r < rows; ++r) {
                    List<E> row = new ArrayList<>(cols);
                    for (int c = 0; c < cols; ++c)
                        row.add(null);
                    dst.add(row);
                }
            }

            @Override
            public List<List<E>> dst() {
                return dst;
            }

            @Override
            public String string(int r, int c) {
                return "" + src.get(r).get(c);
            }
        };
    }

    public default T copy() {
        int rows = rows(), cols = cols();
        create(rows, cols);
        for (int r = 0; r < rows; ++r)
            for (int c = 0; c < cols; ++c)
                move(r, c, r, c);
        return dst();
    }
    
    public default T subMatrix(int top, int left, int rows, int cols) {
        if (top < 0 || top >= rows())
            throw new IllegalArgumentException("top");
        if (left < 0 || left >= cols())
            throw new IllegalArgumentException("cols");
        if (rows < 0 || top + rows > rows())
            throw new IllegalArgumentException("rows");
        if (cols < 0 || left + cols > cols())
            throw new IllegalArgumentException("cols");
        create(rows, cols);
        for (int r = 0; r < rows; ++r)
            for (int c = 0; c < cols; ++c)
                move(r + top, c + left, r, c);
        return dst();
    }

    public default T transpose() {
        int rows = rows(), cols = cols();
        create(cols, rows);
        for (int r = 0; r < rows; ++r)
            for (int c = 0; c < cols; ++c)
                move(r, c, c, r);
        return dst();
    }

    public default T upsideDown() {
        int rows = rows(), cols = cols();
        create(rows, cols);
        for (int r = 0, dr = rows - 1; r < rows; ++r, --dr)
            for (int c = 0; c < cols; ++c)
                move(r, c, dr, c);
        return dst();
    }

    public default T mirror() {
        int rows = rows(), cols = cols();
        create(rows, cols);
        for (int r = 0; r < rows; ++r)
            for (int c = 0, dc = cols - 1; c < cols; ++c, --dc)
                move(r, c, r, dc);
        return dst();
    }

    public default T rotate90() {
        int rows = rows(), cols = cols();
        create(cols, rows);
        for (int r = 0, dc = rows - 1; r < rows; ++r, --dc)
            for (int c = 0, dr = 0; c < cols; ++c, ++dr)
                move(r, c, dr, dc);
        return dst();
    }

    public default T rotate180() {
        int rows = rows(), cols = cols();
        create(rows, cols);
        for (int r = 0, dr = rows - 1; r < rows; ++r, --dr)
            for (int c = 0, dc = cols - 1; c < cols; ++c, --dc)
                move(r, c, dr, dc);
        return dst();
    }

    public default T rotate270() {
        int rows = rows(), cols = cols();
        create(cols, rows);
        for (int r = 0, dc = 0; r < rows; ++r, ++dc)
            for (int c = 0, dr = cols - 1; c < cols; ++c, --dr)
                move(r, c, dr, dc);
        return dst();
    }
}