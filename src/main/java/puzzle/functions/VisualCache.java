package puzzle.functions;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class VisualCache<R> {

    final String name;
    public String name() { return name; }

    final CacheFunction<R> function;
    public CacheFunction<R> function() { return function; }

    private final Map<ObjectArray, R> cache = new HashMap<>();
    final Map<ObjectArray, R> unmodifiableCache = Collections.unmodifiableMap(cache);
    public Map<ObjectArray, R> cache() { return unmodifiableCache; }

    int indent = 2;
    public int indent() { return indent; }
    public VisualCache<R> indent(int indent) { this.indent = indent; return this; }

    int nest = 0;
    public int nest() { return nest; }
    public VisualCache<R> nest(int nest) { this.nest = nest; return this; }

    static Consumer<String> NO_OUTPUT = s -> {};
    Consumer<String> output = s -> System.out.println(s);
    public VisualCache<R> output(Consumer<String> output) {
        Objects.requireNonNull(output, "output");
        this.output = output; return this;
    }
    public VisualCache<R> noOutput() { this.output = NO_OUTPUT; return this; }

    boolean caching = true;
    public boolean caching() { return caching; }
    public VisualCache<R> caching(boolean caching) { this.caching = caching; return this; }

    private VisualCache(String name, CacheFunction<R> function) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(function, "function");
        this.name = name;
        this.function = function;
    }

    public static <R> VisualCache<R> forFunction(String name, CacheFunction<R> function) {
        return new VisualCache<>(name, function);
    }

    static CacheFunction<Void> NO_FUNCTION = a -> null;
    public static VisualCache<Void> forProcedure(String name) {
        VisualCache<Void> result = new VisualCache<>(name, NO_FUNCTION);
        result.caching = false;
        return result;
    }

    public VisualCache<R> clear() {
        nest = 0;
        cache.clear();
        return this;
    }

    static String toString(Object... arguments) {
        String result = Arrays.deepToString(arguments);
        return result.substring(1, result.length() - 1);
    }

    String indentString() {
        return " ".repeat(nest * indent);
    }

    public void enter(Object... arguments) {
        output.accept(indentString() + name + "(" + toString(arguments) + ")");
        ++nest;
    }

    public R exit(R r) {
        --nest;
        output.accept(indentString() + "-> " + r);
        return r;
    }

    public void exit() {
        --nest;
        output.accept(indentString() + "->");
    }

    public R call(Object... arguments) {
        if (!caching)
            return function.apply(arguments);
        ObjectArray a = ObjectArray.of(arguments);
        R cached = cache.get(a);
        if (cached != null) {
            output.accept(indentString() + name + "(" + toString(arguments) + ") -> " + cached + " (from cache)");
            return cached;
        }
        R result = function.apply(arguments);
        cache.put(a, result);
        return result;
    }

    @Override
    public String toString() {
        return cache.toString();
    }

}
