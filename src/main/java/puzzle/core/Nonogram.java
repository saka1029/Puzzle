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
//                + ", backup=" + Arrays.toString(backup)
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
        
        /**
         * boardに指定色をセットします。
         * セットする位置に既に設定されている色がUNDEFまたは指定色の場合のみセットします。
         */
        boolean setColor(int col, int fillColor) {
            if (backup[col] == UNDEF) {
                set(col, fillColor);
                return true;
            }
            return backup[col] == fillColor;
        }
        
        void solve(int index, int no, int start) {
            if (no >= rans.length) {
                for (int i = start; i < end(); ++i)  // 右の余白をすべて白で埋めます。
                    if (!setColor(i, WHITE))
                        return;
                Nonogram.this.solve(index + 1);  // 次のLineに進みます。
            } else {
                if (start >= end())  // 置き場所がなければ何もしません。
                    return;
                int length = rans[no];
                L: for (int i = start, max = end() - length; i <= max; ++i) {
                    for (int j = start; j < i; ++j)  // 黒並びの前を白で埋めます。
                        if (!setColor(j, WHITE))
                            return;
                    for (int j = i, m = i + length; j < m; ++j)  // 黒並びを黒で埋めます。
                        if (!setColor(j, BLACK))
                            continue L;
                    if (i + length < end() && !setColor(i + length, WHITE)) // 黒並びの右端が末尾でなければその右隣に白を埋めます。
                        continue L;
                    solve(index, no + 1, i + length + 1);
                }
            }
        }
        
        void solve(int index) {
            for (int i = 0; i < end(); ++i) // backup
                backup[i] = get(i);
            solve(index, 0, 0);
            for (int i = 0; i < end(); ++i) // restore
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
        for (int[] row : board) {
            for (int c : row)
                System.out.print(c == BLACK ? "*" : c == WHITE ? ".": "?");
            System.out.println();
        }
    }
    
    void solve(int index) {
        if (index >= size)
            print();
        else {
            System.out.println("index=" + index + " line=" + lines[index]);
            print();
            lines[index].solve(index);
        }
    }
    
    public static void solve(int[][] rows, int[][] cols) {
        Nonogram nonogram = new Nonogram(rows, cols);
        nonogram.solve(0);
    }
}

