package puzzle;

import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LSystem {
    public final String start;
    public final Map<String, String> rules;

    private LSystem(String start, Map<String, String> rules) {
        this.start = start;
        this.rules = rules;
    }
    
    public static LSystem of (String start, String... rules) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0, m = rules.length; i < m; i += 2)
            map.put(rules[i], rules[i + 1]);
        return new LSystem(start, map);
    }

    public String next(String current) {
        return current.chars()
            .mapToObj(c -> {
                String s = Character.toString(c);
                return rules.getOrDefault(s, s);
            })
            .collect(Collectors.joining());
    }

    public String generation(int n) {
        String s = start;
        for (int i = 0; i < n; ++i)
            s = next(s);
        return s;
    }

    public Stream<String> stream(int limit) {
        return Stream.iterate(start, this::next).limit(limit);
    }
    
    public static void image(String input, int width, int height, File output,
            Consumer<Graphics2D> start,
            Map<String, Consumer<Graphics2D>> actions) throws IOException {
        try (ImageWriter iw = new ImageWriter(width, height)) {
            Graphics2D g = iw.graphics;
            start.accept(g);
            for (char c : input.toCharArray()) {
                Consumer<Graphics2D> action = actions.get(Character.toString(c));
                Objects.requireNonNull(action, "No action for '" + c + "'");
                action.accept(g);
            }
            iw.writeTo(output);
        }
    }
}