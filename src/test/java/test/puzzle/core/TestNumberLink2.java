package test.puzzle.core;

import static org.junit.Assert.assertArrayEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.junit.Test;

import puzzle.core.Common;

public class TestNumberLink2 {

    static final Logger logger = Common.getLogger(TestNumberLink2.class);

    static class NumberLink {

        static record Point(int r, int c) {
            Point add(Point p) { return new Point(r + p.r, c + p.c); }
            Point right() { return new Point(-c, r); }
            Point left() { return new Point(c, -r); }
        }

        static record Ends(int name, Point start, Point end) {}

        final int rows, cols;
        final int[][] board;
        final Ends[] ends;
        final List<int[][]> results = new ArrayList<>();
        static final Point[] DIR = {
            new Point(1, 0),
            new Point(0, 1),
            new Point(-1, 0),
            new Point(0, -1) };

        NumberLink(int[][] board) {
            this.rows = board.length;
            this.cols = board[0].length;
            this.board = Stream.of(board).map(r -> r.clone()).toArray(int[][]::new);
            NavigableMap<Integer, Ends> map = new TreeMap<>();
            for (int r = 0; r < rows; ++r)
                for (int c = 0; c < cols; ++c) {
                    int name = board[r][c];
                    if (name == 0)
                        continue;
                    Ends e = map.get(name);
                    if (e == null)
                        map.put(name, e = new Ends(name, new Point(r, c), null));
                    else if (e.end != null)
                        throw new RuntimeException("'%d' duplicate".formatted(name));
                    else 
                        map.put(name, new Ends(e.name, e.start, new Point(r, c)));
                }
            this.ends = map.values().stream()
                    .peek(e -> {
                        if (e.end == null)
                            throw new RuntimeException(
                                    "Single end %s".formatted(e));
                    })
                    .toArray(Ends[]::new);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int[] row : board)
                sb.append(Arrays.toString(row))
                        .append(System.lineSeparator());
            // for (Ends e : ends)
            //     sb.append("end")
            //     .append(e)
            //     .append(System.lineSeparator());
            return sb.toString();
        }

        void print(String header) {
            System.out.println(header);
            System.out.println(toString());
        }

        void answer() {
            results.add(Stream.of(board).map(r -> r.clone()).toArray(int[][]::new));
            print("answer:");
        }

        boolean valid(Point p) {
            return p.r >= 0 && p.r < rows && p.c >= 0 && p.c < cols;
        }

        /**
         * (r, c)の上下左右にある同じ数字を数える。
         * ただし上下左右にゴールがある場合は除外して数える。
         */
        int neighbors(int i, Point p) {
            int count = 0;
            for (Point dir : DIR) {
                Point n = p.add(dir);
                if (valid(n)
                        && !(n.equals(ends[i].end)) // not goal
                        && board[n.r][n.c] == ends[i].name)
                    ++count;
            }
            return count;
        }

        boolean check(int i, Point p, Point dir, Point n) {
            if (!valid(n)) return false;
            if (board[n.r][n.c] != 0) return false;
            Point right = dir.right(), left = dir.left();
            Point nn = n.add(dir);
            Point[] points = {n.add(right), n.add(left), nn, nn.add(right), nn.add(left)};
            for (Point x : points) {
                if (!valid(x)) continue;
                if (x.equals(ends[i].end)) continue;
                if (board[x.r][x.c] == ends[i].name) return false;
            }
            return true;
        }

        void walk(int i, Point p) {
            Ends e = ends[i];
            for (Point dir : DIR) {
                Point n = p.add(dir);
                if (n.equals(e.end)) {
                    // System.out.println(this);
                    solve(i + 1);
                } else if (valid(n) && board[n.r][n.c] == 0 && neighbors(i, n) <= 1) {
                    board[n.r][n.c] = e.name;
                    walk(i, n);
                    board[n.r][n.c] = 0;
                }
            }
        }

        void solve(int i) {
            if (i >= ends.length)
                answer();
            else
                walk(i, ends[i].start);
        }

