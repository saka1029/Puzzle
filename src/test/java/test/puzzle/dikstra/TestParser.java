package test.puzzle.dikstra;

import org.junit.Test;
import puzzle.dikstra.Parser;
import static puzzle.dikstra.Parser.*;

public class TestParser {

    @Test
    public void testTokenType() {
        System.out.println(TokenType.MAP);
        System.out.println(Character.isLetter('漢'));
    }

    @Test
    public void testScanner() {
        Parser.parse("346 + ( 変数 - 2  , 3 / 2 )");
    }

}
