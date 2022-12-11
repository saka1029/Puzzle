package puzzle.parsers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;

public interface BooleanExpression extends Predicate<Map<Character, Boolean>> {
    static final BooleanExpression TRUE = context -> true;
    static final BooleanExpression FALSE = context -> false;

    static BooleanExpression variable(char name) {
        return context -> context.get(name);
    }

    default BooleanExpression not() {
        return context -> !test(context);
    }

    default BooleanExpression and(BooleanExpression right) {
        return context -> test(context) && right.test(context);
    }

    default BooleanExpression or(BooleanExpression right) {
        return context -> test(context) || right.test(context);
    }

    default BooleanExpression xor(BooleanExpression right) {
        return context -> test(context) ^ right.test(context);
    }

    default BooleanExpression equals(BooleanExpression right) {
        return context -> test(context) == right.test(context);
    }

    default BooleanExpression implies(BooleanExpression right) {
        return context -> !test(context) || right.test(context);
    }

    public record ExpressionVariables(
        BooleanExpression expression,
        List<Character> variables) {
    }

    static boolean isVariable(int ch) {
        return ch >= 'a' && ch <= 'z';
    }

    /**
     * <pre>
     * [SYNTAX]
     * expression = term { ( '=' | '->' ) term }
     * term       = factor { ( '|' | '^' ) factor }
     * factor     = primary { '&' primary }
     * primary    = [ '!' ] ( 'T' | 'F' | variable | '(' expression ')' )
     * variable   = 'a' | 'b' | ... | 'z'
     * </pre>
     */
    public static ExpressionVariables parse(String source) {
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

            BooleanExpression primary() {
                BooleanExpression e;
                boolean not = eat('!');
                if (eat('T')) {
                    e = BooleanExpression.TRUE;
                } else if (eat('F')) {
                    e = BooleanExpression.FALSE;
                } else if (isVariable(ch)) {
                    char name = (char) ch;
                    get();
                    variables.add(name);
                    e = BooleanExpression.variable(name);
                } else if (eat('(')) {
                    e = expression();
                    if (!eat(')'))
                        throw new RuntimeException("')' expected");
                } else
                    throw new RuntimeException(
                        "unknown char '" + (char) ch + "'");
                if (not)
                    e = e.not();
                return e;
            }

            BooleanExpression factor() {
                BooleanExpression e = primary();
                while (eat('&'))
                    e = e.and(primary());
                return e;
            }

            BooleanExpression term() {
                BooleanExpression e = factor();
                while (true)
                    if (eat('|'))
                        e = e.or(factor());
                    else if (eat('^'))
                        e = e.xor(factor());
                    else
                        break;
                return e;
            }

            BooleanExpression expression() {
                BooleanExpression e = term();
                while (true) {
                    if (eat('='))
                        e = e.equals(term());
                    else if (eat('-')) {
                        if (!eat('>'))
                            throw new RuntimeException("'->' expected");
                        e = e.implies(term());
                    }
                    break;
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

    public static void truthTable(String source) {
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
        System.out.println();
    }
}
