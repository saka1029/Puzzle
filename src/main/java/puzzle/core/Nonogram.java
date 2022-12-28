package puzzle.core;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Nonogram {

    enum Direction {
        Horizontal, Vertical;
    }
    
    static class Ran {
        Direction direction;
        int index;
        int order;
        int length;
        int start, minStart, maxStart;
    }
    
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
                ran.direction = Direction.Horizontal;
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
                ran.direction = Direction.Vertical;
                ran.index = index;
                ran.order = order;
                ran.length = col[order];
                ran.minStart = acc;
                ran.maxStart = acc + free;
                acc += col[order] + 1;
                this.rans[i++] = ran;
            }
        }
    }

    public static void solve(int[][] rows, int[][] cols) {
        new Nonogram(rows, cols);
    }
}
