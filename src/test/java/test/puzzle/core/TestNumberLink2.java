package test.puzzle.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import org.junit.Test;

public class TestNumberLink2 {

    static class NumberLink {
        final int rows, cols;
        final int[][] board;
        record End(int name, int r0, int c0, int r1, int c1) {}
        final End[] ends;

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
                        map.put(name, e = new int[] {r, c, -1, -1});
                    else if (e[2] != -1)
                        throw new RuntimeException("'%d' duplicate".formatted(name));
                    else {
                        e[2] = r;
                        e[3] = c;
                    }
                }
            ends = map.entrySet().stream()
                .map(x -> {
                    int name = x.getKey();
                    int[] e = x.getValue();
                    if (e[2] < 0)
                        throw new RuntimeException("'%d' appears only one time".formatted(name));
                    return new End(name, e[0], e[1], e[2], e[3]);
                })
                .toArray(End[]::new);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int[] row : board)
                sb.append(Arrays.toString(row)).append(System.lineSeparator());
            for (End e : ends)
                sb.append(e).append(System.lineSeparator());
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