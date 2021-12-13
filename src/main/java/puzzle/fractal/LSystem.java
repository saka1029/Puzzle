package puzzle.fractal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public <T> List<T> generation(int n, Map<String, T> map) {
        return generation(n).chars()
            .mapToObj(c -> map.get(Character.toString(c)))
            .toList();
    }
}