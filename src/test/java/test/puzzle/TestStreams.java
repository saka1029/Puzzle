package test.puzzle;

import static org.junit.jupiter.api.Assertions.*;
import static puzzle.Streams.*;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

class TestStreams {

    @Test
    void testZip() {
        Map<Integer, String> map = zip(
            List.of(0, 1, 2, 3, 4).stream(),
            List.of("zero", "one", "two", "three", "four").stream(),
            (k, v) -> Map.entry(k, v)).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
        assertEquals(Map.of(0, "zero", 1, "one", 2, "two", 3, "three", 4, "four"), map);
    }

    @Test
    void testMap() {
        Map<Integer, String> map = map(
            List.of(0, 1, 2, 3, 4).stream(),
            List.of("zero", "one", "two", "three", "four").stream());
        assertEquals(Map.of(0, "zero", 1, "one", 2, "two", 3, "three", 4, "four"), map);
    }

    @Test
    void testPrimes() {
        int[] primes = primes(100).toArray();
        int[] expected = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31,
            37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97};
        assertArrayEquals(expected, primes);
    }

}
