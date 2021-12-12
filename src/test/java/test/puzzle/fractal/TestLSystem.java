package test.puzzle.fractal;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import puzzle.fractal.ImageWriter;
import puzzle.fractal.LSystem;
import puzzle.fractal.Point;
import puzzle.fractal.TurtleGraphics;

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
            Map<String, Runnable> actions = Map.of(
                "F", () -> turtle.forward(),
                "G", () -> turtle.forward(),
                "+", () -> turtle.left(),
                "-", () -> turtle.right());
            double direction = 0;
            turtle.step = 4;
            for (Color color : colors) {
                turtle.position = start;
                turtle.direction = direction;
                turtle.color = color;
                turtle.run(gen, actions);
                direction += 90;
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
     * left 25°". X does not correspond to any drawing action and is used to
     * control the evolution of the curve. The square bracket "[" corresponds to
     * saving the current values for position and angle, which are restored when
     * the corresponding "]" is executed.
     *
     * @throws IOException
     */
    @Test
    void testFractalPlantImage() throws IOException {
        LSystem plant = LSystem.of("X", "X", "F+[[X]-X]-F[-FX]+X", "F", "FF");
        Point size = Point.of(800, 800);
        String gen = plant.generation(6);
        try (ImageWriter iw = new ImageWriter((int) size.x, (int) size.y)) {
            TurtleGraphics turtle = new TurtleGraphics(iw.graphics);
            Map<String, Runnable> actions = Map.of(
                "F", () -> turtle.forward(),
                "X", () -> {
                },
                "+", () -> turtle.left(),
                "-", () -> turtle.right(),
                "[", () -> turtle.push(),
                "]", () -> turtle.pop());
            turtle.rotation = 25;
            turtle.color = new Color(0x008000);
            turtle.position = Point.of(size.x / 2, size.y);
            turtle.direction = -90;
            turtle.run(gen, actions);
            iw.writeTo(new File("data/plant.png"));
        }
    }

    static void dragon(TurtleGraphics t, int level, int rotate) {
        if (level <= 0)
            t.forward();
        else {
            dragon(t, level - 1, 90);
            t.rotate(rotate);
            dragon(t, level - 1, -90);
        }
    }

    @Test
    void testDragonCurveRecursive() throws IOException {
        Point size = Point.of(3200, 3200);
        try (ImageWriter iw = new ImageWriter((int) size.x, (int) size.y)) {
            TurtleGraphics t = new TurtleGraphics(iw.graphics);
            t.position = size.divide(2);
            t.direction = 0;
            t.step = 5;
            dragon(t, 16, 90);
            iw.writeTo(new File("data/dragon-recursive.png"));
        }
    }

    static String F(int n) {
        if (n == 0)
            return "F";
        else
            return F(n - 1) + "+" + G(n - 1);
    }

    static String G(int n) {
        if (n == 0)
            return "G";
        else
            return F(n - 1) + "-" + G(n - 1);
    }

    @Test
    void testDragonCurveAsStaticFunction() {
        assertEquals("F", F(0));
        assertEquals("F+G", F(1));
        assertEquals("F+G+F-G", F(2));
        assertEquals("F+G+F-G+F+G-F-G", F(3));
    }

    @Test
    void testDragonCurveAsLambdaFunction() {
        interface IntStr {
            String apply(int i);
        }
        IntStr[] f = new IntStr[2];
        f[0] = n -> n == 0 ? "F" : f[0].apply(n - 1) + "+" + f[1].apply(n - 1);
        f[1] = n -> n == 0 ? "G" : f[0].apply(n - 1) + "-" + f[1].apply(n - 1);
        assertEquals("F", f[0].apply(0));
        assertEquals("F+G", f[0].apply(1));
        assertEquals("F+G+F-G", f[0].apply(2));
        assertEquals("F+G+F-G+F+G-F-G", f[0].apply(3));
    }

    @Test
    void testDragonCurveAsLambdaFunctionWithMap() {
        interface IntStr {
            String apply(int i);
        }
        Map<String, IntStr> f = new HashMap<>();
        f.put("F", n -> n == 0 ? "F" : f.get("F").apply(n - 1) + "+" + f.get("G").apply(n - 1));
        f.put("G", n -> n == 0 ? "G" : f.get("F").apply(n - 1) + "-" + f.get("G").apply(n - 1));
        assertEquals("F", f.get("F").apply(0));
        assertEquals("F+G", f.get("F").apply(1));
        assertEquals("F+G+F-G", f.get("F").apply(2));
        assertEquals("F+G+F-G+F+G-F-G", f.get("F").apply(3));
    }

    @FunctionalInterface
    interface IntStr {

        String apply(int n);

        static IntStr cond(String then, IntStr... otherwise) {
            return n -> n == 0 ? then
                : Stream.of(otherwise)
                    .map(f -> f.apply(n - 1))
                    .collect(Collectors.joining());
        }

        static IntStr constant(String string) {
            return n -> string;
        }

        static IntStr call(Map<String, IntStr> map, String functionName) {
            return n -> map.get(functionName).apply(n);
        }
    }

    @Test
    void testDragonCurveAsLambda() {
        Map<String, IntStr> map = new HashMap<>();
        map.put("F", IntStr.cond("F",
            IntStr.call(map, "F"),
            IntStr.constant("+"),
            IntStr.call(map, "G")));
        map.put("G", IntStr.cond("G",
            IntStr.call(map, "F"),
            IntStr.constant("-"),
            IntStr.call(map, "G")));
        IntStr f = map.get("F");
        assertEquals("F", f.apply(0));
        assertEquals("F+G", f.apply(1));
        assertEquals("F+G+F-G", f.apply(2));
        assertEquals("F+G+F-G+F+G-F-G", f.apply(3));
    }

    @Test
    void testDragonCurveRecursiveLambda() throws IOException {
        Point size = Point.of(3200, 3200);
        try (ImageWriter iw = new ImageWriter((int) size.x, (int) size.y)) {
            TurtleGraphics t = new TurtleGraphics(iw.graphics);
            Map<String, IntConsumer> map = new HashMap<>();
            map.put("F", n -> {
                if (n == 0)
                    t.forward(5);
                else {
                    map.get("F").accept(n);
                    t.rotate(90);
                    map.get("G").accept(n);
                } ;
            });
            map.put("G", n -> {
                if (n == 0)
                    t.forward(5);
                else {
                    map.get("F").accept(n);
                    t.rotate(-90);
                    map.get("G").accept(n);
                } ;
            });
            t.position = size.divide(2);
            dragon(t, 16, 90);
            iw.writeTo(new File("data/dragon-recursive-lambda.png"));
        }
    }

    static IntConsumer branch(IntConsumer ifZero, IntConsumer... otherwise) {
        return n -> {
            if (n == 0)
                ifZero.accept(n);
            else
                Stream.of(otherwise).forEach(f -> f.accept(n - 1));
        };
    }

    static IntConsumer call(Map<String, IntConsumer> functions, String functionName) {
        return n -> functions.get(functionName).accept(n);
    }

    @Test
    void testDragonCurveRecursiveLambdaWithStaticMethods() throws IOException {
        Point size = Point.of(3200, 3200);
        try (ImageWriter iw = new ImageWriter((int) size.x, (int) size.y)) {
            TurtleGraphics t = new TurtleGraphics(iw.graphics);
            Map<String, IntConsumer> map = new HashMap<>();
            map.put("F", branch(m -> t.forward(5),
                call(map, "F"), m -> t.rotate(90), call(map, "G")));
            map.put("G", branch(m -> t.forward(5),
                call(map, "F"), m -> t.rotate(-90), call(map, "G")));
            t.position = size.divide(2);
            t.color = Color.red;
            map.get("F").accept(16);
            iw.writeTo(new File("data/dragon-recursive-lambda-static.png"));
        }
    }

    @Test
    void testDragonCurveAsIntConsumer() {
        Map<String, IntConsumer> map = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        map.put("F", branch(n -> sb.append("F"),
            call(map, "F"), n -> sb.append("+"), call(map, "G")));
        map.put("G", branch(n -> sb.append("G"),
            call(map, "F"), n -> sb.append("-"), call(map, "G")));
        IntConsumer forg = map.get("F");
        Function<Integer, String> f = n -> {
            sb.setLength(0);
            forg.accept(n);
            return sb.toString();
        };
        assertEquals("F", f.apply(0));
        assertEquals("F+G", f.apply(1));
        assertEquals("F+G+F-G", f.apply(2));
        assertEquals("F+G+F-G+F+G-F-G", f.apply(3));
    }

}
