package test.puzzle.core;

import static java.lang.Character.*;
import static java.lang.Integer.*;
import static org.junit.Assert.assertEquals;
import java.util.Map;

import org.junit.Test;

public class TestCalculator {

    /**
     * expression = term { ( "+" | "-" ) term }
     * term       = factor { ( "*" | "/" ) factor }
     * factor     = [ "-" ]  ( "(" expression ")" | number )
     * number     = digit { digit }
     * digit      = "0" | "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9"
     */
    static int 整数式の評価(String string) {
        int length = string.length();
        return new Object() {
            int index = -1, ch;

            void next() {
                ch = ++index < length ? string.charAt(index) : -1;
            }

            boolean eat(int expected) {
                while (isWhitespace(ch))
                    next();
                if (ch != expected)
                    return false;
                next();
                return true;
            }

            int parse() {
                next();
                int result = expression();
                if (index < length)
                    throw new RuntimeException("Unexpected: " + (char) ch);
                return result;
            }

            int expression() {
                int result = term();
                for (;;)
                    if (eat('+'))
                        result += term();
                    else if (eat('-'))
                        result -= term();
                    else
                        return result;
            }

            int term() {
                int result = factor();
                for (;;)
                    if (eat('*'))
                        result *= factor();
                    else if (eat('/'))
                        result /= factor();
                    else
                        return result;
            }

            int factor() {
                int sign = 1;
                if (eat('-'))
                    sign = -1;
                if (eat('(')) {
                    int result = expression();
                    if (eat(')'))
                        return sign * result;
                    throw new RuntimeException("')' expected");
                } else if (isDigit(ch)) {
                    int start = index;
                    while (isDigit(ch))
                        next();
                    return sign * parseInt(string.substring(start, index));
                } else
                    throw new RuntimeException("unknown char '" + (char) ch + "'");
            }
        }.parse();
    }

    @Test
    public void test整数式の評価() {
        assertEquals(3, 整数式の評価("1 + 2"));
        assertEquals(7, 整数式の評価("1 + 2 * 3"));
        assertEquals(9, 整数式の評価("(1 + 2) * 3"));
        assertEquals(-9, 整数式の評価("-(1 + 2) * 3"));
        assertEquals(18, 整数式の評価("12 * 3 / 2"));
        assertEquals(-18, 整数式の評価("12 * 3 / -2"));
        assertEquals(18, 整数式の評価("１２ * ３ / ２")); // 全角！
    }

    static boolean booleanExpressionEvaluate(String expression, Map<String, Boolean> variables) {
        int length = expression.length();
        return new Object() {
            int index = -1, ch;

            void next() {
                ch = ++index < length ? expression.charAt(index) : -1;
            }

            boolean eat(String expected) {
                while (Character.isWhitespace(ch))
                    next();
                if (!expression.startsWith(expected, index))
                    return false;
                index += expected.length() - 1;
                next();
                return true;
            }

            boolean eval() {
                next();
                boolean result = expression();
                if (index < length)
                    throw new RuntimeException("Invalid char '" + (char) ch + "'");
                return result;
            }

            boolean expression() {
                boolean result = term();
                for (;;)
                    if (eat("OR") || eat("or"))
                        result |= term();
                    else
                        return result;
            }

            boolean term() {
                boolean result = factor();
                for (;;)
                    if (eat("AND") || eat("and"))
                        result &= factor();
                    else
                        return result;
            }

            boolean factor() {
                if (eat("(")) {
                    boolean result = expression();
                    if (eat(")"))
                        return result;
                    throw new RuntimeException("')' expected");
                }
                if (Character.isAlphabetic(ch)) {
                    String variable = Character.toString(ch);
                    next();
                    return variables.get(variable);
                } else
                    throw new RuntimeException("invalid char '" + (char) ch + "'");
            }
        }.eval();
    }

    @Test
    public void testCalcBoolean() {
        Map<String, Boolean> variables = Map.of("a", true, "b", false, "c", true);
        assertEquals(false, booleanExpressionEvaluate("a AND b", variables));
        assertEquals(true, booleanExpressionEvaluate("a OR b", variables));
        assertEquals(true, booleanExpressionEvaluate("a AND (b OR c)", variables));
        assertEquals(true, booleanExpressionEvaluate("a AND b OR c", variables));
        assertEquals(true, booleanExpressionEvaluate("b OR a AND c", variables));
        assertEquals(true, booleanExpressionEvaluate("a and (b or c)", variables));
    }
}
