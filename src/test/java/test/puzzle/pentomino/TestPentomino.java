package test.puzzle.pentomino;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import puzzle.pentomino.Board;
import puzzle.pentomino.Mino;
import puzzle.pentomino.Point;
import puzzle.pentomino.Solver;

class TestPentomino {

    @Test
    void test10x6() {
        int[][] matrix = new int[10][6];
        for (int[] row : matrix)
            Arrays.fill(row, Board.VACANT);
        Board board = new Board(matrix);
        List<Set<Mino>> minos = Mino.allMinosSet(5);
        assertEquals(12, minos.size());
        Set<Board> all = new HashSet<>();
        Set<Board> uniq = new HashSet<>();
        Solver.solve(board, minos, b -> {
            if (all.contains(b)) return;
            Board n = new Board(b.board);
            uniq.add(n);
            all.addAll(n.allDirections());
        });
        assertEquals(2339, uniq.size());
    }

    @Test
    void test12x5() {
        int[][] matrix = new int[12][5];
        for (int[] row : matrix)
            Arrays.fill(row, Board.VACANT);
        Board board = new Board(matrix);
        List<Set<Mino>> minos = Mino.allMinosSet(5);
        assertEquals(12, minos.size());
        Set<Board> all = new HashSet<>();
        Set<Board> uniq = new HashSet<>();
        Solver.solve(board, minos, b -> {
            if (all.contains(b)) return;
            Board n = new Board(b.board);
            uniq.add(n);
            all.addAll(n.allDirections());
        });
        assertEquals(1010, uniq.size());
    }

    @Test
    void test20x3() {
        int[][] matrix = new int[20][3];
        for (int[] row : matrix)
            Arrays.fill(row, Board.VACANT);
        Board board = new Board(matrix);
        List<Set<Mino>> minos = Mino.allMinosSet(5);
        assertEquals(12, minos.size());
        Set<Board> all = new HashSet<>();
        Set<Board> uniq = new HashSet<>();
        Solver.solve(board, minos, b -> {
            if (all.contains(b)) return;
            Board n = new Board(b.board);
            uniq.add(n);
            all.addAll(n.allDirections());
        });
        assertEquals(2, uniq.size());
    }

    @Test
    void test15x4() {
        int[][] matrix = new int[15][4];
        for (int[] row : matrix)
            Arrays.fill(row, Board.VACANT);
        Board board = new Board(matrix);
        List<Set<Mino>> minos = Mino.allMinosSet(5);
        assertEquals(12, minos.size());
        Set<Board> all = new HashSet<>();
        Set<Board> uniq = new HashSet<>();
        Solver.solve(board, minos, b -> {
            if (all.contains(b)) return;
            Board n = new Board(b.board);
            uniq.add(n);
            all.addAll(n.allDirections());
        });
        assertEquals(368, uniq.size());
    }

    @Test
    void test8x8VacantCenter() {
        int[][] matrix = new int[8][8];
        for (int[] row : matrix)
            Arrays.fill(row, Board.VACANT);
        matrix[3][3] = Board.BLOCK;
        matrix[3][4] = Board.BLOCK;
        matrix[4][3] = Board.BLOCK;
        matrix[4][4] = Board.BLOCK;
        Board board = new Board(matrix);
        List<Set<Mino>> minos = Mino.allMinosSet(5);
        assertEquals(12, minos.size());
        Set<Board> all = new HashSet<>();
        Set<Board> uniq = new HashSet<>();
        Solver.solve(board, minos, b -> {
            if (all.contains(b)) return;
            Board n = new Board(b.board);
            uniq.add(n);
            all.addAll(n.allDirections());
        });
        assertEquals(65, uniq.size());
    }

    interface Trans {
        Point trans(Point p, Point s);
        default Trans compose(Trans t) {
            return (p, s) -> t.trans(trans(p, s), s);
        }
    }

    Trans 上下反転 = (p, s) -> Point.of(s.x - p.x - 1, p.y);
    Trans 左右反転 = (p, s) -> Point.of(p.x, s.y - p.y - 1);
    Trans 転置 = (p, s) -> Point.of(p.y, p.x);

    static String[][] m = { {"A", "B"}, {"C", "D"} };

    static String[][] trans(String[][] m, Trans t) {
        Point s = Point.of(m.length, m[0].length);
        String[][] r = new String[s.x][s.y];
        for (int x = 0; x < s.x; ++x)
            for (int y = 0; y < s.y; ++y) {
                Point q = t.trans(Point.of(x, y), s);
                r[q.x][q.y] = m[x][y];
            }
        return r;
    }

    static void print(String[][] m) {
        System.out.println(Arrays.deepToString(m));
    }

    @Test
    public void testTrans() {
        print(m);
        for (int i = 0; i < 4; ++i) {
            print(m = trans(m, 左右反転));
            print(m = trans(m, 転置));
        }

//        print(trans(m, 左右反転));
//        print(trans(m, 上下反転));
//        print(trans(m, 左右反転.compose(上下反転)));
    }

}
