package test.puzzle.parsers;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

import org.junit.Test;

public class TestRPNtoInfix {

    static String postfixToInfix(final String postfix) {
        class Expression {
            final static String ops = "-+/*^";
            String ex;
            int prec = 3;

            Expression(String e) {
                ex = e;
            }

            Expression(String e1, String e2, String o) {
                ex = String.format("%s %s %s", e1, o, e2);
                prec = ops.indexOf(o) / 2;
            }

            @Override
            public String toString() {
                return ex;
            }
        }

        Deque<Expression> expr = new ArrayDeque<>();

        for (String token : postfix.split("\\s+")) {
            char c = token.charAt(0);
            int idx = Expression.ops.indexOf(c);
            if (idx != -1 && token.length() == 1) {

                Expression r = expr.pop();
                Expression l = expr.pop();

                int opPrec = idx / 2;

                if (l.prec < opPrec || (l.prec == opPrec && c == '^'))
                    l.ex = '(' + l.ex + ')';

                if (r.prec < opPrec || (r.prec == opPrec && c != '^'))
                    r.ex = '(' + r.ex + ')';

                expr.push(new Expression(l.ex, r.ex, token));
            } else {
                expr.push(new Expression(token));
            }
            System.out.printf("%s -> %s%n", token, expr);
        }
        return expr.peek().ex;
    }

    @Test
    public void testPostfixToInfix() {
        for (String e : new String[]{"3 4 2 * 1 5 - 2 3 ^ ^ / +", "1 2 + 3 4 + ^ 5 6 + ^"}) {
            System.out.printf("Postfix : %s%n", e);
            System.out.printf("Infix : %s%n", postfixToInfix(e));
            System.out.println();
        }
    }

    @Test
    public void testPrec() {
        String ops = "-+/*^";
        for (char c : ops.toCharArray())
            System.out.printf("op: %c prec: %d%n", c, ops.indexOf(c) / 2);
    }

    record Op(int preceedance, boolean leftAssoc) {}

    static final Map<String, Op> OPS = Map.of(
        "+", new Op(3, true), "-", new Op(3, true),
        "*", new Op(5, true), "/", new Op(5, true),
        "^", new Op(7, false));

    static record Expr(String expr, int preceedance) {
        public Expr(String expr) {
            this(expr, 999);
        }
        public Expr(String l, String r, String op) {
            this("%s%s%s".formatted(l, op, r), OPS.get(op).preceedance);
        }
    }
    
    static String RPNtoInfix(String[] rpn) {
        Deque<Expr> expr = new ArrayDeque<>();
        for (String e : rpn) {
            Op op = OPS.get(e);
            if (op != null) {
                Expr r = expr.pop(), l = expr.pop();
                String rexpr = r.expr, lexpr = l.expr;
                if (l.preceedance < op.preceedance || (l.preceedance == op.preceedance && !op.leftAssoc))
                    lexpr = "(" + l.expr + ")";
                if (r.preceedance < op.preceedance || (r.preceedance == op.preceedance && op.leftAssoc))
                    rexpr = "(" + r.expr + ")";
                expr.push(new Expr(lexpr, rexpr, e));
            } else
                expr.push(new Expr(e));

        }
        return expr.pop().expr;
    }

    @Test
    public void testRPNtoInfix() {
        String[] rnp = "3 4 2 * 1 5 - 2 3 ^ ^ / +".split("\\s+");
        System.out.println(RPNtoInfix(rnp));
        System.out.println(RPNtoInfix("12,3,-,4,5,-,-".split(",")));
    }
}
