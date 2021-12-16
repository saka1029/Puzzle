package test.puzzle.graphics;

import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.junit.jupiter.api.Test;

import puzzle.graphics.SVGTurtle;

class TestSVGTurtle {

    {
        new File("data/SVGTurtle").mkdirs();
    }

    /**
     * 一辺の長さが等しい正三角形から正二十角形までを描画する。
     */
    @Test
    void test正多角形() throws IOException {
        int size = 800;
        try (Writer w = new FileWriter("data/SVGTurtle/正多角形.svg");
            SVGTurtle t = new SVGTurtle(w, size, size)) {
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
        try (Writer w = new FileWriter("data/SVGTurtle/ドラゴン曲線.svg");
            SVGTurtle t = new SVGTurtle(w, size, size)) {
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
    void test対称木() throws IOException {
        int size = 700;
        try (Writer w = new FileWriter("data/SVGTurtle/対称木.svg");
            SVGTurtle t = new SVGTurtle(w, size, size)) {
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

}
