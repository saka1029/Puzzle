package puzzle.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Nonogram {
    
    static final byte BLACK = 1;
    static final byte UNDEF = 0;
    static final byte WHITE = -1;
    
    class Line {
        final boolean horizontal;
        final int row, free;
        final int[] rans;
        final List<byte[]> sets;
        
        Line(boolean horizontal, int row, int[] rans, int end) {
            this.horizontal = horizontal;
            this.row = row;
            this.rans = rans.clone();
            this.free = end - IntStream.of(rans).sum() - rans.length + 1;
            this.sets = sets(rans, end);
        }
        
        static List<byte[]> sets(int[] rans, int end) {
            int size = rans.length;
            List<byte[]> sets = new ArrayList<>();
            new Object() {
                byte[] set = new byte[end];
                void sets(int no, int start) {
                    if (no >= size) {
                        for (int i = start; i < end; ++i)
                            set[i] = WHITE;
                        sets.add(set.clone());
                    } else if (start >= end) {
                        return;
                    } else {
                        int seq = rans[no];
                        for (int i = start, max = end - seq; i <= max; ++i) {
                            for (int j = start; j < i; ++j)
                                set[j] = WHITE;
                            int e = i + seq;
                            for (int j = i; j < e; ++j)
                                set[j] = BLACK;
                            if (e < end)
                                set[e] = WHITE;
                            sets(no + 1, e + 1);
                        }
                    }
                }
            }.sets(0, 0);
            return sets;
        }
        
        byte[] filter(int col, byte color) {
            byte[] and = null;
            for (Iterator<byte[]> it = sets.iterator(); it.hasNext();) {
                byte[] set = it.next();
                if (col >= 0 && set[col] != color)
                    it.remove();
                else if (and == null)
                    and = set.clone();
                else
                    for (int i = 0; i < end(); ++i)
                        if (and[i] != UNDEF && and[i] != set[i])
                            and[i] = UNDEF;
            }
            return and != null ? and : new byte[end()];
        }

        String toString(byte[] bytes) {
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes)
                sb.append(switch (b) {
                    case WHITE -> '.';
                    case BLACK -> '*';
                    default -> '?';
                });
            return sb.toString();
        }

        @Override
        public String toString() {
            return "Line [horizontal=" + horizontal
                + ", row=" + row
                + ", free=" + free
                + ", rans=" + Arrays.toString(rans)
                + ", and=" + toString(filter(-1, UNDEF))
                + ", sets(" + sets.size() + ")=[" + sets.stream().map(bytes -> toString(bytes))
                    .collect(Collectors.joining("|")) + "]"
                + "]";
        }

        int end() {
            return horizontal ? width : height;
        }

        int get(int col) {
            return horizontal ? board[row][col] : board[col][row];
        }
        
        void set(int col, byte value) {
            if (horizontal)
                board[row][col] = value;
            else
                board[col][row] = value;
        }
    }
    
    final int height, width, size;
    final byte[][] board;
    final Line[] rows, cols;
    
    private Nonogram(int[][] rows, int[][] cols) {
        height = rows.length;
        width = cols.length;
        size = height + width;
        board = new byte[height][width];
        this.rows = new Line[height];
        for (int i = 0; i < height; ++i)
            this.rows[i] = new Line(true, i, rows[i], width);
        this.cols = new Line[width];
        for (int i = 0; i < width; ++i)
            this.cols[i] = new Line(false, i, cols[i], height);
    }
    
    void print() {
        System.out.println("board:");
        for (byte[] row : board) {
            for (byte c : row)
                System.out.print(c == BLACK ? "*" : c == WHITE ? ".": "?");
            System.out.println();
        }
        System.out.println("rows:");
        for (Line line : rows)
            System.out.println(line);
        System.out.println("cols:");
        for (Line line : cols)
            System.out.println(line);
    }
    
    public static void solve(int[][] rows, int[][] cols) {
        Nonogram nonogram = new Nonogram(rows, cols);
        nonogram.print();
    }
}

