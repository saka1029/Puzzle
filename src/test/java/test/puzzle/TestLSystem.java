package test.puzzle;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import puzzle.LSystem;

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
        LSystem 藻類 = LSystem.of(
            "A",
            "A", "AB",
            "B", "A");
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
        LSystem dragon = LSystem.of(
            "F",
            "F", "F+G",
            "G", "F-G");
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
        LSystem dragon = LSystem.of(
            "F",
            "F", "F+G",
            "G", "F-G");
        int width = 3000, height = 3000;
        int[] cur = {width / 2, height / 2};
        int[] dir = {0, 4};
        String gen = dragon.generation(16);
        File file = new File("data/dragon.png");
        Consumer<Graphics2D> line = g -> {
            int x = cur[0], y = cur[1];
            int xx = cur[0] += dir[0], yy = cur[1] += dir[1];
            g.drawLine(x, y, xx, yy);
        };
        LSystem.image(gen, width, height, file,
            g -> {},
            Map.of("F", line, "G", line,
                "+", g -> {
                    int x = dir[1], y = -dir[0];
                    dir[0] = x; dir[1] = y;
                },
                "-", g -> {
                    int x = -dir[1], y = dir[0];
                    dir[0] = x; dir[1] = y;
                }));
    }
}
