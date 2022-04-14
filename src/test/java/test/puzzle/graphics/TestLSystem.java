package test.puzzle.graphics;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import org.junit.Test;

import puzzle.graphics.SVGTurtle;
import puzzle.graphics.Turtle;

public class TestLSystem {

    static final File DIR = new File("data/LSystem");
    static {
        DIR.mkdirs();
    }

    @Test
    public void testBinaryTree() throws IOException {
        String start = "0";
        Map<String, String> rules = Map.of(
            "1", "11",
            "0", "1[0]0");
        Map<String, BiConsumer<Turtle, Integer>> commands = Map.of(
            "0", (t, i) -> t.forward(t.step() * i),
            "1", (t, i) -> t.forward(t.step() * i),
            "[", (t, i) -> { t.push(); t.left(); },
            "]", (t, i) -> { t.pop(); t.right(); });
        assertEquals("0", Turtle.lsystem(start, 0, rules));
        assertEquals("1[0]0", Turtle.lsystem(start, 1, rules));
        assertEquals("11[1[0]0]1[0]0", Turtle.lsystem(start, 2, rules));
        assertEquals("1111[11[1[0]0]1[0]0]11[1[0]0]1[0]0", Turtle.lsystem(start, 3, rules));
        assertEquals("11111111[1111[11[1[0]0]1[0]0]11[1[0]0]1[0]0]1111[11[1[0]0]1[0]0]11[1[0]0]1[0]0", Turtle.lsystem(start, 4, rules));
        int size = 800;
        try (Writer w = new FileWriter(new File(DIR, "BinaryTree.svg"));
            Turtle t = new SVGTurtle(w, size, size)) {
            t.position(size / 2.0, size - 2);
            t.step(4);
            t.angle(45);
            t.direction(-90);
            t.lsystem2(start, 7, rules, commands);
        }
    }

    @Test
    public void testSpiralReverse() throws IOException {
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
    public void testSpiral() throws IOException {
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
    public void test_1_1_2_2_3_3() throws IOException {
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

    static final Pattern FS = Pattern.compile("F+");
    static String seq(String start, int n, String... rules) {
        Map<String, String> map = new LinkedHashMap<>();
        for (int i = 0, m = rules.length; i < m; i += 2)
            map.put(rules[i], rules[i + 1]);
        return FS.matcher(Turtle.lsystem(start, n, map)).replaceAll(m -> "" + m.group().length());
    }

    @Test
    public void testNumbers() throws IOException {
        // An = n
        assertEquals("A6", seq("A", 6, "A", "AF"));
        // An = 2 * n
        assertEquals("A12", seq("A", 6, "A", "AFF"));
        // An = 3 * n
        assertEquals("A18", seq("A", 6, "A", "AFFF"));
        // An = 2 ^ n - 1
        assertEquals("A3", seq("A", 2, "A", "AF", "F", "FF"));
        assertEquals("A63", seq("A", 6, "A", "AF", "F", "FF"));
        // An = 2 ^ (n + 1)
        assertEquals("A6", seq("A", 2, "A", "AFF", "F", "FF"));
        assertEquals("A126", seq("A", 6, "A", "AFF", "F", "FF"));
        //
        assertEquals("B1", seq("A", 1, "A", "BF", "B", "AFF", "F", "FF"));
        assertEquals("A4", seq("A", 2, "A", "BF", "B", "AFF", "F", "FF"));
        assertEquals("B9", seq("A", 3, "A", "BF", "B", "AFF", "F", "FF"));
        assertEquals("A20", seq("A", 4, "A", "BF", "B", "AFF", "F", "FF"));
        assertEquals("B41", seq("A", 5, "A", "BF", "B", "AFF", "F", "FF"));
        assertEquals("A84", seq("A", 6, "A", "BF", "B", "AFF", "F", "FF"));
        assertEquals("B169", seq("A", 7, "A", "BF", "B", "AFF", "F", "FF"));
    }

    @Test
    public void testSequence() throws IOException {
        // An = n
        assertEquals("A-1-2-3-4-5-", seq("A", 6, "A", "A-", "-", "F-"));
        // An = 2 * n
        assertEquals("A-2-4-6-8-10-", seq("A", 6, "A", "A-", "-", "FF-"));
        // An = 3 * n
        assertEquals("A-3-6-9-12-15-", seq("A", 6, "A", "A-", "-", "FFF-"));
        // An = (1 / 4) * (2 * n + (-1)^(n + 1) + 1)
        assertEquals("A--1-1-2-2-3-3-4-4-5-5-", seq("A", 6, "A", "A--", "-", "F-"));
        // An = 8 - n
        assertEquals("-7-6-5-4-3-2-1-A", seq("A", 8, "A", "-A", "-", "-F"));
        //
        assertEquals("5-4-3-2-1--A-1-2-3-4-5-", seq("A", 6, "A", "-A-", "-", "F-"));
        // An = 2^n -1
        assertEquals("A-1-3-7-15-31-63-127-", seq("A", 8, "A", "A-", "-", "F-", "F", "FF"));
        // An = (1 / 2) * (3^n -1)
        assertEquals("A-1-4-13-40-121-364-1093-", seq("A", 8, "A", "A-", "-", "F-", "F", "FFF"));
    }

    /*
     * 紗綾形(さやがた)
     */
    @Test
    public void test紗綾形() throws IOException {
        String start = "S";
        Map<String, String> rules = Map.of(
            "S", "A-B-B+A+C-C-A-B-B+A+C-C-",
            "A", "FFFFFFF",
            "B", "F-F+F",
            "C", "F+F-F"
        );
        Map<String, Consumer<Turtle>> commands = Map.of(
            "F", t -> t.forward(),
            "+", t -> t.left(),
            "-", t -> t.right());
        int size = 400;
        try (Writer w = new FileWriter(new File(DIR, "紗綾.svg"));
            Turtle t = new SVGTurtle(w, size, size)) {
            t.position(10, 10);
            t.step(40);
            t.angle(90);
            t.direction(0);
            t.lsystem(start, 60, rules, commands);
        }

    }

}
