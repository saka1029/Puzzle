package test.puzzle;

import static org.junit.jupiter.api.Assertions.*;
import static puzzle.Iterables.*;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

class TestIterables {

    @Test
    void testRange() {
        assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), list(range(0, 10)));
        assertEquals(List.of(0, 2, 4, 6, 8), list(filter(range(0, 10), i -> i % 2 == 0)));
        assertEquals(List.of(100, 120, 140), list(map(filter(List.of(10, 11, 12, 13, 14, 15), i -> i % 2 == 0), i -> i * 10)));
        assertEquals(List.of(10, 8, 6, 4, 2), list(range(10, 0, -2)));
    }

    @Test
    void testMap() {
        assertEquals(List.of(0, 2, 4, 6, 8),
            list(map(range(0, 5), i -> i * 2)));
    }

    @Test
    void testSkip() {
        assertEquals(List.of(5, 6, 7, 8, 9), list(skip(range(0, 10), 5)));
    }

    @Test
    void testLimit() {
        assertEquals(List.of(0, 1, 2, 3, 4), list(limit(range(0, 10), 5)));
        assertEquals(List.of(3, 4, 5), list(limit(skip(range(0, 10), 3), 3)));
    }


    @Test
    void testToArray() {
        assertArrayEquals(new Integer[] {0, 1, 2, 3},
            array(range(0, 4), Integer[]::new));
        assertArrayEquals(new int[] {0, 1, 2, 3}, array(range(0, 4)));
    }

    @Test
    void testNoApply() {
        assertEquals(List.of(0, 2, 4, 6, 8),
            list(filter(range(0, 10), i -> i % 2 == 0)));
        assertEquals(List.of(100, 120, 140),
            list(map(filter(List.of(10, 11, 12, 13, 14, 15),
                i -> i % 2 == 0),
                i -> i * 10)));
    }

    @Test
    void testApply() {
        assertEquals(List.of(0, 2, 4, 6, 8),
            apply(range(0, 10),
                r -> filter(r, i -> i % 2 == 0),
                r -> list(r)));
        assertEquals(List.of(100, 120, 140),
            apply(List.of(10, 11, 12, 13, 14, 15),
                r -> filter(r, i -> i % 2 == 0),
                r -> map(r, i -> i * 10),
                r -> list(r)));
    }

    static Iterable<Integer> primes(int max) {
        Iterable<Integer> primes = range(2, max);
        Function<Integer, Predicate<Integer>> sieve = n -> i -> i == n
            || i % n != 0;
        primes = filter(primes, sieve.apply(2));
        for (int i = 3; i * i <= max; i += 2)
            primes = filter(primes, sieve.apply(i));
        return primes;
    }

    @Test
    void testPrimes() {
        assertEquals(List.of(2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43,
            47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97), list(primes(100)));
    }

    @Test
    void testHashMap() {
        List<String> list = List.of("zero", "one", "two");
        assertEquals(Map.of(0, "zero", 1, "one", 2, "two"),
            apply(range(0, list.size()),
                s -> hashMap(s, Function.identity(), list::get)));
        // assertEquals(Map.of(0, "zero", 1, "one", 2, "two"),
        // TODO: 無名のObjectはapplyの中では使えない。
        // apply(range(0, list.size()),
        // s -> map(s, i -> new Object() { int k = i; String v = list.get(i);
        // }),
        // s -> toMap(s, obj -> obj.k, obj -> obj.v)));
        assertEquals(Map.of(0, "zero", 1, "one", 2, "two"),
            hashMap(map(range(0, list.size()), i -> new Object() {
                final int k = i;
                final String v = list.get(i);
            }), obj -> obj.k, obj -> obj.v));
        var s = map(range(0, list.size()), i -> new Object() {
                int k = i;
                String v = list.get(i);
            });
        assertEquals(Map.of(0, "zero", 1, "one", 2, "two"),
            hashMap(s, obj -> obj.k, obj -> obj.v));
    }

    @Test
    void testReduce() {
        assertEquals(10, reduce(range(0, 5), 0, (a, b) -> a + b));
        assertEquals(10, reduce(range(0, 5), 0, Integer::sum));
        assertEquals(120, reduce(range(1, 6), 1, (a, b) -> a * b));
        assertEquals(0, reduce(List.of(), 0, (a, b) -> a + b));
    }

    @Test
    void testSum() {
        assertEquals(10, sum(range(0, 5)));
        assertEquals(0, sum(List.of()));
    }

    @Test
    void testJoin() {
        assertEquals("0, 1, 2, 3, 4", join(range(0, 5), ", "));
        assertEquals("", join(List.of(), ", "));
    }

}
