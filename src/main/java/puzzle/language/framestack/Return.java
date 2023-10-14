package puzzle.language.framestack;

public class Return implements Value {

    public final int n;
    
    Return(int n) {
        this.n = n;
    }

    @Override
    public String toString() {
        return "return(%d)".formatted(n);
    }
}
