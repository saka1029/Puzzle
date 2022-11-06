package puzzle.language.expression;

public class ParseException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ParseException(String format, Object... args) {
        super(format.formatted(args));
    }

}
