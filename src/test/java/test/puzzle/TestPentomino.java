package test.puzzle;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

class TestPentomino {

    static class Point implements Comparable<Point> {

        public final int x, y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Point add(Point p) {
            return new Point(x + p.x, y + p.y);
        }

        public Point subtract(Point p) {
            return new Point(x - p.x, y - p.y);
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

    static class Mino {

        private final Point[] points;

        public Mino(Point... points) {
            this.points = normalize(points);
        }

        Point[] points() {
            return points.clone();
        }

        static Point[] map(Point[] points, Function<Point, Point> function) {
            return Arrays.stream(points).map(function).toArray(Point[]::new);
        }

        static Point reduce(Point[] points, BinaryOperator<Point> accumulator) {
            return Arrays.stream(points).reduce(accumulator).get();
        }

        Mino mirror() {
            return new Mino(map(points, p -> new Point(p.x, -p.y)));
        }

        Mino transpose() {
            return new Mino(map(points, p -> new Point(p.y, p.x)));
        }

        static Point[] normalize(Point[] points) {
            Objects.requireNonNull(points, "points");
            int size = points.length;
            if (size == 0)
                throw new IllegalArgumentException("points is empty");
            Point[] array = points.clone();
            Arrays.sort(array);
            Point start = array[0];
            return map(array, p -> p.subtract(start));
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(points);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            return Arrays.equals(points, ((Mino) obj).points);
        }

        static final String NL = String.format("%n");

        @Override
        public String toString() {
            Point min = reduce(points,
                (a, b) -> new Point(Math.min(a.x, b.x), Math.min(a.y, b.y)));
            Point max = reduce(points,
                (a, b) -> new Point(Math.max(a.x, b.x), Math.max(a.y, b.y)));
            char[][] mat = new char[max.x - min.x + 3][max.y - min.y + 3];
            for (char[] row : mat)
                Arrays.fill(row, '・');
            for (Point p : points) {
                Point r = p.subtract(min);
                mat[r.x + 1][r.y + 1] = '■';
            }
            StringBuilder sb = new StringBuilder();
            sb.append(Arrays.toString(points)).append(NL);
            for (char[] row : mat)
                sb.append(row).append(NL);
            return sb.toString();
        }

//        @Override
//        public Iterator<Point> iterator() {
//            return Arrays.asList(points).iterator();
//        }
    }

    static Point p(int x, int y) {
        return new Point(x, y);
    }

    @Test
    void test() {
        Point[] fp = {p(0, 1), p(0, 2), p(1, 1), p(1, 0), p(2, 1)};
        System.out.println(Arrays.toString(fp));
        Mino f = new Mino(fp);
        System.out.println(f);
        assertArrayEquals(
            new Point[] {p(0, 0), p(0, 1), p(1, -1), p(1, 0), p(2, 0)},
            f.points());
        System.out.println(f.mirror());
        System.out.println(f.transpose());
    }

    static final Point[] DIR = {new Point(-1, 0), new Point(1, 0),
        new Point(0, -1), new Point(0, 1)};

    static Set<Mino> minos(int n) {
        Set<Mino> set = new HashSet<>();
        Point[] selection = new Point[n];
        Point min = new Point(0, 0);
        selection[0] = min;
        new Object() {
            boolean isSelected(int index, Point p) {
                for (int i = 0; i < index; ++i)
                    if (selection[i].equals(p))
                        return true;
                return false;
            }

            void generate(int index) {
                if (index >= n) {
                    set.add(new Mino(selection));
                    return;
                }
                for (int i = 0; i < index; ++i) {
                    Point prev = selection[i];
                    for (Point dir : DIR) {
                        Point next = prev.add(dir);
//                        if (next.compareTo(min) < 0) continue;
                        if (isSelected(index, next)) continue;
                        selection[index] = next;
                        generate(index + 1);
                    }
                }
            }
        }.generate(1);
        return set;
    }

    @Test
    public void testMinos() {
        Set<Mino> minos = minos(5);
        for (Mino mino : minos)
            System.out.println(mino);
        assertEquals(63, minos.size());
    }

}
