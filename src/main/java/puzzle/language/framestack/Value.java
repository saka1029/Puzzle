package puzzle.language.framestack;

public interface Value extends Executable {
    @Override
    default void execute(Context context) {
        context.push(this);
    }
}
