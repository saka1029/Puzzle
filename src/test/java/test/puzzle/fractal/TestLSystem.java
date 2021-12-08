package test.puzzle.fractal;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.junit.jupiter.api.Test;

import puzzle.fractal.ImageWriter;
import puzzle.fractal.LSystem;

class TestLSystem {

    /**
     * 例1：藻類 L-system 誕生の契機となった、藻類の成長を記述する例。
     *
     * <pre>
     * V ： A, B
     * S ： なし
     * ω ： A
     * P ： (A → AB), (B → A)
     *
     * 順次計算してゆくと、文字列は以下のように成長する。
     *
     * n = 0 ： A
     * n = 1 ： AB
     * n = 2 ： ABA
     * n = 3 ： ABAAB
     * n = 4 ： ABAABABA
     * </pre>
     */
    @Test
    void test藻類() {
        LSystem 藻類 = LSystem.of("A", "A", "AB", "B", "A");
        Iterator<String> it = 藻類.stream(5).iterator();
        assertEquals("A", it.next());
        assertEquals("AB", it.next());
        assertEquals("ABA", it.next());
        assertEquals("ABAAB", it.next());
        assertEquals("ABAABABA", it.next());
        assertFalse(it.hasNext());
    }

    @Test
    void testDragonCurve() {
        LSystem dragon = LSystem.of("F", "F", "F+G", "G", "F-G");
        assertEquals("F", dragon.generation(0));
        assertEquals("F+G", dragon.generation(1));
        assertEquals("F+G+F-G", dragon.generation(2));
        Iterator<String> it = dragon.stream(5).iterator();
        assertEquals("F", it.next());
        assertEquals("F+G", it.next());
        assertEquals("F+G+F-G", it.next());
        assertEquals("F+G+F-G+F+G-F-G", it.next());
        assertEquals("F+G+F-G+F+G-F-G+F+G+F-G-F+G-F-G", it.next());
        assertFalse(it.hasNext());
    }

    @Test
    void testDragonCurveImage() throws IOException {
        LSystem dragon = LSystem.of("F", "F", "F+G", "G", "F-G");
        Point size = Point.of(3200, 3200);
        Point start = size.divide(2);
        String gen = dragon.generation(16);
        Color[] colors = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW};
        try (ImageWriter iw = new ImageWriter((int) size.x, (int) size.y)) {
            TurtleGraphics turtle = new TurtleGraphics(iw.graphics);
            Map<String, Runnable> actions = Map.of("F", () -> turtle.forward(), "G", () -> turtle.forward(), "+",
                () -> turtle.rotate(90), "-", () -> turtle.rotate(-90));
            Point unit = Point.of(0, 4);
            for (Color color : colors) {
                turtle.go(start);
                turtle.unit(unit);
                turtle.color(color);
                turtle.run(gen, actions);
                unit = unit.rotate(90);
            }
            iw.writeTo(new File("data/dragon.png"));
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
     * left 25°". X does not correspond to any drawing action and is used to control
     * the evolution of the curve. The square bracket "[" corresponds to saving the
     * current values for position and angle, which are restored when the
     * corresponding "]" is executed.
     * 
     * @throws IOException
     */
    @Test
    void testFractalPlant() throws IOException {
        LSystem plant = LSystem.of("X", "X", "F+[[X]-X]-F[-FX]+X", "F", "FF");
        int size = 800;
        Point area = Point.of(size, size);
        String gen = plant.generation(6);
        System.out.println(gen);
        try (ImageWriter iw = new ImageWriter((int) area.x, (int) area.y)) {
            TurtleGraphics turtle = new TurtleGraphics(iw.graphics);
            Deque<Point> sc = new LinkedList<>();
            Deque<Point> su = new LinkedList<>();
            Map<String, Runnable> actions = Map.of(
                "F", () -> turtle.forward(),
                "X", () -> {},
                "+", () -> turtle.rotate(25),
                "-", () -> turtle.rotate(-25),
                "[", () -> { sc.push(turtle.current); su.push(turtle.unit); },
                "]", () -> { turtle.go(sc.pop()); turtle.unit(su.pop()); });
            turtle.color(new Color(0x008000));
            turtle.go(Point.of(size / 2, size));
            turtle.unit(Point.of(0, -4));
            turtle.run(gen, actions);
            iw.writeTo(new File("data/plant.png"));
        }

    }
}
