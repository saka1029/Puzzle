package test.puzzle.core;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

/**
 * サロゲートペア対応。
 * 入力は文字列に限る。(Readerは使えない)
 * StringBuilderを使わない。(トークンは入力文字列の開始・終了位置で示せる)
 *
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

                Expression parse() {
                    
                }
            }.parse();
        }
    }

    @Test
    public void test() {
        fail("Not yet implemented");
    }

}
