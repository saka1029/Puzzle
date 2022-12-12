package test.puzzle.parsers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static puzzle.parsers.BooleanExpression.parse;
import static puzzle.parsers.BooleanExpression.truthTable;

import java.util.Map;

import org.junit.Test;

import puzzle.parsers.BooleanExpression;

public class TestBooleanExpression {

    @Test
    public void testTruthTable() {
        truthTable("T");
        truthTable("F");
        truthTable("!F");
        truthTable("!a");
        truthTable("a & b");
        truthTable("a | b");
        truthTable("a ^ b");
        truthTable("a -> b");
        truthTable("a = b");
        truthTable("(a = b) = !(a ^ b)");
        truthTable("(a -> b) = (!a | b)");
        truthTable("(a & b -> c) = (a -> (b -> c))");
        truthTable("(a -> b) & (b -> c) -> (a -> c)");
    }

    @Test
    public void testNot() {
        BooleanExpression e = parse("!a").expression();
        assertFalse(e.test(Map.of('a', true)));
        assertTrue(e.test(Map.of('a', false)));
    }

    @Test
    public void testOr() {
        BooleanExpression e = parse("a | b").expression();
        assertTrue(e.test(Map.of('a', true, 'b', true)));
        assertTrue(e.test(Map.of('a', true, 'b', false)));
        assertTrue(e.test(Map.of('a', false, 'b', true)));
        assertFalse(e.test(Map.of('a', false, 'b', false)));
    }
}
