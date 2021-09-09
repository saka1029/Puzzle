package test.puzzle.indexable;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.IntSummaryStatistics;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import puzzle.Common;
import puzzle.indexable.IndexableIntArray;

class TestIndexableIntArray {

    static final Logger logger = Common.getLogger(TestIndexableIntArray.class);

    @Test
    void testInsertionSort() {
        logger.info(Common.methodName());
        int[] array = {5, 7, 3, 2, 9, 4, 0, 8, 1, 6};
        IndexableIntArray.builder(array).build().insertionSort();
        assertArrayEquals(IntStream.range(0, 10).toArray(), array);
    }

    @Test
    void testInsertionSortRange() {
        logger.info(Common.methodName());
        int[] array = {5, 7, 3, 2, 9, 4, 0, 8, 1, 6};
        IndexableIntArray.builder(array).range(5, array.length).build().insertionSort();
        int[] expected = {5, 7, 3, 2, 9, 0, 1, 4, 6, 8};
        assertArrayEquals(expected, array);
    }

    @Test
    void testQuickSort() {
        logger.info(Common.methodName());
        int[] array = {5, 7, 3, 2, 9, 4, 0, 8, 1, 6};
        IndexableIntArray.builder(array).build().quickSort();
        assertArrayEquals(IntStream.range(0, 10).toArray(), array);
    }

    @Test
    void testQuickSortRange() {
        logger.info(Common.methodName());
        int[] array = {5, 7, 3, 2, 9, 4, 0, 8, 1, 6};
        IndexableIntArray.builder(array).range(5, array.length).build().quickSort();
        int[] expected = {5, 7, 3, 2, 9, 0, 1, 4, 6, 8};
        assertArrayEquals(expected, array);
    }

    static double standardDeviation(Collection<Integer> data) {
        IntSummaryStatistics summary = data.stream().mapToInt(i -> i).summaryStatistics();
        long count = summary.getCount();
        double average = summary.getAverage();
        double diffSum = data.stream()
            .mapToDouble(i -> Math.pow(i - average, 2))
            .sum();
        return Math.sqrt(diffSum / count);
    }

    @Test
    void testShffule() {
        logger.info(Common.methodName());
        Map<int[], Integer> counts = new TreeMap<>(Arrays::compare);
        for (int i = 0; i < 240000; ++i) {
            int[] array = IntStream.range(0, 4).toArray();
            IndexableIntArray.builder(array).build().shuffle();
            counts.compute(array, (k, v) -> v == null ? 1 : v + 1);
        }
        double sd = standardDeviation(counts.values());
        logger.info("標準偏差: " + sd);
        assertEquals(24, counts.size());
        assertTrue(sd < 200);
//        for (Entry<int[], Integer> e : counts.entrySet())
//            logger.info(Arrays.toString(e.getKey()) + " : " + e.getValue());
    }

    @Test
    void testReverse() {
        logger.info(Common.methodName());
        int[] array = {5, 7, 3, 2, 9, 4, 0, 8, 1, 6};
        IndexableIntArray.builder(array).build().reverse();
        int[] expected = {6, 1, 8, 0, 4, 9, 2, 3, 7, 5};
        assertArrayEquals(expected, array);
    }

}
