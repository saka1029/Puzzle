package test.puzzle.language;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

import puzzle.language.Expression;
import puzzle.language.Expression.DoubleFunction;

public class TestExpression {

    static final double DELTA = 5e-6;

    @Test
    public void testPlus() {
        assertEquals(3, Expression.of("1 + 2").eval(null, null), DELTA);
    }

    @Test
    public void testPlusMult() {
        assertEquals(7, Expression.of("1 + 2 * 3").eval(null, null), DELTA);
        assertEquals(9, Expression.of("(1 + 2) * 3").eval(null, null), DELTA);
    }

    @Test
    public void testVariable() {
        Map<String, Expression> variables = Map.of(
            "x", (v, f) -> 2,
            "𩸽", Expression.of("x + 3"));
        assertEquals(9, Expression.of("x^2 + 2 * x + 1").eval(variables, null), DELTA);
        assertEquals(15, Expression.of("𩸽 + 2 * 𩸽").eval(variables, null), DELTA);
    }

    @Test
    public void testFunction() {
        Map<String, DoubleFunction> functions = Map.of(
            "sin", a -> Math.sin(a[0]),
            "hypot", a -> Math.hypot(a[0], a[1]));
        assertEquals(Math.sin(-2), Expression.of("sin(-2)").eval(null, functions), DELTA);
        assertEquals(Math.hypot(5 - 9, 2 + 1), Expression.of("hypot(5 - 9, 2 + 1)").eval(null, functions), DELTA);
    }

}
