package test.puzzle.core;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static puzzle.core.Nonogram.BLACK;
import static puzzle.core.Nonogram.UNDEF;
import static puzzle.core.Nonogram.WHITE;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.junit.Test;

import puzzle.core.Combination;
import puzzle.core.Nonogram;
import puzzle.core.Nonogram.CandidateSet;

public class TestNonogram {

    static void printTestCaseName() {
        System.out.println(Thread.currentThread().getStackTrace()[2].getMethodName());
    }

    static byte[] bytes(byte... bs) {
        return bs;
    }

    @Test
    public void testCandidateSet() {
        printTestCaseName();
        assertEquals("*.*",
            new CandidateSet(new int[] {1, 1}, 3).toString());
        assertEquals("*..|.*.|..*",
            new CandidateSet(new int[] {1}, 3).toString());
        assertEquals("*.*.|*..*|.*.*",
            new CandidateSet(new int[] {1, 1}, 4).toString());
        assertEquals("**.*.|**..*|.**.*",
            new CandidateSet(new int[] {2, 1}, 5).toString());
        assertEquals("*****",
            new CandidateSet(new int[] {5}, 5).toString());
        assertEquals("**.**",
            new CandidateSet(new int[] {2, 2}, 5).toString());
    }

    @Test
    public void testFilter() {
        printTestCaseName();
        assertArrayEquals(bytes(UNDEF, UNDEF, UNDEF),
            new CandidateSet(new int[] {1}, 3).filter(3, -1, UNDEF));
        assertArrayEquals(bytes(UNDEF, BLACK, UNDEF),
            new CandidateSet(new int[] {2}, 3).filter(3, -1, UNDEF));
        assertArrayEquals(bytes(BLACK, BLACK, BLACK),
            new CandidateSet(new int[] {3}, 3).filter(3, -1, UNDEF));
        assertArrayEquals(bytes(UNDEF, UNDEF, UNDEF, UNDEF),
            new CandidateSet(new int[] {2}, 4).filter(4, -1, UNDEF));
        assertArrayEquals(bytes(UNDEF, BLACK, BLACK, UNDEF),
            new CandidateSet(new int[] {3}, 4).filter(4, -1, UNDEF));
    }

    @Test
    public void test3x3() {
        printTestCaseName();
//        int[][] rows = {{1}, {3}, {1}};
//        int[][] cols = {{1}, {3}, {1}};
        int[][][] rc = Nonogram.makeProblem(".*.\n***\n.*.\n", ".");
        Nonogram.solve(rc[0], rc[1]);
    }

    @Test
    public void test3x4() {
        printTestCaseName();
//        int[][] rows = {{1, 1, 1}, {1, 1}, {2, 2}};
//        int[][] cols = {{1, 1}, {2}, {1}, {1}, {3}};
        int[][][] rc = Nonogram.makeProblem(
            "*.*.*\n"
                + ".*..*\n"
                + "**.**\n",
            ".");
        Nonogram.solve(rc[0], rc[1]);
    }

    @Test
    public void test10x10() {
        printTestCaseName();
        int[][] rows = {{5}, {5}, {3}, {3, 2}, {4, 1}, {1, 3, 1}, {1, 1, 3, 1}, {5}, {2, 4}, {1, 4}};
        int[][] cols = {{2, 2}, {4, 1}, {2}, {1, 1}, {1}, {2, 4}, {2, 5}, {3, 4}, {4, 3}, {4, 5}};
        Nonogram.solve(rows, cols);
    }

    /**
     * Nonogramme, Nr. 1 https://www.janko.at/Raetsel/Nonogramme/0001.a.htm
     */
    @Test
    public void test15x15() {
        printTestCaseName();
        String answer = "x x x x x x x x x x x x x x x\r\n"
            + "x x x x x x x x x x - - - x x\r\n"
            + "x - x x x x x x x - - - - - x\r\n"
            + "x - - - - - - - - - - - x - x\r\n"
            + "x - - - - - - - - - - - - - -\r\n"
            + "x - - - - - - x x x - - - - x\r\n"
            + "x - - - - x x x x x - - - x x\r\n"
            + "x x - - x x x x x x - - x x x\r\n"
            + "x x x - x x x x x - - - x x x\r\n"
            + "x x x x - - - - - - - x x x -\r\n"
            + "x x x x x x - x - x x x - x x\r\n"
            + "x x x x x x - x - x x x x x -\r\n"
            + "x x x x x - - - - - - x x x x\r\n"
            + "x x x x x x x x x x x x x x x\r\n"
            + "x x x x x x x x x x x x x x x";
        int[][][] rc = Nonogram.makeProblem(answer, "-");
//        System.out.println("rows=" + Arrays.deepToString(rc[0]));
//        System.out.println("cols=" + Arrays.deepToString(rc[1]));
        Nonogram.solve(rc[0], rc[1]);
    }

