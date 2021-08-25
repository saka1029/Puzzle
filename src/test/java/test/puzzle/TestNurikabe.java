package test.puzzle;

import static java.lang.Math.abs;

import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

import puzzle.Common;

class TestNurikabe {
    
    static final Logger loger = Common.getLogger(TestNurikabe.class);
    
    static final int UNKNOWN = 0;
    static final int BLACK = -1;
    static final int WHITE = -2;
    
    static void nurikabe(int[][] board) {
        new Object() {
            int rows = board.length;
            int cols = board[0].length;
            boolean[][] reachable;
            
            void unreachable(int row, int col) {
                int n = board[row][col] - 1;
                for (int i = -n, r = row + i; i <= n; ++i, ++r)
                    if (r >= 0 && r < rows)
                        for (int j = abs(i) - n, c = col + j, jmax = -j; j <= jmax; ++j, ++c)
                            if (c >= 0 && c < cols)
                                reachable[r][c] = true;
            }

            void unreachable() {
                reachable = new boolean[rows][cols];
                for (int r = 0; r < rows; ++r)
                    for (int c = 0; c < cols; ++c)
                        if (board[r][c] > 0)
                            unreachable(r, c);
//                for (boolean[] row : reachable)
//                    System.out.println(Arrays.toString(row));
                for (int r = 0; r < rows; ++r)
                    for (int c = 0; c < cols; ++c)
                        if (board[r][c] == 0 && !reachable[r][c])
                            board[r][c] = BLACK;
            }
        }.unreachable();
    }
    
    static void print(int[][] board) {
        for (int[] row : board) {
            for (int c : row)
                System.out.printf("%3d ", c);
            System.out.println();
        }
    }

    @Test
    void test() {
        int[][] board = {
            {0, 0, 0, 0, 2, 0, 2, 0, 0, 0,},
            {2, 0, 0, 0, 0, 0, 0, 0, 2, 0,},
            {0, 0, 0, 2, 0, 0, 0, 0, 0, 0,},
            {0, 2, 0, 0, 0, 0, 2, 0, 0, 0,},
            {0, 0, 0, 0, 0, 2, 0, 2, 0, 0,},
            {0, 0, 2, 0, 0, 0, 0, 0, 0, 0,},
            {0, 0, 0, 0, 0, 2, 0, 2, 0, 0,},
            {0, 0, 2, 0, 0, 0, 0, 0, 0, 2,},
            {2, 0, 0, 0, 0, 2, 0, 0, 0, 0,},
            {0, 0, 0, 0, 0, 0, 0, 0, 2, 0,},
        };
        nurikabe(board);
        print(board);
        
    }

}
