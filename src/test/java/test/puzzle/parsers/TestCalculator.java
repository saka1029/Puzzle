package test.puzzle.parsers;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import puzzle.parsers.Calculator;

class TestCalculator {

    Map<String, Double> context = new HashMap<>();

    double calculate(String s) {
        return Calculator.calculate(context, s);
    }

    @Test
    void test() {
        assertEquals(14, calculate("1 * 2 + 3 * 4"));
        assertEquals(21, calculate("(1 + 2) * (3 + 4)"));
        assertEquals(-21, calculate("(1 + 2) * -(3 + 4)"));
        assertEquals(1.0E5 / 3, calculate("1e5 / 3"));
        assertEquals(0.01234E-3, calculate(".01234E-3"));
        assertEquals(1, calculate("sin(π / 2)"));
        assertEquals(10000, calculate("97^2+97*6+9"));
        assertEquals(720, calculate("factorial(6)"));
        assertEquals(9.332621544394415E157, calculate("factorial(100)"));
        assertEquals(1.0, calculate("tan(pi / 4)"), 1e-8);
        assertEquals(Math.PI / 4, calculate("atan(1)"));
        assertEquals(Math.PI / 4, calculate("atan2(100, 100)"));
    }

    /**
     * power operator is right associative.
     */
    @Test
    void testPower() {
        assertEquals(256, calculate("2 ** 2 ** 3"));
        assertEquals(256, calculate("2 ^ 2 ** 3"));
        assertEquals(256, calculate("2 ^ (2 ** 3)"));
        assertEquals(64, calculate("(2 ^ 2) ** 3"));
        assertEquals(16, calculate("2 * 2 ** 3"));
        assertEquals(12, calculate("2 ** 2 * 3"));
        assertEquals(4, calculate("2 ** 2"));
    }

    @Test
    void testSyntaxError() {
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

}
