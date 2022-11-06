package test.puzzle.core;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import test.puzzle.core.TestExpression2.Expression.EvalException;
import test.puzzle.core.TestExpression2.Expression.ParseException;

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
        double eval(Map<String, Expression> context) throws EvalException;
        
        public static class ParseException extends Exception {
            private static final long serialVersionUID = 1L;
            public ParseException(String message) {
                super(message);
            }
        }
        
        public static class EvalException extends Exception {
            private static final long serialVersionUID = 1L;
            public EvalException(String format, Object... args) {
                super(format.formatted(args));
            }
        }

        public static Expression of(String input) throws ParseException {
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
                
                ParseException parseError(String format, Object... args) {
                    return new ParseException(format.formatted(args));
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

                Expression atom() throws ParseException {
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
                                throw new EvalException("undefined variable '%s'", variable);
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

                Expression factor() throws ParseException {
                    Expression atom = atom();
                    if (eat('^')) {
                        Expression left = atom, right = factor();
                        atom = context -> Math.pow(left.eval(context), right.eval(context));
                    }
                    return atom;
                }

                Expression term() throws ParseException {
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

                Expression expression() throws ParseException {
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
                        public double eval(Map<String, Expression> context) throws EvalException {
                            return e.eval(context);
                        }
                        @Override
                        public String toString() {
                            return string;
                        }
                    };
                    return term;
                }

                Expression parse() throws ParseException {
                    Expression expression = expression();
                    if (ch != -1)
                        throw parseError("extra string '%s'", input.substring(index));
                    return expression;
                }
            }.parse();
        }
    }

    static final double DELTA = 5e-6;
    
    @Test
    public void testOf() throws ParseException, EvalException {
        Map<String, Expression> context = Map.of();
        assertEquals(3.0, Expression.of("1+2").eval(context), DELTA);
    }
    
    @Test
    public void testToString() throws ParseException {
        assertEquals("x^2 + 2 * x + 1", Expression.of("x^2 + 2 * x + 1").toString());
    }
    
    @Test
    public void testVariable() throws ParseException, EvalException {
        Map<String, Expression> context = Map.of("x", Expression.of("2"));
        assertEquals(3.0, Expression.of("x + 1").eval(context), DELTA);
        assertEquals(9.0, Expression.of("x^2 + 2 * x + 1").eval(context), DELTA);
    }
    
    @Test
    public void testMinus() throws ParseException, EvalException {
        Map<String, Expression> context = Map.of();
        assertEquals(-3.0, Expression.of("- 3.0").eval(context), DELTA);
    }
    
    @Test
    public void testSubtract() throws ParseException, EvalException {
        Map<String, Expression> context = Map.of();
        assertEquals(-2.0, Expression.of("1.0 - 3.0").eval(context), DELTA);
    }
    
    @Test
    public void testDivision() throws ParseException, EvalException {
        Map<String, Expression> context = Map.of();
        assertEquals(1 / 3.0, Expression.of("1.0 / 3.0").eval(context), DELTA);
    }
    
    @Test
    public void testParen() throws ParseException, EvalException {
        Map<String, Expression> context = Map.of();
        assertEquals(7.0, Expression.of("1 + 2 * 3").eval(context), DELTA);
        assertEquals(9.0, Expression.of("(1 + 2) * 3").eval(context), DELTA);
    }
    
    @Test
    public void testExtraString() {
        try {
            Expression.of("2 * (3 + 4) 2");
            fail();
        } catch (ParseException e) {
            assertEquals("extra string '2'", e.getMessage());
        }
        try {
            Expression.of("3 + 4)");
            fail();
        } catch (ParseException e) {
            assertEquals("extra string ')'", e.getMessage());
        }
    }
}
