package test.puzzle;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import puzzle.Common;
import puzzle.Permutation;

class TestPermutation {

    static final Logger logger = Common.getLogger(TestPermutation.class);

    @Test
    void testCount() {
        assertEquals(1, Permutation.count(3, 0));
        assertEquals(3, Permutation.count(3, 1));
        assertEquals(6, Permutation.count(3, 2));
        assertEquals(6, Permutation.count(3, 3));
        assertEquals(1, Permutation.count(4, 0));
        assertEquals(4, Permutation.count(4, 1));
        assertEquals(12, Permutation.count(4, 2));
        assertEquals(24, Permutation.count(4, 3));
        assertEquals(24, Permutation.count(4, 4));
    }

    static final List<List<Integer>> EXPECTED_4_2 = List.of(
            List.of(0, 1),
            List.of(0, 2),
            List.of(0, 3),
            List.of(1, 0),
            List.of(1, 2),
            List.of(1, 3),
            List.of(2, 0),
            List.of(2, 1),
            List.of(2, 3),
            List.of(3, 0),
            List.of(3, 1),
            List.of(3, 2));

    @Test
    void testIterator_N_R() {
        List<List<Integer>> actual = new ArrayList<>();
        for (Iterator<int[]> i = Permutation.iterator(4, 2); i.hasNext();)
            actual.add(IntStream.of(i.next()).boxed().toList());
        assertEquals(Permutation.count(4, 2), actual.size());
        assertEquals(EXPECTED_4_2, actual);
    }

    @Test
    void testIterable_N_R() {
        List<List<Integer>> actual = new ArrayList<>();
        for (int[] a : Permutation.iterable(4, 2))
            actual.add(IntStream.of(a).boxed().toList());
        assertEquals(Permutation.count(4, 2), actual.size());
        assertEquals(EXPECTED_4_2, actual);
    }

    @Test
    void testStream_N_R() {
        List<List<Integer>> actual = Permutation.stream(4, 2)
            .map(a -> IntStream.of(a).boxed().toList())
            .toList();
        assertEquals(Permutation.count(4, 2), actual.size());
        assertEquals(EXPECTED_4_2, actual);
    }
    
    @Test
    void testStream_33_2() {
        List<List<Integer>> actual = Permutation.stream(33, 2)
            .limit(32)
            .map(a -> IntStream.of(a).boxed().toList())
            .toList();
        assertEquals(32, actual.size());
        // expected = [[0, 1], [0, 2], ... , [0, 32]]
        List<List<Integer>> expected = IntStream.range(1, 33)
            .mapToObj(i -> List.of(0, i))
            .toList();
        assertEquals(expected, actual);
    }

    @Test
    void testStream_65_5() {
        try {
            Stream<int[]> stream = Permutation.stream(65, 5);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("n must b <= 64", e.getMessage());
        }
    }
}
