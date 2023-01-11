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
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Nonogram {

    public static final int UNDEF = 0, BLACK = 1, WHITE = -1;
    public final int height, width;
    final int[][] board;
    final List<List<List<Integer>>> rows = new ArrayList<>(), cols = new ArrayList<>();
    final Deque<Runnable> que = new LinkedList<>();

    Nonogram(int[][] rows, int[][] cols) {
        System.out.println("rows=" + Arrays.deepToString(rows));
        System.out.println("cols=" + Arrays.deepToString(cols));
        height = rows.length;
        width = cols.length;
        board = new int[height][width];
        for (int[] row : rows)
            this.rows.add(candidates(row, width));
        for (int[] col : cols)
            this.cols.add(candidates(col, height));
    }
    
    /**
     * 黒並びの数の配列から候補のリストを作成します。
     * @param rans 黒並びの数の配列を指定します。
     * @param width 黒並びを収める幅を指定します。
     * @return 候補のリストを返します。
     */
    public static List<List<Integer>> candidates(int[] rans, int width) {
        int size = rans.length;
        List<List<Integer>> candidates = new ArrayList<>();
        new Object() {
            Integer[] line = new Integer[width];

            void candidate(int no, int start) {
                if (no >= size) {
                    for (int i = start; i < width; ++i) // 右端に白を詰めます。
                        line[i] = WHITE;
                    candidates.add(List.of(line));
                } else if (start >= width) {
                    return;
                } else {
                    int seq = rans[no];
                    for (int i = start, max = width - seq; i <= max; ++i) {
                        for (int j = start; j < i; ++j)
                            line[j] = WHITE;
                        int e = i + seq;
                        for (int j = i; j < e; ++j) // 黒並びを置きます。
                            line[j] = BLACK;
                        if (e < width)  // 右端でなければひとつ白を置きます。
                            line[e] = WHITE;
                        candidate(no + 1, e + 1);
                    }
                }
            }
        }.candidate(0, 0);
        return candidates;
    }
    
    Runnable checker(boolean horizontal, int row, int col, int color) {
        String methodName = horizontal ? "rowCheck" : "colCheck";
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
                return methodName + "(" + row + ", " + col + ", " + color + ")";
            }
        };
    }
    
    /**
     * 候補の中からrow,col位置がcolorに合致しないものを取り除きます。
     * @param candidates 候補のリストを指定します。
     * @param row
     * @param col
     * @param color
     * @return 残った候補の共通部分を返します。
     *         候補がなくなった場合はnullを返します。
     */
    static List<Integer> common(List<List<Integer>> candidates, int row, int col, int color) {
        List<Integer> common = null;
        for (Iterator<List<Integer>> it = candidates.iterator(); it.hasNext();) {
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
        return common;
    }
    
    void rowCheck(int row, int col, int color) {
        List<Integer> common = common(rows.get(row), row, col, color);
        if (common == null)
            throw new RuntimeException("No available candidate at row " + row);
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
        List<Integer> common = common(cols.get(col), col, row, color);
        if (common == null)
            throw new RuntimeException("No available candidate at col " + col);          // col col
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
//        System.out.println(this);
        for (int[] row : board) {
            for (int cell : row)
                System.out.print(string(cell));
            System.out.println();
        }
    }

    public static void solve(int[][] rows, int[][] cols) {
        Nonogram nonogram = new Nonogram(rows, cols);
        nonogram.solve();
    }
    
    /**
     * 解答から問題を生成します。
     * @param answer 例えば"- x -\nx x x\n- x -\n"のように
     *               黒を"x"、白を"-"で与えます。
     *               各行は改行コード(\r,\r\n,\nのいずれか)で区切ります。
     *               空白文字は全て無視します。
     * @param white  空白を表す文字列を指定します。
     *               黒を表す文字列は指定不要です。
     * @return new 例えばnew int[][][] {{{1}, {3}, {1}}, {{1}, {3}, {1}}}
     *             の形式で返します。返す値をrcとすると
     *             rc[0]は行の制約(int[][])、rc[1]は列の制約(int[][])です。
     */
    public static int[][][] makeProblem(String answer, String white) {
        String sep = Pattern.quote(white) + "+";
        String[][] matrix = answer.lines()
            .map(s -> s.replaceAll("\\s", "").split(""))
            .toArray(String[][]::new);
        int height = matrix.length, width = matrix[0].length;
        int rows[][] = IntStream.range(0, height)
            .mapToObj(r -> Stream.of(IntStream.range(0, width)
                .mapToObj(c -> matrix[r][c])
                .collect(Collectors.joining())
                .split(sep))
                .mapToInt(s -> s.length())
                .filter(i -> i > 0)
                .toArray())
            .toArray(int[][]::new);
        int cols[][] = IntStream.range(0, width)
            .mapToObj(c -> Stream.of(IntStream.range(0, height)
                .mapToObj(r -> matrix[r][c])
                .collect(Collectors.joining())
                .split(sep))
                .mapToInt(s -> s.length())
                .filter(i -> i > 0)
                .toArray())
            .toArray(int[][]::new);
        return new int[][][] {rows, cols};
    }
}