package test.puzzle.core;

import static org.junit.Assert.*;

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
                    if (nextIndex >= length)
                        return -1;
                    index = nextIndex;
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

                Expression atom() {

                }

                Expression factor() {
                    Expression atom = atom();
                    if (eat('^')) {
                        Expression left = atom(), right = factor();
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
                    return term;
                }

                Expression parse() {
                    Expression expression = expression();
                    return expression;
                }
            }.parse();
        }
    }

    @Test
    public void test() {
        fail("Not yet implemented");
    }

}
