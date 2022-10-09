package test.puzzle.core;

import static org.junit.Assert.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class TestMatrixConverter {

    interface MatrixConverter<T> {
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

        @SuppressWarnings("unchecked")
        static <E> E[] alloc(E[] src, int size) {
            return (E[])Array.newInstance(src.getClass().componentType(), size);
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
        
        @SuppressWarnings("unchecked")
        static <E> List<E> alloc(List<E> src, int size) {
            return (List<E>)Arrays.asList(new Object[size]);
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
                        List<E> row = src.get(r);
                        dst.add(alloc(row, cols));
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

    @Test
    public void testInt() {
        int[][] src = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}};
        int[][] dst = MatrixConverter.of(src).copy();
        assertArrayEquals(src, dst);
    }

    @Test
    public void testStringArray() {
        String[][] src = {
            {"1", "2"},
            {"3", "4"},
            {"5", "6"}};
        String[][] dst = MatrixConverter.of(src).copy();
        assertArrayEquals(src, dst);
    }

    @Test
    public void testStringList() {
        List<List<String>> src = List.of(
            List.of("1", "2"),
            List.of("3", "4"),
            List.of("5", "6"));
        MatrixConverter<List<List<String>>> m = MatrixConverter.of(src);
        List<List<String>> copy = m.copy();
        assertEquals(src, copy);
        List<List<String>> trans = m.transpose();
        List<List<String>> expectedTrans = List.of(
            List.of("1", "3", "5"),
            List.of("2", "4", "6"));
        assertEquals(expectedTrans, trans);
    }

    @Test
    public void testString() {
        String nl = System.lineSeparator();
        int[][] src = {
            {1, 2},
            {3, 4},
            {5, 6}};
        assertEquals("1 2" + nl + "3 4" + nl + "5 6" + nl,
            MatrixConverter.of(src).string());
        assertEquals("1, 2" + nl + "3, 4" + nl + "5, 6" + nl,
            MatrixConverter.of(src).string(", "));
    }

    @Test
    public void testTranspose() {
        int[][] src = {
            {1, 2},
            {3, 4},
            {5, 6}};
        int[][] dst = MatrixConverter.of(src).transpose();
        int[][] expected = {
            {1, 3, 5},
            {2, 4, 6}};
        assertArrayEquals(expected, dst);
    }

    @Test
    public void testUpsideDown() {
        int[][] src = {
            {1, 2},
            {3, 4},
            {5, 6}};
        int[][] dst = MatrixConverter.of(src).upsideDown();
        int[][] expected = {
            {5, 6},
            {3, 4},
            {1, 2}};
        assertArrayEquals(expected, dst);
    }

    @Test
    public void testMirror() {
        int[][] src = {
            {1, 2},
            {3, 4},
            {5, 6}};
        int[][] dst = MatrixConverter.of(src).mirror();
        int[][] expected = {
            {2, 1},
            {4, 3},
            {6, 5}};
        assertArrayEquals(expected, dst);
    }

    @Test
    public void testMirrorString() {
        String[][] src = {
            {"1", "2"},
            {"3", "4"},
            {"5", "6"}};
        String[][] dst = MatrixConverter.of(src).mirror();
        String[][] expected = {
            {"2", "1"},
            {"4", "3"},
            {"6", "5"}};
        assertArrayEquals(expected, dst);
    }

    @Test
    public void testRotate90() {
        int[][] src = {
            {1, 2},
            {3, 4},
            {5, 6}};
        int[][] dst = MatrixConverter.of(src).rotate90();
        int[][] expected = {
            {5, 3, 1},
            {6, 4, 2}};
        assertArrayEquals(expected, dst);
    }

    @Test
    public void test180() {
        int[][] src = {
            {1, 2},
            {3, 4},
            {5, 6}};
        int[][] dst = MatrixConverter.of(src).rotate180();
        int[][] expected = {
            {6, 5},
            {4, 3},
            {2, 1}};
        assertArrayEquals(expected, dst);
    }

    @Test
    public void testRotate270() {
        int[][] src = {
            {1, 2},
            {3, 4},
            {5, 6}};
        int[][] dst = MatrixConverter.of(src).rotate270();
        int[][] expected = {
            {2, 4, 6},
            {1, 3, 5}};
        assertArrayEquals(expected, dst);
        System.out.println(MatrixConverter.of(dst).string());
    }
}
