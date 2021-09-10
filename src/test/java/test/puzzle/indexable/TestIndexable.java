package test.puzzle.indexable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static puzzle.Common.methodName;

import java.util.Arrays;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import puzzle.Common;
import puzzle.indexable.IndexComparator;
import puzzle.indexable.IndexSwapper;
import puzzle.indexable.SimpleIndexable;

class TestIndexable {

    static final Logger logger = Common.getLogger(TestIndexable.class);

    static IndexComparator comp(int[] a, int key) {
        return (l, r) -> Integer.compare(key, a[r]);
    }

    static IndexSwapper swap(int[] a) {
        return (l, r) -> {
            int temp = a[l];
            a[l] = a[r];
            a[r] = temp;
        };
    }

    @Test
    void testBinarySearch() {
        logger.info(methodName());
        int[] ints = IntStream.range(0, 8).map(i -> i * 2).toArray();
        logger.info(Arrays.toString(ints));
        // test Arrays.binarySearch
        assertEquals(2, Arrays.binarySearch(ints, 4));
        assertEquals(-1, Arrays.binarySearch(ints, -1));
        assertEquals(-3, Arrays.binarySearch(ints, 3));
        assertEquals(-9, Arrays.binarySearch(ints, 15));
        // test Indexable
        assertEquals(2, new SimpleIndexable(comp(ints, 4), swap(ints), 0, ints.length).binarySearch());
        assertEquals(-1, new SimpleIndexable(comp(ints, -1), swap(ints), 0, ints.length).binarySearch());
        assertEquals(-3, new SimpleIndexable(comp(ints, 3), swap(ints), 0, ints.length).binarySearch());
        assertEquals(-9, new SimpleIndexable(comp(ints, 15), swap(ints), 0, ints.length).binarySearch());
    }
}
