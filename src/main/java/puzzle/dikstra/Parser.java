package puzzle.dikstra;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Parser {

    public enum TokenType {
        EOF("EOF"),
        NUMBER("NUMBER"),
        ID("ID"),
        LP("("),
        RP(")"),
        COMMA(","),
        POW("^", 4, false),
        MULT("*", 3, true),
        DIV("/", 3, true),
        PLUS("+", 2, true),
        MINUS("-", 2, true);

        TokenType(String string, int priority, boolean leftAssoc) {
            this.string = string;
            this.priority = priority;
            this.leftAssoc = leftAssoc;
            this.isOperator = true;
        }

        TokenType(String string) {
            this.string = string;
            this.priority = -1;
            this.leftAssoc = true;
            this.isOperator = false;
        }

        public static final Map<String, TokenType> MAP = Stream.of(values())
            .filter(e -> e.priority >= 0)
            .collect(Collectors.toMap(e -> e.string, e -> e));

        public final String string;
        public final int priority;
        public final boolean leftAssoc;
        public final boolean isOperator;
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
        public boolean equals(Object obj) {
            return obj instanceof Token t
                && t.type.equals(type) && t.string.equals(string);
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
        switch (ch) {
            case -1: return token = Token.EOF;
            case '(': get(); return token = Token.LP;
            case ')': get(); return token = Token.RP;
            case ',': get(); return token = Token.COMMA;
            default:
                if (Character.isDigit(ch)) {
                    while (Character.isDigit(ch))
                        get();
                    return token = new Token(TokenType.NUMBER, new String(input, start, index - start - 1));
                } else if (isIdFirst(ch)) {
                    get();
                    while (isIdRest(ch))
                        get();
                    String s = new String(input, start, index - start - 1);
                    TokenType t = TokenType.MAP.get(s);
                    return token = t != null ? new Token(t, s) : new Token(TokenType.ID, s);
                } else if (isOperator(ch)) {
                    while (isOperator(ch))
                        get();
                    String s = new String(input, start, index - start - 1);
                    TokenType t = TokenType.MAP.get(s);
                    if (t == null)
                        throw new RuntimeException("Unknown operator '%s'".formatted(s));
                    return token = new Token(t, s);
                } else 
                    throw new RuntimeException("Unknown character '%c'".formatted(ch));
        }
    }

    /**
     * <pre>
     *     読み込むべきトークンがある間、以下を繰り返す（ここで示すアルゴリズムには、中置記法の演算子の他、
     *     atan2(1, 2) といったような（すなわち、前置で括弧と引数セパレータによる引数リストが引き続いている）
     *     「関数」の扱いも含まれている）。
     *        トークンを1つ読み込む。
     *        トークンが数値の場合、それを出力キューに追加する。
     *        トークンが関数の場合、それをスタックにプッシュする。
     *        トークンが関数の引数セパレータ（カンマなど）の場合
     *            スタックのトップにあるトークンが左括弧となるまで、スタックから演算子をポップして
     *            出力キューに追加する動作を繰り返す。左括弧が出てこない場合、引数セパレータの位置がおかしいか、
     *            左右の括弧が不一致となっている（エラー）。
     *        トークンが演算子 o1 の場合
     *            スタックのトップに演算子トークン o2 があり、o1 が左結合性（英語版）で、かつ優先順位が o2 と
     *            等しいか低い場合、あるいは、o1 の優先順位が o2 より低い場合、以下を繰り返す。
     *                o2 をスタックからポップし、出力キューに追加する。
     *            o1 をスタックにプッシュする。
     *        トークンが左括弧の場合、スタックにプッシュする。
     *        トークンが右括弧の場合
     *            スタックのトップにあるトークンが左括弧になるまで、スタックからポップした演算子を
     *            出力キューに追加する動作を繰り返す。
     *            左括弧をスタックからポップするが、出力には追加せずに捨てる。
     *            スタックのトップにあるトークンが関数トークンなら、それをポップして出力キューに追加する。
     *            左括弧がスタック上に見つからなかった場合、左右の括弧の不一致がある（エラー）。
     *    読み込むべきトークンがない場合
     *        スタック上に演算子トークンがある間、以下を繰り返す。
     *            スタックのトップにある演算子トークンが括弧なら、括弧の不一致がある（エラー）。
     *            演算子をスタックからポップして出力キューに追加する。
     *    終了
     */
    List<Token> parse() {
        List<Token> output = new ArrayList<>();
        Deque<Token> stack = new ArrayDeque<>();
        getToken();
        L: while (true) {
            System.out.println("token:" + token + " stack:" + stack + " output:" + output);
            switch (token.type) {
                case EOF:
                    break L;
                case NUMBER:
                    output.add(token);
                    getToken();
                    break;
                case ID:
                    Token id = token;
                    if (getToken().type == TokenType.LP) {  // 関数のとき "ID("
                        stack.push(id);                     // push ID
                        stack.push(token);                  // push '('
                        getToken();
                    } else                                  // 変数のとき
                        output.add(id);
                    break;
                case COMMA:
                    getToken();     // skip ','
                    while (!stack.isEmpty()) {
                        if (stack.peek().type == TokenType.LP)
                            break;
                        output.add(stack.pop());
                    }
                    if (stack.isEmpty())
                        throw new RuntimeException("Missing '('");
                    break;
                case LP:
                    stack.push(token);
                    getToken();
                    break;
                case RP:
                    getToken();
                    while (!stack.isEmpty()) {
                        if (stack.peek().type == TokenType.LP)
                            break;
                        output.add(stack.pop());
                    }
                    if (stack.isEmpty())
                        throw new RuntimeException("Missing '('");
                    stack.pop();    // '('を捨てる
                    if (!stack.isEmpty() && stack.peek().type == TokenType.ID)
                        output.add(stack.pop());
                    break;
                default:
                    if (!token.type.isOperator)
                        throw new RuntimeException("Unknown token %s".formatted(token));
                    Token o1 = token;
                    while (!stack.isEmpty()) {
                        Token o2 = stack.peek();
                        if (o2.type.isOperator && o1.type.leftAssoc && o1.type.priority <= o2.type.priority
                            || o1.type.priority < o2.type.priority)
                            output.add(stack.pop());
                        else
                            break;
                    }
                    stack.push(o1);
                    getToken();
                    break;
            }
        }
        while (!stack.isEmpty()) {
            Token t = stack.peek();
            if (t.type == TokenType.LP || t.type == TokenType.RP) 
                throw new RuntimeException("'(' and ')' unmatch");
            else if (t.type.isOperator)
                output.add(stack.pop());
            else
                break;
        }
        System.out.println("LAST token:" + token + " stack:" + stack + " output:" + output);
        return output;
    }

    public static List<Token> parse(String input) {
        System.out.println("*** " + input);
        Parser parser = new Parser(input);
        parser.get();
        return parser.parse();
    }
}
