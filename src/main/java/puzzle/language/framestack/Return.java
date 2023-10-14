package puzzle.language.framestack;

public class Return implements Value {

    public final int args, returns;
    
    Return(int args, int returns) {
        this.args = args;
        this.returns = returns;
    }

    public static Return of(int args, int returns) {
        return new Return(args, returns);
    }

    @Override
    public String toString() {
        return "return(%d, %d)".formatted(args, returns);
    }
}
