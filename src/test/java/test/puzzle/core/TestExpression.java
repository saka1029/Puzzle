package test.puzzle.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Test;

import puzzle.core.Expression;
import puzzle.core.Expression.ParseException;

public class TestExpression {

    static final double E = 0.00005;

    @Test
    public void testNumber() {
        assertEquals(12.3, Expression.of("12.3").eval(Map.of()), E);
        assertEquals(-12.3, Expression.of("-12.3").eval(Map.of()), E);
        assertEquals(-12, Expression.of("-1.2e1").eval(Map.of()), E);
        assertEquals(-12, Expression.of("-0.12E2").eval(Map.of()), E);
        assertEquals(-12, Expression.of("-120e-1").eval(Map.of()), E);
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
        assertEquals("1 + 2 + 3", Expression.of("  1 + 2 + 3  ").toString());
    }

    @Test
    public void testPlus() {
        assertEquals(3.45 + 12.3, Expression.of("3.45 + xyz").eval(Map.of("xyz", 12.3)), E);
        assertEquals(12.3 + 3.45, Expression.of("xyz + 3.45").eval(Map.of("xyz", 12.3)), E);
    }

    @Test
    public void testMinus() {
        assertEquals(3.45 - 12.3, Expression.of("3.45 - xyz").eval(Map.of("xyz", 12.3)), E);
        assertEquals(12.3 - 3.45, Expression.of("xyz - 3.45").eval(Map.of("xyz", 12.3)), E);
    }

    @Test
    public void testMultiply() {
        assertEquals(3.45 * 12.3, Expression.of("3.45 * xyz").eval(Map.of("xyz", 12.3)), E);
        assertEquals(12.3 * 3.45, Expression.of("xyz * 3.45").eval(Map.of("xyz", 12.3)), E);
    }

    @Test
    public void testDivide() {
        assertEquals(3.45 / 12.3, Expression.of("3.45 / xyz").eval(Map.of("xyz", 12.3)), E);
        assertEquals(12.3 / 3.45, Expression.of("xyz / 3.45").eval(Map.of("xyz", 12.3)), E);
    }

    @Test
    public void testPlusMult() {
        assertEquals(2 + 3 * 12.3, Expression.of("2 + 3 * xyz").eval(Map.of("xyz", 12.3)), E);
        assertEquals(3 * 12.3 + 2, Expression.of("3 * xyz + 2").eval(Map.of("xyz", 12.3)), E);
    }

    @Test
    public void testParen() {
        assertEquals(2 + 3 * 12.3, Expression.of("2 + 3 * xyz").eval(Map.of("xyz", 12.3)), E);
        assertEquals((2 + 3) * 12.3, Expression.of("(2 + 3) * xyz").eval(Map.of("xyz", 12.3)), E);
        assertEquals(3 * (12.3 + 2), Expression.of("3 * (xyz + 2)").eval(Map.of("xyz", 12.3)), E);
    }

    @Test
    public void testParseException() {
        try {
            Expression.of("1 + 2)");
            fail();
        } catch (ParseException e) {
            assertEquals("extra string ')'", e.getMessage());
        }
        try {
            Expression.of("1 + +)");
            fail();
        } catch (ParseException e) {
            assertEquals("unknown char '+'", e.getMessage());
        }
    }

    /**
     * 再帰下降構文解析 (Recursive Descent Paring)
     * SYNTAX
     * expr = term { '+' term }
     * term = digit
     */
    static String parseLeftAssociative(String s) {
        return new Object() {
            int length = s.length(), index = 0, ch = get();

            int get() {
                return ch = index < length ? s.charAt(index++) : -1;
            }

            String term() {
                String t = Character.toString(ch);
                get();
                return t;
            }

            String expr() {
                String t = term();
                while (true) {
                    if (ch == '+') {
                        get(); // skip '+'
                        t = "(" + t + "+" + term() + ")";
                    } else
                        break;
                }
                return t;
            }
        }.expr();
    }

    @Test
    public void testParseLeftAssociative() {
        assertEquals("(((1+2)+3)+4)", parseLeftAssociative("1+2+3+4"));
    }

    /**
     * 再帰下降構文解析 (Recursive Descent Paring)
     * SYNTAX
     * expr = term [ '+' expr ]
     * term = digit
     */
    static String parseRightAssociative(String s) {
        return new Object() {
            int length = s.length(), index = 0, ch = get();

            int get() {
                return ch = index < length ? s.charAt(index++) : -1;
            }

            String term() {
                String t = Character.toString(ch);
                get();
                return t;
            }

            String expr() {
                String t = term();
                if (ch == '+') {
                    get(); // skip '+'
                    t = "(" + t + "+" + expr() + ")";
                }
                return t;
            }
        }.expr();
    }

    @Test
    public void testParseRightAssociative() {
        assertEquals("(1+(2+(3+4)))", parseRightAssociative("1+2+3+4"));
    }
}
