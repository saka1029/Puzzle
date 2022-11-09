package test.puzzle.language;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

import puzzle.language.Expression;
import puzzle.language.Expression.DFunction;

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
    public void testMinus() {
        assertEquals(-5, Expression.of("1 - 2 * 3").eval(null, null), DELTA);
        assertEquals(-3, Expression.of("(1 - 2) * 3").eval(null, null), DELTA);
        assertEquals(7, Expression.of("1 - 2 * -3").eval(null, null), DELTA);
    }

    @Test
    public void testDivide() {
        assertEquals(1 / 3.0, Expression.of("1 / 3").eval(null, null), DELTA);
    }

    @Test
    public void testPowerPower() {
        assertEquals(Math.pow(2, Math.pow(3, 2)), Expression.of("2 ^ 3 ^ 2").eval(null, null), DELTA);
        assertEquals(Math.pow(Math.pow(2, 3), 2), Expression.of("(2 ^ 3) ^ 2").eval(null, null), DELTA);
    }

    @Test
    public void testVariable() {
        Map<String, Double> variables = Map.of("x", 2.0, "𩸽", 5.0);
        assertEquals(9, Expression.of("x^2 + 2 * x + 1").eval(variables, null), DELTA);
        assertEquals(15, Expression.of("𩸽 + 2 * 𩸽").eval(variables, null), DELTA);
    }

    @Test
    public void testToString() {
        assertEquals("𩸽 + 2 * 𩸽", Expression.of("  𩸽 + 2 * 𩸽  ").toString());
    }

    @Test
    public void testFunction() {
        Map<String, DFunction> functions = Map.of(
            "sin", a -> Math.sin(a[0]),
            "hypot", a -> Math.hypot(a[0], a[1]));
        assertEquals(Math.sin(-2), Expression.of("sin(-2)").eval(null, functions), DELTA);
        assertEquals(Math.hypot(5 - 9, 2 + 1), Expression.of("hypot(5 - 9, 2 + 1)").eval(null, functions), DELTA);
    }

    @Test
    public void testStandardFunctions() {
        assertEquals(Math.acos(2), Expression.of("acos(2)").eval(null, Expression.STANDARD_FUNCTIONS), DELTA);
        assertEquals(Math.asin(2), Expression.of("asin(2)").eval(null, Expression.STANDARD_FUNCTIONS), DELTA);
        assertEquals(Math.atan(2), Expression.of("atan(2)").eval(null, Expression.STANDARD_FUNCTIONS), DELTA);
        assertEquals(Math.ceil(2), Expression.of("ceil(2)").eval(null, Expression.STANDARD_FUNCTIONS), DELTA);
        assertEquals(Math.cos(2), Expression.of("cos(2)").eval(null, Expression.STANDARD_FUNCTIONS), DELTA);
        assertEquals(Math.cosh(2), Expression.of("cosh(2)").eval(null, Expression.STANDARD_FUNCTIONS), DELTA);
        assertEquals(Math.exp(2), Expression.of("exp(2)").eval(null, Expression.STANDARD_FUNCTIONS), DELTA);
        assertEquals(Math.floor(2), Expression.of("floor(2)").eval(null, Expression.STANDARD_FUNCTIONS), DELTA);
        assertEquals(Math.hypot(2, 3), Expression.of("hypot(2, 3)").eval(null, Expression.STANDARD_FUNCTIONS), DELTA);
        assertEquals(Math.log(2), Expression.of("log(2)").eval(null, Expression.STANDARD_FUNCTIONS), DELTA);
        assertEquals(Math.log10(2), Expression.of("log10(2)").eval(null, Expression.STANDARD_FUNCTIONS), DELTA);
        assertEquals(Math.max(2, 3), Expression.of("max(2,3)").eval(null, Expression.STANDARD_FUNCTIONS), DELTA);
        assertEquals(Math.min(2, 3), Expression.of("min(2,3)").eval(null, Expression.STANDARD_FUNCTIONS), DELTA);
        assertEquals(Math.pow(2, 3), Expression.of("pow(2,3)").eval(null, Expression.STANDARD_FUNCTIONS), DELTA);
//      assertEquals(RANDOM.nextDouble(), Expression.of("random()").eval(null, Expression.STANDARD_FUNCTIONS), DELTA);
        assertEquals(Math.round(2), Expression.of("round(2)").eval(null, Expression.STANDARD_FUNCTIONS), DELTA);
        assertEquals(Math.signum(2), Expression.of("signum(2)").eval(null, Expression.STANDARD_FUNCTIONS), DELTA);
        assertEquals(Math.sin(2), Expression.of("sin(2)").eval(null, Expression.STANDARD_FUNCTIONS), DELTA);
        assertEquals(Math.sinh(2), Expression.of("sinh(2)").eval(null, Expression.STANDARD_FUNCTIONS), DELTA);
        assertEquals(Math.sqrt(2), Expression.of("sqrt(2)").eval(null, Expression.STANDARD_FUNCTIONS), DELTA);
        assertEquals(Math.tan(2), Expression.of("tan(2)").eval(null, Expression.STANDARD_FUNCTIONS), DELTA);
        assertEquals(Math.tanh(2), Expression.of("tanh(2)").eval(null, Expression.STANDARD_FUNCTIONS), DELTA);
        assertEquals(Math.toRadians(2), Expression.of("toRadians(2)").eval(null, Expression.STANDARD_FUNCTIONS), DELTA);
        assertEquals(Math.toDegrees(2), Expression.of("toDegrees(2)").eval(null, Expression.STANDARD_FUNCTIONS), DELTA);
    }
    
    @Test
    public void testRandom() {
        Expression e = Expression.of("random()");
        for (int i = 0; i < 100; ++i) {
            double r = e.eval(null, Expression.STANDARD_FUNCTIONS);
//            System.out.println(r);
            assertTrue(r >= 0);
            assertTrue(r < 1);
        }
    }
}
