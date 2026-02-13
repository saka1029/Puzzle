package test.puzzle.core;

import static org.junit.Assert.assertEquals;

import java.util.ArrayDeque;
import java.util.Deque;

import org.junit.Test;

public class TestKomachi2 {

    enum Operator {
        Append(-1) { public int eval(int l, int r) { throw new RuntimeException("Cant eval");}},
        Minus(0) { public int eval(int l, int r) { return l - r;}},
        Plus(0) { public int eval(int l, int r) { return l + r;}},
        Divide(1) { public int eval(int l, int r) { return l / r;}},
        Multiply(1) { public int eval(int l, int r) { return l * r;}};

        static final Operator[] all = Operator.values();
        public final int precedance;

        Operator(int precedance) {
            this.precedance = precedance;
        }

        public static Operator at(int index) {
            return all[index];
        }

        public abstract int eval(int left, int right);
    }

    static class Calculator {
        final Deque<Integer> stack = new ArrayDeque<>();
        final Deque<Operator> opStack = new ArrayDeque<>();

        void reduce() {
            while (!opStack.isEmpty()) {
                int r = stack.pop(), l = stack.pop();
                stack.push(opStack.pop().eval(l, r));
            }
        }

        public void add(Operator op, int digit) {
            switch (op) {
                case Append: stack.push(stack.pop() * 10 + digit); break;
                default:
                    reduce();
                    opStack.push(op);
                    stack.push(digit);
                    break;
            }
        }
    }

    @Test
    public void testOperator() {
        assertEquals(Operator.Append, Operator.at(0));
        assertEquals(Operator.Multiply, Operator.at(4));
    }

    @Test
    public void testCalculator() {
        Calculator c = new Calculator();
        c.add(Operator.Plus, 1);
        c.add(Operator.Append, 2);
        c.add(Operator.Append, 3);
        assertEquals(123, (int)c.stack.pop());
    }

    static int eval(List<Integer> input) {
        return new Object() {
            int parse() {

            }
        }.parse();
    }
}
