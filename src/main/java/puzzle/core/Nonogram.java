package puzzle.core;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Nonogram {

    static final Comparator<Ran> RAN_ORDER = Comparator.comparing((Ran r) -> r.maxStart - r.minStart)
            .thenComparing(r -> !r.horizontal)
            .thenComparing(r -> r.index)
            .thenComparing(r -> r.order);
    static final int WHITE = -1, UNKNOWN = 0, BLACK = 1;
    final int height, width, size;
    final int[][] board;
    final Ran[] rans;
    int count = 0;
    
    class Ran {
        boolean horizontal;
        int index;
        int order;
        int length;
        int start, minStart, maxStart;

        @Override
        public String toString() {
            return "Ran[horizontal=" + horizontal
                + ", index=" + index + ", order=" + order
                + ", length=" + length + ", start=" + start
                + ", minStart=" + minStart + ", maxStart=" + maxStart + "]";
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
    }
    
    Nonogram(int[][] rows, int[][] cols) {
        this.height = rows.length;
        this.width = cols.length;
        this.board = new int[height][width];
        this.size = Stream.of(rows).mapToInt(a -> a.length).sum()
            + Stream.of(cols).mapToInt(a -> a.length).sum();
        this.rans = new Ran[size];
        int i = 0;
        for (int index = 0; index < height; ++index) {
            int[] row = rows[index];
            int count = row.length;
            int sum = IntStream.of(row).sum();
            int free = width - sum - (count - 1);
            int acc = 0;
            for (int order = 0; order < count; ++order) {
                Ran ran = new Ran();
                ran.horizontal = true;
                ran.index = index;
                ran.order = order;
                ran.length = row[order];
                ran.minStart = acc;
                ran.maxStart = acc + free;
                acc += row[order] + 1;
                this.rans[i++] = ran;
            }
        }
        for (int index = 0; index < width; ++index) {
            int[] col = cols[index];
            int count = col.length;
            int sum = IntStream.of(col).sum();
            int free = height - sum - (count - 1);
            int acc = 0;
            for (int order = 0; order < count; ++order) {
                Ran ran = new Ran();
                ran.horizontal = false;
                ran.index = index;
                ran.order = order;
                ran.length = col[order];
                ran.minStart = acc;
                ran.maxStart = acc + free;
                acc += col[order] + 1;
                this.rans[i++] = ran;
            }
        }
        Arrays.sort(rans, RAN_ORDER);
    }
    
    void answer() {
        if (count > 5)
            return;
        System.out.println(count++ + ":");
        for (int[] row : board) {
            for (int c : row)
                System.out.print(c == BLACK ? "*" : ".");
            System.out.println();
        }
    }
    
    int start(int i) {
        Ran ran = rans[i];
        int start = ran.minStart;
        if (i > 0) {
            Ran prev = rans[i - 1];
            if (prev.horizontal == ran.horizontal && prev.index == ran.index)
                start = Math.max(start, prev.start + prev.length + 1);
        }
        return start;
    }

    void  solve(int i, int start) {
        Ran ran = rans[i];
        int end = start + ran.length;
        // 左隣に黒がないことを確認する。
        if (start > 0 && ran.get(start - 1) == BLACK)
            return;
        // 右隣に黒がないことを確認する。
        if (end < ran.end()  && ran.get(end) == BLACK)
            return;
        // 黒の並びの配置場所に白がないことを確認する。
        for (int j = start; j < end; ++j)
            if (ran.get(j) == WHITE)
                return;
        // 退避
        ran.start = start;
        int left = start > 0 ? start - 1 : 0;
        int right = end < ran.end() ? end + 1 : end;
        int[] backup = new int[right - left];
        for (int j = 0, k = left; k < right; ++j, ++k)
            backup[j] = ran.get(k);
        // 配置
        if (start > 0)
            ran.set(start - 1, WHITE);
        for (int j = start; j < end; ++j)
            ran.set(j, BLACK);
        if (end < ran.end())
            ran.set(end, WHITE);
        // 再帰
        solve(i + 1);
        // 回復
        ran.start = 0;
        for (int j = 0, k = left; k < right; ++j, ++k)
            ran.set(k, backup[j]);
    }

    void solve(int i) {
        if (i >= size)
            answer();
        else
            for (int start = start(i), max = rans[i].maxStart; start <= max; ++start)
                solve(i, start);
    }

    void print() {
        System.out.println("height=" + height + " width=" + width); 
        for (Ran r : rans)
            System.out.println(r);
    }

    public static void solve(int[][] rows, int[][] cols) {
        Nonogram nonogram = new Nonogram(rows, cols);
        nonogram.print();
        nonogram.solve(0);
    }
}
