package test.puzzle.core;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.stream.IntStream;

import org.junit.Test;

public class TestNonogram {

    static final int UNKNOWN = 0, WHITE = -1, BLACK = 1;

    static void solve(int[][] rows, int[][] cols) {
        int height = rows.length, width = cols.length;
        int[][] a = new int[height][width];
        new Object() {

            void solve() {
                System.out.printf("height=%d width=%d%n", height, width);
                for (int i = 0; i < height; ++i) {
                    int[] row = rows[i];
                    int c = row.length, s = IntStream.of(row).sum();
                    System.out.printf("row=%d s=%d f=%d n=%s%n",
                        i, s, width - s - c + 1, Arrays.toString(row));
                }
                for (int i = 0; i < width; ++i) {
                    int[] col = cols[i];
                    int c = col.length, s = IntStream.of(col).sum();
                    System.out.printf("col=%d s=%d f=%d n=%s%n",
                        i, s, height - s - c + 1, Arrays.toString(col));
                }

            }
        }.solve();
    }

    @Test
    public void test() {
        int[][] rows = {{5}, {5}, {3}, {3, 2}, {4, 1}, {1, 3, 1}, {1, 1, 3, 1}, {5}, {2, 4}, {1, 4}};
        int[][] cols = {{2, 2}, {4, 1}, {2}, {1, 1}, {1}, {2, 4}, {2, 5}, {3, 4}, {4, 3}, {4, 5}};
        solve(rows, cols);
    }

}
