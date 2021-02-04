package puzzle.pentomino;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Board implements Cloneable {

    public static final int VACANT = -1;
    public static final int BLOCK = -2;

    public final int[][] board;

    public Board(int[][] board) {
        this.board = board;
    }

    public int height() {
        return board.length;
    }

    public int width() {
        return board[0].length;
    }

    public boolean placeable(Point p) {
        return p.x >= 0 && p.x < board.length
            && p.y >= 0 && p.y < board[p.x].length
            && board[p.x][p.y] == VACANT;
    }

    public boolean placeable(Point p, Mino mino) {
        for (Point m : mino)
            if (!placeable(p.add(m)))
                return false;
        return true;
    }

    public Point next(Point p) {
        for (int x = p.x, sy = p.y; x < board.length; ++x, sy = 0)
            for (int y = sy; y < board[x].length; ++y) {
                Point next = Point.of(x, y);
                if (board[x][y] == VACANT)
                    return next;
            }
        return null;
    }

    public int get(Point p) {
        return board[p.x][p.y];
    }

    public boolean set(Point p, Mino mino, int value) {
        for (Point e : mino) {
            Point a = e.add(p);
            board[a.x][a.y] = value;
        }
        return true;
    }

    public void unset(Point p, Mino mino) {
        for (Point e : mino) {
            Point a = e.add(p);
            board[a.x][a.y] = VACANT;
        }
    }

    public Board transpose() {
        int width = width(), height = height();
        int[][] result = new int[width][height];
        for (int r = 0; r < height; ++r)
            for (int c = 0; c < width; ++c)
                result[c][r] = board[r][c];
        return new Board(result);
    }

    public Board mirror() {
        int width = width(), height = height();
        int[][] result = new int[height][width];
        for (int r = 0; r < height; ++r)
            for (int c = 0, cc = width - 1; c < width; ++c, --cc)
                result[r][cc] = board[r][c];
        return new Board(result);
    }

    boolean isSameShape(Board other) {
        int height = height(), width = width();
        if (other.height() != height || other.width() != width)
            return false;
        int[][] b = other.board;
        for (int r = 0; r < height; ++r)
            for (int c = 0; c < width; ++c)
                if ((board[r][c] == VACANT) != (b[r][c] ==VACANT))
                    return false;
        return true;
    }

    public Set<Board> allDirections() {
        Set<Board> result = new HashSet<>();
        Board b = this;
        for (int i = 0; i < 4; ++i) {
            if (isSameShape(b))
                result.add(b);
            b = b.mirror();
            if (isSameShape(b))
                result.add(b);
            b = b.transpose();
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int[] row : board) {
            for (int e : row)
                sb.append(String.format("%3d", e));
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    @Override
    public Board clone() {
        return new Board(Arrays.stream(board)
            .map(row -> row.clone())
            .toArray(int[][]::new));
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        return Arrays.deepEquals(board, ((Board)obj).board);
    }
}
