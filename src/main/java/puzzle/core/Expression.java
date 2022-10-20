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
            
            boolean eat(int expect) {
                if (ch == expect) {
                    get();
                    return true;
                }
                return false;
            }
            
            Expression parse() {
                return v -> 1.23;
            }
            
        }.parse();
    }
}
