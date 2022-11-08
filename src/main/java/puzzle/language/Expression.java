package puzzle.language;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

/**
 * 
 * <pre>
 * SYNTAX
 * expression = term { ("+" | "-" ) term }
 * term       = factor { ( "*" | "/" ) factor }
 * factor     = atom [ '^' factor ]
 * atom       = [ "-" ] ( "(" expression ")" | varfunc | number )
 * varfunc    = id [ "(" [ expression { "," expression } ] ")" ]
 * </pre>
 */
@FunctionalInterface
public interface Expression {
    
    double eval(
        Map<String, Double> variables,
        Map<String, DFunction> functions) throws EvalException;
    
    @FunctionalInterface
    public interface DFunction {
        double eval(double... args);
    }

    public static class ParseException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public ParseException(String format, Object... args) {
            super(format.formatted(args));
        }
    }

    public static class EvalException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public EvalException(String format, Object... args) {
            super(format.formatted(args));
        }
    }
    
    static final Random RANDOM = new Random();

    public static final Map<String, DFunction> STANDARD_FUNCTIONS = Map.ofEntries(
        Map.entry("acos", a -> Math.acos(a[0])),
        Map.entry("asin", a -> Math.asin(a[0])),
        Map.entry("atan", a -> Math.atan(a[0])),
        Map.entry("ceil", a -> Math.ceil(a[0])),
        Map.entry("cos", a -> Math.cos(a[0])),
        Map.entry("cosh", a -> Math.cosh(a[0])),
        Map.entry("exp", a -> Math.exp(a[0])),
        Map.entry("floor", a -> Math.floor(a[0])),
        Map.entry("hypot", a-> Math.hypot(a[0], a[1])),
        Map.entry("log", a -> Math.log(a[0])),
        Map.entry("log10", a -> Math.log10(a[0])),
        Map.entry("max", a-> Math.max(a[0], a[1])),
        Map.entry("min", a-> Math.min(a[0], a[1])),
        Map.entry("pow", a-> Math.pow(a[0], a[1])),
        Map.entry("random", a-> RANDOM.nextDouble()),
        Map.entry("round", a-> Math.round(a[0])),
        Map.entry("signum", a -> Math.signum(a[0])),
        Map.entry("sin", a -> Math.sin(a[0])),
        Map.entry("sinh", a -> Math.sinh(a[0])),
        Map.entry("sqrt", a -> Math.sqrt(a[0])),
        Map.entry("tan", a -> Math.tan(a[0])),
        Map.entry("tanh", a -> Math.tanh(a[0])),
        Map.entry("toRadians", a -> Math.toRadians(a[0])),
        Map.entry("toDegrees", a -> Math.toDegrees(a[0]))
    );

    public static Expression of(String source) {
        return new Object() {
            int length = source.length(), index, nextIndex = 0, ch = get();
            
            int get() {
                index = nextIndex;
                if (index >= length)
                    return ch = -1;
                ch = source.codePointAt(nextIndex);
                nextIndex += Character.isSupplementaryCodePoint(ch) ? 2 : 1;
                return ch;
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
            
            boolean isDigit(int ch) {
                return ch >= '0' && ch <= '9';
            }
            
            boolean isIdFirst(int ch) {
                return Character.isJavaIdentifierStart(ch);
            }

            boolean isIdRest(int ch) {
                return Character.isJavaIdentifierPart(ch);
            }
            
            void integer() {
                while (isDigit(ch))
                    get();
            }
            
            Expression number() {
                int start = index;
                integer();
                if (eat('.'))
                    integer();
                if (eat('e') || eat('E')) {
                    if (eat('-') || eat('+'))
                        /* do nothing */;
                    integer();
                }
                double value = Double.parseDouble(source.substring(start, index));
                return (v, f) -> value;
            }

            Expression varfunc() {
                int start = index;
                do {
                    get();
                } while (isIdRest(ch));
                String name = source.substring(start, index);
                if (!eat('('))
                    return (v, f) -> {
                        Double d = v.get(name);
                        if (d == null)
                            throw new EvalException("variable '%s' undefined", name);
                        return d;
                    };
                List<Expression> args = new ArrayList<>();
                if (!eat(')')) {
                    do {
                        args.add(expression());
                    } while (eat(','));
                    if (!eat(')'))
                        throw new ParseException("')' expected");
                }
                return (v, f) -> {
                    DFunction e = f.get(name);
                    if (e == null)
                        throw new EvalException("function '%s' undefined", name);
                    double[] a = args.stream().mapToDouble(x -> x.eval(v, f)).toArray();
                    return e.eval(a);
                };
            }

            Expression atom() {
                boolean minus = eat('-');
                Expression atom;
                if (isDigit(ch))
                    atom = number();
                else if (isIdFirst(ch))
                    atom = varfunc();
                else if (eat('(')) {
                    atom = expression();
                    if (!eat(')'))
                        throw new ParseException("'(' expected");
                } else
                    throw new ParseException("unknown char '%c'", ch);
                if (minus) {
                    Expression e = atom;
                    atom = (v, f) -> -e.eval(v, f);
                }
                return atom;
            }

            Expression factor() {
                Expression atom = atom();
                if (eat('^')) {
                    Expression left = atom, right = atom();
                    atom = (v, f) -> Math.pow(left.eval(v, f), right.eval(v, f));
                }
                return atom;
            }

            Expression term() {
                Expression factor = factor();
                while (true) {
                    if (eat('*')) {
                        Expression left = factor, right = factor();
                        factor = (v, f) -> left.eval(v, f) * right.eval(v, f);
                    } else if (eat('/')) {
                        Expression left = factor, right = factor();
                        factor = (v, f) -> left.eval(v, f) / right.eval(v, f);
                    } else
                        break;
                }
                return factor;
            }

            Expression expression() {
                Expression term = term();
                while (true) {
                    if (eat('+')) {
                        Expression left = term, right = term();
                        term = (v, f) -> left.eval(v, f) + right.eval(v, f);
                    } else if (eat('-')) {
                        Expression left = term, right = term();
                        term = (v, f) -> left.eval(v, f) - right.eval(v, f);
                    } else
                        break;
                }
                return term;
            }

            Expression parse() {
                Expression e = expression();
                return e;
            }
        }.parse();
    }

}
