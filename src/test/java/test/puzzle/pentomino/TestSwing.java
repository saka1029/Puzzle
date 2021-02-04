package test.puzzle.pentomino;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import puzzle.pentomino.Board;
import puzzle.pentomino.Mino;
import puzzle.pentomino.Point;
import puzzle.pentomino.Solver;

public class TestSwing extends JFrame {

    JPanel panel;
    Board board;
    Color[] colors;



    public TestSwing(Board board, List<Set<Mino>> minos, Color[] colors) {
        setTitle("Pentomino");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        this.board = board.clone();
        this.colors = colors;
        panel = new JPanel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                draw((Graphics2D) g);
            }
        };
        add(panel);
        new Thread(() -> {
            Solver.solve(board, minos, b -> {
                synchronized (this) {
                    this.board = b.clone();
                }
                try {
                    SwingUtilities.invokeAndWait(() -> repaint());
                    Thread.sleep(100);
                } catch (InterruptedException | InvocationTargetException e) { }
                return true;
            }, b -> {
                synchronized (this) {
                    this.board = b.clone();
                }
                try {
                    SwingUtilities.invokeAndWait(() -> repaint());
//                    Thread.sleep(20);
                } catch (InterruptedException | InvocationTargetException e) { }
            });
        }).start();
        setVisible(true);
    }

    private synchronized void draw(Graphics2D g) {
        // g.translate(5, 5);
        // g.setStroke(new BasicStroke(8));
        int h = board.height(), w = board.width();
        int size = Math.min(panel.getWidth() / h, panel.getHeight() / w);
        int oh = (panel.getHeight() - w * size) / 2, ow = (panel.getWidth() - h * size) / 2;
        g.translate(ow, oh);
        for (int r = 0; r < w; ++r)
            for (int c = 0; c < h; ++c) {
                g.setColor(colors[board.get(Point.of(c, r)) + 2]);
                g.fillRect(c * size, r * size, size, size);
            }
    }

    public static void main(String[] args) {
        int[][] matrix = new int[10][6];
        for (int[] row : matrix)
            Arrays.fill(row, Board.VACANT);
//        matrix[3][3] = Board.BLOCK;
//        matrix[3][4] = Board.BLOCK;
//        matrix[4][3] = Board.BLOCK;
//        matrix[4][4] = Board.BLOCK;
        Color[] colors = {
            Mino.WHITE,
            Mino.SILVER,
            Mino.GRAY,
            Mino.NAVY,
            Mino.BLUE,
            Mino.AQUA,
            Mino.TEAL,
            Mino.OLIVE,
            Mino.GREEN,
            Mino.LIME,
            Mino.YELLOW,
            Mino.ORANGE,
            Mino.RED,
            Mino.MAROON,
            Mino.FUCHSIA,
            Mino.PURPLE,
            Mino.BLACK,
        };

        Board board = new Board(matrix);
        new TestSwing(board, Mino.allMinosSet(5), colors);
    }

}
