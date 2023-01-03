package puzzle.core;

import java.util.stream.IntStream;

public class Nonogram {
    
    static final int BLACK = 1;
    static final int UNDEF = 0;
    static final int WHITE = -1;
    
    class Line {
        final boolean horizontal;
        final int index, free;
        final int[] rans, backup;
        
        Line(boolean horizontal, int index, int[] rans, int end) {
            this.horizontal = horizontal;
            this.index = index;
            this.rans = rans.clone();
            this.free = end - IntStream.of(rans).sum() - rans.length + 1;
            this.backup = new int[end];
        }
        
        int end() {
            return horizontal ? width : height;
        }

        int get(int col) {
            return horizontal ? board[index][col] : board[col][index];
        }
        
        void set(int col, int value) {
            if (horizontal)
                board[index][col] = value;
            else
                board[col][index] = value;
        }
        
        void solve(int no, int start) {
            if (no >= rans.length) {
                for (int i = start; i < end(); ++i) {
                    if (backup[i] == BLACK)
                        return;
                    else
                        set(i, WHITE);
                }
                Nonogram.this.solve(index + 1);
            } else {
                for (int i = start, max = end() - rans[no]; i < max; ++i) {
                    
                }
            }
        }
        
        void solve() {
            // backup
            for (int i = 0; i < end(); ++i)
                backup[i] = get(i);
            solve(0, 0);
            // restore
            for (int i = 0; i < end(); ++i)
                set(i, backup[i]);
        }
    }
    
    final int height, width, size;
    final int[][] board;
    final Line[] lines;
    
    private Nonogram(int[][] rows, int[][] cols) {
        height = rows.length;
        width = cols.length;
        size = height + width;
        board = new int[height][width];
        lines = new Line[size];
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
    
    void solve(int index) {
        if (index >= size)
            print();
        else
            lines[index].solve();
    }
    
    public static void solve(int[][] rows, int[][] cols) {
        Nonogram nonogram = new Nonogram(rows, cols);
        nonogram.print();
    }
}

