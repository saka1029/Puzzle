package puzzle.language.expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
public interface Expression {
    
    double eval(
        Map<String, Expression> variables,
        Map<String, Func> functions) throws EvalException;
    
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

            Expression varfunc() {
                int start = index;
                do {
                    get();
                } while (isIdRest(ch));
                String name = source.substring(start, index);
                if (!eat('('))
                    return (v, f) -> {
                        Expression e = v.get(name);
                        if (e == null)
                            throw new EvalException("variable '%s' undefined", name);
                        return e.eval(v, f);
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
                    Func e = f.get(name);
                    if (e == null)
                        throw new EvalException("function '%s' undefined", name);
                    double[] a = args.stream().mapToDouble(x -> x.eval(v, f)).toArray();
                    return e.eval(a);
                };
            }

            Expression atom() {
                boolean minus = eat('-');
                Expression atom;
                if (eat('(')) {
                    atom = expression();
                    if (!eat(')'))
                        throw new ParseException("'(' expected");
                } else if (isDigit(ch)) {
                    int start = index;
                    integer();
                    if (eat('.'))
                        integer();
                    if (eat('e') || eat('E')) {
                        if (!eat('-'))
                            eat('+');
                        integer();
                    }
                    double value = Double.parseDouble(source.substring(start, index));
                    atom = (v, f) -> value;
                } else if (isIdFirst(ch)) {
                    atom = varfunc();
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
                return atom();
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