    /**
     * Nonogramme, Nr. 50 https://www.janko.at/Raetsel/Nonogramme/0050.a.htm
     */
    @Test
    public void test25x30() {
        printTestCaseName();
        String answer = "- - - - - - - - - - - - - - x x x x x x x x x x x x - - - -\r\n"
            + "- - - - - - - - - - - - - x x x x x x x x x x x x - - - - -\r\n"
            + "- - - - - - - - - - - - x x x x x x x x x x x x - - - - - -\r\n"
            + "- - - - - - - - - - - - x x x x x x x x x x x x - - - - - -\r\n"
            + "- - - - - - - - - - - x x x x x x x x x x x x - - - - - - -\r\n"
            + "- - - - - - - - - - - x x x x x x x x x x x x - - - - - - -\r\n"
            + "- - - - - - - - - - x x x x x x x x x x x x - - - - - - - -\r\n"
            + "- - - - - - - - - - x x x x x x x x x x x x - - - - - - - -\r\n"
            + "- - - - - - - - - - x x x x x x x x x x x x - - - - - - - -\r\n"
            + "- - - - - - - - - - - x x x x x x x x x x x x - - - - - - -\r\n"
            + "- - - - - - - - - - - x x x x x x x x x x x x - - - - - - -\r\n"
            + "- - - - - - - - - - - x x x x x x x x x x x x x - - - - - -\r\n"
            + "x x - - - - - - - - - - x x x x x x x x x x x x - - - - - -\r\n"
            + "- x x - - - - - - - - - x x x x x x x x x x x x x - - - - -\r\n"
            + "- x x x - - - - - - - - - x x x x x x x x x x x x - - - - -\r\n"
            + "- - x x x - - - - - - - - x x x x x x x x x x x x x - - - -\r\n"
            + "- - x x x x x - - - - - - - - - - - x - - - - - - - - - - -\r\n"
            + "- - - x x x x x x x - - - - - - - - x - - - - - - - - - - -\r\n"
            + "- - - - x x x x x x x x x - - - - - x - - - - - - - - x x x\r\n"
            + "- - - - - x x x x x x x x x x x x x x x x x x x x x x x x -\r\n"
            + "- - - - - - x x x x x x x x x x x x x x x x x x x x x x x -\r\n"
            + "- - - - - - - x x x x x x x x x x x x x x x x x x x x x - -\r\n"
            + "- - - - - - - - x x x x x x x x x x x x x x x x x x x x - -\r\n"
            + "- - - - - - - - - x x x x x x x x x x x x x x x x x x - - -\r\n"
            + "- - - - - - - - - - x x x x x x x x x x x x x x x x - - - -";
        int[][][] rc = Nonogram.makeProblem(answer, "-");
        Nonogram.solve(rc[0], rc[1]);
    }

    /**
     * このページではPythonによるNonogramを解くプログラムを紹介しています。 ここではコンビネーションによる候補の列挙を行っています。
     * 
     * 120 行のコードでノノグラムを解く | ヘニー デ ハーダー | | データサイエンスに向けて
     * https://towardsdatascience.com/solving-nonograms-with-120-lines-of-code-a7c6e0f627e4
     * 
     * <pre>
     * 黒並びの数 : 3
     * 黒並び    : {1, 1, 1}
     * 黒並び計  : 3
     * 幅       : 7
     * 場所の数  : 5
     * 組合せ数  : 5C3 = 5*4*3 / 3*2*1 = 10
     * 5C3       : 並び       5C3の直前の数を引いたもの
     * 0 1 2     : *.*.*..    0 0 0
     * 0 1 3     : *.*..*.    0 1 2
     * 0 1 4     : *.*...*    0 1 3
     * 0 2 3     : *..*.*.    0 2 1
     * 0 2 4     : *..*..*    0 2 2
     * 0 3 4     : *...*.*    0 3 1
     * 1 2 3     : .*.*.*.    1 1 1
     * 1 2 4     : .*.*..*    1 1 2
     * 1 3 4     : .*..*.*    1 2 1
     * 2 3 4     : ..*.*.*    2 1 1
     * </pre>
     * 
     * 「5C3の直前の数を引いたもの」が各黒並びの前にある白並びの数となるようです。 これをCombination.iterable(n,
     * r)を使ってコード化したものが以下メソッドです。
     */
    static List<byte[]> candidates(int[] rans, int width) {
        int r = rans.length, sum = IntStream.of(rans).sum();
        int free = width - sum - r + 1;
        int n = r + free;
        List<byte[]> candidates = new ArrayList<>();
        for (int[] combination : Combination.iterable(n, r)) {
            byte[] candidate = new byte[width];
            int p = 0;
            for (int i = 0; i < r; ++i) {
                int whites = combination[i], blacks = rans[i];
                if (i > 0)
                    whites -= combination[i - 1];
                Arrays.fill(candidate, p, p + whites, WHITE);
                p += whites;
                Arrays.fill(candidate, p, p + blacks, BLACK);
                p += blacks;
            }
            while (p < width)
                candidate[p++] = WHITE;
            candidates.add(candidate);
        }
        return candidates;
    }

