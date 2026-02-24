package puzzle.dikstra;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Parser {

    public enum TokenType {
        EOF("EOF", -1, true),
        NUMBER("NUMBER", -1, true),
        ID("ID", -1, true),
        LP("(", -1, true),
        RP(")", -1, true),
        COMMA(",", -1, true),
        POW("^", 4, false),
        MULT("*", 3, true),
        DIV("/", 3, true),
        PLUS("+", 2, true),
        MINUS("-", 2, true);

        TokenType(String string, int priority, boolean leftAssoc) {
            this.string = string;
            this.priority = priority;
            this.leftAssoc = leftAssoc;
        }

        public static final Map<String, TokenType> MAP = Stream.of(values())
            .filter(e -> e.priority >= 0)
            .collect(Collectors.toMap(e -> e.string, e -> e));

        public final String string;
        public final int priority;
        public final boolean leftAssoc;
    }

    public static class Token {
        public static Token EOF = new Token(TokenType.EOF, "");
        public static Token LP = new Token(TokenType.LP, "(");
        public static Token RP = new Token(TokenType.RP, ")");
        public static Token COMMA = new Token(TokenType.COMMA, ",");
        public final TokenType type;
        public final String string;
        public Token(TokenType type, String string){
            this.type = type;
            this.string = string;
        }
        @Override
        public String toString() {
            return type + ":" + string;
        }
    }

    final int[] input;
    int index = 0;
    int ch;
    Token token;
    String string;

    Parser(String input) {
        this.input = input.codePoints().toArray();
        this.index = 0;
    }

    int get() {
        if (index < input.length)
            return ch = input[index++];
        index = input.length + 1;
        return ch = -1;
    }

    static boolean isIdFirst(int ch) {
        return Character.isLetter(ch) || ch == '_';
    }

    static boolean isIdRest(int ch) {
        return isIdFirst(ch) || Character.isDigit(ch);
    }

    static boolean isOperator(int ch) {
        return switch (ch) {
            case '!', '#', '$', '%', '&', '-', '=', '^', '~', '|',
                '@', '+', ';', ':', '*', 
                '<', '>', '.', '?', '/' -> true;
            default -> false;
        };
    }

    Token getReturn(Token token) {
        get();
        return token;
    }

    Token getToken() {
        while (Character.isWhitespace(ch))
            get();
        int start = index - 1;
        return token = switch (ch) {
            case -1 -> Token.EOF;
            case '(' -> getReturn(Token.LP);
            case ')' -> getReturn(Token.RP);
            case ',' -> getReturn(Token.COMMA);
            default -> {
                if (Character.isDigit(ch)) {
                    while (Character.isDigit(ch))
                        get();
                    yield new Token(TokenType.NUMBER, new String(input, start, index - start - 1));
                } else if (isIdFirst(ch)) {
                    get();
                    while (isIdRest(ch))
                        get();
                    String s = new String(input, start, index - start - 1);
                    TokenType t = TokenType.MAP.get(s);
                    yield t != null ? new Token(t, s) : new Token(TokenType.ID, s);
                } else if (isOperator(ch)) {
                    while (isOperator(ch))
                        get();
                    String s = new String(input, start, index - start - 1);
                    TokenType t = TokenType.MAP.get(s);
                    if (t == null)
                        throw new RuntimeException("Unknown operator '%s'".formatted(s));
                    yield new Token(t, s);
                } else 
                    throw new RuntimeException("Unknown character '%c'".formatted(ch));

            }
        };
    }

    void parse() {
        while (true) {
            getToken();
            System.out.println(token);
            if (token.type == TokenType.EOF)
                break;;
        }
    }

    public static void parse(String input) {
        Parser parser = new Parser(input);
        parser.get();
        parser.parse();
    }
}
