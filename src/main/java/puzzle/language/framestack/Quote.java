package puzzle.language.framestack;

public class Quote implements Executable {

    public final Executable element;
    
    Quote(Executable element) {
        this.element = element;
    }

    public static Quote of(Executable element) {
        return new Quote(element);
    }

    @Override
    public void execute(Context context) {
        context.push(element);
    }

    @Override
    public String toString() {
        return "'" + element;
    }
}
