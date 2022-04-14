package test.puzzle.core;


import static org.junit.Assert.assertEquals;
import org.junit.Test;
import puzzle.parsers.ScannerCalculator;

public class TestScannerCalculator {

    ScannerCalculator calculator = new ScannerCalculator();
    static final double DELTA = 1e-8;

    @Test
    public void testExpression() {
        assertEquals(4, calculator.calculate("1 + 3"), DELTA);
        assertEquals(-5, calculator.calculate("1 + 3 * -2"), DELTA);
    }

    @Test
    public void testAssignment() {
        assertEquals(5, calculator.calculate("a = 2 + 3"), DELTA);
        assertEquals(-3, calculator.calculate("2 + -a"), DELTA);
        assertEquals(25, calculator.calculate("a * a"), DELTA);
        assertEquals(10, calculator.calculate("b = c = a + a"), DELTA);
        assertEquals(10, calculator.calculate("b"), DELTA);
        assertEquals(10, calculator.calculate("c"), DELTA);
    }

    @Test
    public void testYouTube1() {
        assertEquals(100, calculator.calculate("2015 * 98 - 2014 * 99 + 2016"), DELTA);
    }

}
