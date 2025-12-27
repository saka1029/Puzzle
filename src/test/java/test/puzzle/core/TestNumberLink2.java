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
        final int rows, cols;
        final int[][] board;
        final int[][] ends;
        final List<int[][]> results = new ArrayList<>();
        static final int[][] DIR = { { 1, 0 }, { 0, 1 }, { -1, 0 }, { 0, -1 } };
        static final int[][] DIAG = { { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 } };

        NumberLink(int[][] board) {
            this.rows = board.length;
            this.cols = board[0].length;
            this.board = Stream.of(board).map(r -> r.clone()).toArray(int[][]::new);
            NavigableMap<Integer, int[]> map = new TreeMap<>();
            for (int r = 0; r < rows; ++r)
                for (int c = 0; c < cols; ++c) {
                    int name = board[r][c];
                    if (name == 0)
                        continue;
                    int[] e = map.get(name);
                    if (e == null)
                        map.put(name, e = new int[] { name, r, c, -1, -1 });
                    else if (e[3] != -1)
                        throw new RuntimeException("'%d' duplicate".formatted(name));
                    else {
                        e[3] = r;
                        e[4] = c;
                    }
                }
            this.ends = map.values().stream()
                    .peek(e -> {
                        if (e[3] == -1)
                            throw new RuntimeException(
                                    "Single end %s".formatted(Arrays.toString(e)));
                    })
                    .toArray(int[][]::new);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int[] row : board)
                sb.append(Arrays.toString(row))
                        .append(System.lineSeparator());
            // for (int[] e : ends)
            // sb.append("end")
            // .append(Arrays.toString(e))
            // .append(System.lineSeparator());
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

        boolean valid(int r, int c) {
            return r >= 0 && r < rows && c >= 0 && c < cols;
        }

        /**
         * (r, c)の上下左右にある同じ数字を数える。
         * ただし上下左右にゴールがある場合は除外して数える。
         */
        int neighbors(int i, int r, int c) {
            int count = 0;
            for (int[] dir : DIR) {
                int rr = r + dir[0], cc = c + dir[1];
                if (valid(rr, cc)
                        && !(rr == ends[i][3] && cc == ends[i][4]) // not goal
                        && board[rr][cc] == ends[i][0])
                    ++count;
            }
            return count;
        }

        /**
         * (r, c)の斜め上下左右にある同じ数字を数える。
         * ただし斜め上下左右にゴールがある場合は除外して数える。
         */
        int diagonal(int i, int r, int c) {
            int count = 0;
            for (int[] dir : DIAG) {
                int rr = r + dir[0], cc = c + dir[1];
                if (valid(rr, cc)
                        && !(rr == ends[i][3] && cc == ends[i][4]) // not goal
                        && board[rr][cc] == ends[i][0])
                    ++count;
            }
            return count;
        }

        void walk(int i, int r, int c) {
            for (int[] dir : DIR) {
                int rr = r + dir[0], cc = c + dir[1];
                if (rr == ends[i][3] && cc == ends[i][4]) {
                    // System.out.println(this);
                    solve(i + 1);
                } else if (valid(rr, cc) && board[rr][cc] == 0
                        && neighbors(i, rr, cc) <= 1
                /* && diagonal(i, rr, cc) == 0 */) {
                    board[rr][cc] = ends[i][0];
                    walk(i, rr, cc);
                    board[rr][cc] = 0;
                }
            }
        }

        void solve(int i) {
            if (i >= ends.length)
                answer();
            else
                walk(i, ends[i][1], ends[i][2]);
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
