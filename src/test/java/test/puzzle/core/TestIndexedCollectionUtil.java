package test.puzzle.core;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static puzzle.core.Common.getLogger;
import static puzzle.core.Common.methodName;
import static puzzle.core.IndexedCollectionUtil.compare;
import static puzzle.core.IndexedCollectionUtil.nextPermutation;
import static puzzle.core.IndexedCollectionUtil.quickSort;
import static puzzle.core.IndexedCollectionUtil.reverse;
import static puzzle.core.IndexedCollectionUtil.shuffle;
import static puzzle.core.IndexedCollectionUtil.swap;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.IntBinaryOperator;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import org.junit.Test;

import puzzle.core.IndexedCollectionUtil.IntBiConsumer;

public class TestIndexedCollectionUtil {

    static final Logger logger = getLogger(TestIndexedCollectionUtil.class);

    @Test
    public void testQuickSortInts() {
        logger.info(methodName());
        int[] ints = {5, 8, 3, 4, 1, 0, 2, 7, 9, 6};
        quickSort((l, r) -> Integer.compare(ints[l], ints[r]),
            (l, r) -> swap(ints, l, r),
            0, ints.length);
        assertArrayEquals(IntStream.range(0, 10).toArray(), ints);
    }

    @Test
    public void testQuickSortIntsReverse() {
        logger.info(methodName());
        int[] ints = {5, 8, 3, 4, 1, 0, 2, 7, 9, 6};
        quickSort((l, r) -> -Integer.compare(ints[l], ints[r]),
            (l, r) -> swap(ints, l, r),
            0, ints.length);
        int len = ints.length;
        assertArrayEquals(IntStream.range(0, len).map(i -> len - i - 1).toArray(), ints);
    }

