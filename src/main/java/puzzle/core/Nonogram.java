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
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Nonogram {

    public static final byte UNDEF = 0, BLACK = 1, WHITE = -1;
    public final int height, width;
    final byte[][] board;
    final List<CandidateSet> rows = new ArrayList<>(), cols = new ArrayList<>();
    final Deque<BooleanSupplier> que = new LinkedList<>();
    Consumer<byte[][]> listener = null;

    Nonogram(int[][] rows, int[][] cols) {
        System.out.println("rows=" + Arrays.deepToString(rows));
        System.out.println("cols=" + Arrays.deepToString(cols));
        height = rows.length;
        width = cols.length;
        board = new byte[height][width];
        for (int[] row : rows)
            this.rows.add(new CandidateSet(row, width));
        for (int[] col : cols)
            this.cols.add(new CandidateSet(col, height));
    }
    
    BooleanSupplier checker(boolean horizontal, int row, int col, byte color) {
        String methodName = horizontal ? "rowCheck" : "colCheck";
        return new BooleanSupplier() {

            @Override
            public boolean getAsBoolean() {
                return horizontal
                    ? rowCheck(row, col, color)
                    : colCheck(row, col, color);
            }

            @Override
            public String toString() {
                return methodName + "(" + row + ", " + col + ", " + color + ")";
            }
        };
    }
    
    boolean rowCheck(int row, int col, byte color) {
        boolean changed = false;
        byte[] common = rows.get(row).filter(row, col, color);
        if (common == null)
            throw new RuntimeException("No available candidate at row " + row);
        for (int i = 0, size = common.length; i < size; ++i) {
            byte c = common[i], b = board[row][i];
            if (c == UNDEF) continue;
            if (b == UNDEF) {
                board[row][i] = c;
                changed = true;
                que.add(checker(false, row, i, c));
            } else if (c != b)
                throw new RuntimeException("Conflict at (" + row + ", " + i + ")");
        }
        return changed;
    }

    boolean colCheck(int row, int col, byte color) {
        boolean changed = false;
        byte[] common = cols.get(col).filter(col, row, color);
        if (common == null)
            throw new RuntimeException("No available candidate at col " + col);          // col col
        for (int i = 0, size = common.length; i < size; ++i) {
            byte c = common[i], b = board[i][col];
            if (c == UNDEF) continue;
            if (b == UNDEF) {
                board[i][col] = c;                                                      // board[i][col]
                changed = true;
                que.add(checker(true, i, col, c));
            } else if (c != b)
                throw new RuntimeException("Conflict at (" + i + ", " + col + ")");
        }
        return changed;
    }

    public static String string(byte color) {
        return switch (color) {
            case BLACK -> "*";
            case WHITE -> ".";
            default -> "?";
        };
    }

    public static String string(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes)
            sb.append(string(b));
        return sb.toString();
    }

    @Override
    public String toString() {
        try (StringWriter sw = new StringWriter();
            PrintWriter w = new PrintWriter(sw)) {
            w.printf("%dx%d%n", height, width);
            for (byte[] row : board)
                w.println(string(row));
            w.println("rows:");
            for (int i = 0; i < height; ++i)
                w.printf("%d: %s%n", i, rows.get(i));
            w.println("cols:");
            for (int i = 0; i < width; ++i)
                w.printf("%d: %s%n", i, cols.get(i));
            w.println("que:");
            int i = 0;
            for (BooleanSupplier e : que)
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
        if (listener != null)
            listener.accept(board);
        System.out.println(this);
        while (!que.isEmpty()) {
            BooleanSupplier r = que.remove();
            boolean changed = r.getAsBoolean();
            if (listener != null && changed)
                listener.accept(board);
        }
        if (listener != null)
            listener.accept(board);
//        System.out.println(this);
        for (byte[] row : board)
            System.out.println(string(row));
    }

    public static void solve(int[][] rows, int[][] cols) {
        Nonogram nonogram = new Nonogram(rows, cols);
        nonogram.solve();
    }

    public static void solve(int[][] rows, int[][] cols, Consumer<byte[][]> listener) {
        Nonogram nonogram = new Nonogram(rows, cols);
        nonogram.listener = listener;
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
                .mapToInt(String::length)
                .filter(i -> i > 0)
                .toArray())
            .toArray(int[][]::new);
        int cols[][] = IntStream.range(0, width)
            .mapToObj(c -> Stream.of(IntStream.range(0, height)
                .mapToObj(r -> matrix[r][c])
                .collect(Collectors.joining())
                .split(sep))
                .mapToInt(String::length)
                .filter(i -> i > 0)
                .toArray())
            .toArray(int[][]::new);
        return new int[][][] {rows, cols};
    }

    /**
     * 候補の集合です。
     */
    public static class CandidateSet {
        final int width;
        final List<byte[]> candidates = new ArrayList<>();

        /**
         * 黒並びの数の配列から候補のリストを作成します。
         * @param rans 黒並びの数の配列を指定します。
         * @param width 黒並びを収める幅を指定します。
         */
        public CandidateSet(int[] rans, int width) {
            this.width = width;
            int size = rans.length;
            new Object() {
                byte[] candidate = new byte[width];
                void add(int index, int start) {
                    if (index >= size) {
                        Arrays.fill(candidate, start, width, WHITE);  // 残りを白で埋める。
                        candidates.add(candidate.clone());
                    } else if (start >= width) {
                        return; // 黒並びを配置できなければ何もしない。
                    } else {
                        if (index > 0) // 先頭でなければまず白を追加する。
                            candidate[start++] = WHITE;
                        for (int i = start, ran = rans[index]; i + ran <= width; ++i) {
                            Arrays.fill(candidate, start, i, WHITE);
                            Arrays.fill(candidate, i, i + ran, BLACK);
                            add(index + 1, i + ran);
                        }
                    }
                }
            }.add(0, 0);
        }

        /**
         * 候補の中からrow,col位置がcolorに合致しないものを取り除きます。
         * @param row
         * @param col
         * @param color
         * @return 残った候補の共通部分を返します。
         *         候補がなくなった場合はnullを返します。
         */
        public byte[] filter(int row, int col, byte color) {
            byte[] common = null;
            for (Iterator<byte[]> it = candidates.iterator(); it.hasNext();) {
                byte[] line = it.next();
                if (color != Nonogram.UNDEF && line[col] != color)
                    it.remove();
                else if (common == null)
                    common = line.clone();
                else 
                    for (int i = 0; i < width; ++i) {
                        int c = common[i];
                        if (c != Nonogram.UNDEF && c != line[i])
                            common[i] = Nonogram.UNDEF;
                    }
            }
            return common;
        }

        @Override
        public String toString() {
            return candidates.stream()
                .map(Nonogram::string)
                .collect(Collectors.joining("|"));
        }
    }
}