package test.puzzle;

import java.util.Arrays;
import java.util.Objects;

import org.junit.jupiter.api.Test;

/**
 * java - "mini chess" recursion doesn't work as expected - Stack Overflow
 * https://stackoverflow.com/questions/62569733/mini-chess-recursion-doesnt-work-as-expected?noredirect=1#comment110653652_62569733
 *
 * The shortest Knight's path (Incinerator) - ナイトの最短ルート - summer_tree_home
 * http://py3.hateblo.jp/entry/2014/03/04/190051
 */
class Testナイトの最短ルート {

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
    void test() {
        int[][] distances = shortestDistances(4, 4, point(3, 0));
        for (int[] row : distances)
            System.out.println(Arrays.toString(row));
    }

    static class Path {
        Path from;
        final Point point;
        int distance = Integer.MAX_VALUE;
        
        Path(Point point) {
            this.point = point;
        }
        
        private void toString(StringBuilder sb) {
            if (from != null) {
                from.toString(sb);
                sb.append(" -> ");
            }
            sb.append(distance)
                .append(":")
                .append(point);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            toString(sb);
            return sb.toString();
        }
    }

    static Path[][] shortestPaths(int height, int width, Point start) {
        Path[][] paths = new Path[height][width];
        for (int row = 0; row < height; ++row)
            for (int col = 0; col < width; ++col)
                paths[row][col]= new Path(point(row, col));
        new Object() {
            void set(Point to, Path from, int distance) {
                if (to.x < 0 || to.x >= height || to.y < 0 || to.y >= width)
                    return;
                Path current = paths[to.x][to.y];
                if (distance >= current.distance)
                    return;
                current.distance = distance;
                current.from = from;
                for (Point direction : DIRECTIONS)
                    set(to.plus(direction), current, distance + 1);
            }
        }.set(start, null, 0);
        return paths;
    }

    @Test
    public void testShortestPaths() {
        Path[][] paths = shortestPaths(4, 4, point(3, 0));
        System.out.println(paths[0][3]);
        System.out.println(paths[0][0]);
    }

}
