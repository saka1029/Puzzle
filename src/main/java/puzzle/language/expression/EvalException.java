package puzzle.language.expression;

public class EvalException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public EvalException(String format, Object... args) {
        super(format.formatted(args));
    }

}
