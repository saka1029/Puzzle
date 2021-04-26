package test.puzzle;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import puzzle.parsers.ScannerCalculator;

class TestScannerCalculator {

    ScannerCalculator calculator = new ScannerCalculator();

    @Test
    void testExpression() {
        assertEquals(4, calculator.calculate("1 + 3"));
        assertEquals(-5, calculator.calculate("1 + 3 * -2"));
    }

    @Test
    void testAssignment() {
        assertEquals(5, calculator.calculate("a = 2 + 3"));
        assertEquals(-3, calculator.calculate("2 + -a"));
        assertEquals(25, calculator.calculate("a * a"));
        assertEquals(10, calculator.calculate("b = c = a + a"));
        assertEquals(10, calculator.calculate("b"));
        assertEquals(10, calculator.calculate("c"));
    }

}
