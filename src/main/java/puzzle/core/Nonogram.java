package puzzle.core;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.IntStream;

public class Nonogram {
    
    static final int BLACK = 1;
    static final int UNDEF = 0;
    static final int WHITE = -1;
    
    class Line {
        final boolean horizontal;
        final int row, free;
        final int[] rans, backup;
        
        Line(boolean horizontal, int row, int[] rans, int end) {
            this.horizontal = horizontal;
            this.row = row;
            this.rans = rans.clone();
            this.free = end - IntStream.of(rans).sum() - rans.length + 1;
            this.backup = new int[end];
        }
        

        @Override
        public String toString() {
            return "Line[horizontal=" + horizontal
                + ", row=" + row
                + ", rans=" + Arrays.toString(rans)
                + ", free=" + free
                + ", backup=" + Arrays.toString(backup)
                + "]";
        }

        int end() {
            return horizontal ? width : height;
        }

        int get(int col) {
            return horizontal ? board[row][col] : board[col][row];
        }
        
        void set(int col, int value) {
            if (horizontal)
                board[row][col] = value;
            else
                board[col][row] = value;
        }
        
        boolean fillColor(int i, int fillColor) {
            if (backup[i] != UNDEF && backup[i] != fillColor)
                return false;
            set(i, fillColor);
            return true;
        }
        
        void solve(int index, int no, int start) {
            if (no >= rans.length) {
                for (int i = start; i < end(); ++i)  // 右の余白をすべて白で埋める。
                    fillColor(i, WHITE);
                Nonogram.this.solve(index + 1);  // 次のLineに進む。
            } else {
                if (start >= end())
                    return;
                int length = rans[no];
                L: for (int i = start, max = end() - length; i <= max; ++i) {
                    for (int j = start; j < i; ++j)  // 黒並びの前を白で埋める。
                        if (!fillColor(j, WHITE))
                            return;
                    for (int j = i, m = i + length; j < m; ++j)  // 黒並びを黒で埋める。
                        if (!fillColor(j, BLACK))
                            continue L;
                    if (i + length < end() && !fillColor(i + length, WHITE))
                        continue L;
                    solve(index, no + 1, i + length + 1);
                }
            }
        }
        
        void solve(int index) {
            // backup
            for (int i = 0; i < end(); ++i)
                backup[i] = get(i);
            solve(index, 0, 0);
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
        for (int j = 0; j < height; ++i, ++j)
            lines[i] = new Line(true, j, rows[j], width);
        for (int j = 0; j < width; ++i, ++j)
            lines[i] = new Line(false, j, cols[j], height);
        Arrays.sort(lines, Comparator.comparing(line -> line.free));
    }
    
    void print() {
//        System.out.printf("%d x %d%n", height, width);
        for (int[] row : board) {
            for (int c : row)
                System.out.print(c == BLACK ? "*" : c == WHITE ? ".": "?");
            System.out.println();
        }
    }
    
    void solve(int index) {
        if (index >= size) {
            System.out.println("[[[ answer ]]]");
            print();
        } else
            lines[index].solve(index);
    }
    
    public static void solve(int[][] rows, int[][] cols) {
        Nonogram nonogram = new Nonogram(rows, cols);
        nonogram.solve(0);
    }
}

