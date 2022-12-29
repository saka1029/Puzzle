package puzzle.core;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Nonogram {

    static class Ran {
        boolean horizontal;
        int index;
        int order;
        int length;
        int start, minStart, maxStart;
        @Override
        public String toString() {
            return "Ran[horizontal=" + horizontal + ", index=" + index + ", order=" + order + ", length=" + length
                + ", start=" + start + ", minStart=" + minStart + ", maxStart=" + maxStart + "]";
        }
    }
    
    static final Comparator<Ran> RAN_ORDER = Comparator.comparing((Ran r) -> r.maxStart - r.minStart)
            .thenComparing(r -> !r.horizontal)
            .thenComparing(r -> r.index)
            .thenComparing(r -> r.order);
    static final int WHITE = -1, UNKNOWN = 0, BLACK = 1;
    final int height, width, size;
    final int[][] board;
    final Ran[] rans;
    
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
        System.out.println("height=" + height + " width=" + width); 
        for (Ran r : rans)
            System.out.println(r);
    }

    public static void solve(int[][] rows, int[][] cols) {
        Nonogram nonogram = new Nonogram(rows, cols);
    }
}
