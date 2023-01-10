package puzzle.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    final Deque<Event> que = new LinkedList<>();

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

    public static List<Integer> filter(List<List<Integer>> availables, int size, int col, int color) {
        List<Integer> and = null;
        for (Iterator<List<Integer>> it = availables.iterator(); it.hasNext();) {
            List<Integer> line = it.next();
            if (col >= 0 && line.get(col) == color)
                it.remove();
            else if (and == null)
                and = new ArrayList<>(line);
            else
                for (int i = 0; i < size; ++i)
                    if (and.get(i) != UNDEF && !and.get(i).equals(line.get(i)))
                        and.set(i, UNDEF);
        }
        return and != null ? and : Arrays.asList(new Integer[size]);
    }

    public List<Integer> filter(boolean isRow, int row, int col, int color) {
        return isRow
            ? filter(rows.get(row), width, col, color)
            : filter(cols.get(col), height, row, color);
    }
    
    int getColor(boolean isRow, int row, int col) {
        return isRow ? board[row][col] : board[col][row];
    }

    void setColor(boolean isRow, int row, int col, int color) {
        int old = getColor(isRow, row, col);
        if (color == old)
            return;
        if (isRow) {
            board[row][col] = color;
            que.add(new Event(false, row, col, color));
        } else {
            board[col][row] = color;
            que.add(new Event(true, row, col, color));
        }
    }

    public void changed(boolean isRow, int row, int col, int color) {
        List<Integer> and = filter(isRow, row, col, color);
        for (int i = 0, size = and.size(); i < size; ++i)
            if (and.get(i) != UNDEF)
                setColor(isRow, row, col, color);
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
            .collect(Collectors.joining(", ", "[", "]"));
    }

    static final String NL = System.lineSeparator();
        
    @Override
    public String toString() {
//        return "Nonogram [height=" + height + ", width=" + width + ", board=" + Arrays.toString(board) + ", rows="
//            + rows + ", cols=" + cols + ", que=" + que + "]";
        StringBuilder sb = new StringBuilder();
        sb.append(height).append("x").append(width).append(NL);
        for (int[] row : board) {
            for (int cell : row)
                sb.append(switch (cell) {
                    case BLACK -> '*';
                    case WHITE -> '.';
                    default -> '?';
                });
            sb.append(NL);
        }
        sb.append("rows:").append(System.lineSeparator());
        for (int i = 0; i < height; ++i)
            sb.append(i).append(": ").append(string(rows.get(i))).append(NL);
        sb.append("cols:").append(System.lineSeparator());
        for (int i = 0; i < width; ++i)
            sb.append(i).append(": ").append(string(cols.get(i))).append(NL);
        sb.append("que: ").append(que).append(System.lineSeparator());
        return sb.toString();
    }

    public static void solve(int[][] rows, int[][] cols) {
        Nonogram nonogram = new Nonogram(rows, cols);
        System.out.println(nonogram);
    }
}