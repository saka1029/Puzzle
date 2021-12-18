package test.puzzle.graphics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import puzzle.graphics.ImageTurtle;
import puzzle.graphics.ImageWriter;
import puzzle.graphics.Turtle;

class TestImageTurtle {

    {
        new File("data/ImageTurtle").mkdirs();
    }

    /**
     * 一辺の長さが等しい正三角形から正二十角形までを描画する。
     */
    @Test
    void test正多角形() throws IOException {
        int size = 800;
        try (OutputStream os = new FileOutputStream("data/ImageTurtle/正多角形.png");
            ImageWriter iw = new ImageWriter(os, size, size);
            Turtle t = new ImageTurtle(iw)) {
            t.step(100);
            t.penColor(Color.BLUE);
            for (int i = 3; i <= 20; ++i) {
                t.position(300, 10);
                t.direction(0);
                t.angle(360.0 / i);
                for (int j = 0; j < i; ++j) {
                    t.forward();
                    t.right();
                }
            }
        }
    }

    @Test
    void testドラゴン曲線() throws IOException {
        int size = 800;
        try (OutputStream os = new FileOutputStream("data/ImageTurtle/ドラゴン曲線.png");
            ImageWriter iw = new ImageWriter(os, size, size);
            Turtle t = new ImageTurtle(iw)) {
            t.step(4);
            double angle = 90;
            var obj = new Object() {
                void F(int n, int dir) {
                    if (n == 0)
                        t.forward();
                    else {
                        F(n - 1, 1);
                        t.rotate(angle * dir);
                        F(n - 1, -1);
                    }
                }
            };
            double dir = 0;
            for (Color c : new Color[] {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW}) {
                t.position(size / 2, size / 2);
                t.direction(dir);
                t.penColor(c);
                obj.F(12, 1);
                dir += 90;
            }
        }
    }

    @Test
    void testドラゴン曲線LSystem() throws IOException {
        String start = "F";
        Map<String, String> rules = Map.of(
            "F", "F+G", "G", "F-G");
        Map<String, Consumer<Turtle>> commands = Map.of(
            "F", t -> t.forward(), "G", t -> t.forward(),
            "+", t -> t.left(), "-", t -> t.right());
        int size = 800;
        try (OutputStream os = new FileOutputStream("data/ImageTurtle/ドラゴン曲線LSystem.png");
            ImageWriter w = new ImageWriter(os, size, size);
            Turtle t = new ImageTurtle(w)) {
            t.step(4);
            t.angle(90);
            double dir = 0;
            String gen = Turtle.lsystem(start, 12, rules);
            for (Color c : new Color[] {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW}) {
                t.position(size / 2, size / 2);
                t.direction(dir);
                t.penColor(c);
                t.run(gen, commands);
                dir += 90;
            }
        }
    }

    @Test
    void test対称木() throws IOException {
        int size = 700;
        try (OutputStream os = new FileOutputStream("data/ImageTurtle/対称木.png");
            ImageWriter iw = new ImageWriter(os, size, size);
            Turtle t = new ImageTurtle(iw)) {
            t.position(size / 2, size / 2);
            t.step(20);
            t.penWidth(2);
            t.direction(-90);
            t.penColor(Color.GREEN);
            t.angle(30);
            var obj = new Object() {
                void tree(double step, int level) {
                    if (level <= 0)
                        return;
                    t.forward(step);
                    t.right();
                    tree(0.8 * step, level - 1);
                    t.left();
                    t.left();
                    tree(0.8 * step, level - 1);
                    t.right();
                    t.backward(step);
                }
            };
            obj.tree(80, 7);
        }
    }

    @Test
    void testヒルベルト曲線() throws IOException {
        int size = 130;
        try (OutputStream os = new FileOutputStream("data/ImageTurtle/ヒルベルト曲線.png");
            ImageWriter iw = new ImageWriter(os, size, size);
            Turtle t = new ImageTurtle(iw)) {
            t.position(2, 2);
            t.step(2);
            double angle = 90;
            var obj = new Object() {
                void hilbert(int rule, int dept) {
                    if (dept <= 0)
                        return;
                    t.right(angle * rule);
                    hilbert(-rule, dept - 1);
                    t.forward();
                    t.left(angle * rule);
                    hilbert(rule, dept - 1);
                    t.forward();
                    hilbert(rule, dept - 1);
                    t.left(angle * rule);
                    t.forward();
                    hilbert(-rule, dept - 1);
                    t.right(angle * rule);
                }
            };
            obj.hilbert(1, 6);
        }
    }

    @Test
    void testLSystem() throws IOException {
        Map<String, String> rules = Map.of("A", "-BF+AFA+FB-", "B", "+AF-BFB-FA+");
        assertEquals("A", Turtle.lsystem("A", 0, rules));
        assertEquals("-BF+AFA+FB-", Turtle.lsystem("A", 1, rules));
        assertEquals("-+AF-BFB-FA+F+-BF+AFA+FB-F-BF+AFA+FB-+F+AF-BFB-FA+-",
            Turtle.lsystem("A", 2, rules));
    }

    void testヒルベルト曲線LSystem() throws IOException {
        String start = "A";
        Map<String, String> rules = Map.of(
            "A", "-BF+AFA+FB-",
            "B", "+AF-BFB-FA+");
        Map<String, Consumer<Turtle>> commands = Map.of(
            "F", x -> x.forward(),
            "+", x -> x.left(),
            "-", x -> x.right());
        int size = 130;
        try (OutputStream os = new FileOutputStream("data/ImageTurtle/ヒルベルト曲線-LSystem.png");
            ImageWriter iw = new ImageWriter(os, size, size);
            Turtle t = new ImageTurtle(iw)) {
            t.position(2, 2);
            t.step(2);
            t.angle(90);
            t.lsystem(start, 6, rules, commands);
        }
    }

    /**
     * Example 7: Fractal plant
     * <pre>
     * variables : X F
     * constants : + − [ ]
     * start  : X
     * rules  : (X → F+[[X]-X]-F[-FX]+X), (F → FF)
     * angle  : 25°
     * </pre>
     *
     * Here, F means "draw forward", − means "turn right 25°", and + means "turn
     * left 25°". X does not correspond to any drawing action and is used to
     * control the evolution of the curve. The square bracket "[" corresponds to
     * saving the current values for position and angle, which are restored when
     * the corresponding "]" is executed.
     *
     * @see <a href='https://en.wikipedia.org/wiki/L-system#Example_7:_Fractal_plant'>Fractal plant</a>
     *
     * @throws IOException
     * @throws FileNotFoundException
     */
    @Test
    void testFractalPlant() throws FileNotFoundException, IOException {
        String start = "X";
        Map<String, String> rules = Map.of(
            "X", "F+[[X]-X]-F[-FX]+X",
            "F", "FF");
        Map<String, Consumer<Turtle>> commands = Map.of(
            "F", t -> t.forward(),
            "+", t -> t.left(),
            "-", t -> t.right(),
            "[", t -> t.push(),
            "]", t -> t.pop());
        int size = 800;
        try (OutputStream os = new FileOutputStream("data/ImageTurtle/FractalPlant-LSystem.png");
            ImageWriter iw = new ImageWriter(os, size, size);
            Turtle t = new ImageTurtle(iw)) {
            t.position(0, size);
            t.step(5);
            t.angle(25);
            t.penColor(Color.GREEN);
            t.direction(-65);
            t.lsystem(start, 6, rules, commands);
        }
    }

}
