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

    boolean check(Ran ran, int start) {
        // 左隣に黒がないことを確認する。
        if (start > 0 && ran.get(start - 1) == BLACK)
            return false;
        // 右隣に黒がないことを確認する。
        if (start + ran.length < ran.end()  && ran.get(start + ran.length) == BLACK)
            return false;
        // 黒の並びの配置場所に白がないことを確認する。
        for (int i = start; i < start + ran.length; ++i)
            if (ran.get(i) == WHITE)
                return false;
        return true;
    }

    void set(int i) {
        Ran ran = rans[i];
        
    }

    void unset(int i) {
        Ran ran = rans[i];
    }

    void solve(int i) {
        Ran ran = rans[i];
        if (i >= size)
            answer();
        else {
            for (int start = start(i); start <= ran.maxStart; ++start) {
                if (check(ran, start)) {
                    set(i);
                    solve(i + 1);
                    unset(i);
                }
            }
        }
    }

    void print() {
        System.out.println("height=" + height + " width=" + width); 
        for (Ran r : rans)
            System.out.println(r);
    }

    public static void solve(int[][] rows, int[][] cols) {
        Nonogram nonogram = new Nonogram(rows, cols);
        nonogram.print();
    }
}
