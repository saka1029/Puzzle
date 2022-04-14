package test.puzzle.parsers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import puzzle.parsers.Calculator;

public class TestCalculator {

    Map<String, Double> context = new HashMap<>();

    double calculate(String s) {
        return Calculator.calculate(context, s);
    }

    @Test
    public void test() {
        assertEquals(14, calculate("1 * 2 + 3 * 4"), 1e-8);
        assertEquals(21, calculate("(1 + 2) * (3 + 4)"), 1e-8);
        assertEquals(-21, calculate("(1 + 2) * -(3 + 4)"), 1e-8);
        assertEquals(1.0E5 / 3, calculate("1e5 / 3"), 1e-8);
        assertEquals(0.01234E-3, calculate(".01234E-3"), 1e-8);
        assertEquals(1, calculate("sin(π / 2)"), 1e-8);
        assertEquals(10000, calculate("97^2+97*6+9"), 1e-8);
        assertEquals(720, calculate("factorial(6)"), 1e-8);
        assertEquals(9.332621544394415E157, calculate("factorial(100)"), 1e-8);
        assertEquals(1.0, calculate("tan(pi / 4)"), 1e-8);
        assertEquals(Math.PI / 4, calculate("atan(1)"), 1e-8);
        assertEquals(Math.PI / 4, calculate("atan2(100, 100)"), 1e-8);
    }

    /**
     * power operator is right associative.
     */
    @Test
    public void testPower() {
        assertEquals(256, calculate("2 ** 2 ** 3"), 1e-8);
        assertEquals(256, calculate("2 ^ 2 ** 3"), 1e-8);
        assertEquals(256, calculate("2 ^ (2 ** 3)"), 1e-8);
        assertEquals(64, calculate("(2 ^ 2) ** 3"), 1e-8);
        assertEquals(16, calculate("2 * 2 ** 3"), 1e-8);
        assertEquals(12, calculate("2 ** 2 * 3"), 1e-8);
        assertEquals(4, calculate("2 ** 2"), 1e-8);
    }

    @Test
    public void testSyntaxError() {
        try {
            calculate("2 + 3) ");
            fail();
        } catch (RuntimeException e) {
            assertEquals("extra string ') '", e.getMessage());
        }
        try {
            calculate("(2 + 3 ");
            fail();
        } catch (RuntimeException e) {
            assertEquals("')' expected", e.getMessage());
        }
        try {
            calculate("2 + X");
            fail();
        } catch (RuntimeException e) {
            assertEquals("unknown constant 'X'", e.getMessage());
        }
        try {
            calculate("2 + X()");
            fail();
        } catch (RuntimeException e) {
            assertEquals("unknown function 'X'", e.getMessage());
        }
        try {
            calculate("2 + atan2(1)");
            fail();
        } catch (RuntimeException e) {
            assertEquals("'atan2' takes 2 arguments but 1", e.getMessage());
        }
    }

    /**
     * 1996 平成8年　大阪府 - YouTube
     * https://www.youtube.com/watch?v=HhRVwem1e3Y
     */
    @Test
    public void testYouTube() {
        String[] o = {"+", "-", "*", "/"};
        int length = o.length;
        for (int a = 0; a < length; ++a)
        for (int b = 0; b < length; ++b)
        for (int c = 0; c < length; ++c) {
            String e = "1" + o[a] + "9" + o[b] + "9" + o[c] + "6";
            double x = calculate(e);
//            System.out.println(e + " = " + x);
//            System.out.println("√(" + e + ") = " + Math.sqrt(x));
            if (x == 64)
                assertEquals("1+9+9*6", e);
        }
    }

}
