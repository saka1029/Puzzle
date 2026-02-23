package puzzle.dikstra;

public class 操車場 {

    public enum TokenType {
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

        public static final TokenType[] ALL = values();

        public final String string;
        public final int priority;
        public final boolean leftAssoc;
    }

}