    static void assertCandidatesEquals(List<byte[]> expected, List<byte[]> actual) {
        int size = expected.size();
        if (size != actual.size())
            fail("size expected = " + size + " but actual = " + actual.size());
        for (int i = 0; i < size; ++i)
            assertArrayEquals(expected.get(i), actual.get(i));
    }

    @Test
    public void testCandidateByCombination() {
        printTestCaseName();
        assertCandidatesEquals(List.of(
            bytes(BLACK, WHITE, BLACK, WHITE, BLACK, WHITE),
            bytes(BLACK, WHITE, BLACK, WHITE, WHITE, BLACK),
            bytes(BLACK, WHITE, WHITE, BLACK, WHITE, BLACK),
            bytes(WHITE, BLACK, WHITE, BLACK, WHITE, BLACK)),
            candidates(new int[] {1, 1, 1}, 6));
        assertCandidatesEquals(List.of(
            bytes(BLACK, BLACK, BLACK, BLACK, BLACK, WHITE),
            bytes(WHITE, BLACK, BLACK, BLACK, BLACK, BLACK)),
            candidates(new int[] {5}, 6));
        assertCandidatesEquals(List.of(
            bytes(BLACK, BLACK, BLACK, BLACK, BLACK)),
            candidates(new int[] {5}, 5));
    }

