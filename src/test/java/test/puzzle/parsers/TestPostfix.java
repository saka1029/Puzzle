package test.puzzle.parsers;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;

import org.junit.jupiter.api.Test;

class TestPostfix {

    static final Map<Integer, Integer> PRIORITY = Map.of(
        (int) '+', 1, (int) '-', 1, (int) '*', 2, (int) '/', 2, (int) '%', 2, (int) '^', 3);

    /**
     * expression = factor { ( '+' | '-' | '*' | '/' | '%' | '^' ) factor }
     * factor     = '(' expression ')' | number
     * number     = [ '-' ] DIGIT { DIGIT }
     * @param infix
     * @return
     */
    static String convertToPostfix(String infix) {
        StringBuilder postfix = new StringBuilder();
        new Object() {
            int index = 0;
            int ch = get();
            int token;

            int get() {
                return ch = index < infix.length() ? infix.charAt(index++) : -1;
            }

            void put(int... chars) {
                for (int c : chars)
                    postfix.append((char) c);
            }

            void spaces() {
                while (Character.isWhitespace(ch))
                    get();
            }

            boolean match(int... expects) {
                spaces();
                for (int e : expects)
                    if (ch == e) {
                        token = ch;
                        get();
                        return true;
                    }
                return false;
            }

            void number() {
                if (ch == '-') {
                    put(ch); get();
                    if (!Character.isDigit(ch))
                        throw new RuntimeException("digits expected");
                }
                do {
                    put(ch); get();
                } while (Character.isDigit(ch));
            }

            void factor() {
                if (match('(')) {
                    expression();
                    if (!match(')'))
                        throw new RuntimeException("')' expected");
                } else if (ch == '-' || Character.isDigit(ch))
                    number();
                else
                    throw new RuntimeException("unknown char '" + (char) ch + "'");
            }

            void expression() {
                Deque<Integer> stack = new LinkedList<>();
                factor();
                while (match('+', '-', '*', '/', '%', '^')) {
                    int priority = PRIORITY.get(token);
                    while (!stack.isEmpty() && priority <= PRIORITY.get(stack.peek()))
                        put(' ', stack.pop());
                    stack.push(token);
                    put(' ');
                    factor();
                }
                while (!stack.isEmpty())
                    put(' ', stack.pop());
            }
        }.expression();
        return postfix.toString();
    }

    @Test
    void testConvertToPostfix() {
        assertEquals("6 2 5 * + 8 4 / -", convertToPostfix("6 + 2 * 5 - 8 / 4"));
        assertEquals("6 2 5 ^ % 8 4 / 5 * -", convertToPostfix("6 % 2 ^ 5 - 8 / 4 * 5"));
        assertEquals("654 21 54 87 - * 4 / +", convertToPostfix("654 + 21 * (54 - 87) / 4"));
        assertEquals("11 22 33 5 ^ * + 3 -", convertToPostfix("11   +   22 * 33 ^ 5   -   3"));
        assertEquals("1 2 * 33 5 ^ +", convertToPostfix("1 * 2 + 33 ^ 5"));
        assertEquals("1 2 33 + * 5 -", convertToPostfix("1 * (2 + 33) - 5"));
        assertEquals("1 2 + 3 + 4 - 5 +", convertToPostfix("1 + 2 + 3 - 4 + 5"));
        assertEquals("1 2 3 4 5 + - + +", convertToPostfix("1 + (2 + (3 - (4 + 5)))"));
        assertEquals("-1 2 -3 + +", convertToPostfix("-1 + (2 + -3)"));
    }

}
