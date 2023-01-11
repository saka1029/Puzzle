package puzzle.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Nonogram {

    public static final int UNDEF = 0, BLACK = 1, WHITE = -1;
    public final int height, width;
    final int[][] board;
    final List<List<List<Integer>>> rows = new ArrayList<>(), cols = new ArrayList<>();

    record Event(boolean isRow, int row, int col, int color) {
        @Override
        public String toString() {
            return "Event(%s, %d, %d, %d)".formatted(isRow, row, col, color);
        }
    }
    final Deque<Runnable> que = new LinkedList<>();

    Nonogram(int[][] rows, int[][] cols) {
        height = rows.length;
        width = cols.length;
        board = new int[height][width];
        for (int[] row : rows)
            this.rows.add(availables(row, width));
        for (int[] col : cols)
            this.cols.add(availables(col, height));
    }
    
    public static List<List<Integer>> availables(int[] rans, int width) {
        int size = rans.length;
        List<List<Integer>> availables = new ArrayList<>();
        new Object() {
            Integer[] line = new Integer[width];

            void available(int no, int start) {
                if (no >= size) {
                    for (int i = start; i < width; ++i)
                        line[i] = WHITE;
                    availables.add(List.of(line));
                } else if (start >= width) {
                    return;
                } else {
                    int seq = rans[no];
                    for (int i = start, max = width - seq; i <= max; ++i) {
                        for (int j = start; j < i; ++j)
                            line[j] = WHITE;
                        int e = i + seq;
                        for (int j = i; j < e; ++j)
                            line[j] = BLACK;
                        if (e < width)
                            line[e] = WHITE;
                        available(no + 1, e + 1);
                    }
                }
            }
        }.available(0, 0);
        return availables;
    }
    
    Runnable checker(boolean horizontal, int row, int col, int color) {
        return new Runnable() {

            @Override
            public void run() {
                if (horizontal)
                    rowCheck(row, col, color);
                else
                    colCheck(row, col, color);
            }

            @Override
            public String toString() {
                return (horizontal ? "rowCheck(" : "colCheck(")
                    + row + ", " + col + ", " + color + ")";
            }
        };
    }
    
    void rowCheck(int row, int col, int color) {
        List<Integer> common = null;
        for (Iterator<List<Integer>> it = rows.get(row).iterator(); it.hasNext();) {
            List<Integer> line = it.next();
            if (color != UNDEF && line.get(col) != color)
                it.remove();
            else if (common == null)
                common = new ArrayList<>(line);
            else 
                for (int i = 0, size = common.size(); i < size; ++i) {
                    int c = common.get(i);
                    if (c != UNDEF && c != line.get(i))
                        common.set(i, UNDEF);
                }
        }
        if (common == null)
            throw new RuntimeException("No available sequence at row " + row);
        for (int i = 0, size = common.size(); i < size; ++i) {
            int c = common.get(i), b = board[row][i];
            if (c == UNDEF) continue;
            if (b == UNDEF) {
                board[row][i] = c;
                que.add(checker(false, row, i, c));
            } else if (c != b)
                throw new RuntimeException("Conflict at (" + row + ", " + i + ")");
        }
    }

    void colCheck(int row, int col, int color) {
        List<Integer> common = null;
        for (Iterator<List<Integer>> it = cols.get(col).iterator(); it.hasNext();) {    // cols col
            List<Integer> line = it.next();
            if (color != UNDEF && line.get(row) != color)                               // row
                it.remove();
            else if (common == null)
                common = new ArrayList<>(line);
            else 
                for (int i = 0, size = common.size(); i < size; ++i) {
                    int c = common.get(i);
                    if (c != UNDEF && c != line.get(i))
                        common.set(i, UNDEF);
                }
        }
        if (common == null)
            throw new RuntimeException("No available sequence at col " + col);          // col col
        for (int i = 0, size = common.size(); i < size; ++i) {
            int c = common.get(i), b = board[i][col];
            if (c == UNDEF) continue;
            if (b == UNDEF) {
                board[i][col] = c;                                                      // board[i][col]
                que.add(checker(true, i, col, c));
            } else if (c != b)
                throw new RuntimeException("Conflict at (" + i + ", " + col + ")");
        }
    }

    static String string(int color) {
        return switch (color) {
            case BLACK -> "*";
            case WHITE -> ".";
            default -> "?";
        };
    }

    static String string(List<List<Integer>> lines) {
        return lines.stream()
            .map(x -> x.stream().map(c -> string(c)).collect(Collectors.joining()))
            .collect(Collectors.joining("|"));
    }

    @Override
    public String toString() {
        try (StringWriter sw = new StringWriter();
            PrintWriter w = new PrintWriter(sw)) {
            w.printf("%dx%d%n", height, width);
            for (int[] row : board) {
                for (int cell : row)
                    w.print(string(cell));
                w.println();
            }
            w.println("rows:");
            for (int i = 0; i < height; ++i)
                w.printf("%d: %s%n", i, string(rows.get(i)));
            w.println("cols:");
            for (int i = 0; i < width; ++i)
                w.printf("%d: %s%n", i, string(cols.get(i)));
            w.println("que:");
            int i = 0;
            for (Runnable e : que)
                w.printf("%d: %s%n", i++, e);
            return sw.toString();
        } catch (IOException e1) {
            throw new RuntimeException(e1);
        }
    }
    
    void solve() {
        for (int row = 0; row < height; ++row)
            rowCheck(row, -1, UNDEF);
        for (int col = 0; col < width; ++col)
            colCheck(-1, col, UNDEF);
        System.out.println(this);
        while (!que.isEmpty()) {
            Runnable r = que.remove();
            r.run();
        }
        System.out.println(this);
    }

    public static void solve(int[][] rows, int[][] cols) {
        Nonogram nonogram = new Nonogram(rows, cols);
        nonogram.solve();
    }
}