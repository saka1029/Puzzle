package test.puzzle.dikstra;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import puzzle.dikstra.Parser;

import static puzzle.dikstra.Parser.*;

public class TestParser {

    static Token t(Type t, String s) {
        return new Token(t, s);
    }

    @Test
    public void testParser() {
        Parser parser = new Parser();
        assertEquals(List.of(t(Type.NUMBER, "123"), t(Type.NUMBER, "456"), t(Type.OPERATOR, "+")), parser.parse("123+456"));
        assertEquals(List.of(t(Type.NUMBER, "123"), t(Type.NUMBER, "456"), t(Type.OPERATOR, "+")), parser.parse("(123+456)"));
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
        assertEquals(expected, new Parser().parse("3 + 4 * 2 / ( 1 - 5 ) ^ 2 ^ 3"));
    }

    @Test
    public void testParser2Map() {
        Map<String, Operator> operators = Map.of(
            "plus", new Operator(3, true),
            "minus", new Operator(3, true),
            "mult", new Operator(5, true),
            "div", new Operator(5, true),
            "pow", new Operator(7, false));
        Parser parser = new Parser(operators);
        List<Token> expected = List.of(
            new Token(Type.NUMBER, "3"),
            new Token(Type.NUMBER, "4"),
            new Token(Type.NUMBER, "2"),
            new Token(Type.OPERATOR, "mult"),
            new Token(Type.NUMBER, "1"),
            new Token(Type.NUMBER, "5"),
            new Token(Type.OPERATOR, "minus"),
            new Token(Type.NUMBER, "2"),
            new Token(Type.NUMBER, "3"),
            new Token(Type.OPERATOR, "pow"),
            new Token(Type.OPERATOR, "pow"),
            new Token(Type.OPERATOR, "div"),
            new Token(Type.OPERATOR, "plus")
        );
        assertEquals(expected, parser.parse("3 plus 4 mult 2 div ( 1 minus 5 ) pow 2 pow 3"));

    }
}
