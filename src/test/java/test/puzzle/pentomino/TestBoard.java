package test.puzzle.pentomino;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Set;

import org.junit.jupiter.api.Test;

import puzzle.pentomino.Board;
import puzzle.pentomino.Mino;
import puzzle.pentomino.Point;

class TestBoard {

    @Test
    void testSetUnset() {
        int[][] a = new int[3][3];
        for (int[] r : a)
            Arrays.fill(r, Board.VACANT);
        Board board = new Board(a);
        Mino mino = Mino.of(0, 0, 0, 1);
        assertEquals(true, board.placeable(Point.of(1, 1), mino));
        board.set(Point.of(1, 1), mino, 2);
        assertEquals(Board.VACANT, board.get(Point.of(1, 0)));
        assertEquals(2, board.get(Point.of(1, 1)));
        assertEquals(2, board.get(Point.of(1, 2)));
        assertEquals(false, board.placeable(Point.of(1, 1), mino));
        assertEquals(false, board.placeable(Point.of(1, 2), mino));
        assertEquals(false, board.placeable(Point.of(1, 3), mino));
        board.unset(Point.of(1, 1), mino);
        assertEquals(Board.VACANT, board.get(Point.of(1, 0)));
        assertEquals(Board.VACANT, board.get(Point.of(1, 1)));
        assertEquals(Board.VACANT, board.get(Point.of(1, 2)));
        assertEquals(true, board.placeable(Point.of(1, 1), mino));
        assertEquals(false, board.placeable(Point.of(1, 2), mino));
        assertEquals(false, board.placeable(Point.of(1, 3), mino));
    }

//    @Test
//    void testNext() {
//        int[][] a = new int[3][3];
//        for (int[] r : a)
//            Arrays.fill(r, Board.VACANT);
//        Board board = new Board(a);
//        Point p = Point.of(0, 0);
//        assertEquals(Point.of(0, 1), board.next(Point.of(0, 0)));
//        assertEquals(Point.of(0, 2), board.next(Point.of(0, 1)));
//        assertEquals(Point.of(1, 0), board.next(Point.of(0, 2)));
//        assertEquals(Point.of(1, 1), board.next(Point.of(1, 0)));
//        assertEquals(Point.of(1, 2), board.next(Point.of(1, 1)));
//        assertEquals(Point.of(2, 0), board.next(Point.of(1, 2)));
//        assertEquals(Point.of(2, 1), board.next(Point.of(2, 0)));
//        assertEquals(Point.of(2, 2), board.next(Point.of(2, 1)));
//        assertEquals(null, board.next(Point.of(2, 2)));
//    }

    @Test
    public void testToString() {
        int[][] a = new int[3][3];
        for (int[] r : a)
            Arrays.fill(r, Board.VACANT);
        Board board = new Board(a);
        String NL = System.lineSeparator();
        String s = " -1 -1 -1" + NL
            + " -1 -1 -1" + NL
            + " -1 -1 -1" + NL;
        assertEquals(s, board.toString());
    }

    static int[][] transpose(int[][] matrix) {
        int height = matrix.length, width = matrix[0].length;
        int[][] result = new int[width][height];
        for (int r = 0; r < height; ++r)
            for (int c = 0; c < width; ++c)
                result[c][r] = matrix[r][c];
        return result;
    }

    static int[][] mirror(int[][] matrix) {
        int height = matrix.length, width = matrix[0].length;
        int[][] result = new int[height][width];
        for (int r = 0; r < height; ++r)
            for (int c = 0, cc = width - 1; c < width; ++c, --cc)
                result[r][cc] = matrix[r][c];
        return result;
    }

    static void print(int[][] matrix) {
        for (int[] row : matrix)
            System.out.println(Arrays.toString(row));
        System.out.println();
    }

    @Test
    public void testAllDirectionsNoVacant() {
        int[][] m = {
            {0, 1, 2},
            {3, 4, 5},
            {6, 7, 8},
        };
        Board b = new Board(m);
        Set<Board> all = b.allDirections();
        assertEquals(8, all.size());
    }

    @Test
    public void testAllDirectionsCenterVacant() {
        int[][] m = {
            {0, 1, 2},
            {3, Board.VACANT, 5},
            {6, 7, 8},
        };
        Board b = new Board(m);
        Set<Board> all = b.allDirections();
        assertEquals(8, all.size());
    }

    @Test
    public void testAllDirections1Vacant() {
        int[][] m = {
            {0, 1, 2},
            {3, 4, Board.VACANT},
            {6, 7, 8},
        };
        Board b = new Board(m);
        Set<Board> all = b.allDirections();
        assertEquals(2, all.size());
    }

    @Test
    public void testAllDirections2Vacant() {
        int[][] m = {
            {Board.VACANT, 1, 2},
            {3, 4, 5},
            {6, 7, Board.VACANT},
        };
        Board b = new Board(m);
        Set<Board> all = b.allDirections();
        assertEquals(4, all.size());
    }


}
