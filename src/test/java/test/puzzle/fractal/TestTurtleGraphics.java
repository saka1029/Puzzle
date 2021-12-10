package test.puzzle.fractal;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import puzzle.fractal.ImageWriter;
import puzzle.fractal.Point;
import puzzle.fractal.TurtleGraphics;

class TestTurtleGraphics {

    @Test
    void testStar() throws IOException {
        int n = 5;
        try (ImageWriter iw = new ImageWriter(400, 400)) {
            TurtleGraphics t = new TurtleGraphics(iw.graphics);
            t.position = Point.of(100, 200);
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

    static void sierpinski(TurtleGraphics t, double length, int n) {
        if (n == 0) {
            for (int i = 0; i < 3; ++i) {
                t.forward(length);
                t.left();
                t.left();
            }
        } else {
            double half = length / 2;
            sierpinski(t, half, n - 1);
            t.forward(half);
            sierpinski(t, half, n - 1);
            t.forward(-half);
            t.left();
            t.forward(half);
            t.right();
            sierpinski(t, half, n - 1);
            t.left();
            t.forward(-half);
            t.right();
        }
    }

    @Test
    void testSielpinskiTriangle() throws IOException {
        try (ImageWriter iw = new ImageWriter(4200, 4200)) {
            TurtleGraphics t = new TurtleGraphics(iw.graphics);
            t.position = Point.of(10, 200);
            t.rotation = 60;
            sierpinski(t, 4096, 8);
            iw.writeTo(new File("data/sierpinski.png"));
        }
    }

}
