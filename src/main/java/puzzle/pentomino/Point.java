package puzzle.pentomino;

import java.util.Arrays;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public class Point implements Comparable<Point> {

    public final int x, y;

    Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Point of(int x, int y) {
        return new Point(x, y);
    }

    public Point add(Point p) {
        return new Point(x + p.x, y + p.y);
    }

    public Point subtract(Point p) {
        return new Point(x - p.x, y - p.y);
    }

    public static Point[] map(Point[] points, Function<Point, Point> function) {
        return Arrays.stream(points).map(function).toArray(Point[]::new);
    }

    public static Point reduce(Point[] points, BinaryOperator<Point> accumulator) {
        return Arrays.stream(points).reduce(accumulator).get();
    }

    public static Point[] array(int... points) {
        int length = points.length;
        if (length % 2 != 0)
            throw new IllegalArgumentException("size of points must be even");
        Point[] array = new Point[length / 2];
        for (int i = 0, k = 0; i < length; i += 2, ++k)
            array[k] = new Point(points[i], points[i + 1]);
        return array;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Point other = (Point) obj;
        return x == other.x && y == other.y;
    }

    @Override
    public int compareTo(Point o) {
        int result = Integer.compare(x, o.x);
        if (result == 0)
            result = Integer.compare(y, o.y);
        return result;
    }

    @Override
    public String toString() {
        return x + "@" + y;
    }
}