    public static void main(String args[]) {
        String answer = "- x x x - x x x x x x x - x x x x - - x x x x x -\r\n"
            + "- x x - x x x x x x x x x - x - - x x x x - x x x\r\n"
            + "x - x - x x - - - x x x x - - - x x - - x x - x x\r\n"
            + "x - x x x - - x x - x x x x x x x - x x - x - x x\r\n"
            + "x x - x x x - x - - - - - - - - - - x x - x - x -\r\n"
            + "x x - x x - - - - - - - - - - - - x - - - x - x -\r\n"
            + "- x - x x x - x - - - - - - - - - x x - x x - - -\r\n"
            + "- x - - x x x x - x - - - - - x - - x x x - - - -\r\n"
            + "- - - - x x x x - x - x x - - x - - x - - - - - -\r\n"
            + "- - - - - x x x - - - x x - - - - - x - - - - - -\r\n"
            + "- - - - - x x x x - - x x - - - - x x x - - - - -\r\n"
            + "- - - - - x x x x x - - - - - x x x - x - - - - -\r\n"
            + "- - - - - x x x x x x x x x x x - - - x x - - - -\r\n"
            + "- - - - - - x - - - - - - - - - - - - - x x - - -\r\n"
            + "- - - - - - x x - - - - - - - - - - - - - x - - -\r\n"
            + "- - - - - - x x x x x x x x x x x - - - - x x - -\r\n"
            + "- - - - - - - x x x x - - x x x - - - - - - x - -\r\n"
            + "- - - - - - - x x x - - - - x - - - - - - - x - -\r\n"
            + "- - - - - - - x x x - - - - x - - - - - - - x x x\r\n"
            + "- - - - - - - - x x x - - - - - - - - - - - x - x\r\n"
            + "- - - - - - - - x x x - - - - - - - - - - x x x x\r\n"
            + "- - - - - - - - x x x x - - - - - - - - x x - - -\r\n"
            + "- - - - - - - - - x x x x x x x x x x x x - - - -\r\n"
            + "- - - - - - - - - x x x x x x x x x x x x x - - -\r\n"
            + "- - - - - - - - - x x x x x x x x x x x x x - - -";
//        String answer =
//            "- - - - - - - - - - - - x x x x x - - -\r\n"
//            + "- - - - - - - - - - x x x x x x x x - -\r\n"
//            + "- - - - - - - - - x x x x x x x - - x -\r\n"
//            + "- - - - - - - - - x x x x x x - - x x -\r\n"
//            + "- - - - - - - - x x x x - x - - x x x x\r\n"
//            + "- - x x x x x - - - x x - - x - x x x x\r\n"
//            + "- x x x x x x x x - - x - - - x x x x x\r\n"
//            + "- x x x x x x x x x x - x x x - - x x x\r\n"
//            + "x x x x x x x - - - x x x - - - - x x x\r\n"
//            + "x x x x x x x x - - x - - x x - - x x x\r\n"
//            + "x x x x x x x - x - x x - - x x x x x x\r\n"
//            + "x x x x x x - - x x - x x - - x x x x x\r\n"
//            + "x x x x x - - x x - - - x x - - x x x x\r\n"
//            + "x x x x - - x x x - - - x x x - - x x x\r\n"
//            + "x - x - - x x x x x x x x x x x - - x x\r\n"
//            + "x x - - x x x x x x x x x x x x x - - x\r\n"
//            + "x x - x - - x x x x x x x x x x x x - x\r\n"
//            + "- x x x x x - - - - - - - - - - - x - x\r\n"
//            + "- - - x x x x x x x x x x x x x x x - x\r\n"
//            + "- - - - - x x x x x x x x x x x x - x -";
//        String answer =
//            "- - - - x x x x x x x x x x x x x x x x - - - - -\r\n"
//            + "- - x x x x x x x x x x x x x x x x x x x - - - -\r\n"
//            + "- x x x x x x x x x x x x x x x x x x x x x - - -\r\n"
//            + "x x x x x x x x x x x x x x x x x x x x x x x - -\r\n"
//            + "x x x x x x x - - - - - x x x x x x x x x x x - -\r\n"
//            + "x x x x x x x - - - - - - - - x x x x x x x x - -\r\n"
//            + "x x x x x x x - - - - - - - - - x x x x x x x - -\r\n"
//            + "- x x x - x x - - - - - x x x - - - x x x x - - -\r\n"
//            + "x x x x - x - - - - x x x x x x x x x x x x - - -\r\n"
//            + "x x x x - x x x x x x x x x x x x x x - x x x - -\r\n"
//            + "- - x x - x x x - - x x x x x x - x - - x x x - -\r\n"
//            + "- - x x x x x x - - - x x x x x - x - - x x x - -\r\n"
//            + "- x x x x x x - - - - - - x x - - - - x x x x - -\r\n"
//            + "- x x x - x x - x - x - - - - - - - - x - x x - -\r\n"
//            + "- x x x - x x - - - - - - - - - - - - x - x - - -\r\n"
//            + "- - x - x x - - - - - - - - x x - - - - - x - - -\r\n"
//            + "- - x - x x - - x x x x x x - - - - - x - - - - -\r\n"
//            + "- - - - - x x - - x x x - - - - - - x x - - - - -\r\n"
//            + "- - - - - x x - - - - - - - - - x x x - - - - - -\r\n"
//            + "- - - - - - x x - - - - - - - x x x - x x - - - -\r\n"
//            + "- - - - - - x - x x - - - - x x x - - x x - - - -\r\n"
//            + "- - - - - - - - - x x x x x x x x - - x x x - - -\r\n"
//            + "- - - - - - x x x x - x x x x x - - - x x x x x x\r\n"
//            + "- - - - - - x x x x - - x x x x - - - - x x x x x\r\n"
//            + "- - - - - - x x x x - - - - x - - - - - - x x x x";
        int[][][] rc = Nonogram.makeProblem(answer, "-");
        System.out.println("rows=" + Arrays.deepToString(rc[0]));
        System.out.println("cols=" + Arrays.deepToString(rc[1]));
        new JFrame("Nonogram") {
            private static final long serialVersionUID = 1L;
            byte[][] board;
            JPanel panel = new JPanel() {
                private static final long serialVersionUID = 1L;
                @Override
                public synchronized void paint(Graphics g) {
                    super.paint(g);
                    if (board == null)
                        return;
                    int h = board.length, w = board[0].length;
                    int unit = Math.min(getHeight() / h, getWidth() / w);
                    for (int i = 0; i < h; ++i) {
                        for (int j = 0; j < w; ++j) {
                            switch (board[i][j]) {
                                case BLACK: g.setColor(Color.BLACK); break;
                                case WHITE: g.setColor(Color.WHITE); break;
                                default: g.setColor(Color.LIGHT_GRAY); break;
                            }
                            g.fillRect(j * unit, i * unit, unit, unit);
                        }
                    }
                }
            };

            {
                new Thread(() -> Nonogram.solve(rc[0], rc[1], b -> changed(b))).start();
                setSize(600, 400);
                add(panel);
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                setVisible(true);
            }

            void changed(byte[][] board) {
                synchronized (panel) {
                    this.board = Stream.of(board)
                        .map(row -> Arrays.copyOf(row, row.length))
                        .toArray(byte[][]::new);
                }
                SwingUtilities.invokeLater(() -> repaint());
                try { Thread.sleep(50); } catch (InterruptedException e) { }
            }
        };
    }
}
