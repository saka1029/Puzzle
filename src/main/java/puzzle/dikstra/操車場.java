package puzzle.dikstra;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class 操車場 {

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
        public final TokenType type;
        public final String string;
        public Token(TokenType type, String string){
            this.type = type;
            this.string = string;
        }
    }

    public static class Scanner {
        final int[] input;
        int index = 0;
        int ch;
        List<Token> tokens = new ArrayList<>();

        Scanner(String input) {
            this.input = input.codePoints().toArray();
            this.index = 0;
        }

        int get() {
            return index < input.length ? input[index++] : -1;
        }

        void scan() {
            get();
        }

        public static List<Token> scan(String input) {
            Scanner scanner = new Scanner(input);
            scanner.scan();
            return scanner.tokens;
        }
    }

}
