package test.puzzle.fractal;

import java.awt.Color;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.junit.jupiter.api.Test;

import puzzle.fractal.TurtleSVG;

class TestTurtleSVG {

    @Test
    void testSquare() throws IOException {
        try (Writer w = new FileWriter("data/svg-square.svg");
            TurtleSVG t = new TurtleSVG(400, 400, w)) {
            t.x = 50;
            t.y = 50;
            t.step = 150;
            t.angle = 90;
            for (int i = 0; i < 4; ++i) {
                t.forward();
                t.rotate();
            }
        }
    }

    @Test
    void testDragon() throws IOException {
        int size = 1200;
        try (Writer w = new FileWriter("data/svg-dragon.svg");
            TurtleSVG t = new TurtleSVG(size, size, w)) {
            t.x = size / 2;
            t.y = size / 2;
            t.step = 2;
            t.penWidth(1);
            t.angle = 90;
            new Object() {
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

                void draw(int n) {
                    int dir = 0;
                    for (Color c : new Color[] {Color.RED, Color.GREEN, Color.BLUE, Color.BLACK}) {
                        t.x = t.y = size / 2;
                        t.direction = dir;
                        t.penColor(c);
                        F(n);
                        dir += 90;
                    }

                }
            }.draw(16);
        }
    }

    @Test
    public void testFractalPlant() throws IOException {
        int size = 3400;
        try (Writer w = new FileWriter("data/svg-fractal-plant.svg");
            TurtleSVG t = new TurtleSVG(size, size, w)) {
            t.x = size / 2;
            t.y = size;
            t.step = 20;
            t.direction = -90;
            t.penWidth(5);
            t.angle = 25;
            t.penColor(new Color(0x00ff00));
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
                }
            }.X(6);
        }
    }
}
