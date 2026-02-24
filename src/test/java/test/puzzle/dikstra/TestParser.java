package test.puzzle.dikstra;

import org.junit.Test;
import puzzle.dikstra.Parser;
import static org.junit.Assert.assertEquals;
import static puzzle.dikstra.Parser.*;

import java.util.List;

public class TestParser {

    @Test
    public void testTokenType() {
        System.out.println(TokenType.MAP);
        System.out.println(Character.isLetter('漢'));
    }

    static final Token N123 = new Token(TokenType.NUMBER, "123");
    static final Token N456 = new Token(TokenType.NUMBER, "456");
    static final Token A = new Token(TokenType.ID, "A");
    static final Token PLUS = new Token(TokenType.PLUS, "+");
    static final Token MINUS = new Token(TokenType.MINUS, "-");
    static final Token MULT = new Token(TokenType.MULT, "*");
    static final Token DIV = new Token(TokenType.DIV, "/");

    @Test
    public void testParser() {
        assertEquals(List.of(N123, N456, PLUS), Parser.parse("123+456"));
        assertEquals(List.of(A, N456, PLUS), Parser.parse("A+456"));
        assertEquals(List.of(N123, A, PLUS), Parser.parse("123+A"));
    }

    @Test
    public void testParen() {
        assertEquals(List.of(N123, N456, PLUS), Parser.parse("(123+456)"));
    }

    // @Test
    // public void testWikipedia() {
    //     Parser.parse("3 + 4 * 2 / ( 1 - 5 ) ^ 2 ^ 3");
    // }

}
