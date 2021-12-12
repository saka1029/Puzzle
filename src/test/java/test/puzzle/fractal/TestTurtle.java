package test.puzzle.fractal;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import puzzle.fractal.ImageWriter;
import puzzle.fractal.Turtle;

class TestTurtle {

    @Test
    void testSquare() throws IOException {
        try (ImageWriter iw = new ImageWriter(200, 200)) {
            Turtle t = new Turtle(iw.graphics);
            t.run("50x50y3w4(100f90r)");
            iw.writeTo(new File("data/square.png"));
        }
    }

    @Test
    void testStar() throws IOException {
        try (ImageWriter iw = new ImageWriter(200, 200)) {
            Turtle t = new Turtle(iw.graphics);
            t.run("50x 50y 30a 2w #ff0000#c 5(100f144r)");
            iw.writeTo(new File("data/star.png"));
        }
    }

}
