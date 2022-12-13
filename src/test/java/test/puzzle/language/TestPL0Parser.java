package test.puzzle.language;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class TestPL0Parser {

    static final List<String> RESERVED = List.of(
        "const", "var", "procedure", "call", "begin", "end", "if", "then", "while", "do", "odd");

    /**
     * program = block "." .
     * block      = [ "const" ident "=" number {"," ident "=" number} ";"]
     *              [ "var" ident {"," ident} ";"]
     *              { "procedure" ident ";" block ";" } statement .
     * statement  = [ ident ":=" expression | "call" ident |
     *              "begin" statement {";" statement } "end" |
     *              "if" condition "then" statement |
     *              "while" condition "do" statement ].
     * condition  = "odd" expression |
     *              expression ("="|"#"|"<"|"<="|">"|">=") expression .
     * expression = [ "+"|"-"] term { ("+"|"-") term}.
     * term       = factor {("*"|"/") factor}.
     * factor     = ident | number | "(" expression ")".
     */
    public static void parse(String source) {
        int length = source.length();
        new Object() {
            int index = 0;
            String token;
            
            RuntimeException error(String format, Object... args) {
                return new RuntimeException(format.formatted(args));
            }

            int ch() {
                return index < length ? source.charAt(index) : -1;
            }
            
            void spaces() {
                while (Character.isWhitespace(ch()))
                    ++index;
            }

            boolean eat(String... expects) {
                spaces();
                for (String expect : expects)
                    if (source.startsWith(expect, index)) {
                        token = expect;
                        index += expect.length();
                        return true;
                    }
                return false;
            }
            
            boolean isNumber(int ch) {
                return ch >= '0' && ch <= '9';
            }
            
            boolean isIdFirst(int ch) {
                return ch >= 'a' && ch <= 'z'
                    || ch >= 'A' && ch <= 'Z';
            }
            
            boolean isIdRest(int ch) {
                return isIdFirst(ch) || isNumber(ch);
            }

            boolean eatWord(String expected) {
                spaces();
                if (!isIdFirst(ch()))
                    return false;
                int start = index;
                do {
                    ++index;
                } while (isIdRest(ch()));
                token = source.substring(start, index);
                if (expected == null && !RESERVED.contains(token.toLowerCase()))
                    return true;
                if (expected != null && expected.equalsIgnoreCase(token))
                    return true;
                index = start;
                return false;
            }

            boolean eatNumber() {
                spaces();
                if (!isNumber(ch()))
                    return false;
                int start = index;
                do {
                    ++index;
                } while (isNumber(ch()));
                token = source.substring(start, index);
                return true;
            }
            
            void factor() {
                if (eat("(")) {
                    expression();
                    if (!eat(")"))
                        throw error("')' expected");
                } else if (eatNumber()) {
                } else if (eatWord(null)) {
                }
            }

            void term() {
                factor();
                while (eat("*", "/"))
                    factor();
            }
            
            void expression() {
                if (eat("+", "-")) {
                }
                term();
                while (eat("+", "-"))
                    term();
            }
            
            void condition() {
                if (eat("odd")) {
                    expression();
                } else {
                    expression();
                    if (!eat("=", "#", "<=", ">=", "<", ">"))
                        throw error("'=', '#', '<', '<=', '>' or '>=' expected");
                    expression();
                }
            }
            
            void statement() {
                if (eatWord(null)) {
                    if (!eat(":="))
                        throw error("':=' expected");
                    expression();
                } else if (eatWord("call")) {
                    if (!eatWord(null))
                        throw error("identifier expected");
                } else if (eat("!")) {
                    expression();
                } else if (eat("?")) {
                    if (!eatWord(null))
                        throw error("identifier expected");
                } else if (eatWord("begin")) {
                    do {
                        statement();
                    } while (eat(";"));
                    if (!eatWord("end"))
                        throw error("'end' expected");
                } else if (eatWord("if")) {
                    condition();
                    if (!eatWord("then"))
                        throw error("'then' expected");
                    statement();
                } else if (eatWord("while")) {
                    condition();
                    if (!eatWord("do"))
                        throw error("'do' expected");
                    statement();
                }
            }

            void block() {
                if (eatWord("const")) {
                    do {
                        if (!eatWord(null))
                            throw error("identifier expected");
                        if (!eat("="))
                            throw error("'=' expected");
                        if (!eatNumber())
                            throw error("number expected");
                    } while (eat(","));
                    if (!eat(";"))
                        throw error("';' expected");
                }
                if (eatWord("var")) {
                    do {
                        if (!eatWord(null))
                            throw error("identifier expected");
                    } while (eat(","));
                    if (!eat(";"))
                        throw error("';' expected");
                }
                if (eatWord("procedure")) {
                    if (!eatWord(null))
                        throw error("identifier expected");
                    if (!eat(";"))
                        throw error("';' expected");
                    block();
                    if (!eat(";"))
                        throw error("';' expected");
                }
                statement();
            }

            void program() {
                block();
                if (!eat("."))
                    throw error("'.' expected");
            }

            void parse() {
                program();
            }
        }.parse();
    }
    
    enum Type { RESERVED, IDENT, NUMBER, END; }
    static void parserTokenize(String source) {
        new Object() {
            int length = source.length(), index = 0;
            Type type;
            String nextToken, token;
            
            RuntimeException error(String format, Object... args) {
                return new RuntimeException(format.formatted(args));
            }
            
            int ch() {
                return index < length ? source.charAt(index) : -1;
            }
            
            boolean isNumber(int ch) {
                return ch >= '0' && ch <= '9';
            }
            
            boolean isIdFirst(int ch) {
                return ch >= 'a' && ch <= 'z'
                    || ch >= 'A' && ch <= 'Z';
            }
            
            boolean isIdRest(int ch) {
                return isIdFirst(ch) || isNumber(ch);
            }

            void get() {
                while (Character.isWhitespace(ch()))
                    ++index;
                int start = index;
                switch (ch()) {
                    case -1:
                        type = Type.END;
                        nextToken = null;
                        break;
                    case '(': case ')': case ';': case ',': case '.':
                    case '?': case '!': case '=': case '#':
                    case '+': case '-': case '*': case '/':
                        type = Type.RESERVED;
                        nextToken = Character.toString(ch());
                        ++index;
                        break;
                    case '<': case '>':  case ':':
                        char f = (char)ch();
                        ++index;
                        type = Type.RESERVED;
                        if (ch() == '=') {
                            nextToken = f + "=";
                            ++index;
                        } else
                            nextToken = Character.toString(f);
                        break;
                    default:
                        if (isIdFirst(ch())) {
                            do {
                                ++index;
                            } while (isIdRest(ch()));
                            nextToken = source.substring(start, index);
                            type = RESERVED.contains(nextToken.toLowerCase()) ? Type.RESERVED : Type.IDENT;
                        } else if (isNumber(ch())) {
                            do {
                                ++index;
                            } while (isNumber(ch()));
                            type = Type.NUMBER;
                            nextToken = source.substring(start, index);
                        }
                        break;
                }
            }
            
            boolean eat(String... expects) {
                for (String expect : expects)
                    if (nextToken.equalsIgnoreCase(expect)) {
                        token = nextToken;
                        get();
                        return true;
                    }
                return false;
            }
            
            boolean eat(Type expect) {
                if (expect == type) {
                    token = nextToken;
                    get();
                    return true;
                }
                return false;
            }
            
            void check(String message, String... expects) {
                if (!eat(expects))
                    throw error(message);
            }
            
            void check(String message, Type type) {
                if (!eat(type))
                    throw error(message);
            }
            
            void factor() {
                if (eat(Type.IDENT)) {
                } else if (eat(Type.NUMBER)) {
                } else if (eat("(")) {
                    expression();
                    check("')' expected", ")");
                }
            }

            void term() {
                factor();
                while (eat("*", "/"))
                    factor();
            }

            void expression() {
                if (eat("+", "-")) {
                }
                term();
                while (eat("+", "-"))
                    term();
            }

            void condition() {
                if (eat("odd")) {
                    expression();
                } else {
                    expression();
                    check("'=', '#', '<', '<=', '>', '>=' expected",
                        "=", "#", "<", "<=", ">", ">=");
                    expression();
                }
            }

            void statement() {
                if (eat(Type.IDENT)) {
                    check("':=' expected", ":=");
                    expression();
                } else if (eat("call")) {
                    check("ident expected", Type.IDENT);
                } else if (eat("begin")) {
                    do {
                        statement();
                    } while (eat(";"));
                    check("'end' expected", "end");
                } else if (eat("if")) {
                    condition();
                    check("'then' expected", "then");
                    statement();
                } else if (eat("while")) {
                    condition();
                    check("'do' expected", "do");
                    statement();
                } else if (eat("?")) {
                    check("ident expected", Type.IDENT);
                } else if (eat("!")) {
                    expression();
                }
            }

            void block() {
                if (eat("const")) {
                    do {
                        check("ident expected", Type.IDENT);
                        check("=", "'=' expected");
                        check("number expected", Type.NUMBER);
                    } while (eat(","));
                    check("';' expected", ";");
                } 
                if (eat("var")) {
                    do {
                        check("ident expected", Type.IDENT);
                    } while (eat(","));
                    check("';' expected", ";");
                }
                if (eat("procedure")) {
                    check("ident expected", Type.IDENT);
                    check("';' expected", ";");
                    block();
                    check("';' expected", ";");
                }
                statement();
            }

            void program() {
                block();
                check("'.' expected", ".");
            }

            void parse() {
                get();
                program();
                check("extra tokens", Type.END);
            }
        }.parse();
    }

    @Test
    public void testParse() {
        String source = "VAR x, squ;\r\n"
            + "\r\n"
            + "PROCEDURE square;\r\n"
            + "BEGIN\r\n"
            + "   squ := x * x\r\n"
            + "END;\r\n"
            + "\r\n"
            + "BEGIN\r\n"
            + "   x := 1;\r\n"
            + "   WHILE x <= 10 DO\r\n"
            + "   BEGIN\r\n"
            + "      CALL square;\r\n"
            + "      ! squ;\r\n"
            + "      x := x + 1;\r\n"
            + "   END\r\n"
            + "END.";
        parse(source);
    }

    @Test
    public void testParseTokenize() {
        String source = "VAR x, squ;\r\n"
            + "\r\n"
            + "PROCEDURE square;\r\n"
            + "BEGIN\r\n"
            + "   squ := x * x\r\n"
            + "END;\r\n"
            + "\r\n"
            + "BEGIN\r\n"
            + "   x := 1;\r\n"
            + "   WHILE x <= 10 DO\r\n"
            + "   BEGIN\r\n"
            + "      CALL square;\r\n"
            + "      ! squ;\r\n"
            + "      x := x + 1;\r\n"
            + "   END\r\n"
            + "END.";
        parserTokenize(source);
    }

}
