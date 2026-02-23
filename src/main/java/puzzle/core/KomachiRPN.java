package puzzle.core;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 小町算を解きます。
 * 使用可能な演算子は+, -, *, /です。
 * カッコを使用することができます。
 * 解法は可能なすべての式をRPNで生成し、
 * 条件を満たすものだけを抽出することによって行います。
 * 生成されたRPNから木構造の式を生成して値を計算します。
 * 【例】
 * 数字の並び: 1, 2, 3, 4, 5
 * 計算結果の値: 10
 * 解:
 * (((1+2)/3)+4)+5 = 1,2,+,3,/,4,+,5,+ = 10
 */
public class KomachiRPN {

    public static final int PLUS = -100, MINUS = -99, MULT = -98, DIV = -97;
    static final Map<Integer, String> OPSTR = Map.of(PLUS, "+", MINUS, "-", MULT, "*", DIV, "/");
    static final Map<Integer, BinaryOperator<Rational>> OPS = Map.of(
        PLUS, Rational::add, MINUS, Rational::subtract, MULT, Rational::multiply, DIV, Rational::divide);

    public static abstract class Tree {
        public final Rational value;
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

    public static Tree tree(Cons<Integer> rpn) {
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
            .collect(Collectors.joining(","));
    }

    final Rational ratGoal;
    final int[] digits;
    final int digitsSize;
    final List<Cons<Integer>> list;
    int count = 0;

    KomachiRPN(int[] digits, int goal, List<Cons<Integer>> list) {
        this.digits = digits;
        this.digitsSize = digits.length;
        this.ratGoal = Rational.of(goal);
        this.list = list;
    }

    public static void solve(int[] digits, int goal) {
        solve(digits, goal, null);
    }

    void check(Cons<Integer> rpn) {
        Cons<Integer> rrpn = rpn.reverse();
        if (list != null)
            list.add(rrpn);
        Tree tree = tree(rrpn);
        String str = string(rrpn);
        if (tree.value.equals(ratGoal))
            System.out.println(++count + ": "
                + tree.toString().replaceFirst("^\\((.*)\\)$", "$1")
                + " = " + str + " = " + ratGoal);
    }

    public static void solve(int[] digits, int goal, List<Cons<Integer>> list) {
        KomachiRPN komachi = new KomachiRPN(digits, goal, list);
        komachi.solve(0, 0, 0, Cons.nil());
    }

    void solve(int i, int numberCount, int operatorCount, Cons<Integer> rpn) {
        // digitsがすべてrpnに追加され、rpn上の演算子の数がrpn上の数値の数-1に等しい。
        if (i >= digitsSize && operatorCount >= numberCount - 1) {
            check(rpn);
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
}
