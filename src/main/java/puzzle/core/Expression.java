package puzzle.core;

import java.util.Map;
import java.util.Objects;

/**
 * 数式をコンパイルします。
 * 
 * <pre>
 * *** 1st version ***
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
            
            Expression paren() {
                Expression e = expression();
                if (!eat(')'))
                    throw new ParseException("')' expected");
                return e;
            }
            
            Expression number() {
                bufferClear();
                while (Character.isDigit(ch))
                    bufferAppendGet(ch);
                if (ch == '.') {
                    bufferAppendGet(ch);
                    while (Character.isDigit(ch))
                        bufferAppendGet(ch);
                }
                if (ch == 'e' || ch == 'E') {
                    bufferAppendGet(ch);
                    if (ch == '-')
                        bufferAppendGet(ch);
                    while (Character.isDigit(ch))
                        bufferAppendGet(ch);
                }
                double value = Double.parseDouble(bufferString());
                return v -> value;
            }
            
            Expression variable() {
                bufferClear();
                bufferAppendGet(ch);
                while (Character.isAlphabetic(ch) || Character.isDigit(ch) || ch == '_')
                    bufferAppendGet(ch);
                String name = bufferString();
                return v -> {
                    Double value = v.get(name);
                    if (value == null)
                        throw new RuntimeException("variable `%s` not defined".formatted(name));
                    return value;
                };
            }

            Expression factor() {
                Expression e;
                boolean minus = false;
                if (eat('-'))
                    minus = true;
                if (eat('('))
                    e = paren();
                else if (Character.isDigit(ch))
                    e = number();
                else if (Character.isAlphabetic(ch))
                    e = variable();
                else
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
                return e;
            }

            Expression parse() {
                Expression e = expression();
                if (index < length)
                    throw new ParseException("extra string '%s'", s.substring(index - 1));
                return new Expression() {
                    @Override
                    public double eval(Map<String, Double> variables) {
                        return e.eval(variables);
                    }
                    @Override
                    public String toString() {
                        return s;
                    }
                };
            }
            
        }.parse();
    }
}
