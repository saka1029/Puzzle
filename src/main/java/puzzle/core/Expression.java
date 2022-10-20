package puzzle.core;

import java.util.Map;

/**
 * <pre>
 * *** 1st version ***
 * expression = term { ("+" | "-" ) term }
 * term       = factor { ( "*" | "/" ) factor }
 * factor    = [ "-" ] ( "(" expression ")" | variable | number )
 * </pre>
 * 
 * <pre>
 * expression = term { ("+" | "-") term }
 * term       = factor { ("*" | "/" ) factor }
 * factor     = primary [ "^" factor ]
 * primary    = primary | element
 * element    = [ "-" ] ( "(" expression ")" | variable | number )
 * </pre>
 */
public interface Expression {

    double eval(Map<String, Double> variables);

    public static Expression of(String s) {
        return new Object() {
            int length = s.length(), index = 0, ch = get();
            
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
            
            Expression factor() {
                
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
                return e;
            }
            
        }.parse();
    }
}
