package puzzle.core;

import java.util.Map;
import java.util.Objects;

/**
 * 数式をコンパイルします。
 * 
 * <pre>
 * * Syntax (1st version) *
 * expression = term { ("+" | "-" ) term }
 * term       = factor { ( "*" | "/" ) factor }
 * factor    = [ "-" ] ( "(" expression ")" | variable | number )
 * </pre>
 */
public interface Expression {

    double eval(Map<String, Double> variables);

    public class ParseException extends RuntimeException {
        private static final long serialVersionUID = 1L;
        ParseException(String format, Object... args) {
            super(format.formatted(args));
        }
    }

    public static Expression of(String s) {
        return new Object() {
            int length = s.length(), index = 0, ch = get();
            StringBuilder sb = new StringBuilder();
            
            void bufferClear() {
                sb.setLength(0);
            }
            
            void bufferAppend(int ch) {
                sb.append((char)ch);
            }
            
            void bufferAppendGet(int ch) {
                bufferAppend(ch);
                get();
            }
            
            String bufferString() {
                return sb.toString();
            }
            
            int get() {
                return ch = index < length ? s.charAt(index++) : -1;
            }
            
            void spaces() {
                while (Character.isWhitespace(ch))
                    get();
            }

            boolean eat(int expect) {
                spaces();
                if (ch == expect) {
                    get();
                    return true;
                }
                return false;
            }
            
            void appendInteger() {
                while (Character.isDigit(ch))
                    bufferAppendGet(ch);
            }
            
            double number() {
                bufferClear();
                appendInteger();
                if (ch == '.') {
                    bufferAppendGet(ch);
                    appendInteger();
                }
                if (ch == 'e' || ch == 'E') {
                    bufferAppendGet(ch);
                    if (ch == '-')
                        bufferAppendGet(ch);
                    appendInteger();
                }
                return Double.parseDouble(bufferString());
            }
            
            String variable() {
                bufferClear();
                bufferAppendGet(ch);
                while (Character.isAlphabetic(ch) || Character.isDigit(ch) || ch == '_')
                    bufferAppendGet(ch);
                return bufferString();
            }

            Expression factor() {
                Expression e;
                boolean minus = false;
                if (eat('-'))
                    minus = true;
                if (eat('(')) {
                    e = expression();
                    if (!eat(')'))
                        throw new ParseException("')' expected");
                } else if (Character.isDigit(ch)) {
                    double value = number();
                    e = v -> value;
                } else if (Character.isAlphabetic(ch)) {
                    String name = variable();
                    e = v -> {
                        Double value = v.get(name);
                        if (value == null)
                            throw new RuntimeException("undefined variable `%s`".formatted(name));
                        return value;
                    };
                } else
                    throw new ParseException("unknown char '%c'", (char)ch);
                if (minus) {
                    Expression org = e;
                    e = v -> -org.eval(v);
                }
                return e;
            }

            Expression term() {
                Expression e = factor();
                while (true) {
                    if (eat('*')) {
                        Expression l = e, r = factor();
                        e = v -> l.eval(v) * r.eval(v);
                    } else if (eat('/')) {
                        Expression l = e, r = factor();
                        e = v -> l.eval(v) / r.eval(v);
                    } else
                        break;
                }
                return e;
            }

            Expression expression() {
                int start = index - 1;
                Expression e = term();
                while (true) {
                    if (eat('+')) {
                        Expression l = e, r = term();
                        e = v -> l.eval(v) + r.eval(v);
                    } else if (eat('-')) {
                        Expression l = e, r = term();
                        e = v -> l.eval(v) - r.eval(v);
                    } else
                        break;
                }
<<<<<<< Updated upstream
                int end = index;
                Expression re = e;
                String rs = s.substring(start, end).trim();
=======
                return e;
            }

            Expression parse() {
                Expression e = expression();
                if (ch != -1)
                    throw new ParseException("extra string '%s'", s.substring(index - 1));
>>>>>>> Stashed changes
                return new Expression() {
                    @Override
                    public double eval(Map<String, Double> variables) {
                        return re.eval(variables);
                    }
                    @Override
                    public String toString() {
                        return rs;
                    }
                };
            }

            Expression parse() {
                Expression e = expression();
                if (index < length)
                    throw new ParseException("extra string '%s'", s.substring(index - 1));
                return e;
            }
            
        }.parse();
    }
}
