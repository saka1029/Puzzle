package puzzle.pentomino;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;

public class Piece {

    // mutable!!
    public final int[][] points;

    public Piece(int[][] points) {
        this.points = normalize(points);
    }

    public static final Comparator<int[]> COMPARE_POINT;
    static {
        Comparator<int[]> t = Comparator.comparingInt(p -> p[0]);
        COMPARE_POINT = t.thenComparing(p -> p[1]);
    }

    static int[][] normalize(int[][] piece) {
        int[][] result = Arrays.stream(piece)
            .sorted(COMPARE_POINT)
            .toArray(int[][]::new);
        // 先頭を{0, 0}にする。
        int dx = result[0][0];
        int dy = result[0][1];
        for (int[] point : result) {
            point[0] -= dx;
            point[1] -= dy;
        }
        return result;
    }

    public Piece rotateRight() {
        int[][] rotated = Arrays.stream(points)
            .map(p -> new int[] {p[1], -p[0]})
            .toArray(int[][]::new);
        return new Piece(rotated);
    }

    public Piece transform() {
        int[][] rotated = Arrays.stream(points)
            .map(p -> new int[] {p[1], p[0]})
            .toArray(int[][]::new);
        return new Piece(rotated);
    }

    /**
     * 回転、反転で得られるすべてのピースを返します。
     * 最大8要素を返します。
     */
    public static Piece[] allOf(int[][] points) {
        Set<Piece> result = new LinkedHashSet<>();
        Piece p = new Piece(points);
        // 4回右に回転
        for (int i = 0; i < 4; ++i, p = p.rotateRight())
            result.add(p);
        // 転置
        p = p.transform();
        // 4回右に回転
        for (int i = 0; i < 4; ++i, p = p.rotateRight())
            result.add(p);
        return result.toArray(Piece[]::new);
    }

    private static final String NL = String.format("%n");

    @Override
    public String toString() {
        int size = points.length;
        char[][] box = new char[size][size];
        for (char[] row : box)
            Arrays.fill(row, '.');
        int dx = Integer.MAX_VALUE;
        int dy = Integer.MAX_VALUE;
        for (int[] point : points) {
            dx = Math.min(dx, point[0]);
            dy = Math.min(dy, point[1]);
        }
        for (int[] point : points)
            box[point[0] - dx][point[1] - dy] = '#';
        StringBuilder sb = new StringBuilder();
        sb.append(Arrays.deepToString(points)).append(NL);
        for (char[] row : box)
            sb.append(new String(row)).append(NL);
        return sb.toString();
    }

    /**
     * directions()でSetに格納するときに使用する。
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;
        if (obj.getClass() != getClass()) return false;
        return Arrays.deepEquals(points, ((Piece)obj).points);
    }

    /**
     * directions()でSetに格納するときに使用する。
     */
    @Override
    public int hashCode() {
        return Arrays.deepHashCode(points);
    }

}
