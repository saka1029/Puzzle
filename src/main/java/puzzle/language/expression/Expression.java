package puzzle.language.expression;

import java.util.Map;

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

            Expression parse() {
                
            }
        }.parse();
    }

}
