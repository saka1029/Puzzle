package test.puzzle.fractal;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.Test;

import puzzle.fractal.ImageWriter;
import puzzle.fractal.LSystem;
import puzzle.fractal.TurtleGraphics;

class TestTurtleGraphics {

    @Test
    void testStar() throws IOException {
        int n = 5;
        try (ImageWriter iw = new ImageWriter(400, 400)) {
            TurtleGraphics t = new TurtleGraphics(iw.graphics);
            t.x = 100;
            t.y = 200;
            t.direction = 0;
            t.step = 200;
            t.width = 2;
            for (int i = 0; i < n; ++i) {
                t.forward();
                t.rotate(360.0 / n * 2);
            }
            iw.writeTo(new File("data/star.png"));
        }
    }

    /**
     * Example 5: Sierpinski triangle The Sierpinski triangle drawn using an
     * L-system.
     *
     * <pre>
     *  variables : F G
     *  constants : + −
     *  start  : F−G−G
     *  rules  : (F → F−G+F+G−F), (G → GG)
     *  angle  : 120°
     * </pre>
     *
     * Here, F means "draw forward", G means "draw forward", + means "turn left
     * by angle", and − means "turn right by angle".
     */
    static void sierpinski(TurtleGraphics t, int n) {
        double angle = 120;
        new Object() {
            void F(int i) {
                if (i == 0)
                    t.forward();
                else {
                    F(i - 1);
                    t.rotate(-angle);
                    G(i - 1);
                    t.rotate(angle);
                    F(i - 1);
                    t.rotate(angle);
                    G(i - 1);
                    t.rotate(-angle);
                    F(i - 1);
                }
            }

            void G(int i) {
                if (i == 0)
                    t.forward();
                else {
                    G(i - 1);
                    G(i - 1);
                }
            }

            void start() {
                F(n);
                t.rotate(-angle);
                G(n);
                t.rotate(-angle);
                G(n);
            }
        }.start();
    }

    @Test
    void testSielpinskiTriangle() throws IOException {
        try (ImageWriter iw = new ImageWriter(4200, 4200)) {
            TurtleGraphics t = new TurtleGraphics(iw.graphics);
            t.x = 10;
            t.y = 4100;
            t.step = 16;
            sierpinski(t, 8);
            iw.writeTo(new File("data/sierpinski.png"));
        }
    }

    @Test
    /**
     * start : F−G−G rules : (F → F−G+F+G−F), (G → GG)
     */
    void testLSystemSielpinskiTriangle() throws IOException {
        LSystem ls = LSystem.of("F-G-G", "F", "F-G+F+G-F", "G", "GG");
        try (ImageWriter iw = new ImageWriter(4200, 4200)) {
            TurtleGraphics t = new TurtleGraphics(iw.graphics);
            double angle = 120;
            t.x = 10;
            t.y = 4100;
            t.step = 16;
            t.run(ls.generation(8), Map.of(
                "F", () -> t.forward(),
                "G", () -> t.forward(),
                "+", () -> t.rotate(angle),
                "-", () -> t.rotate(-angle)));
            iw.writeTo(new File("data/sierpinski.png"));
        }
    }

    @Test
    void testPolygon() throws IOException {
        int n = 13;
        try (ImageWriter iw = new ImageWriter(400, 400)) {
            TurtleGraphics t = new TurtleGraphics(iw.graphics);
            t.x = 150;
            t.y = 20;
            t.direction = 0;
            t.width = 2;
            for (int i = 0; i < n; ++i) {
                t.forward(90);
                t.rotate(360.0 / n);
            }
            iw.writeTo(new File("data/polygon.png"));
        }
    }

    /**
     * Example 7: Fractal plant
     *
     * <pre>
     * See also: Barnsley fern
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
     * @throws IOException
     */
    @Test
    public void testFractalPlant() throws IOException {
        try (ImageWriter iw = new ImageWriter(4000, 4000)) {
            TurtleGraphics t = new TurtleGraphics(iw.graphics);
            t.x = 2000;
            t.y = 4000;
            t.step = 20;
            t.direction = -90;
            t.width = 5;
            t.angle = 25;
            t.color = new Color(0x00ff00);
            new Object() {

                void X(int n) {
                    if (n == 0)
                        /* do nothing */;
                    else {
                        F(n - 1);
                        t.left();
                        t.push();
                        t.push();
                        X(n - 1);
                        t.pop();
                        t.right();
                        X(n - 1);
                        t.pop();
                        t.right();
                        F(n - 1);
                        t.push();
                        t.right();
                        F(n - 1);
                        X(n - 1);
                        t.pop();
                        t.left();
                        X(n - 1);
                    }
                }

                void F(int n) {
                    if (n == 0)
                        t.forward();
                    else {
                        F(n - 1);
                        F(n - 1);
                    }
//                    for (int i = 0, m = 1 << n; i < m; ++i)
//                        t.forward();
                }
            }.X(6);
            iw.writeTo(new File("data/fractal-plant.png"));
        }
    }

}
