package test.puzzle;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static puzzle.Common.getLogger;
import static puzzle.Common.methodName;
import static puzzle.IndexedCollectionUtil.quickSort;
import static puzzle.IndexedCollectionUtil.reverse;
import static puzzle.IndexedCollectionUtil.shuffle;
import static puzzle.IndexedCollectionUtil.swap;

import java.util.Arrays;
import java.util.Collection;
import java.util.IntSummaryStatistics;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

class TestIndexedCollectionUtil {

    static final Logger logger = getLogger(TestIndexedCollectionUtil.class);

    @Test
    void testQuickSortInts() {
        logger.info(methodName());
        int[] ints = {5, 8, 3, 4, 1, 0, 2, 7, 9, 6};
        quickSort((l, r) -> Integer.compare(ints[l], ints[r]),
            (l, r) -> swap(ints, l, r),
            0, ints.length);
        assertArrayEquals(IntStream.range(0, 10).toArray(), ints);
    }

    @Test
    void testQuickSortIntsReverse() {
        logger.info(methodName());
        int[] ints = {5, 8, 3, 4, 1, 0, 2, 7, 9, 6};
        quickSort((l, r) -> -Integer.compare(ints[l], ints[r]),
            (l, r) -> swap(ints, l, r),
            0, ints.length);
        int len = ints.length;
        assertArrayEquals(IntStream.range(0, len).map(i -> len - i - 1).toArray(), ints);
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
    void testShuffle() {
        logger.info(methodName());
        TreeMap<int[], Integer> counts = new TreeMap<>(Arrays::compare);
        for (int i = 0; i < 240000; ++i) {
            int[] ints = IntStream.range(0, 4).toArray();
            shuffle((l, r) -> swap(ints, l, r), 0, ints.length);
            counts.compute(ints, (k, v) -> v == null ? 1 : v + 1);
        }
//        for (Entry<int[], Integer> e : counts.entrySet())
//            logger.info(Arrays.toString(e.getKey()) + " : " + e.getValue());
        assertEquals(24, counts.size());
        double sd = standardDeviation(counts.values());
        logger.info("標準偏差 = " + sd);
        assertTrue(sd < 200);
    }

    @Test
    void testShufflePartial() {
        logger.info(methodName());
        TreeMap<int[], Integer> counts = new TreeMap<>(Arrays::compare);
        for (int i = 0; i < 240000; ++i) {
            int[] ints = IntStream.range(0, 5).toArray();
            shuffle((l, r) -> swap(ints, l, r), 1, ints.length);
            counts.compute(ints, (k, v) -> v == null ? 1 : v + 1);
        }
//        for (Entry<int[], Integer> e : counts.entrySet())
//            logger.info(Arrays.toString(e.getKey()) + " : " + e.getValue());
        assertEquals(24, counts.size());
        double sd = standardDeviation(counts.values());
        logger.info("標準偏差 = " + sd);
        assertTrue(sd < 200);
    }

    @Test
    public void testReverse() {
        logger.info(methodName());
        int[] ints = {5, 8, 3, 4, 1, 0, 2, 7, 9, 6};
        reverse((l, r) -> swap(ints, l, r), 0, ints.length);
        int[] expected = {6, 9, 7, 2, 0, 1, 4, 3, 8, 5};
        assertArrayEquals(expected, ints);
    }

    @Test
    public void testReverseFirst3() {
        logger.info(methodName());
        int[] ints = {5, 8, 3, 4, 1, 0, 2, 7, 9, 6};
        reverse((l, r) -> swap(ints, l, r), 0, 3);
        int[] expected = {3, 8, 5, 4, 1, 0, 2, 7, 9, 6};
        assertArrayEquals(expected, ints);
    }

    @Test
    public void testReverseMid3() {
        logger.info(methodName());
        int[] ints = {5, 8, 3, 4, 1, 0, 2, 7, 9, 6};
        reverse((l, r) -> swap(ints, l, r), 3, 6);
        int[] expected = {5, 8, 3, 0, 1, 4, 2, 7, 9, 6};
        assertArrayEquals(expected, ints);
    }

    @Test
    public void testReverseLast3() {
        logger.info(methodName());
        int[] ints = {5, 8, 3, 4, 1, 0, 2, 7, 9, 6};
        reverse((l, r) -> swap(ints, l, r), ints.length - 3, ints.length);
        int[] expected = {5, 8, 3, 4, 1, 0, 2, 6, 9, 7};
        assertArrayEquals(expected, ints);
    }

}
