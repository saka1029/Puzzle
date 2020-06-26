package test.puzzle;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

class Te開いた騎士巡歴問題 {
    
    static final int[] moveRows = { -2, -1, 1, 2, 2, 1, -1, -2 };
    static final int[] moveCols = { 1, 2, 2, 1, -1, -2, -2, -1 };

    static int 閉じた騎士巡歴(int height, int width, int startRow, int startCol, Consumer<int[][]> solution) {
        int[][] board = new int[height][width];
        int numberOfPlaces = height * width;
        int numberOfMoves = moveRows.length;
        return new Object() {

            int count = 0;

            void search(int i, int row, int col) {
                if (row < 0 || row >= height || col < 0 || col >= width) return;
                if (i == numberOfPlaces + 1 && board[row][col] == 1) {
                    ++count;
                    solution.accept(board);
                } else if (board[row][col] == 0) {
                    board[row][col] = i;
                    for (int j = 0; j < numberOfMoves; ++j)
                        search(i + 1, row + moveRows[j], col + moveCols[j]);
                    board[row][col] = 0;
                }
            }
            
            int run() {
                search(1, startRow, startCol);     
                return count;
            }
        }.run();
    }

    /**
     * Wikipediaによれば5x5の場合、1728通りのパスがあります。
     * これは始点を5x5通りに変えて実行したときの総和です。
     * 
     * Knight's tour - Wikipedia
     * https://en.wikipedia.org/wiki/Knight%27s_tour#Number_of_tours
     */
    @Test
    void testWikipedia() {
        int height = 6;
        int width = 6;
        int total = 0;
        for (int i = 0; i < height; ++i)
            for (int j = 0; j < width; ++j)
                total += 閉じた騎士巡歴(height, width, i, j, board -> {});
        System.out.println(total);
        assertEquals(1728, total);
    }

}
