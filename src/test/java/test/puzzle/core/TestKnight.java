package test.puzzle.core;

import java.util.Arrays;
import java.util.Objects;

import org.junit.Test;

/**
 * java - "mini chess" recursion doesn't work as expected - Stack Overflow
 * https://stackoverflow.com/questions/62569733/mini-chess-recursion-doesnt-work-as-expected?noredirect=1#comment110653652_62569733
 *
 * The shortest Knight's path (Incinerator) - ナイトの最短ルート - summer_tree_home
 * http://py3.hateblo.jp/entry/2014/03/04/190051
 */
public class TestKnight {

    static class Point {
        public final int x, y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Point plus(Point o) {
            return new Point(x + o.x, y + o.y);
        }

        public boolean lessThan(Point o) {
            return x < o.x && y < o.y;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (getClass() != obj.getClass()) return false;
            Point o = (Point)obj;
            return x == o.x && y == o.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        @Override
        public String toString() {
            return  x + "@" + y;
        }
    }

    static Point point(int x, int y) {
        return new Point(x, y);
    }

    static final Point[] DIRECTIONS = {
        point(-2, -1), point(-2, 1), point(-1, -2), point(-1, 2),
        point(1, -2), point(1, 2), point(2, -1), point(2, 1) };

    static int[][] shortestDistances(int height, int width, Point start) {
        int[][] board = new int[height][width];
        for (int[] row : board)
            Arrays.fill(row, Integer.MAX_VALUE);
        new Object() {
            void set(Point p, int distance) {
                if (p.x < 0 || p.x >= height || p.y < 0 || p.y >= width)
                    return;
                if (distance >= board[p.x][p.y])
                    return;
                board[p.x][p.y] = distance;
                for (Point direction : DIRECTIONS)
                    set(p.plus(direction), distance + 1);
            }
        }.set(start, 0);
        return board;
    }

    @Test
    public void testShortestDistances() {
        int[][] board = shortestDistances(4, 4, point(3, 0));
        for (int[] row : board)
            System.out.println(Arrays.toString(row));
    }

}
