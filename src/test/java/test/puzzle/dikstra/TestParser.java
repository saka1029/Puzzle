package test.puzzle.dikstra;

import org.junit.Test;
import puzzle.dikstra.Parser;
import static puzzle.dikstra.Parser.*;

import java.util.List;

public class TestParser {

    @Test
    public void testTokenType() {
        System.out.println(TokenType.MAP);
        System.out.println(Character.isLetter('漢'));
    }

    @Test
    public void testScanner() {
        List<Token> tokens = Parser.parse("346 + (変数34 - 2*9- 3/2)");
        System.out.println(tokens);
    }

}
