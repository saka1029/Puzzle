package test.puzzle;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TestRPN {

    /**
     * expression ::= term {('+' | '-') term}
     * term       ::= factor {('*' | '/') factor}
     * factor     ::= ['-'] ('(' expression ')'|number)
     * @param source
     * @return
     */
    static String rpn(String source) {
        return new Object() {
            StringBuilder sb = new StringBuilder();
            int index = 0;
            int ch = get();

            int get() {
                return ch = index < source.length() ? source.charAt(index++) : -1;
            }

            boolean eat(int c) {
                while (Character.isWhitespace(ch))
                    get();
                if (ch != c) return false;
                get();
                return true;
            }

            void number() {
                while (Character.isDigit(ch)) {
                    sb.append((char)ch);
                    get();
                }
                sb.append(" ");
            }

            void factor(String op) {
                String minus = "";
                if (eat('-')) minus = "negate ";
                if (eat('(')) {
                    expression();
                    if (!eat(')'))
                        throw new RuntimeException("')' expected");
                } else if (Character.isDigit(ch))
                    number();
                sb.append(minus);
                sb.append(op);
            }

            void term(String op) {
                factor("");
                while (true)
                    if (eat('*')) factor("* ");
                    else if (eat('/')) factor("/ ");
                    else break;
                sb.append(op);
            }

            void expression() {
                term("");
                while (true)
                    if (eat('+')) term("+ ");
                    else if (eat('-')) term("- ");
                    else break;
            }

            String parse() {
                expression();
                if (ch != -1)
                    throw new RuntimeException(
                        "extra string '" + source.substring(index - 1) + "'");
                return sb.toString().trim();
            }
        }.parse();
    }

    @Test
    void test() {
        assertEquals("1 2 +", rpn("1 + 2"));
        assertEquals("1 2 3 * +", rpn("1 + 2 * 3"));
        assertEquals("1 2 + 3 4 - /", rpn("(1 + 2) / (3 - 4)"));
        assertEquals("1 2 + 3 + 4 + 5 + 6 +", rpn("1 + 2 + 3 + 4 + 5 + 6"));
        assertEquals("1 2 3 4 5 6 + + + + +", rpn("1 + (2 + (3 + (4 + (5 + 6))))"));
        assertEquals("1 2 negate +", rpn("1 + -2"));
        assertEquals("3 1 2 + negate *", rpn("3 * -(1 + 2)"));
    }

}
