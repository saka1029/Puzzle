package puzzle.pentomino;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Mino implements Iterable<Point> {

    /*
     * Colors - A nicer color palette for the web.
     * http://clrs.cc/
     */
    public static final Color WHITE = new Color(0xFFFFFF);
    public static final Color NAVY = new Color(0x001f3f);
    public static final Color BLUE = new Color(0x0074D9);
    public static final Color AQUA = new Color(0x7FDBFF);
    public static final Color TEAL = new Color(0x39CCCC);
    public static final Color OLIVE = new Color(0x3D9970);
    public static final Color GREEN = new Color(0x2ECC40);
    public static final Color LIME = new Color(0x01FF70);
    public static final Color YELLOW = new Color(0xFFDC00);
    public static final Color ORANGE = new Color(0xFF851B);
    public static final Color RED = new Color(0xFF4136);
    public static final Color MAROON = new Color(0x85144b);
    public static final Color FUCHSIA = new Color(0xF012BE);
    public static final Color PURPLE = new Color(0xB10DC9);
    public static final Color BLACK = new Color(0x111111);
    public static final Color GRAY = new Color(0xAAAAAA);
    public static final Color SILVER = new Color(0xDDDDDD);

    private final Point[] points;

    Mino(Point... points) {
        this.points = normalize(points);
    }

    public static Mino of(int... points) {
        return new Mino(Point.array(points));
    }

    public Point[] points() {
        return points.clone();
    }

    public Mino rotate() {
        return new Mino(Point.map(points, p -> new Point(p.y, -p.x)));
    }

    // public Mino mirror() {
    // return new Mino(Point.map(points, p -> new Point(p.x, -p.y)));
    // }

    public Mino transpose() {
        return new Mino(Point.map(points, p -> new Point(p.y, p.x)));
    }

    public Set<Mino> allDirections() {
        Set<Mino> result = new HashSet<>();
        Mino p = this;
        for (int i = 0; i < 4; ++i, p = p.rotate()) // 4回右に回転
            result.add(p);
        p = p.transpose(); // 転置
        for (int i = 0; i < 4; ++i, p = p.rotate()) // 4回右に回転
            result.add(p);
        return result;
    }

    static Point[] normalize(Point[] points) {
        Objects.requireNonNull(points, "points");
        int size = points.length;
        if (size == 0)
            throw new IllegalArgumentException("points is empty");
        Point[] array = points.clone();
        Arrays.sort(array);
        Point start = array[0];
        return Point.map(array, p -> p.subtract(start));
    }

    static final Point[] DIR = {new Point(-1, 0), new Point(1, 0),
        new Point(0, -1), new Point(0, 1)};

    /**
     * n-minoのすべてを返します。 回転・反転した同型のminoを含みます。
     *
     * @param n
     *            求めるミノのサイズを指定します。
     * @return
     */
    public static Set<Mino> allMinos(int n) {
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
                        if (isSelected(index, next))
                            continue;
                        selection[index] = next;
                        generate(index + 1);
                    }
                }
            }
        }.generate(1);
        return set;
    }

    public static List<Set<Mino>> allMinosSet(int n) {
        Set<Mino> allMinos = allMinos(n);
        List<Set<Mino>> allMinosSet = new ArrayList<>();
        for (Mino mino : allMinos)
            if (!allMinosSet.stream().anyMatch(set -> set.contains(mino)))
                allMinosSet.add(mino.allDirections());
        return allMinosSet;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(points);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        return Arrays.equals(points, ((Mino) obj).points);
    }

    static final String NL = System.lineSeparator();

    @Override
    public String toString() {
        Point min = Point.reduce(points,
            (a, b) -> new Point(Math.min(a.x, b.x), Math.min(a.y, b.y)));
        Point max = Point.reduce(points,
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

    @Override
    public Iterator<Point> iterator() {
        return Arrays.asList(points).iterator();
    }
}
