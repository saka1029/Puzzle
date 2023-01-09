package puzzle.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
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

        int end() {
            return horizontal ? width : height;
        }

        byte get(int col) {
            return horizontal ? board[row][col] : board[col][row];
        }
        
        static Runnable changed(Line line, int col, byte color) {
            return new Runnable() {
                @Override
                public void run() {
                    line.changed(col, color);
                }
                @Override
                public String toString() {
                    if (line.horizontal)
                        return "(" + line.row + ", " + col + ", " + color + ")";
                    else
                        return "(" + col + ", " + line.row + ", " + color + ")";
                }
            };
        }
        
        void set(int col, byte color) {
            byte old = get(col);
            if (color == old)
                return;
            if (horizontal) {
                board[row][col] = color;
                que.add(changed(cols[col], row, color));
//                que.add(() -> cols[col].changed(row, color));
            } else {
                board[col][row] = color;
                que.add(changed(rows[row], col, color));
//                que.add(() -> rows[row].changed(col, color));
            }
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
        
        void changed(int col, byte color) {
            byte[] and = filter(col, color);
            for (int i = 0; i < end(); ++i) {
                byte c = and[i];
                if (c != UNDEF)
                    set(i, c);
            }
        }

        @Override
        public String toString() {
            return "Line [horizontal=" + horizontal
                + ", row=" + row
                + ", free=" + free
                + ", rans=" + Arrays.toString(rans)
                + ", and=" + Nonogram.toString(filter(-1, UNDEF))
                + ", sets(" + sets.size() + ")=[" + sets.stream().map(bytes -> Nonogram.toString(bytes))
                    .collect(Collectors.joining("|")) + "]"
                + "]";
        }
    }
    
    final int height, width;
    final byte[][] board;
    final Line[] rows, cols;
    final Deque<Runnable> que = new LinkedList<>();
    
    private Nonogram(int[][] rows, int[][] cols) {
        height = rows.length;
        width = cols.length;
        board = new byte[height][width];
        this.rows = new Line[height];
        for (int i = 0; i < height; ++i)
            this.rows[i] = new Line(true, i, rows[i], width);
        this.cols = new Line[width];
        for (int i = 0; i < width; ++i)
            this.cols[i] = new Line(false, i, cols[i], height);
    }

    static String toString(byte[] bytes) {
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
        try (StringWriter sw = new StringWriter();
            PrintWriter w = new PrintWriter(sw)) {
            w.println("board:");
            for (byte[] row : board)
                w.println(toString(row));
            w.println("rows:");
            for (Line line : rows)
                w.println(line);
            w.println("cols:");
            for (Line line : cols)
                w.println(line);
            w.println("que:" + que);
            return sw.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    void solve() {
        for (Line row : rows)
            row.changed(-1, UNDEF);
        for (Line col : cols)
            col.changed(-1, UNDEF);
        System.out.println(this);
        while (!que.isEmpty())
            que.remove().run();
    }
    
    public static void solve(int[][] rows, int[][] cols) {
        Nonogram nonogram = new Nonogram(rows, cols);
        System.out.println(nonogram);
        nonogram.solve();
        System.out.println(nonogram);
    }
}

