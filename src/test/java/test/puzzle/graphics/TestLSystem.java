package test.puzzle.graphics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

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
        Map<String, BiConsumer<Turtle, Integer>> commands = Map.of(
            "F", (t, i) -> t.forward(t.step() * i),
            "-", (t, i) -> t.right(t.angle() * i));
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
            t.lsystem2(start, 60, rules, commands);
        }
    }

    @Test
    void testSpiral() throws IOException {
        int n = 5;
        String start = "A";
        Map<String, String> rules = Map.of(
            "A", "A-",
            "-", "F-");
        Map<String, BiConsumer<Turtle, Integer>> commands = Map.of(
            "F", (t, i) -> t.forward(t.step() * i),
            "-", (t, i) -> t.right(t.angle() * i));
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
            t.lsystem2(start, 60, rules, commands);
        }
    }

    /**
     * 1, 1, 2, 3, 3, 4, 4, 5, 5,
     */
    @Test
    void test_1_1_2_2_3_3() throws IOException {
        int n = 4;
        String start = "A";
        Map<String, String> rules = Map.of(
            "A", "A--",
            "-", "F-");
        Map<String, BiConsumer<Turtle, Integer>> commands = Map.of(
            "F", (t, i) -> t.forward(t.step() * i),
            "-", (t, i) -> t.right(t.angle() * i));
        assertEquals("A", Turtle.lsystem(start, 0, rules));
        assertEquals("A--", Turtle.lsystem(start, 1, rules));
        assertEquals("A--F-F-", Turtle.lsystem(start, 2, rules));
        assertEquals("A--F-F-FF-FF-", Turtle.lsystem(start, 3, rules));
        assertEquals("A--F-F-FF-FF-FFF-FFF-", Turtle.lsystem(start, 4, rules));
        assertEquals("A--F-F-FF-FF-FFF-FFF-FFFF-FFFF-", Turtle.lsystem(start, 5, rules));
        for (int i = 0; i < 8; ++i)
            System.out.println(Turtle.lsystem(start, i, rules));
        int size = 400;
        try (Writer w = new FileWriter(new File(DIR, "Spiral_1_1_2_2_3_3.svg"));
            Turtle t = new SVGTurtle(w, size, size)) {
            t.position(size / 2.0, size / 2.0);
            t.step(4);
            t.angle(360.0 / n);
            t.direction(0);
            t.lsystem2(start, 52, rules, commands);
        }
    }

    /**
     * 1, 2, 4, 6, 8
     * A0 = 1
     * An = (An - 1) * 2
     * @throws IOException
     */
    @Test
    void testX2() throws IOException {
        String start = "A";
        Map<String, String> rules = Map.of(
            "A", "A-",
            "-", "FF-");
        assertEquals("A", Turtle.lsystem(start, 0, rules));
        assertEquals("A-", Turtle.lsystem(start, 1, rules));
        assertEquals("A-FF-", Turtle.lsystem(start, 2, rules));
        assertEquals("A-FF-FFFF-", Turtle.lsystem(start, 3, rules));
        assertEquals("A-FF-FFFF-FFFFFF-", Turtle.lsystem(start, 4, rules));
        assertEquals("A-FF-FFFF-FFFFFF-FFFFFFFF-", Turtle.lsystem(start, 5, rules));
    }

    /**
     * 1, 3, 7, 15
     * A0 = 1
     * An = An-1 * 2 + 1
     * @throws IOException
     */
    @Test
    void testX2plus1() throws IOException {
        String start = "A";
        Map<String, String> rules = Map.of(
            "A", "A-",
            "-", "F-",
            "F", "FF");
        for (int i = 0; i < 8; ++i) {
            String g = Turtle.lsystem(start, i, rules);
            System.out.print(g);
            System.out.println(" " + Pattern.compile("F+").matcher(g).replaceAll(m -> "" + m.group().length()));
        }
    }

}
