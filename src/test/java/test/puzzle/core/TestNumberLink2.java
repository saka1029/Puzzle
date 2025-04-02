package test.puzzle.core;

import java.util.Arrays;
import java.util.NavigableMap;
import java.util.TreeMap;
import org.junit.Test;

public class TestNumberLink2 {

    static class NumberLink {
        final int rows, cols;
        final int[][] board;
        final int[][] ends;

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

    }

    @Test
    public void testNumberLink() {
        int[][] board = new int[][] {
            {0, 0, 0, 0, 3, 2, 1},
            {0, 0, 0, 0, 1, 0, 0},
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 2, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0},
            {0, 3, 5, 0, 0, 4, 0},
            {4, 0, 0, 0, 0, 0, 5}};
        NumberLink problem = new NumberLink(board);
        System.out.println(problem);
    }
}