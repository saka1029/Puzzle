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
        try (Writer w = new FileWriter("data/SVGTurtle/正多角形.svg")) {
            try (SVGTurtle t = new SVGTurtle(w, size, size)) {
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
    }

    @Test
    void testドラゴン曲線() throws IOException {
        int size = 800;
        try (Writer w = new FileWriter("data/SVGTurtle/ドラゴン曲線.svg")) {
            try (SVGTurtle t = new SVGTurtle(w, size, size)) {
                t.step(4);
                t.angle(90);
                var obj = new Object() {
                    void F(int n) {
                        if (n == 0)
                            t.forward();
                        else {
                            F(n - 1);
                            t.left();
                            G(n - 1);
                        }
                    }

                    void G(int n) {
                        if (n == 0)
                            t.forward();
                        else {
                            F(n - 1);
                            t.right();
                            G(n - 1);
                        }
                    }
                };
                double dir = 0;
                for (Color c : new Color[] {Color.RED, Color.GREEN, Color.BLUE, Color.LIGHT_GRAY}) {
                    t.position(size / 2, size/ 2);
                    t.direction(dir);
                    t.penColor(c);
                    obj.F(12);
                    dir += 90;
                }
            }
        }
    }

}
