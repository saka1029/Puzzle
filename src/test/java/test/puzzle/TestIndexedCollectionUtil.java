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
import java.util.Collections;
import java.util.Comparator;
import java.util.IntSummaryStatistics;
import java.util.List;
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

    @Test
    void testQuickSortMid3() {
        logger.info(methodName());
        int[] ints = {5, 8, 3, 4, 1, 0, 2, 7, 9, 6};
        quickSort((l, r) -> Integer.compare(ints[l], ints[r]),
            (l, r) -> swap(ints, l, r),
            3, 6);
        int[] expected = {5, 8, 3, 0, 1, 4, 2, 7, 9, 6};
        assertArrayEquals(expected, ints);
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
        // for (Entry<int[], Integer> e : counts.entrySet())
        // logger.info(Arrays.toString(e.getKey()) + " : " + e.getValue());
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
        // for (Entry<int[], Integer> e : counts.entrySet())
        // logger.info(Arrays.toString(e.getKey()) + " : " + e.getValue());
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

    /**
     * Returns: index of the search key, if it is contained in the array;
     * otherwise, (-(insertion point) - 1). The insertion point is defined as
     * the point at which the key would be inserted into the array: the index of
     * the first element greater than the key, or a.length if all elements in
     * the array are less than the specified key. Note that this guarantees that
     * the return value will be >= 0 if and only if the key is found.
     */
    @Test
    public void testArraysBinarySearch() {
        logger.info(methodName());
        int[] ints = IntStream.range(0, 8).map(i -> i * 2).toArray();
        logger.info(Arrays.toString(ints));
        assertEquals(2, Arrays.binarySearch(ints, 4));
        assertEquals(-1, Arrays.binarySearch(ints, -1));
        assertEquals(-3, Arrays.binarySearch(ints, 3));
        assertEquals(-9, Arrays.binarySearch(ints, 15));
    }
    
    /**
     * コンパレータの使い方は以下のとおり。
     * キーで指定した値はcompare(left, right)の呼び出しにおいて、
     * 常にright(右辺)に指定される。
     */
    @Test
    public void testCollectionsBinarySearch() {
        logger.info(methodName());
        List<Integer> list = IntStream.range(0, 8).mapToObj(i -> i * 2).toList();
        Comparator<Integer> comp = (a, b) -> {
            logger.info("compare " + a + " and " + b);
            return Integer.compare(a, b);
        };
        logger.info("list = " + list.toString());
        logger.info("search " + 4);
        assertEquals(2, Collections.binarySearch(list, 4, comp));
        logger.info("search " + -1);
        assertEquals(-1, Collections.binarySearch(list, -1, comp));
        logger.info("search " + 3);
        assertEquals(-3, Collections.binarySearch(list, 3, comp));
        logger.info("search " + 15);
        assertEquals(-9, Collections.binarySearch(list, 15, comp));
        // 2021-09-06T17:29:41.629 情報 TestIndexedCollectionUtil testCollectionsBinarySearch 
        // 2021-09-06T17:29:41.706 情報 TestIndexedCollectionUtil list = [0, 2, 4, 6, 8, 10, 12, 14] 
        // 2021-09-06T17:29:41.708 情報 TestIndexedCollectionUtil search 4 
        // 2021-09-06T17:29:41.710 情報 TestIndexedCollectionUtil compare 6 and 4 
        // 2021-09-06T17:29:41.712 情報 TestIndexedCollectionUtil compare 2 and 4 
        // 2021-09-06T17:29:41.712 情報 TestIndexedCollectionUtil compare 4 and 4 
        // 2021-09-06T17:29:41.718 情報 TestIndexedCollectionUtil search -1 
        // 2021-09-06T17:29:41.720 情報 TestIndexedCollectionUtil compare 6 and -1 
        // 2021-09-06T17:29:41.720 情報 TestIndexedCollectionUtil compare 2 and -1 
        // 2021-09-06T17:29:41.721 情報 TestIndexedCollectionUtil compare 0 and -1 
        // 2021-09-06T17:29:41.722 情報 TestIndexedCollectionUtil search 3 
        // 2021-09-06T17:29:41.723 情報 TestIndexedCollectionUtil compare 6 and 3 
        // 2021-09-06T17:29:41.723 情報 TestIndexedCollectionUtil compare 2 and 3 
        // 2021-09-06T17:29:41.724 情報 TestIndexedCollectionUtil compare 4 and 3 
        // 2021-09-06T17:29:41.725 情報 TestIndexedCollectionUtil search 15 
        // 2021-09-06T17:29:41.727 情報 TestIndexedCollectionUtil compare 6 and 15 
        // 2021-09-06T17:29:41.728 情報 TestIndexedCollectionUtil compare 10 and 15 
        // 2021-09-06T17:29:41.730 情報 TestIndexedCollectionUtil compare 12 and 15 
        // 2021-09-06T17:29:41.730 情報 TestIndexedCollectionUtil compare 14 and 15 
    }
}
