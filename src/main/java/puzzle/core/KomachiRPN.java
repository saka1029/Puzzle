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

    record Operator(String name, BinaryOperator<Rational> function, int preceedance) {}
    static final Map<Integer, Operator> OPERATORS = Map.of(
        PLUS, new Operator("+", Rational::add, 2),
        MINUS, new Operator("-", Rational::subtract, 2),
        MULT, new Operator("*", Rational::multiply, 4),
        DIV, new Operator("/", Rational::divide, 4));

    public static abstract class Tree {
        public final Rational value;
        Tree(Rational value) {
            this.value = value;
        }
        /**
         * @return 演算子の優先順位を返す。
         */
        abstract int prec();
    }

    static class Node extends Tree {
        final int operator;
        final Tree left, right;

        Node(int operator, Tree left, Tree right) {
            super(OPERATORS.get(operator).function.apply(left.value, right.value));
            this.operator = operator;
            this.left = left;
            this.right = right;
        }

        @Override
        int prec() {
            return OPERATORS.get(operator).preceedance;
        }

        /**
         * カッコの出力をなるべく抑止する。
         */
        @Override
        public String toString() {
            String ls = left.toString(), rs = right.toString();
            if (left.prec() < prec())
                ls = "(" + ls + ")";
            if (right.prec() <= prec())
                rs = "(" + rs + ")";
            return "%s%s%s".formatted(ls, OPERATORS.get(operator).name, rs);
        }
    }

    static class Leaf extends Tree {
        Leaf(int value) {
            super(Rational.of(value));
        }

        @Override
        int prec() {
            return 99999999;
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
            .map(e -> OPERATORS.containsKey(e) ? OPERATORS.get(e).name : ("" + e))
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
                + tree.toString()
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
            for (int k = i + 1; k <= digits.length; ++k)   // 数値をrpnに追加する。
                solve(k, numberCount + 1, operatorCount, rpn.cons(IntStream.range(i, k)
                    .map(j -> digits[j])
                    .reduce(0, (a, b) -> 10 * a +b)));
            if (operatorCount < numberCount - 1)  // 演算子をrpnに追加する。
                for (int j = PLUS; j <= DIV; ++j)
                    solve(i, numberCount, operatorCount + 1, rpn.cons(j));
        }
    }
}
