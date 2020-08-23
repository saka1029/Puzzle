package test.puzzle.functions;

import static java.math.BigInteger.*;
import static org.junit.jupiter.api.Assertions.*;
import static puzzle.functions.Memoizer.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import puzzle.functions.Memoizer;

class TestMemoizer {

    @Test
    public void testFibonacci() {
        Memoizer<BigInteger, BigInteger> fibonacci = memoize(self -> n -> n.equals(ZERO) ? ZERO
            : n.equals(ONE) ? ONE
                : self.apply(n.subtract(ONE)).add(self.apply(n.subtract(TWO))));
        assertEquals(valueOf(0), fibonacci.apply(BigInteger.valueOf(0)));
        assertEquals(valueOf(1), fibonacci.apply(BigInteger.valueOf(1)));
        assertEquals(valueOf(1), fibonacci.apply(BigInteger.valueOf(2)));
        assertEquals(valueOf(2), fibonacci.apply(BigInteger.valueOf(3)));
        assertEquals(valueOf(3), fibonacci.apply(BigInteger.valueOf(4)));
        assertEquals(valueOf(5), fibonacci.apply(BigInteger.valueOf(5)));
        assertEquals(valueOf(8), fibonacci.apply(BigInteger.valueOf(6)));
        assertEquals(valueOf(13), fibonacci.apply(BigInteger.valueOf(7)));
        assertEquals(valueOf(21), fibonacci.apply(BigInteger.valueOf(8)));
        assertEquals(valueOf(34), fibonacci.apply(BigInteger.valueOf(9)));
        System.out.println(fibonacci);
        assertEquals(Map.of(
            valueOf(0), valueOf(0),
            valueOf(1), valueOf(1),
            valueOf(2), valueOf(1),
            valueOf(3), valueOf(2),
            valueOf(4), valueOf(3),
            valueOf(5), valueOf(5),
            valueOf(6), valueOf(8),
            valueOf(7), valueOf(13),
            valueOf(8), valueOf(21),
            valueOf(9), valueOf(34)), fibonacci.cache());
        for (long i = 0; i < 1000; ++i)
            fibonacci.apply(valueOf(i));
    }

    @Test
    public void testNestedMemoize() {
        Memoizer<Integer, Memoizer<Integer, Memoizer<Integer, Integer>>> tarai = memoize(
            self -> x -> memoize(selfY -> y -> memoize(selfZ -> z -> x <= y ? y
                : self.apply(self.apply(x - 1).apply(y).apply(z))
                    .apply(self.apply(y - 1).apply(z).apply(x))
                    .apply(self.apply(z - 1).apply(x).apply(y)))));
        assertEquals(3, tarai.apply(3).apply(2).apply(1));
        /*
         * tarai(3, 2, 1) = tarai(tarai(2, 2, 1), tarai(1, 1, 3), tarai(0, 3,
         * 2)) = tarai(2, 1, 3) = tarai(tarai(1, 1, 3), tarai(0, 3, 2), tarai(2,
         * 2, 1)) = tarai(1, 3, 2) = 3 tarai(0, 3, 2) = 3 tarai(1, 1, 3) = 1
         * tarai(1, 3, 2) = 3 tarai(2, 1, 3) = 3 tarai(2, 2, 1) = 2 tarai(3, 2,
         * 1) = 3
         */
        System.out.println(tarai);
        Map<Integer, Map<Integer, Map<Integer, Integer>>> expected = Map.of(0, Map.of(3, Map.of(2, 3)),
            1, Map.of(1, Map.of(3, 1),
                3, Map.of(2, 3)),
            2, Map.of(1, Map.of(3, 3),
                2, Map.of(1, 2)),
            3, Map.of(2, Map.of(1, 3)));
        // for文でMapを取り出す。
        Map<Integer, Map<Integer, Map<Integer, Integer>>> xmap = new HashMap<>();
        for (var x : tarai.cache().entrySet()) {
            Map<Integer, Map<Integer, Integer>> ymap = new HashMap<>();
            for (var y : x.getValue().cache().entrySet())
                ymap.put(y.getKey(), y.getValue().cache());
            xmap.put(x.getKey(), ymap);
        }
        System.out.println(expected.equals(xmap));
        assertEquals(expected, xmap);
        // StreamでMapを取り出す。
        Map<Integer, Map<Integer, Map<Integer, Integer>>> xmap2 = tarai.cache().entrySet().stream()
            .map(e -> Map.entry(e.getKey(), e.getValue().cache().entrySet().stream()
                .map(f -> Map.entry(f.getKey(), f.getValue().cache()))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue))))
            .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
        assertEquals(expected, xmap2);
    }

    /*
     * recordを使ってメモ化します。 recordを使うと、複数の引数を単一の引数に置き換えることができます。
     */
    record Args(int x, int y, int z) {
    }

    static Integer call(Function<Args, Integer> self, int x, int y, int z) {
        return self.apply(new Args(x, y, z));
    }

    @Test
    public void testRecordMemoize() {
        Memoizer<Args, Integer> tarai = memoize(self -> args -> args.x <= args.y ? args.y
            : self.apply(new Args(self.apply(new Args(args.x - 1, args.y, args.z)),
                self.apply(new Args(args.y - 1, args.z, args.x)),
                self.apply(new Args(args.z - 1, args.x, args.y)))));
        Memoizer<Args, Integer> tarai2 = memoize(self -> args -> args.x <= args.y ? args.y
            : call(self, call(self, args.x - 1, args.y, args.z),
                call(self, args.y - 1, args.z, args.x),
                call(self, args.z - 1, args.x, args.y)));
        assertEquals(3, tarai.apply(new Args(3, 2, 1)));
        for (Entry<Args, Integer> e : tarai.cache().entrySet())
            System.out.println(e);
        Map<Args, Integer> expected = Map.of(new Args(0, 3, 2), 3,
            new Args(1, 1, 3), 1,
            new Args(1, 3, 2), 3,
            new Args(2, 1, 3), 3,
            new Args(2, 2, 1), 2,
            new Args(3, 2, 1), 3);
        assertEquals(expected, tarai.cache());
    }

    /**
     * java - Digit Square Sum - Stack Overflow
     * https://stackoverflow.com/questions/63460022/digit-square-sum
     */
    @Test
    public void testDigitSquareSum() throws IOException {
        Memoizer<Integer, Integer> digitSquareSum = memoize(self -> n -> {
            int sum = 0;
            while (n > 0) {
                int digit = n % 10;
                sum += digit * digit;
                n /= 10;
            }
            return sum;
        });
        for (int i = 0; i <= 500; ++i)
            digitSquareSum.apply(i);
        System.out.println(digitSquareSum);
        Map<Integer, Integer> map = digitSquareSum.cache();
        Path file = Paths.get("data", "digitSquareSum.gml");
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(file))) {
            out.println("graph [");
            for (long i : map.keySet()) {
                out.println("  node [");
                out.println("    id " + i);
                out.println("    label \"" + i + "\"");
                out.println("  ]");
            }
            for (Entry<Integer, Integer> e : map.entrySet()) {
                out.println("  edge [");
                out.println("    source " + e.getKey());
                out.println("    target " + e.getValue());
                out.println("  ]");
            }
            out.println("]");
        }
    }

}