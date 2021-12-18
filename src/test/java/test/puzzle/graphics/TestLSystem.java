package test.puzzle.graphics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import puzzle.graphics.SVGTurtle;
import puzzle.graphics.Turtle;

class TestLSystem {
    
    static final File DIR = new File("data/LSystem");
    static {
        DIR.mkdirs();
    }

    @Test
    void testSpiralReverse() throws IOException {
        int n = 5;
        String start = "A";
        Map<String, String> rules = Map.of(
            "A", "-A",
            "-", "F-");
        Map<String, Consumer<Turtle>> commands = Map.of(
            "F", t -> t.forward(),
            "-", t -> t.right());
        assertEquals("A", Turtle.lsystem(start, 0, rules));
        assertEquals("-A", Turtle.lsystem(start, 1, rules));
        assertEquals("F--A", Turtle.lsystem(start, 2, rules));
        assertEquals("FF-F--A", Turtle.lsystem(start, 3, rules));
        assertEquals("FFF-FF-F--A", Turtle.lsystem(start, 4, rules));
        assertEquals("FFFF-FFF-FF-F--A", Turtle.lsystem(start, 5, rules));
        int size = 400;
        try (Writer w = new FileWriter(new File(DIR, "SpiralReverse.svg"));
            Turtle t = new SVGTurtle(w, size, size)) {
            t.position(60, 2);
            t.step(4);
            t.angle(360.0 / n);
            t.direction(0);
            t.lsystem(start, 60, rules, commands);
        }
    }

    @Test
    void testSpiral() throws IOException {
        int n = 5;
        String start = "A";
        Map<String, String> rules = Map.of(
            "A", "A-",
            "-", "F-");
        Map<String, Consumer<Turtle>> commands = Map.of(
            "F", t -> t.forward(),
            "-", t -> t.right());
        assertEquals("A", Turtle.lsystem(start, 0, rules));
        assertEquals("A-", Turtle.lsystem(start, 1, rules));
        assertEquals("A-F-", Turtle.lsystem(start, 2, rules));
        assertEquals("A-F-FF-", Turtle.lsystem(start, 3, rules));
        assertEquals("A-F-FF-FFF-", Turtle.lsystem(start, 4, rules));
        assertEquals("A-F-FF-FFF-FFFF-", Turtle.lsystem(start, 5, rules));
        int size = 400;
        try (Writer w = new FileWriter(new File(DIR, "Spiral.svg"));
            Turtle t = new SVGTurtle(w, size, size)) {
            t.position(size / 2.0, size / 2.0);
            t.step(4);
            t.angle(360.0 / n);
            t.direction(0);
            t.lsystem(start, 60, rules, commands);
        }
    }


}
