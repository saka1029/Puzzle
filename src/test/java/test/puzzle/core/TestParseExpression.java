package test.puzzle.core;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestParseExpression {

    /**
     * <pre>
     * expression ::= term { ( '+' | '-' ) term }
     * term ::= factor { ( '*' | '/' ) factor }
     * factor ::= [ '-' ] ( number | '(' expression ')' )
     * </pre>
     * @return
     */
    double parse(String source) {
        return new Object() {
            int index = 0;
            int ch = get();

            int get() { return ch = index < source.length() ? source.charAt(index++) : -1; }

            void skipSpaces() {
                while (Character.isWhitespace(ch)) get();
            }

            boolean eat(int expected) {
                if (ch != expected) return false;
                get();
                return true;
            }

            double number() {
                StringBuilder sb = new StringBuilder();
                while (Character.isDigit(ch)) {
                    sb.append((char)ch);
                    get();
                }
                return Double.parseDouble(sb.toString());
            }

            double factor() {
                skipSpaces();
                double sign = 1;
                if (eat('-')) sign = -1;
                if (Character.isDigit(ch))
                    return sign * number();
                else if (eat('(')) {
                    double result = sign * expression();
                    skipSpaces();
                    if (!eat(')')) throw new RuntimeException("')' expected");
                    return result;
                } else
                    throw new RuntimeException("unknown character '" + ((char)ch) + "' found");
            }

            double term() {
                double result = factor();
                skipSpaces();
                while (true) {
                    if (eat('*')) result *= factor();
                    else if (eat('/')) result /= factor();
                    else break;
                    skipSpaces();
                }
                return result;
            }

            double expression() {
                double result = term();
                skipSpaces();
                while (true) {
                    if (eat('+')) result += term();
                    else if (eat('-')) result -= term();
                    else break;
                    skipSpaces();
                }
                return result;
            }

            double parse() {
                double result = expression();
                if (ch != -1)
                    throw new RuntimeException("extra string '" + source.substring(index - 1) + "'");
                return result;
            }
        }.parse();
    }

    @Test
    public void testParse() {
        assertEquals(3.0, parse("1 + 2  "), 1e-8);
        assertEquals(-1.0, parse("-1"), 1e-8);
        assertEquals(11.0, parse("1 + 2 * 3 + 4"), 1e-8);
        assertEquals(-3.0, parse("(1 + 2) * (3 - 4)"), 1e-8);
        assertEquals(3.0 / 7.0, parse("(1 + 2) / (3 + 4)"), 1e-8);

    }

    static void runtimeException(Runnable r, String message) {
        try {
            r.run();
            fail();
        } catch (RuntimeException e) {
            assertEquals(message, e.getMessage());
        }
    }

    @Test
    public void testError() {
        runtimeException(() -> parse(")"), "unknown character ')' found");
        runtimeException(() -> parse("(2 + 3"), "')' expected");
        runtimeException(() -> parse("2 + 3  )"), "extra string ')'");
    }
}
