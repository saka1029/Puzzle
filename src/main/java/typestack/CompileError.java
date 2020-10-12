package typestack;

public class CompileError extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CompileError(String format, Object... args) {
        super(String.format(format, args));
    }

}
