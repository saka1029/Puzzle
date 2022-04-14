package test.puzzle.parsers;

import static org.junit.Assert.*;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

import org.junit.Test;

public class TestExpressionNode {

    static final Map<Integer, Integer> PRIORITY = Map.of(
        (int)'+',1, (int)'-', 1, (int)'*', 2, (int)'/', 2, (int)'%', 2, (int)'^', 3);

    interface Node {
        String  postfix();
        default String prefix() { return toString(); }
    }

    record Binary(int operator, Node left, Node right) implements Node {
        @Override public String toString() { return String.format("(%c %s %s)", operator, left, right); }
        @Override public String postfix() { return String.format("%s %s %c", left.postfix(), right.postfix(), operator); }
    }

    record Number(int number) implements Node {
        @Override public String toString() { return Integer.toString(number); }
        @Override public String postfix() { return Integer.toString(number); }
    }

    /**
     * expression = factor { ( '+' | '-' | '*' | '/' | '%' | '^' ) factor }
     * factor     = '(' expression ')' | number
     * number     = [ '-' ] DIGIT { DIGIT }
     */
    static Node parse(String infix) {
        return new Object() {
            int index = 0;
            int ch = get();
            int token;

            RuntimeException error(String format, Object... args) {
                return new RuntimeException(String.format(format, args));
            }

            int get() { return ch = index < infix.length() ? infix.charAt(index++) : -1; }

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

            Number number() {
                StringBuilder number = new StringBuilder();
                if (ch == '-') {
                    number.append((char)ch);
                    get();
                    if (!Character.isDigit(ch))
                        throw error("digit expected");
                }
                do {
                    number.append((char)ch);
                    get();
                } while (Character.isDigit(ch));
                return new Number(Integer.parseInt(number.toString()));
            }

            Node factor() {
                if (match('(')) {
                    Node enclosed = expression();
                    if (!match(')'))
                        throw error("')' expected");
                    return enclosed;
                } else if (ch == '-' || Character.isDigit(ch)) {
                    return number();
                } else
                    throw error("unknown char '%c'", ch);
            }

            /**
             * <pre>
             * INPUT:
             * operatorStack: [ ... operator ]
             * nodeStack:     [ ... left right ]
             *
             * OUTPUT:
             * operatorStack: [ ... ]
             * nodeStack:     [ ... Binary(operator, left, right) ]
             * </pre>
             */
            void makeNode(Deque<Integer> operatorStack, Deque<Node> nodeStack) {
                Node right = nodeStack.pop(), left = nodeStack.pop();
                nodeStack.push(new Binary(operatorStack.pop(), left, right));
            }

            Node expression() {
                Deque<Node> nodeStack = new ArrayDeque<>();
                Deque<Integer> operatorStack = new ArrayDeque<>();
                nodeStack.push(factor());
                while (match('+', '-', '*', '/', '%', '^')) {
                    int priority = PRIORITY.get(token);
                    while (!operatorStack.isEmpty() && PRIORITY.get(operatorStack.peek()) >= priority)
                        makeNode(operatorStack, nodeStack);
                    operatorStack.push(token);
                    nodeStack.push(factor());
                }
                while (!operatorStack.isEmpty())
                    makeNode(operatorStack, nodeStack);
                return nodeStack.pop();
            }

            Node parse() {
                return expression();
            }
        }.parse();
    }

    static Binary b(int op, Node left, Node right) {
        return new Binary(op, left, right);
    }

    static Number n(int num) {
        return new Number(num);
    }

    @Test
    public void test() {
        assertEquals(b('-', b('+', n(1), b('*', n(2), b('^', n(3), n(4)))), n(5)), parse("1 + 2 * 3 ^ 4 - 5"));
        assertEquals("1 2 3 4 ^ * + 5 -", parse("1 + 2 * 3 ^ 4 - 5").postfix());
        assertEquals("(- (+ 1 (* 2 (^ 3 4))) 5)", parse("1 + 2 * 3 ^ 4 - 5").prefix());
    }

}
