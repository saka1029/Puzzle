package test.puzzle.core;

import static org.junit.Assert.assertEquals;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;

import puzzle.core.Cons;
import puzzle.core.Rational;

public class TestKomachiRPN {

    static final int PLUS = -100, MINUS = -99, MULT = -98, DIV = -97;
    static final Map<Integer, String> OPSTR = Map.of(PLUS, "+", MINUS, "-", MULT, "*", DIV, "/");
    static final Map<Integer, BinaryOperator<Rational>> OPS = Map.of(
        PLUS, Rational::add, MINUS, Rational::subtract, MULT, Rational::multiply, DIV, Rational::divide);

    static abstract class Tree {
        final Rational value;
        Tree(Rational value) {
            this.value = value;
        }
    }

    static class Node extends Tree {
        final String operator;
        final Tree left, right;

        Node(int operator, Tree left, Tree right) {
            super(OPS.get(operator).apply(left.value, right.value));
            this.operator = OPSTR.get(operator);
            this.left = left;
            this.right = right;
        }

        @Override
        public String toString() {
            return "(%s%s%s)".formatted(left, operator, right);
        }
    }

    static class Leaf extends Tree {
        Leaf(int value) {
            super(Rational.of(value));
        }

        @Override
        public String toString() {
            return "" + value; 
        }
    }

    static Tree tree(Cons<Integer> rpn) {
        Deque<Tree> stack = new ArrayDeque<>();
        for (int e : rpn)
            if (e >= 0)
                stack.push(new Leaf(e));
            else {
                Tree right = stack.pop(), left = stack.pop();
                stack.push(new Node(e, left, right));
            }
        return stack.pop();
    }

    static String string(Cons<Integer> rpn) {
        return rpn.stream()
            .map(e -> OPSTR.containsKey(e) ? OPSTR.get(e) : ("" + e))
            .collect(Collectors.joining(",", "[", "]"));
    }

    static void solve(int[] digits, int goal) {
        solve(digits, goal, null);
    }

    static void solve(int[] digits, int goal, List<Cons<Integer>> list) {
        new Object() {
            Rational ratGoal = Rational.of(goal);
            final int digitsSize = digits.length;
            void solve(int i, int numberCount, int operatorCount, Cons<Integer> rpn) {
                // digitsがすべてrpnに追加され、rpn上の演算子の数がrpn上の数値の数-1に等しい。
                if (i >= digitsSize && operatorCount >= numberCount - 1) {
                    Cons<Integer> rrpn = rpn.reverse();
                    if (list != null)
                        list.add(rrpn);
                    Tree tree = tree(rrpn);
                    String str = string(rrpn);
                    if (tree.value.equals(ratGoal))
                        System.out.println(tree + " " + str + " = " + ratGoal);
                } else {
                    for (int k = i + 1; k <= digits.length; ++k) {  // 数値をrpnに追加する。
                        int number = IntStream.range(i, k)
                                .map(j -> digits[j])
                                .reduce(0, (a, b) -> 10 * a + b);
                        solve(k, numberCount + 1, operatorCount, rpn.cons(number));
                    }
                    if (operatorCount < numberCount - 1) {  // 演算子をrpnに追加する。
                        solve(i, numberCount, operatorCount + 1, rpn.cons(PLUS));
                        solve(i, numberCount, operatorCount + 1, rpn.cons(MINUS));
                        solve(i, numberCount, operatorCount + 1, rpn.cons(MULT));
                        solve(i, numberCount, operatorCount + 1, rpn.cons(DIV));
                    }
                }
            }
        }.solve(0, 0, 0, Cons.nil());
    }

    @Test
    public void testSolve() {
        int[] digits = {1, 2};
        List<Cons<Integer>> list = new ArrayList<>();
        solve(digits, 3, list);
        assertEquals(
            List.of(Cons.of(1, 2, PLUS), Cons.of(1, 2, MINUS), Cons.of(1, 2, MULT), Cons.of(1, 2, DIV), Cons.of(12)),
            list);
        assertEquals(
            List.of("(1+2)", "(1-2)", "(1*2)", "(1/2)", "12"),
            list.stream().map(c -> tree(c).toString()).toList());
        assertEquals(
            List.of(Rational.of(3), Rational.of(-1), Rational.of(2), Rational.of(1, 2), Rational.of(12)),
            list.stream().map(c -> tree(c).value).toList());
    }

    @Test
    public void testKomachi() {
        int[] digits = {1, 2, 3, 4, 5};
        solve(digits, 10);
    }

    @Test
    public void testTicket() {
        solve(new int[] {9,9,9,9}, 10);
    }
}
