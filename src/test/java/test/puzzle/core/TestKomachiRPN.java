package test.puzzle.core;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

import org.junit.Test;

import puzzle.core.Cons;
import puzzle.core.Rational;

public class TestKomachiRPN {

    static int patterns(int n) {
        return new Object() {
            int count = 0;
            void solve(int i, int j, String s) {
                if (i >= n && j >= n - 1)
                    System.out.println(++count + " : " + s);
                else {
                    if (i < n)
                        solve(i + 1, j, s + (char)(i + '1'));
                    if (j < n - 1 && i - j >= 2)
                        solve(i, j + 1, s + (char)(j + 'a'));
                }
            }
            int solve() {
                solve(0, 0, "");
                return count;
            }
        }.solve();
    }

    @Test
    public void testPatterns() {
        for (int i = 2; i < 10; ++i)
            System.out.printf("%3d %8d%n", i, patterns(i));
    }

    static final Map<String, Integer> PRECEDENCE = Map.of(
        "+", 0, "-", 0, "*", 1, "/", 1);

    static int precedence(String left, String right) {
        return PRECEDENCE.get(left) - PRECEDENCE.get(right);
    }

    static class Node {
        public final String value;
        public final Node left, right;
        public Node parent;

        Node(String value, Node left, Node right) {
            this.value = value;
            this.left = left;
            this.right = right;
            if (left != null)
                left.parent = this;
            if (right != null)
                right.parent = this;
        }

        public static  Node of(String value, Node left, Node right) {
            return new Node(value, left, right);
        }

        public static  Node of(String value) {
            return new Node(value, null, null);
        }

        @Override
        public String toString() {
            if (left == null)
                return String.valueOf(value);
            else if (parent != null && precedence(value, parent.value) < 0)
                return "(%s%s%s)".formatted(left, value, right);
            else
                return "%s%s%s".formatted(left, value, right);
        }
    }

    static Node tree(String rpn) {
        Deque<Node> stack = new ArrayDeque<>();
        for (char c : rpn.toCharArray())
            if (Character.isDigit(c))
                stack.push(Node.of(Character.toString(c)));
            else {
                Node right = stack.pop(), left = stack.pop();
                stack.push(Node.of(Character.toString(c), left, right));
            }
        return stack.pop();
    }

    @Test
    public void testTree() {
        // String expr = "123+4+-56+*";
        String expr = "12+3*4+5+6*7*8+9+";
        Node tree = tree(expr);
        System.out.println(expr + " -> " + tree);
    }

    static final int PLUS = -100, MINUS = -99, MULT = -98, DIV = -97;

    static void komachiRPN(int[] digits, int goal) {
        new Object() {
            int max = digits.length;
            Rational rgoal = Rational.of(goal);

            Rational eval(Cons<Integer> rpn) {
                Deque<Rational> stack = new ArrayDeque<>();
                for (int e : rpn.reverse()) {
                    if (e >= 0)
                        stack.push(Rational.of(e));
                    else {
                        Rational right = stack.pop(), left = stack.pop();
                        switch (e) {
                            case PLUS: stack.push(left.add(right)); break;
                            case MINUS: stack.push(left.subtract(right)); break;
                            case MULT: stack.push(left.multiply(right)); break;
                            case DIV: stack.push(left.divide(right)); break;
                        }
                    }
                }
                return stack.pop();
            }

            void answer(Cons<Integer> rpn) {
            }

            void solve(int i, int j, int k, int ts, int t, Cons<Integer> rpn) {
                if (i >= max && k == j - 1 && ts == 0) {
                    if (eval(rpn).equals(rgoal))
                        answer(rpn);
                } else {
                    if (i < max) solve(i + 1, j, k, ts + 1, t * 10 + digits[i], rpn);
                    if (ts > 0) solve(i, j + 1, k, 0, 0, rpn.cons(t));
                    if (j - k >= 2) {
                        solve(i, j, k + 1, ts, t, rpn.cons(PLUS));
                        solve(i, j, k + 1, ts, t, rpn.cons(MINUS));
                        solve(i, j, k + 1, ts, t, rpn.cons(MULT));
                        solve(i, j, k + 1, ts, t, rpn.cons(DIV));
                    }
                }

            }
        }.solve(0, 0, 0, 0, 0, Cons.nil());
    }
}
