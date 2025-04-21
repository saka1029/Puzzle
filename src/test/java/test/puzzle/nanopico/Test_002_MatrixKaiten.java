package test.puzzle.nanopico;

import static org.junit.Assert.assertArrayEquals;
import java.util.function.Function;

import org.junit.Test;

public class Test_002_MatrixKaiten {

    public static void trans(int[][] matrix) {
        int n = matrix.length;
        for (int r = 0; r < n; ++r)
            for (int c = r + 1; c < n; ++c) {
                int temp = matrix[c][r];
                matrix[c][r] = matrix[r][c];
                matrix[r][c] = temp;
            }
    }

    @Test
    public void testTrans() {
        int[][] m3 = new int[][] {
            {00, 01, 02},
            {10, 11, 12},
            {20, 21, 22},
        };
        trans(m3);
        assertArrayEquals(new int[][] {
            {00, 10, 20},
            {01, 11, 21},
            {02, 12, 22},
        }, m3);
        int[][] m4 = new int[][] {
            {00, 01, 02, 03},
            {10, 11, 12, 13},
            {20, 21, 22, 23},
            {30, 31, 32, 33},
        };
        trans(m4);
        assertArrayEquals(new int[][] {
            {00, 10, 20, 30},
            {01, 11, 21, 31},
            {02, 12, 22, 32},
            {03, 13, 23, 33},
        }, m4);
    }

    public static void rot(int[][] matrix) {
        int n = matrix.length, rmax = (n + 1) / 2, cmax = n / 2;
        for (int r = 0; r < rmax; ++r) {
            int rr = n - r - 1;
            for (int c = 0; c < cmax; ++c) {
                int cc = n - c - 1;
                int temp = matrix[r][c];
                matrix[r][c] = matrix[c][rr];
                matrix[c][rr] = matrix[rr][cc];
                matrix[rr][cc] = matrix[cc][r];
                matrix[cc][r] = temp;
            }
        }
    }

    @Test
    public void testRot() {
        int[][] m3 = new int[][] {
            {00, 01, 02},
            {10, 11, 12},
            {20, 21, 22},
        };
        rot(m3);
        assertArrayEquals(new int[][] {
            {02, 12, 22},
            {01, 11, 21},
            {00, 10, 20},
        }, m3);
        int[][] m4 = new int[][] {
            {00, 01, 02, 03},
            {10, 11, 12, 13},
            {20, 21, 22, 23},
            {30, 31, 32, 33},
        };
        rot(m4);
        assertArrayEquals(new int[][] {
            {03, 13, 23, 33},
            {02, 12, 22, 32},
            {01, 11, 21, 31},
            {00, 10, 20, 30},
        }, m4);
    }

    static final int N = 9;
    record P(int r, int c) {}
    static P p(int r, int c) { return new P(r, c); }

    static class Matrix {
        final int[][] matrix = new int[N][N];
        {
            for (int r = 0; r < N; ++r)
                for (int c = 0; c < N; ++c)
                    matrix[r][c] = 10 * (r + 1) + (c + 1);
        }
        Function<P, P> accessor = x -> p(x.r, x.c);

        int get(P p) {
            return matrix[p.r][p.c];
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int r = 0; r < N; ++r) {
                for (int c = 0; c < N; ++c)
                    sb.append(" ").append(get(accessor.apply(p(r, c))));
                sb.append(System.lineSeparator());
            }
            return sb.toString();
        }
    }
    static final Function<P, P> IDENT = x -> p(x.r, x.c);
    static final Function<P, P> TRANS = x -> p(x.c, x.r);
    static final Function<P, P> ROT = x -> p(x.c, N - x.r - 1);

    static final Function<P, P> A = IDENT;
    static final Function<P, P> B = ROT;
    static final Function<P, P> C = B.andThen(ROT);
    static final Function<P, P> D = C.andThen(ROT);
    static final Function<P, P> E = A.andThen(TRANS);
    static final Function<P, P> F = C.andThen(TRANS);
    static final Function<P, P> G = B.andThen(TRANS);
    static final Function<P, P> H = D.andThen(TRANS);


    @Test
    public void testMatrix() {
        Matrix m = new Matrix();
        m.accessor = A; System.out.printf("A:%n%s%n", m);
        m.accessor = B; System.out.printf("B:%n%s%n", m);
        m.accessor = C; System.out.printf("C:%n%s%n", m);
        m.accessor = D; System.out.printf("D:%n%s%n", m);
        m.accessor = E; System.out.printf("E:%n%s%n", m);
        m.accessor = F; System.out.printf("F:%n%s%n", m);
        m.accessor = G; System.out.printf("G:%n%s%n", m);
        m.accessor = H; System.out.printf("H:%n%s%n", m);
    }
}
