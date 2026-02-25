package puzzle.dikstra;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;

public class Parser {

    public enum Type { ID, NUMBER, LP, OPERATOR }
    public record Operator(int priority, boolean leftAssoc) { }
    public record Token(Type type, String value) { }

    static final Map<String, Operator> OPERATORS = Map.of(
            "+", new Operator(3, true),
            "-", new Operator(3, true),
            "*", new Operator(5, true),
            "/", new Operator(5, true),
            "^", new Operator(7, false));

    final Map<String, Operator> operators;
    final int[][] operatorNames;
    
    public Parser(Map<String, Operator> operators) {
        this.operators = operators;
        this.operatorNames = operators.keySet().stream()
            .map(s -> s.codePoints().toArray())
            .toArray(int[][]::new);
    }

    public Parser() {
        this(OPERATORS);
    }

    static boolean isDigit(int ch) {
        return Character.isDigit(ch);
    }

    static boolean isIdFirst(int ch) {
        return Character.isLetter(ch) || ch == '_';
    }

    static boolean isIdRest(int ch) {
        return isIdFirst(ch) || Character.isDigit(ch);
    }

    int[] codePoints;
    int index, ch;

    int get() {
        if (index < codePoints.length)
            return ch = codePoints[index++];
        index = codePoints.length + 1;
        return ch = -1;
    }

    boolean eat(int... expected) {
        int backup = index;
        for (int e : expected) {
            if (ch != e) {
                index = backup - 1;
                get();
                return false;
            }
            get();
        }
        return true;
    }

    final List<Token> output = new ArrayList<>();
    final Deque<Token> stack = new ArrayDeque<>();

    public List<Token> parse(String input) {
        output.clear();
        stack.clear();
        codePoints = input.codePoints().toArray();
        index = 0;
        get();
        while (true) {
            while (Character.isWhitespace(ch))
                get();
            int start = index - 1;
            if (ch == -1) {
                break;
            } else if (eat('(')) {
                stack.push(new Token(Type.LP, "("));
            } else if (eat(')')) {
                while (!stack.isEmpty()) {
                    if (stack.peek().type == Type.LP)
                        break;
                    output.add(stack.pop());
                }
                if (stack.isEmpty())
                    throw new RuntimeException("Missing '('");
                stack.pop(); // '('を捨てる
                if (!stack.isEmpty() && stack.peek().type == Type.ID)
                    output.add(stack.pop());
            } else if (eat(',')) {
                while (!stack.isEmpty()) {
                    if (stack.peek().type == Type.LP)
                        break;
                    output.add(stack.pop());
                }
                if (stack.isEmpty())
                    throw new RuntimeException("Missing '('");
            } else if (isDigit(ch)) {
                while (isDigit(ch))
                    get();
                String number = new String(codePoints, start, index - start - 1);
                output.add(new Token(Type.NUMBER, number));
            } else {
                String op = null;
                for (int[] name : operatorNames) {
                    if (eat(name)) {
                        op = new String(name, 0, name.length);
                        break;
                    }
                }
                if (op == null) {
                    if (isIdFirst(ch))
                        get();
                    while (isIdRest(ch))
                        get();
                    String id = new String(codePoints, start, index - start - 1);
                    output.add(new Token(Type.ID, id));
                } else {
                    // operator
                    Token o1 = new Token(Type.OPERATOR, op);
                    Operator op1 = operators.get(op);
                    while (!stack.isEmpty()) {
                        Token o2 = stack.peek();
                        if (o2.type != Type.OPERATOR)
                            break;
                        Operator op2 = operators.get(o2.value);
                        if (op1.leftAssoc && op1.priority <= op2.priority
                                || op1.priority < op2.priority)
                            output.add(stack.pop());
                        else
                            break;
                    }
                    stack.push(o1);
                }
            }
        }
        while (!stack.isEmpty()) {
            Token t = stack.peek();
            if (t.type == Type.LP)
                throw new RuntimeException("'(' and ')' unmach");
            else if (t.type == Type.OPERATOR)
                output.add(stack.pop());
            else
                break;
        }
        return output;
    }
}