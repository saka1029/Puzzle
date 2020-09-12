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
        assertEquals(list(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), list(range(0, 10)));
        assertEquals(list(0, 2, 4, 6, 8), list(filter(i -> i % 2 == 0, range(0, 10))));
        assertEquals(list(100, 120, 140), list(map(i -> i * 10, filter(i -> i % 2 == 0, list(10, 11, 12, 13, 14, 15)))));
        assertEquals(list(10, 8, 6, 4, 2), list(range(10, 0, -2)));
    }

    @Test
    void testMap() {
        assertEquals(list(0, 2, 4, 6, 8),
            arrayList(map(i -> i * 2, range(0, 5))));
    }

    @Test
    void testSkip() {
        Iterable<Integer> skipped;
        assertEquals(list(5, 6, 7, 8, 9), list(skipped = skip(5, range(0, 10))));
        assertEquals(list(5, 6, 7, 8, 9), list(skipped));
    }

    @Test
    void testLimit() {
        Iterable<Integer> limited;
        assertEquals(list(0, 1, 2, 3, 4), list(limit(5, range(0, 10))));
        assertEquals(list(3, 4, 5), list(limited = limit(3, skip(3, range(0, 10)))));
        assertEquals(list(3, 4, 5), arrayList(limited));
    }


    @Test
    void testArray() {
        assertArrayEquals(new Integer[] {0, 1, 2, 3},
            array(Integer[]::new, range(0, 4)));
        assertArrayEquals(new int[] {0, 1, 2, 3}, array(range(0, 4)));
    }

    @Test
    void testNoApply() {
        assertEquals(list(0, 2, 4, 6, 8),
            arrayList(filter(i -> i % 2 == 0, range(0, 10))));
        assertEquals(list(100, 120, 140),
            arrayList(map(i -> i * 10, filter(i -> i % 2 == 0, list(10, 11, 12, 13, 14, 15)))));
    }

    @Test
    void testApply() {
        assertEquals(List.of(0, 2, 4, 6, 8),
            apply(range(0, 10),
                r -> filter(i -> i % 2 == 0, r),
                r -> arrayList(r)));
        assertEquals(List.of(100, 120, 140),
            apply(List.of(10, 11, 12, 13, 14, 15),
                r -> filter(i -> i % 2 == 0, r),
                r -> map(i -> i * 10, r),
                r -> arrayList(r)));
        // assertEquals(Map.of(0, "zero", 1, "one", 2, "two"),
        // TODO: 無名のObjectはapplyの中では使えない。
        // apply(range(0, list.size()),
        // s -> map(s, i -> new Object() {
        //     int k = i;
        //     String v = list.get(i);
        // }), // s -> toMap(s, obj -> obj.k, obj -> obj.v)));
    }

    static Iterable<Integer> primes(int max) {
        Iterable<Integer> primes = range(2, max);
        Function<Integer, Predicate<Integer>> sieve = n -> i -> i == n
            || i % n != 0;
        primes = filter(sieve.apply(2), primes);
        for (int i = 3; i * i <= max; i += 2)
            primes = filter(sieve.apply(i), primes);
        return primes;
    }

    @Test
    void testPrimes() {
        assertEquals(list(2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43,
            47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97), list(primes(100)));
    }

    @Test
    void testHashMap() {
        List<String> list = List.of("zero", "one", "two");
        assertEquals(Map.of(0, "zero", 1, "one", 2, "two"),
            apply(range(0, list.size()),
                s -> hashMap(Function.identity(), list::get, s)));
        assertEquals(Map.of(0, "zero", 1, "one", 2, "two"),
            hashMap(obj -> obj.key, obj -> obj.value, map(i -> new Object() {
                @SuppressWarnings("unused")
                final int key = i;                // The value of the field new Object(){}.key is not used
                @SuppressWarnings("unused")
                final String value = list.get(i);   // The value of the field new Object(){}.value is not used
            }, range(0, list.size()))));
        var s = map(i -> new Object() {
                int k = i;
                String v = list.get(i);
            }, range(0, list.size()));
        assertEquals(Map.of(0, "zero", 1, "one", 2, "two"),
            hashMap(obj -> obj.k, obj -> obj.v, s));
    }

    @Test
    void testReduce() {
        assertEquals(10, reduce(0, (a, b) -> a + b, range(0, 5)));
        assertEquals(10, reduce(0, Integer::sum, range(0, 5)));
        assertEquals(120, reduce(1, (a, b) -> a * b, range(1, 6)));
        assertEquals(0, reduce(0, (a, b) -> a + b, List.of()));
    }

    @Test
    void testSum() {
        assertEquals(10, sum(range(0, 5)));
        assertEquals(0, sum(list()));
    }

    @Test
    void testJoin() {
        assertEquals("0, 1, 2, 3, 4", join(", ", range(0, 5)));
        assertEquals("", join(", ", list()));
    }

    @Test
    void testPermutation() {
        List<List<String>> expected3 = list(
            list("a", "b", "c"),
            list("a", "c", "b"),
            list("b", "a", "c"),
            list("b", "c", "a"),
            list("c", "a", "b"),
            list("c", "b", "a")
        );
        assertEquals(expected3, list(permutation(3, list("a", "b", "c"))));
        List<List<Integer>> expected2 = list(
            list(0, 1),
            list(0, 2),
            list(1, 0),
            list(1, 2),
            list(2, 0),
            list(2, 1)
        );
        assertEquals(expected2, list(permutation(2, list(0, 1, 2))));
        int[][] expected0 = {
            {0, 1},
            {0, 2},
            {1, 0},
            {1, 2},
            {2, 0},
            {2, 1}
        };
        assertArrayEquals(expected0, array(int[][]::new, permutation(3, 2)));
        assertEquals(expected2, list(map(intArray -> list(intArray), permutation(3, 2))));
    }

}
