package test.puzzle.nanopico;

import java.util.function.Function;

import org.junit.Test;

public class Test_002_MatrixKaiten {

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
