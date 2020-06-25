package test.puzzle;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

/**
 * java - "mini chess" recursion doesn't work as expected - Stack Overflow
 * https://stackoverflow.com/questions/62569733/mini-chess-recursion-doesnt-work-as-expected?noredirect=1#comment110653652_62569733
 *
 * The shortest Knight's path (Incinerator) - ナイトの最短ルート - summer_tree_home
 * http://py3.hateblo.jp/entry/2014/03/04/190051
 */
class Testナイトの最短ルート {

    static final int[][] DIRECTIONS = {
        {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
        {1, -2}, {1, 2}, {2, -1}, {2, 1}};

    static int[][] shortestDistances(int height, int width, int startX, int startY) {
        int[][] board = new int[height][width];
        for (int[] row : board)
            Arrays.fill(row, Integer.MAX_VALUE);
        new Object() {
            void set(int x, int y, int distance) {
                if (x < 0 || x >= height || y < 0 || y >= width)
                    return;
                if (board[x][y] < distance)
                    return;
                board[x][y] = distance;
                for (int[] direction : DIRECTIONS)
                    set(x + direction[0], y + direction[1], distance + 1);
            }
        }.set(startX, startY, 0);
        return board;
    }

    @Test
    void test() {
    int[][] distances = shortestDistances(4, 4, 3, 0);
    for (int[] row : distances)
        System.out.println(Arrays.toString(row));
}

}
