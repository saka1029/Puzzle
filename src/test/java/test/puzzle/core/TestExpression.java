package test.puzzle.core;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import puzzle.core.Expression;

public class TestExpression {

    static final double E = 0.00005;

    @Test
    public void testNumber() {
        assertEquals(12.3, Expression.of("12.3").eval(Map.of()), E);
        assertEquals(-12.3, Expression.of("-12.3").eval(Map.of()), E);
    }

    @Test
    public void testVariable() {
        assertEquals(12.3, Expression.of("xyz").eval(Map.of("xyz", 12.3)), E);
        assertEquals(-12.3, Expression.of("-xyz").eval(Map.of("xyz", 12.3)), E);
    }

    @Test
    public void testToString() {
        assertEquals("12.3", Expression.of("12.3").toString());
        assertEquals("-xyz", Expression.of("-xyz").toString());
        assertEquals("12.3 - xyz", Expression.of("12.3 - xyz").toString());
    }
    
    @Test
    public void testPlus() {
        assertEquals(15.75, Expression.of("3.45 + xyz").eval(Map.of("xyz", 12.3)), E);
        assertEquals(15.75, Expression.of("xyz + 3.45").eval(Map.of("xyz", 12.3)), E);
    }
    
    @Test
    public void testMinus() {
        assertEquals(-8.85, Expression.of("3.45 - xyz").eval(Map.of("xyz", 12.3)), E);
        assertEquals(8.85, Expression.of("xyz - 3.45").eval(Map.of("xyz", 12.3)), E);
    }
    
    @Test
    public void testMultiply() {
        assertEquals(42.435, Expression.of("3.45 * xyz").eval(Map.of("xyz", 12.3)), E);
        assertEquals(42.435, Expression.of("xyz * 3.45").eval(Map.of("xyz", 12.3)), E);
    }
    
    @Test
    public void testDivide() {
        assertEquals(0.280487804878049, Expression.of("3.45 / xyz").eval(Map.of("xyz", 12.3)), E);
        assertEquals(3.56521739130435, Expression.of("xyz / 3.45").eval(Map.of("xyz", 12.3)), E);
    }
    
    @Test
    public void testPlusMult() {
        assertEquals(38.9, Expression.of("2 + 3 * xyz").eval(Map.of("xyz", 12.3)), E);
        assertEquals(38.9, Expression.of("3 * xyz + 2").eval(Map.of("xyz", 12.3)), E);
    }
    
    @Test
    public void testParen() {
        assertEquals(61.5, Expression.of("(2 + 3) * xyz").eval(Map.of("xyz", 12.3)), E);
        assertEquals(42.9, Expression.of("3 * (xyz + 2)").eval(Map.of("xyz", 12.3)), E);
    }
    
    static abstract class Node {
        final Node left, right;
        Node(Node left, Node right) {
            this.left = left;
            this.right = right;
        }
        abstract double eval(Map<String, Double> variables);
    }
    
    @Test
    public void testNode() {
        Node one = new Node(null, null) {
            @Override
            double eval(Map<String, Double> variables) {
                return 1;
            }
        };
        Node minus = new Node(one, null) {
            @Override
            double eval(Map<String, Double> variables) {
                return -left.eval(variables);
            }
        };
        Node plus = new Node(minus, one) {
            @Override
            double eval(Map<String, Double> variables) {
                return left.eval(variables) + right.eval(variables);
            }
        };
        assertEquals(0, plus.eval(Map.of()), E);
    }
    
    @Test
    public void testNodeInterface() {
        interface E {
            double eval(Map<String, Double> variables);
        }
        
        E one = v -> 1;
        E minus = v -> -one.eval(v);
        E plus = v -> minus.eval(v) + one.eval(v);
        assertEquals(0, plus.eval(Map.of()), E);
    }

}
