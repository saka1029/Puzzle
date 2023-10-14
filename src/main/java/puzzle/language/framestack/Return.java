package puzzle.language.framestack;

public class Return implements Value {

    public static Return ONE = of(1);
    public final int n;
    
    Return(int n) {
        this.n = n;
    }

    public static Return of(int n) {
        return new Return(n);
    }

    @Override
    public String toString() {
        return "return(%d)".formatted(n);
    }
}