    @Test
    public void testQuickSortMid3() {
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
    public void testShuffle() {
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
    public void testShufflePartial() {
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

    @Test
    public void testNextPermutationInt3() {
        int[] ints = {0, 1, 2};
        IntBinaryOperator comp = (l, r) -> compare(ints, l, r);
        IntBiConsumer swap = (l, r) -> swap(ints, l, r);
        int end = ints.length;
        int[][] expected = {
            {0, 1, 2},
            {0, 2, 1},
            {1, 0, 2},
            {1, 2, 0},
            {2, 0, 1},
            {2, 1, 0},
        };
        for (int i = 0, size = expected.length; i < size; ++i) {
            int[] e = expected[i];
            assertArrayEquals(e, ints);
            assertTrue(i + 1 < size == nextPermutation(comp, swap, 0, end));
        }
    }

    @Test
    public void testNextPermutationInt3From4() {
        int[] ints = {0, 1, 2, 3};
        IntBinaryOperator comp = (l, r) -> compare(ints, l, r);
        IntBiConsumer swap = (l, r) -> swap(ints, l, r);
        int end = ints.length;
        int[][] expected = {
            {0, 1, 2, 3},
            {0, 1, 3, 2},
            {0, 2, 1, 3},
            {0, 2, 3, 1},
            {0, 3, 1, 2},
            {0, 3, 2, 1},
        };
        for (int i = 0, size = expected.length; i < size; ++i) {
            int[] e = expected[i];
            assertArrayEquals(e, ints);
            assertTrue(i + 1 < size == nextPermutation(comp, swap, 1, end));
        }
    }

    @Test
    public void testNextPermutationList() {
        List<Integer> list = Arrays.asList(0, 1, 2);
        IntBinaryOperator comp = (l, r) -> compare(list, l, r);
        IntBiConsumer swap = (l, r) -> swap(list, l, r);
        int end = list.size();
        List<List<Integer>> expected = List.of(
            List.of(0, 1, 2),
            List.of(0, 2, 1),
            List.of(1, 0, 2),
            List.of(1, 2, 0),
            List.of(2, 0, 1),
            List.of(2, 1, 0)
        );
        for (int i = 0, size = expected.size(); i < size; ++i) {
            List<Integer> e = expected.get(i);
            assertEquals(e, list);
            assertTrue(i + 1 < size == nextPermutation(comp, swap, 0, end));
        }
    }

    @Test
    public void testNextPermutationStringBuilder() {
        StringBuilder sb = new StringBuilder("ABC");
        IntBinaryOperator comp = (l, r) -> compare(sb, l, r);
        IntBiConsumer swap = (l, r) -> swap(sb, l, r);
        int end = sb.length();
        List<String> expected = List.of( "ABC", "ACB", "BAC", "BCA", "CAB", "CBA");
        for (int i = 0, size = expected.size(); i < size; ++i) {
            String e = expected.get(i);
            assertEquals(e, sb.toString());
            assertTrue(i + 1 < size == nextPermutation(comp, swap, 0, end));
        }
    }

    public interface IntIndexable {
        int size();
        int compare(int left, int right);
        void swap(int left, int right);
    }

    public static class IntArray implements IntIndexable {

        public final int[] array;
        public final IntBinaryOperator indexComparator;

        private IntArray(int[] array, IntBinaryOperator indexComparator) {
            Objects.requireNonNull(array, "array");
            Objects.requireNonNull(indexComparator, "array");
            this.array = array;
            this.indexComparator = indexComparator;
        }

        public static IntArray of(int[] array) {
            return new IntArray(array, (left, right) -> Integer.compare(array[left], array[right]));
        }

        public static IntArray withIndexComparator(int[] array, IntBinaryOperator IndexComparator) {
            return new IntArray(array, IndexComparator);
        }

        public static IntArray of(int[] array, Comparator<Integer> comparator) {
            return new IntArray(array, (left, right) -> comparator.compare(array[left], array[right]));
        }

        @Override
        public int size() {
            return array.length;
        }

        @Override
        public int compare(int left, int right) {
            return indexComparator.applyAsInt(left, right);
        }

        @Override
        public void swap(int left, int right) {
            int temp = array[left];
            array[left] = array[right];
            array[right] = temp;
        }
    }

    /*
     * C言語による挿入ソート
     * 8   4   3   7   6   5   2   1   （初期データ）
     * 4   8   3   7   6   5   2   1   （1回目のループ終了時）
     * 3   4   8   7   6   5   2   1   （2回目のループ終了時）
     * 3   4   7   8   6   5   2   1   （3回目のループ終了時）
     * 3   4   6   7   8   5   2   1   （4回目のループ終了時）
     * 3   4   5   6   7   8   2   1   （5回目のループ終了時）
     * 2   3   4   5   6   7   8   1   （6回目のループ終了時）
     * 1   2   3   4   5   6   7   8   （7回目のループ終了時。ソート完了）
     *
     * void
     * insertion_sort(int data[], size_t n) {
     *     for (size_t i = 1; i < n; i++) {
     *         if (data[i - 1] > data[i]) {
     *             size_t j = i;
     *             int tmp = data[i];
     *             do {
     *                 data[j] = data[j - 1];
     *                 j--;
     *             } while (j > 0 && data[j - 1] > tmp);
     *             data[j] = tmp;
     *         }
     *     }
     */

    /*
     * C言語による挿入ソート(swapを使用するもの)
     * void insertionSort(int numbers[], int array_size)
     * {
     *     int i, j;
     *
     *     for (i=1; i < array_size; i++) { //整列されていない部分の先頭を指す
     *
     *         j = i; // 交換要素のためのインデックス
     *
     *         // 整列済みの場合は処理しない
     *         while ((j > 0) && (numbers[j-1] > numbers[j])) {
     *             // 整列されていない隣り合う要素を交換する
     *             swap(&numbers[j-1], &numbers[j]);
     *
     *             // 隣り合う要素のインデックスを更新
     *             j--;
     *         }
     *     }
     * }
     *
     * void swap(int *p_from, int *p_to) {
     *     int tmp;
     *     tmp = *p_from;
     *     *p_from = *p_to;
     *     *p_to = tmp;
     * }
     */

    static void insertionSort(IntIndexable indexable) {
        for (int i = 1, n = indexable.size(); i < n; i++)
            for (int j = i; j > 0 && indexable.compare(j - 1, j) > 0; --j)
                indexable.swap(j - 1, j);
    }

    @Test
    public void testInsertionSort() {
        int[] ints = {5, 8, 3, 4, 1, 0, 2, 7, 9, 6};
        IntIndexable indexable = IntArray.of(ints);
        insertionSort(indexable);
        int[] expected = ints.clone();
        Arrays.sort(expected);
        logger.info(Arrays.toString(ints));
        assertArrayEquals(expected, ints);
    }

    @Test
    public void testInsertionSortComparator() {
        int[] ints = {5, 8, 3, 4, 1, 0, 2, 7, 9, 6};
        IntIndexable indexable = IntArray.of(ints, Integer::compare);
        insertionSort(indexable);
        int[] expected = ints.clone();
        Arrays.sort(expected);
        logger.info(Arrays.toString(ints));
        assertArrayEquals(expected, ints);
    }

    @Test
    public void testInsertionSortWithIndexComparator() {
        int[] ints = {5, 8, 3, 4, 1, 0, 2, 7, 9, 6};
        IntIndexable indexable = IntArray.withIndexComparator(ints,
            (left, right) -> Integer.compare(ints[left], ints[right]));
        insertionSort(indexable);
        int[] expected = ints.clone();
        Arrays.sort(expected);
        logger.info(Arrays.toString(ints));
        assertArrayEquals(expected, ints);
    }
}
