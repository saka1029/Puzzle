package test.puzzle.core;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * サロゲートペア対応。
 * 入力は文字列に限る。(Readerは使えない)
 * StringBuilderを使わない。(トークンは入力文字列の開始・終了位置で示せる)
 *
 * <pre>
 * Syntax
 * expression = term { ("+" | "-" ) term }
 * term       = factor { ( "*" | "/" ) factor }
 * factor     = atom [ '^' factor ]
 * atom       = [ "-" ] ( "(" expression ")" | variable | number )
 * </pre>

 */
public class TestExpression2 {

    public interface Expression {
        double eval(Map<String, Expression> context);
        
        public static Expression of(String input) {
            return new Object() {
                int length = input.length(), index = 0, nextIndex = 0, ch = get();
                
                int get() {
                    index = nextIndex;
                    if (nextIndex >= length)
                        return ch = -1;
                    ch = input.codePointAt(nextIndex);
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
                
                RuntimeException parseError(String format, Object... args) {
                    return new RuntimeException(format.formatted(args));
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

                Expression atom() {
                    Expression atom;
                    boolean sign = eat('-');
                    if (eat('(')) {
                        atom = expression();
                        if (!eat(')'))
                            parseError("')' expected");
                    } else if (isDigit(ch)) {
                        int start = index;
                        integer();
                        if (eat('.'))
                            integer();
                        if (eat('e') || eat('E')) {
                            eat('-');
                            integer();
                        }
                        double value = Double.parseDouble(input.substring(start, index));
                        atom = context -> value;
                    } else if (isIdFirst(ch)) {
                        int start = index;
                        get();
                        while (isIdRest(ch))
                            get();
                        String variable = input.substring(start, index);
                        atom = context -> {
                            Expression e = context.get(variable);
                            if (e == null)
                                throw new RuntimeException("undefined variable '%s'".formatted(variable));
                            return e.eval(context);
                        };
                    } else
                        throw parseError("unknown char '%c'", ch);
                    if (sign) {
                        Expression unary = atom;
                        atom = context -> -unary.eval(context);
                    }
                    return atom;
                }

                Expression factor() {
                    Expression atom = atom();
                    if (eat('^')) {
                        Expression left = atom, right = factor();
                        atom = context -> Math.pow(left.eval(context), right.eval(context));
                    }
                    return atom;
                }

                Expression term() {
                    Expression factor = factor();
                    while (true)
                        if (eat('*')) {
                            Expression left = factor, right = factor();
                            factor = context -> left.eval(context) * right.eval(context);
                        } else if (eat('/')) {
                            Expression left = factor, right = factor();
                            factor = context -> left.eval(context) / right.eval(context);
                        } else
                            break;
                    return factor;

                }

                Expression expression() {
                    int start = index;
                    Expression term = term();
                    while (true)
                        if (eat('+')) {
                            Expression left = term, right = term();
                            term = context -> left.eval(context) + right.eval(context);
                        } else if (eat('-')) {
                            Expression left = term, right = term();
                            term = context -> left.eval(context) - right.eval(context);
                        } else
                            break;
                    String string = input.substring(start, index).trim();
                    Expression e = term;
                    term = new Expression() {
                        @Override
                        public double eval(Map<String, Expression> context) {
                            return e.eval(context);
                        }
                        @Override
                        public String toString() {
                            return string;
                        }
                    };
                    return term;
                }

                Expression parse() {
                    Expression expression = expression();
                    return expression;
                }
            }.parse();
        }
    }

    static final double DELTA = 5e-6;
    
    @Test
    public void testOf() {
        Map<String, Expression> context = Map.of();
        assertEquals(3.0, Expression.of("1+2").eval(context), DELTA);
    }
    
    @Test
    public void testToString() {
        assertEquals("x^2 + 2 * x + 1", Expression.of("x^2 + 2 * x + 1").toString());
    }
    
    @Test
    public void testVariable() {
        Map<String, Expression> context = Map.of("x", Expression.of("2"));
        assertEquals(3.0, Expression.of("x + 1").eval(context), DELTA);
        assertEquals(9.0, Expression.of("x^2 + 2 * x + 1").eval(context), DELTA);
    }
    
    @Test
    public void testMinus() {
        Map<String, Expression> context = Map.of();
        assertEquals(-3.0, Expression.of("- 3.0").eval(context), DELTA);
    }
    
    @Test
    public void testSubtract() {
        Map<String, Expression> context = Map.of();
        assertEquals(-2.0, Expression.of("1.0 - 3.0").eval(context), DELTA);
    }
    
    @Test
    public void testDivision() {
        Map<String, Expression> context = Map.of();
        assertEquals(1 / 3.0, Expression.of("1.0 / 3.0").eval(context), DELTA);
    }
}
