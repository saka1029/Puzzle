package test.puzzle;

import static org.junit.jupiter.api.Assertions.*;
import static puzzle.Iterables.*;
import static puzzle.functions.FunctionFilter.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import org.junit.jupiter.api.Test;


class TestKaprekarNumber {

    static int number(Iterable<Character> digit) {
        return Integer.parseInt(join("", digit));
//        return reduce(0, (x, y) -> x * 10 + y, map(c -> Character.digit(c, 10), digit));
    }

    static int max(int n) {
        return number(sorted(reverseOrder(), chars("" + n)));
    }

    static int min(int n) {
        return number(sorted(chars("" + n)));
    }

    static boolean isKaprekar(int n) {
        return max(n) - min(n) == n;
    }

    @Test
    void testIterable() {
        assertEquals(4321, max(2431));
        assertEquals(1234, min(2431));
        assertEquals(6174, max(6174) - min(6174));
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 10000; ++i)
            if (isKaprekar(i))
                list.add(i);
        assertEquals(List.of(0, 495, 6174), list);
    }

    static Function<Function<Integer, Integer>, Function<Integer, Integer>> kaprekar =
        self -> n -> max(n) - min(n);

    @Test
    void testCache() throws IOException {
        Map<Integer, Integer> cache = new HashMap<>();
        Function<Integer, Integer> kaprekarFunction = function(memoize(cache, kaprekar));
        for (int i = 0; i < 1000; ++i)
            kaprekarFunction.apply(i);
        Path file = Paths.get("data", "kaprekar.gml");
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(file))) {
            out.println("graph [");
            for (int a : cache.keySet()) {
                out.println("  node [");
                out.println("    id " + a);
                out.println("    label \"" + a + "\"");
                out.println("  ]");
            }
            for (Entry<Integer, Integer> e : cache.entrySet()) {
                out.println("  edge [");
                out.println("    source " + e.getKey());
                out.println("    target " + e.getValue());
                out.println("  ]");
            }
            out.println("]");
        }

    }

}
