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
            print("anser:");
        }

        boolean canGo(int r, int c) {
            return r >= 0 && r < rows && c >= 0 && c < cols && board[r][c] == 0;
        }

        void walk(int i, int r, int c) {
            int[] e = ends[i];
            if (r == e[3] && c == e[4]) {
                print("path of %d:".formatted(e[0]));
                solve(i + 1);
            } else {
                int count = 0;
                for (int j = 0; j < DIR_SIZE; ++j) {
                    int rr = r + DIR[j][0], cc = c + DIR[j][1];
                    if (canGo(rr, cc)) {
                        ++count;
                        board[rr][cc] = e[0];
                        walk(i, rr, cc);
                        board[rr][cc] = 0;
                    }
                }
                if (count == 0)
                    print("stuck at %d,%d:".formatted(r, c));
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

    @Test
    public void testNumberLink() {
        NumberLink problem = new NumberLink(B7x7);
        System.out.println(problem);
    }

    @Test
    public void testSolve() {
        NumberLink problem = new NumberLink(B7x7);
        problem.solve();
    }
}