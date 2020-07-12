package test.puzzle;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

class Test閉じた騎士巡歴問題 {

    static final int[][] MOVES = {{-2, 1}, {-1, 2}, {1, 2}, {2, 1}, {2, -1}, {1, -2}, {-1, -2}, {-2, -1},};

    /**
     * 各セルごとに移動可能なセル位置を列挙します。
     *
     * @param height
     * @param width
     * @return int[height][width][moves][axis] moves
     *      セル位置(height, width)から移動できる先は
     *      moves[height][width].length種類あります。
     *      移動先の行はmoves[height][width][move][0]です。
     *      移動先の列はmoves[height][width][move][1]です。
     */
    static int[][][][] allMoves(int height, int width) {
        int size = MOVES.length;
        int[][][][] moves = new int[height][width][][];
        List<int[]> list = new ArrayList<>();
        for (int row = 0; row < height; ++row)
            for (int col = 0; col < width; ++col) {
                list.clear();
                for (int i = 0; i < size; ++i) {
                    int nextRow = row + MOVES[i][0];
                    int nextCol = col + MOVES[i][1];
                    if (nextRow >= 0 && nextRow < height
                        && nextCol >= 0 && nextCol < width)
                        list.add(new int[] {nextRow, nextCol});
                }
                moves[row][col] = list.toArray(int[][]::new);
            }
        return moves;
    }

    static int 閉じた騎士巡歴(int height, int width, int startRow, int startCol, Consumer<int[][]> solution) {
        int[][] board = new int[height][width];
        int[][][][] moves = allMoves(height, width);
        int numberOfPlaces = height * width;
        return new Object() {

            int count = 0;

            void search(int i, int row, int col) {
//                if (row < 0 || row >= height || col < 0 || col >= width)
//                    return;
                if (i == numberOfPlaces + 1 && board[row][col] == 1) {
                    ++count;
                    solution.accept(board);
                } else if (board[row][col] == 0) {
                    board[row][col] = i;
                    int[][] next = moves[row][col];
                    for (int j = 0, size = next.length; j < size; ++j)
                        search(i + 1, next[j][0], next[j][1]);
                    board[row][col] = 0;
                }
            }

            int run() {
                search(1, startRow, startCol);
                return count;
            }
        }.run();
    }

    @Test
    public void testAllMoves() {
        int height = 4;
        int width = 4;
        int[][][][] moves = allMoves(height, width);
        for (int r = 0; r < height; ++r) {
            for (int c = 0; c < width; ++c)
                System.out.print(moves[r][c].length + " ");
            System.out.println();
        }
        int[][] moveSize = Arrays.stream(moves)
            .map(row -> Arrays.stream(row)
                .mapToInt(move -> move.length)
                .toArray())
            .toArray(int[][]::new);
        assertArrayEquals(new int[][] {
            {2,3,3,2},
            {3,4,4,3},
            {3,4,4,3},
            {2,3,3,2}
        }, moveSize);
        assertEquals(2, moves[0][0].length);
        assertArrayEquals(new int[][] {{1, 2}, {2, 1}}, moves[0][0]);
        assertEquals(2, moves[0][width - 1].length);
        assertEquals(2, moves[height - 1][0].length);
        assertEquals(2, moves[height - 1][width - 1].length);
        assertArrayEquals(new int[][] {{2, 1}, {1, 2}}, moves[height - 1][width - 1]);
        assertArrayEquals(new int[][] {{0, 3}, {2, 3}, {3, 2}, {3, 0}}, moves[1][1]);
    }

    /**
     * <pre>
     * 6x6の最初の解
     * [1,  22, 27, 30,  3, 20]
     * [26, 31,  2, 21,  8, 29]
     * [23, 36, 25, 28, 19,  4]
     * [32, 13, 34,  7, 16,  9]
     * [35, 24, 11, 14,  5, 18]
     * [12, 33,  6, 17, 10, 15]
     * </pre>
     *
     * Knight's tour - Wikipedia
     * https://en.wikipedia.org/wiki/Knight%27s_tour#Number_of_tours
     */
    @Test
    void testWikipedia() {
        Consumer<int[][]> print = board -> {
            for (int[] row : board)
                System.out.println(Arrays.toString(row));
            System.out.println();
            throw new RuntimeException();
        };
        int height = 6;
        int width = 6;
        try {
            閉じた騎士巡歴(height, width, 0, 0, print);
        } catch (RuntimeException e) {
        }
    }

}
