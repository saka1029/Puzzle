package test.puzzle.dikstra;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import static puzzle.dikstra.Parser2.*;

public class TestParser2 {

    static Token t(Type t, String s) {
        return new Token(t, s);
    }

    @Test
    public void testParser() {
        assertEquals(List.of(t(Type.NUMBER, "123"), t(Type.NUMBER, "456"), t(Type.OPERATOR, "+")), parse("123+456"));
        assertEquals(List.of(t(Type.NUMBER, "123"), t(Type.NUMBER, "456"), t(Type.OPERATOR, "+")), parse("(123+456)"));
    }

    /**
     * 操車場アルゴリズム
     * 詳細な実施例
     * https://ja.wikipedia.org/wiki/%E6%93%8D%E8%BB%8A%E5%A0%B4%E3%82%A2%E3%83%AB%E3%82%B4%E3%83%AA%E3%82%BA%E3%83%A0#%E8%A9%B3%E7%B4%B0%E3%81%AA%E5%AE%9F%E6%96%BD%E4%BE%8B
     */
    @Test
    public void testWikipedia() {
        List<Token> expected = List.of(
            new Token(Type.NUMBER, "3"),
            new Token(Type.NUMBER, "4"),
            new Token(Type.NUMBER, "2"),
            new Token(Type.OPERATOR, "*"),
            new Token(Type.NUMBER, "1"),
            new Token(Type.NUMBER, "5"),
            new Token(Type.OPERATOR, "-"),
            new Token(Type.NUMBER, "2"),
            new Token(Type.NUMBER, "3"),
            new Token(Type.OPERATOR, "^"),
            new Token(Type.OPERATOR, "^"),
            new Token(Type.OPERATOR, "/"),
            new Token(Type.OPERATOR, "+")
        );
        assertEquals(expected, parse("3 + 4 * 2 / ( 1 - 5 ) ^ 2 ^ 3"));
    }
}
