package puzzle.functions;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class VisualCache<R> {

    final String functionName;
    public String functionName() { return functionName; }

    final CacheFunction<R> function;

    private final Map<ObjectArray, R> cacheInternal = new HashMap<>();

    final Map<ObjectArray, R> cache = Collections.unmodifiableMap(cacheInternal);
    public Map<ObjectArray, R> cach() { return cache; }

    int indent = 2;
    public int indent() { return indent; }
    public VisualCache<R> indent(int indent) { this.indent = indent; return this; }

    int nest = 0;
    public int nest() { return nest; }
    public VisualCache<R> nest(int nest) { this.nest = nest; return this; }

    boolean visual = true;
    public boolean visual() { return visual; }
    public VisualCache<R> visual(boolean visual) { this.visual = visual; return this; }

    Consumer<String> out = s -> System.out.println(s);
    public VisualCache<R> out(Consumer<String> out) { this.out = out; return this; }

    boolean caching = true;
    public boolean caching() { return caching; }
    public VisualCache<R> caching(boolean caching) { this.caching = caching; return this; }

    public VisualCache(String functionName, CacheFunction<R> function) {
        this.functionName = functionName;
        this.function = function;
    }

    public void clear() {
        nest = 0;
        cacheInternal.clear();
    }

    static String toString(Object... arguments) {
        String result = Arrays.deepToString(arguments);
        return result.substring(1, result.length() - 1);
    }

    String indentString() {
        return " ".repeat(nest * indent);
    }

    public void enter(Object... arguments) {
        if (visual)
            out.accept(indentString() + functionName + "(" + toString(arguments) + ")");
        ++nest;
    }

    public R exit(R r) {
        --nest;
        if (visual)
            out.accept(indentString() + "-> " + r);
        return r;
    }

    public void exit() {
        --nest;
    }

    public R call(Object... arguments) {
        if (!caching)
            return function.apply(arguments);
        ObjectArray a = ObjectArray.of(arguments);
        R cached = cacheInternal.get(a);
        if (cached != null) {
            out.accept(indentString() + functionName + "(" + toString(arguments) + ") -> " + cached + " (from cache)");
            return cached;
        }
        R result = function.apply(arguments);
        cacheInternal.put(a, result);
        return result;
    }

    @Override
    public String toString() {
        return cacheInternal.toString();
    }
}
