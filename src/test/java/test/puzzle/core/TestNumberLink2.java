package test.puzzle.core;

import java.util.Arrays;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.junit.Test;

import puzzle.core.Common;

public class TestNumberLink2 {

    static final Logger logger = Common.getLogger(TestNumberLink2.class);

    static class NumberLink {
        final int rows, cols;
        final int[][] board;
        final int[][] ends;
        static final int[][] DIR = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
        static final int DIR_SIZE = DIR.length;

        NumberLink(int[][] board) {
            this.rows = board.length;
            this.cols = board[0].length;
            this.board = new int[rows][];
            for (int i = 0; i < rows; ++i)
                this.board[i] = Arrays.copyOf(board[i], cols);
            NavigableMap<Integer, int[]> map = new TreeMap<>();
            for (int r = 0; r < rows; ++r)
                for (int c = 0; c < cols; ++c) {
                    int name = board[r][c];
                    if (name == 0)
                        continue;
                    int[] e = map.get(name);
                    if (e == null)
                        map.put(name, e = new int[] {name, r, c, -1, -1});
                    else if (e[3] != -1)
                        throw new RuntimeException("'%d' duplicate".formatted(name));
                    else {
                        e[3] = r;
                        e[4] = c;
                    }
                }
            ends = map.values().stream()
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
            for (int[] e : ends)
                sb.append("end")
                    .append(Arrays.toString(e))
                    .append(System.lineSeparator());
            return sb.toString();
        }

        void print(String header) {
            System.out.println(header);
            System.out.println(toString());
        }

        void answer() {
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
            for (int j = 0; j < DIR_SIZE; ++j) {
                int rr = r + DIR[j][0], cc = c + DIR[j][1];
                if (valid(rr, cc)
                    && rr != ends[i][3] && cc != ends[i][4] // not goal
                    && board[rr][cc] == ends[i][0])
                    ++count;
            }
            return count;
        }

        void walk(int i, int r, int c) {
            for (int j = 0; j < DIR_SIZE; ++j) {
                int rr = r + DIR[j][0], cc = c + DIR[j][1];
                if (rr == ends[i][3] && cc == ends[i][4]) {
                    solve(i + 1);
                } else if (valid(rr, cc) && board[rr][cc] == 0 && neighbors(i, rr, cc) <= 1) {
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

        void solve() {
            solve(0);
        }
    }

    static final int[][] B7x7 = new int[][] {
        {0, 0, 0, 0, 3, 2, 1},
        {0, 0, 0, 0, 1, 0, 0},
        {0, 0, 0, 0, 0, 0, 0},
        {0, 0, 2, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0},
        {0, 3, 5, 0, 0, 4, 0},
        {4, 0, 0, 0, 0, 0, 5}};
    static final int[][] B4x4 = new int[][] {
        {1, 0, 0, 0},
        {0, 0, 0, 1},
        {0, 3, 0, 2},
        {2, 0, 0, 3}};
    static final int[][] B5x5 = new int[][] {
        {1, 2, 3, 0, 0},
        {0, 0, 0, 4, 0},
        {0, 4, 2, 0, 0},
        {0, 0, 0, 0, 0},
        {0, 0, 1, 3, 0}};
    static final int[][] B9x9 = new int[][] {
        {0, 5, 0, 0, 0, 0, 0, 0, 3},
        {0, 0, 0, 9, 0, 0, 0, 0, 4},
        {0, 0, 1, 0, 0, 9, 0, 0, 0},
        {0, 0, 2, 7, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 8, 0, 0},
        {0, 0, 0, 6, 0, 1, 7, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 2, 0, 6, 0, 8, 3, 0},
        {5, 0, 0, 0, 0, 0, 4, 0, 0}};
    static final int[][] B10x10 = new int[][] {
        {6, 0, 0, 0, 7, 0, 0, 0, 0, 6},
        {0, 0, 0, 0, 0, 0, 1, 0, 9, 0},
        {0, 7, 0, 10, 0, 0, 0, 0, 1, 0},
        {0, 0, 0, 8, 0, 0, 0, 10, 0, 0},
        {0, 0, 0, 0, 0, 0, 11, 0, 0, 11},
        {5, 0, 0, 2, 0, 0, 0, 0, 0, 0},
        {0, 0, 12, 0, 0, 0, 8, 0, 0, 0},
        {0, 4, 0, 0, 0, 0, 5, 0, 9, 0},
        {0, 2, 0, 3, 0, 0, 0, 0, 0, 0},
        {4, 0, 0, 0, 0, 3, 0, 0, 0, 12}};

    @Test
    public void testNumberLink() {
        NumberLink problem = new NumberLink(B7x7);
        System.out.println(problem);
    }

    @Test
    public void testSolveB4x4() {
        NumberLink problem = new NumberLink(B4x4);
        problem.solve();
    }

    @Test
    public void testSolveB5x5() {
        NumberLink problem = new NumberLink(B5x5);
        problem.solve();
    }

    @Test
    public void testSolveB7x7() {
        NumberLink problem = new NumberLink(B7x7);
        problem.solve();
    }

    @Test
    public void testSolveB9x9() {
        NumberLink problem = new NumberLink(B9x9);
        problem.solve();
    }

    @Test
    public void testSolveB10x10() {
        NumberLink problem = new NumberLink(B10x10);
        problem.solve();
    }
}