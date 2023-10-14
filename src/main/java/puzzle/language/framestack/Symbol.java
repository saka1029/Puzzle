package puzzle.language.framestack;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Symbol implements Executable {
    
    static final Map<String, Symbol> symbols = new HashMap<>();
    public final String name;
    
    private Symbol(String name) {
        this.name = name;
    }
    
    public static Symbol of(String name) {
        return symbols.computeIfAbsent(name, k -> new Symbol(k));
    }

    @Override
    public void execute(Context context) {
        Executable value = context.globals.get(this);
        Objects.requireNonNull(value, "Symbol " + this + " is not defined");
        value.execute(context);
    }
    
    @Override
    public String toString() {
        return name;
    }
}
