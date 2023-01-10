package puzzle.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Nonogram {

    static final int UNDEF = 0, BLACK = 1, WHITE = -1;
    final int height, width;
    final int[][] board;
    final List<List<List<Integer>>> rows = new ArrayList<>(), cols = new ArrayList<>();
    
    Nonogram(int[][] rows, int[][] cols) {
        height = rows.length;
        width = cols.length;
        board = new int[height][width];
        for (int[] row : rows)
            this.rows.add(available(row, width));
        for (int[] col : cols)
            this.rows.add(available(col, height));
    }
    
    public static List<List<Integer>> available(int[] rans, int width) {
        int size = rans.length;
        List<List<Integer>> result = new ArrayList<>();
        new Object() {
            List<Integer> line = Arrays.asList(new Integer[width]);

            void available(int no, int start) {
                if (no >= size) {
                    for (int i = start; i < width; ++i)
                        line.set(i, WHITE);
                    result.add(new ArrayList<>(line));
                } else if (start >= width) {
                    return;
                } else {
                    int seq = rans[no];
                    for (int i = start, max = width - seq; i <= max; ++i) {
                        for (int j = start; j < i; ++j)
                            line.set(j, WHITE);
                        int e = i + seq;
                        for (int j = i; j < e; ++j)
                            line.set(j, BLACK);
                        if (e < width)
                            line.set(e, WHITE);
                        available(no + 1, e + 1);
                    }
                }
            }
        }.available(0, 0);
        return result;
    }
    
    public static void solve(int[][] rows, int[][] cols) {
        Nonogram nonogram = new Nonogram(rows, cols);
    }
}