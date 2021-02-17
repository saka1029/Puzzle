package test.puzzle.functions;

import static org.junit.jupiter.api.Assertions.*;
import static puzzle.functions.FunctionFilter.*;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

import puzzle.Common;

class TestFunctionFilter {

    static final Logger logger = Common.getLogger(TestFunctionFilter.class);

    static Function<Function<Integer, Integer>, Function<Integer, Integer>> fibonacci =
        self -> n ->
            n == 0 ? 0 :
            n == 1 ? 1 :
            self.apply(n - 1) + self.apply(n - 2);

    @Test
    void testTrace() {
        StringBuilder sb = new StringBuilder();
        function(trace("fibonacci", s -> sb.append(s).append("\n"), fibonacci)).apply(4);
        String expected =
            "fibonacci(4)\n"
            + "  fibonacci(3)\n"
            + "    fibonacci(2)\n"
            + "      fibonacci(1)\n"
            + "      1\n"
            + "      fibonacci(0)\n"
            + "      0\n"
            + "    1\n"
            + "    fibonacci(1)\n"
            + "    1\n"
            + "  2\n"
            + "  fibonacci(2)\n"
            + "    fibonacci(1)\n"
            + "    1\n"
            + "    fibonacci(0)\n"
            + "    0\n"
            + "  1\n"
            + "3\n";
        assertEquals(expected, sb.toString());
    }

    @Test
    void testMemoize() {
        Map<Integer, Integer> cache = new TreeMap<>();
        function(memoize(cache, fibonacci)).apply(4);
        Map<Integer, Integer> expected = Map.of(0, 0, 1, 1, 2, 1, 3, 2, 4, 3);
        assertEquals(expected, cache);
    }

    @Test
    void testMemoizeTrace() {
        StringBuilder trace = new StringBuilder();
        Map<Integer, Integer> cache = new TreeMap<>();
        function(memoize(cache, trace("fibonacci", s -> trace.append(s).append("\n"), fibonacci))).apply(5);
        String expected =
            "fibonacci(5)\n"
            + "  fibonacci(4)\n"
            + "    fibonacci(3)\n"
            + "      fibonacci(2)\n"
            + "        fibonacci(1)\n"
            + "        1\n"
            + "        fibonacci(0)\n"
            + "        0\n"
            + "      1\n"
            + "    2\n"
            + "  3\n"
            + "5\n";
        assertEquals(expected, trace.toString());
    }

    @Test
    void testStatistics() {
        Map<Integer, Integer> stat = new TreeMap<>();
        function(statistics(stat, fibonacci)).apply(5);
        Map<Integer, Integer> expected = Map.of(0, 3, 1, 5, 2, 3, 3, 2, 4, 1, 5, 1);
        assertEquals(expected, stat);
    }

    static record I3(int x, int y, int z) {}

    static Function<Function<I3, Integer>, Function<I3, Integer>> tarai =
        self -> a ->
            a.x <= a.y ? a.y :
            self.apply(new I3(self.apply(new I3(a.x - 1, a.y, a.z)),
                              self.apply(new I3(a.y - 1, a.z, a.x)),
                              self.apply(new I3(a.z - 1, a.x, a.y))));

    static Function<Function<I3, Integer>, Function<I3, Integer>> tak =
        self -> a ->
            a.x <= a.y ? a.z :
            self.apply(new I3(self.apply(new I3(a.x - 1, a.y, a.z)),
                              self.apply(new I3(a.y - 1, a.z, a.x)),
                              self.apply(new I3(a.z - 1, a.x, a.y))));

    static Function<Function<I3, Integer>, Function<I3, Integer>> t0 =
        self -> a ->
            a.x <= a.y ? a.y :
            a.x > a.y && a.y <= a.z ? a.z :
            a.x;

    /**
     * 竹内関数 - Wikipedia
     * https://ja.wikipedia.org/wiki/%E7%AB%B9%E5%86%85%E9%96%A2%E6%95%B0
     */
    @Test
    void testTarai() {
        // 竹内版
        Map<I3, Integer> taraiStat = new HashMap<>();
        assertEquals(12, function(statistics(taraiStat, tarai)).apply(new I3(12, 6, 0)));
        assertEquals(12604860 + 1, taraiStat.values().stream().mapToInt(i -> i).sum());
        // マッカーシー版
        Map<I3, Integer> takStat = new HashMap<>();
        assertEquals(1, function(statistics(takStat, tak)).apply(new I3(12, 6, 0)));
        assertEquals(63608 + 1, takStat.values().stream().mapToInt(i -> i).sum());
        // 非再帰版
        assertEquals(12, function(tarai).apply(new I3(12, 6, 0)));
        assertEquals(12, function(t0).apply(new I3(12, 6, 0)));
    }

    @Test
    void testCurriedArguments() {
        Map<Integer, Function<Integer, Function<Integer, Integer>>> cache = new HashMap<>();
        Function<Integer, Function<Integer, Function<Integer, Integer>>> tarai =
            function(memoize(cache, self -> x ->
                function(memoize(selfY -> y ->
                    function(memoize(selfZ -> z -> x <= y ? y
                        : self.apply(self.apply(x - 1).apply(y).apply(z))
                              .apply(self.apply(y - 1).apply(z).apply(x))
                              .apply(self.apply(z - 1).apply(x).apply(y))))))));
        assertEquals(3, tarai.apply(3).apply(2).apply(1));
        assertEquals("{0={3={2=3}}, 1={1={3=1}, 3={2=3}}, 2={1={3=3}, 2={1=2}}, 3={2={1=3}}}", cache.toString());
    }
}