        List<int[][]> solve() {
            print("problem:");
            solve(0);
            return results;
        }
    }

    static final int[][] B7x7 = new int[][] {
            { 0, 0, 0, 0, 3, 2, 1 },
            { 0, 0, 0, 0, 1, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 2, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0 },
            { 0, 3, 5, 0, 0, 4, 0 },
            { 4, 0, 0, 0, 0, 0, 5 } };
    static final int[][] A7x7 = new int[][] {
            { 3, 3, 3, 3, 3, 2, 1 },
            { 3, 1, 1, 1, 1, 2, 1 },
            { 3, 1, 2, 2, 2, 2, 1 },
            { 3, 1, 2, 1, 1, 1, 1 },
            { 3, 1, 1, 1, 5, 5, 5 },
            { 3, 3, 5, 5, 5, 4, 5 },
            { 4, 4, 4, 4, 4, 4, 5 } };

    static final int[][] B4x4 = new int[][] {
            { 1, 0, 0, 0 },
            { 0, 0, 0, 1 },
            { 0, 3, 0, 2 },
            { 2, 0, 0, 3 } };
    static final int[][] A4x4 = new int[][] {
            { 1, 1, 1, 1 },
            { 2, 2, 2, 1 },
            { 2, 3, 2, 2 },
            { 2, 3, 3, 3 } };

    static final int[][] B5x5 = new int[][] {
            { 1, 2, 3, 0, 0 },
            { 0, 0, 0, 4, 0 },
            { 0, 4, 2, 0, 0 },
            { 0, 0, 0, 0, 0 },
            { 0, 0, 1, 3, 0 } };
    static final int[][] A5x5 = new int[][] {
            { 1, 2, 3, 3, 3 },
            { 1, 2, 2, 4, 3 },
            { 1, 4, 2, 4, 3 },
            { 1, 4, 4, 4, 3 },
            { 1, 1, 1, 3, 3 } };

    static final int[][] B9x9 = new int[][] {
            { 0, 5, 0, 0, 0, 0, 0, 0, 3 },
            { 0, 0, 0, 9, 0, 0, 0, 0, 4 },
            { 0, 0, 1, 0, 0, 9, 0, 0, 0 },
            { 0, 0, 2, 7, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 8, 0, 0 },
            { 0, 0, 0, 6, 0, 1, 7, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 2, 0, 6, 0, 8, 3, 0 },
            { 5, 0, 0, 0, 0, 0, 4, 0, 0 } };
    static final int[][] A9x9 = new int[][] {
            { 5, 5, 8, 8, 8, 8, 8, 3, 3 },
            { 5, 8, 8, 9, 9, 9, 8, 3, 4 },
            { 5, 8, 1, 1, 1, 9, 8, 3, 4 },
            { 5, 8, 2, 7, 1, 1, 8, 3, 4 },
            { 5, 8, 2, 7, 7, 1, 8, 3, 4 },
            { 5, 8, 2, 6, 7, 1, 7, 3, 4 },
            { 5, 8, 2, 6, 7, 7, 7, 3, 4 },
            { 5, 8, 2, 6, 6, 8, 8, 3, 4 },
            { 5, 8, 8, 8, 8, 8, 4, 4, 4 }, };

    static final int[][] B10x10 = new int[][] {
            { 6, 0, 0, 0, 7, 0, 0, 0, 0, 6 },
            { 0, 0, 0, 0, 0, 0, 1, 0, 9, 0 },
            { 0, 7, 0, 10, 0, 0, 0, 0, 1, 0 },
            { 0, 0, 0, 8, 0, 0, 0, 10, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 11, 0, 0, 11 },
            { 5, 0, 0, 2, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 12, 0, 0, 0, 8, 0, 0, 0 },
            { 0, 4, 0, 0, 0, 0, 5, 0, 9, 0 },
            { 0, 2, 0, 3, 0, 0, 0, 0, 0, 0 },
            { 4, 0, 0, 0, 0, 3, 0, 0, 0, 12 } };
    static final int[][] A10x10 = new int[][] {
            { 6, 7, 7, 7, 7, 6, 6, 6, 6, 6 },
            { 6, 7, 6, 6, 6, 6, 1, 1, 9, 9 },
            { 6, 7, 6, 10, 10, 10, 10, 1, 1, 9 },
            { 6, 6, 6, 8, 8, 8, 10, 10, 9, 9 },
            { 5, 5, 5, 5, 5, 8, 11, 11, 9, 11 },
            { 5, 2, 2, 2, 5, 8, 8, 11, 9, 11 },
            { 2, 2, 12, 12, 5, 5, 8, 11, 9, 11 },
            { 2, 4, 4, 12, 12, 5, 5, 11, 9, 11 },
            { 2, 2, 4, 3, 12, 12, 12, 11, 11, 11 },
            { 4, 4, 4, 3, 3, 3, 12, 12, 12, 12 }, };

    @Test
    public void testNumberLink() {
        NumberLink problem = new NumberLink(B7x7);
        assertArrayEquals(A7x7, problem.solve().get(0));
    }

    @Test
    public void testSolveB4x4() {
        NumberLink problem = new NumberLink(B4x4);
        assertArrayEquals(A4x4, problem.solve().get(0));
    }

    @Test
    public void testSolveB5x5() {
        NumberLink problem = new NumberLink(B5x5);
        assertArrayEquals(A5x5, problem.solve().get(0));
    }

    @Test
    public void testSolveB7x7() {
        NumberLink problem = new NumberLink(B7x7);
        assertArrayEquals(A7x7, problem.solve().get(0));
    }

    @Test
    public void testSolveB9x9() {
        NumberLink problem = new NumberLink(B9x9);
        assertArrayEquals(A9x9, problem.solve().get(0));
    }

    @Test
    public void testSolveB10x10() {
        NumberLink problem = new NumberLink(B10x10);
        assertArrayEquals(A10x10, problem.solve().get(0));
    }

    static final int[][] S4x4 = new int[][] {
            { 1, 0, 0, 0 },
            { 0, 0, 0, 0 },
            { 0, 0, 0, 0 },
            { 0, 0, 0, 1 } };

    // @Test
    public void testCheckPaths() {
        NumberLink problem = new NumberLink(S4x4);
        problem.solve();
    }

    /**
     * https://www.gmpuzzles.com/blog/2016/10/numberlink-serkan-yurekli-2/
     */
    static final int[][] B24x16 = new int[][] {
            { 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 13, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 19, 0, 0, 0, 0, 0, 0, 15, 0, 0, 0, 0, 0, 0, 18, 0, 0, 0, 1, 10, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 18, 0, 0, 0, 0, 0, 0, 0, 0, 0, 23, 0 },
            { 0, 0, 0, 0, 0, 15, 0, 0, 0, 0, 11, 0, 0, 0, 0, 0, 0, 0, 8, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 8, 0, 0, 0, 0, 0, 0, 0, 22, 0, 0, 0 },
            { 0, 0, 0, 0, 19, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 21, 0, 0, 0, 0, 17, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 20, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 3, 0, 0, 0 },
            { 0, 0, 0, 10, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 14, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 9, 0, 0, 0, 0, 11, 0, 0, 0, 0, 14, 0, 0, 0, 0, 0, 0, 0, 6, 0, 0, 0, 0 },
            { 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 12, 0, 0, 0, 0, 0, 21, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 20, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 24, 0, 0, 0, 0, 0 },
            { 0, 9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 4, 7, 0, 0, 0, 12, 0, 0, 0, 0, 0, 0, 13, 0, 0, 0, 0, 0, 0, 24, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 17, 0, 0, 0, 0, 23, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 22 },
    };

    // @Test
    public void testSolveB24x16() {
        NumberLink problem = new NumberLink(B24x16);
        problem.solve();
    }
}
