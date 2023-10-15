package puzzle.language.framestack;

import java.util.stream.Collectors;

public class Block extends List {
    
    public final int args, returns;
    
    Block(int args, int returns, Executable... elements) {
        super(elements);
        this.args = args;
        this.returns = returns;
    }
    
    public static Block of(int args, int returns, Executable... elements) {
        return new Block(args, returns, elements);
    }


    @Override
    public String toString() {
        String head = "(@ %d %d ".formatted(args, returns);
        return elements.stream()
            .map(Executable::toString)
            .collect(Collectors.joining(", ", head, ")"));
    }
}
