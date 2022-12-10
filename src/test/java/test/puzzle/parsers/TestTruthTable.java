package test.puzzle.parsers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;

import org.junit.Test;

public class TestTruthTable {

    interface Expression extends Predicate<Map<Character, Boolean>> {}
    
    record ExpressionVariables(
        Expression expression,
        List<Character> variables) {}

    static boolean isVariable(int ch) {
        return ch >= 'a' && ch <= 'z';
    }

    static ExpressionVariables parse(String source) {
        return new Object() {
            int length = source.length(), index = 0, ch = get();
            Set<Character> variables = new TreeSet<>();

            int get() {
                return ch = index < length ? source.charAt(index++) : -1;
            }

            boolean eat(int expected) {
                while (Character.isWhitespace(ch))
                    get();
                if (ch == expected) {
                    get();
                    return true;
                }
                return false;
            }

            Expression factor() {
                Expression e;
                boolean not = eat('~');
                if (eat('(')) {
                    e = expression();
                    if (!eat(')'))
                        throw new RuntimeException("')' expected");
                } else if (isVariable(ch)) {
                    char variable = (char)ch;
                    get();
                    variables.add(variable);
                    e = context -> context.get(variable);
                } else
                    throw new RuntimeException(
                        "unknown char '%c'".formatted((char) ch));
                if (not) {
                    Expression f = e;
                    e = context -> !f.test(context);
                }
                return e;
            }

            Expression term() {
                Expression e = factor();
                while (eat('&')) {
                    Expression l = e, r = factor();
                    e = context -> l.test(context) & r.test(context);
                }
                return e;
            }

            Expression expression() {
                Expression e = term();
                while (eat('|')) {
                    Expression l = e, r = term();
                    e = context -> l.test(context) | r.test(context);
                }
                return e;
            }
            
            ExpressionVariables parse() {
                return new ExpressionVariables(
                    expression(),
                    new ArrayList<>(variables));
            }
        }.parse();
    }
    
    static final boolean[] BOOLEANS = {true, false};

    static void truthTable(String source) {
        ExpressionVariables ev = parse(source);
        int size = ev.variables().size();
        Map<Character, Boolean> context = new HashMap<>();
        for (char c : ev.variables())
            System.out.print(c + " ");
        System.out.println("| " + source);
        System.out.println("--".repeat(size) + "+-" + "-".repeat(source.length()));
        new Object() {

            String toString(boolean value) {
                return value ? "T " : "F ";
            }

            void gen(int index, String line) {
                if (index >= size)
                    System.out.println(line + "| "
                        + toString(ev.expression().test(context)));
                else
                    for (boolean value : BOOLEANS) {
                        context.put(ev.variables().get(index), value);
                        gen(index + 1, line + toString(value));
                    }
            }
        }.gen(0, "");
    }
    
    @Test
    public void testTruthTable() {
        truthTable("~a");
        System.out.println();
        truthTable("a & b");
        System.out.println();
        truthTable("a | b");
        System.out.println();
        truthTable("a | b & c");
        System.out.println();
        truthTable("(a | b) & c");
        System.out.println();
        truthTable("~a | b");
        System.out.println();
        truthTable("a | b & (~c | d)");
    }

    @Test
    public void testNot() {
        ExpressionVariables ev = parse("~a");
        Expression e = ev.expression();
        assertFalse(e.test(Map.of('a', true)));
        assertTrue(e.test(Map.of('a', false)));
    }

    @Test
    public void testOr() {
        ExpressionVariables ev = parse("a | b");
        Expression e = ev.expression();
        assertTrue(e.test(Map.of('a', true, 'b', true)));
        assertTrue(e.test(Map.of('a', true, 'b', false)));
        assertTrue(e.test(Map.of('a', false, 'b', true)));
        assertFalse(e.test(Map.of('a', false, 'b', false)));
    }

}
