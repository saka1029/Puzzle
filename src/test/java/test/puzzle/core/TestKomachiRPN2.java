package test.puzzle.core;

import static org.junit.Assert.assertEquals;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;

import org.junit.Test;

import puzzle.core.Cons;
import puzzle.core.Rational;

public class TestKomachiRPN2 {

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

    static void solve(int[] digits, int goal) {
        solve(digits, goal, false);
    }

    static List<Cons<Integer>> solve(int[] digits, int goal, boolean needReturn) {
        return new Object() {
            Rational ratGoal = Rational.of(goal);
            List<Cons<Integer>> list = new ArrayList<>();
            final int digitsSize = digits.length;
            void solve(int i, int rpnTermSize, int rpnOpSize, int rpnDigitsSize, int termDigitsSize, int term, Cons<Integer> rpn) {
                // digitsがすべてrpnに追加され、rpn上の演算子の数がrpn上の数値の数-1に等しい。
                if (rpnDigitsSize >= digitsSize && rpnOpSize >= rpnTermSize - 1) {
                    Cons<Integer> rrpn = rpn.reverse();
                    if (needReturn)
                        list.add(rrpn);
                    Tree tree = tree(rrpn);
                    if (tree.value.equals(ratGoal))
                        System.out.println(tree + " = " + ratGoal);
                } else {
                    if (i < digitsSize)                 // termにdigitsをひとつ追加する。
                        solve(i + 1, rpnTermSize, rpnOpSize, rpnDigitsSize, termDigitsSize + 1, term * 10 + digits[i], rpn);
                    if (termDigitsSize > 0)             // termをrpnに追加する。
                        solve(i, rpnTermSize + 1, rpnOpSize, rpnDigitsSize + termDigitsSize, 0, 0, rpn.cons(term));
                    if (rpnOpSize < rpnTermSize - 1) {  // 演算子をrpnに追加する。
                        solve(i, rpnTermSize, rpnOpSize + 1, rpnDigitsSize, termDigitsSize, term, rpn.cons(PLUS));
                        solve(i, rpnTermSize, rpnOpSize + 1, rpnDigitsSize, termDigitsSize, term, rpn.cons(MINUS));
                        solve(i, rpnTermSize, rpnOpSize + 1, rpnDigitsSize, termDigitsSize, term, rpn.cons(MULT));
                        solve(i, rpnTermSize, rpnOpSize + 1, rpnDigitsSize, termDigitsSize, term, rpn.cons(DIV));
                    }
                }
            }
            List<Cons<Integer>> solve() {
                solve(0, 0, 0, 0, 0, 0, Cons.nil());
                return list;
            }
        }.solve();
    }

    @Test
    public void testSolve() {
        int[] digits = {1, 2};
        List<Cons<Integer>> list = solve(digits, 3, true);
        assertEquals(
            List.of(Cons.of(12), Cons.of(1, 2, PLUS), Cons.of(1, 2, MINUS), Cons.of(1, 2, MULT), Cons.of(1, 2, DIV)),
            list);
        assertEquals(
            List.of("12", "(1+2)", "(1-2)", "(1*2)", "(1/2)"),
            list.stream().map(c -> tree(c).toString()).toList());
        assertEquals(
            List.of(Rational.of(12), Rational.of(3), Rational.of(-1), Rational.of(2), Rational.of(1,2)),
            list.stream().map(c -> tree(c).value).toList());
    }

    @Test
    public void testKomachi() {
        int[] digits = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        solve(digits, 100);
    }
}
