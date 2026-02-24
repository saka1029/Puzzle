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
    static final Token POW = new Token(TokenType.POW, "^");
    static final Token LP = Token.LP;
    static final Token RP = Token.RP;
    static final Token COMMA = Token.COMMA;

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

    @Test
    public void testFuncall() {
        Token F = new Token(TokenType.ID, "F");
        assertEquals(List.of(N123, N456, PLUS, N456, F), Parser.parse("F(123+456,456)"));
    }

    /**
     * 操車場アルゴリズム
     * 詳細な実施例
     * https://ja.wikipedia.org/wiki/%E6%93%8D%E8%BB%8A%E5%A0%B4%E3%82%A2%E3%83%AB%E3%82%B4%E3%83%AA%E3%82%BA%E3%83%A0#%E8%A9%B3%E7%B4%B0%E3%81%AA%E5%AE%9F%E6%96%BD%E4%BE%8B
     */
    @Test
    public void testWikipedia() {
        List<Token> expected = List.of(
            new Token(TokenType.NUMBER, "3"),
            new Token(TokenType.NUMBER, "4"),
            new Token(TokenType.NUMBER, "2"),
            MULT,
            new Token(TokenType.NUMBER, "1"),
            new Token(TokenType.NUMBER, "5"),
            MINUS,
            new Token(TokenType.NUMBER, "2"),
            new Token(TokenType.NUMBER, "3"),
            POW,
            POW,
            DIV,
            PLUS
        );
        assertEquals(expected, Parser.parse("3 + 4 * 2 / ( 1 - 5 ) ^ 2 ^ 3"));
    }

}
