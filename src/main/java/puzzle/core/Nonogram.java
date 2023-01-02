package puzzle.core;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Nonogram {
    
    static final int BLACK = 1;
    static final int UNDEF = 0;
    static final int WHITE = -1;
    
    class Line {
        final boolean horizontal;
        final int index, free;
        final int[] rans;
        
        Line(boolean horizontal, int index, int[] rans, int end) {
            this.horizontal = horizontal;
            this.index = index;
            this.rans = rans.clone();
            this.free = end - IntStream.of(rans).sum() - rans.length + 1;
        }
        
        int end() {
            return horizontal ? width : height;
        }

        int get(int row, int col) {
            return horizontal ? board[row][col] : board[col][row];
        }
        
        void set(int row, int col, int value) {
            if (horizontal)
                board[row][col] = value;
            else
                board[col][row] = value;
        }
    }
    
    final int height, width;
    final int[][] board;
    final Line[] lines;
    
    private Nonogram(int[][] rows, int[][] cols) {
        height = rows.length;
        width = cols.length;
        board = new int[height][width];
        lines = new Line[height + width];
        int i = 0;
        for (int j = 0; j < height; ++j)
            lines[i++] = new Line(true, j, rows[j], width);
        for (int j = 0; j < width; ++j)
            lines[i++] = new Line(false, j, cols[j], height);
    }
    
    void print() {
        System.out.printf("height=%d width=%d%n", height, width);
        for (int[] row : board) {
            for (int c : row)
                System.out.print(c == BLACK ? "*" : ".");
            System.out.println();
        }
    }
    
    public static void solve(int[][] rows, int[][] cols) {
        Nonogram nonogram = new Nonogram(rows, cols);
        nonogram.print();
    }
}

