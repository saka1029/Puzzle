package test.puzzle;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;

import org.junit.jupiter.api.Test;

class TestLifeGame {

    /**
     * 誕生 死んでいるセルに隣接する生きたセルがちょうど3つあれば、次の世代が誕生する。 生存
     * 生きているセルに隣接する生きたセルが2つか3つならば、次の世代でも生存する。 過疎
     * 生きているセルに隣接する生きたセルが1つ以下ならば、過疎により死滅する。 過密
     * 生きているセルに隣接する生きたセルが4つ以上ならば、過密により死滅する。
     */
    static record Point(int x, int y) {
        public Point[] neighbors() {
            return new Point[] {new Point(x - 1, y - 1), new Point(x - 1, y),
                new Point(x - 1, y + 1), new Point(x, y - 1),
                new Point(x, y + 1), new Point(x + 1, y - 1),
                new Point(x + 1, y), new Point(x + 1, y + 1)};
        }

        public Point add(int x, int y) {
            return new Point(this.x + x, this.y + y);
        }

        public Point add(Point p) {
            return new Point(x + p.x, y + p.y);
        }
    }

    static class LifeGame {

        final Set<Point> lives = new HashSet<>();

        public static LifeGame of(Point... lives) {
            LifeGame g = new LifeGame();
            for (Point p : lives)
                g.lives.add(p);
            return g;
        }

        public void next() {
            Set<Point> death = new HashSet<>();
            Set<Point> birthible = new HashSet<>();
            Set<Point> birth = new HashSet<>();
            for (Point p : lives) {
                Point[] neighbors = p.neighbors();
                int count = 0;
                for (Point n : neighbors)
                    if (lives.contains(n))
                        ++count;
                    else
                        birthible.add(n);
                if (count <= 1 || count >= 4)
                    death.add(p);
            }
            for (Point p : birthible) {
                Point[] neighbors = p.neighbors();
                int count = 0;
                for (Point n : neighbors)
                    if (lives.contains(n))
                        ++count;
                if (count == 3)
                    birth.add(p);
            }
            lives.removeAll(death);
            lives.addAll(birth);
        }

    }

    /**
     * pentomino F
     *
     * <pre>
     *    0 1 2
     * 0  . * *
     * 1  * * .
     * 2  . * .
     * </pre>
     *
     */
    static final Point[] F = {new Point(0, 1), new Point(0, 2), new Point(1, 0),
        new Point(1, 1), new Point(2, 1)};

    @Test
    void test() {
        LifeGame g = LifeGame.of(F);
        int h = 20, w = 20;
        Point offset = new Point(10, 10);
        byte[][] space = new byte[h][w];
        for (int i = 0; i < 10; ++i, g.next()) {
            for (byte[] row : space)
                Arrays.fill(row, (byte) 0);
            for (Point p : g.lives) {
                Point a = p.add(offset);
                if (a.x >= 0 && a.x < h && a.y >= 0 && a.y < w)
                    space[a.x][a.y] = 1;
            }
            System.out.println(" *** " + i);
            for (byte[] row : space)
                System.out.println(Arrays.toString(row));
        }

    }

    public static void main(String[] args) {
        JFrame f = new JFrame() {
            final int CELL_SIZE = 4;
            final LifeGame game;
            {
                setTitle("Life game");
                game = LifeGame.of(F);
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                getContentPane().setPreferredSize(new Dimension(800, 600));
                pack();
                addKeyListener(new KeyListener() {
                    @Override public void keyTyped(KeyEvent e) {
                        game.next();
                        repaint();
                    }
                    @Override public void keyReleased(KeyEvent e) { }
                    @Override public void keyPressed(KeyEvent e) { }
                });
//                addMouseListener(new MouseListener() {
//                    @Override public void mouseReleased(MouseEvent e) { }
//                    @Override public void mousePressed(MouseEvent e) { }
//                    @Override public void mouseExited(MouseEvent e) { }
//                    @Override public void mouseEntered(MouseEvent e) { }
//                    @Override public void mouseClicked(MouseEvent e) {
//                        game.next();
//                        repaint();
//                    }
//                });
                setVisible(true);
            }

            @Override
            public void paint(Graphics g) {
                super.paint(g);
                System.out.println("paint point count=" + game.lives.size());
//                g.fillRect(x + left, y + top, 40, 40);
                Rectangle rect = getBounds();
                System.out.println("rect=" + rect);
                Insets insets = getInsets();
                System.out.println("insets=" + insets);
                int left = insets.left;
                int top = insets.top;
                int maxX = rect.width / CELL_SIZE / 2;
                int maxY = rect.height / CELL_SIZE / 2;
                System.out.println("maxX=" + maxX + " maxY=" + maxY);
                for (int x = -maxX; x < maxX; ++x)
                    for (int y = -maxY; y < maxY; ++y)
                        if (game.lives.contains(new Point(x, y)))
                            g.fillRect((x + maxX) * CELL_SIZE + left, (y + maxY) * CELL_SIZE + top,
                                CELL_SIZE, CELL_SIZE);
            }

        };
    }

}
