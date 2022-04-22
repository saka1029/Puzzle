package test.puzzle.indexable;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static puzzle.core.Common.methodName;

import java.util.Arrays;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import org.junit.Test;

import puzzle.core.Common;
import puzzle.indexable.IndexComparator;
import puzzle.indexable.IndexSwapper;
import puzzle.indexable.Indexable;
import puzzle.indexable.SimpleIndexable;

public class TestIndexable {

    static final Logger logger = Common.getLogger(TestIndexable.class);

    static class IntArrayIndexable implements Indexable {
        int[] array;

        public IntArrayIndexable(int[] array) {
            this.array = array;
        }

        @Override
        public int compare(int leftIndex, int rightIndex) {
            return Integer.compare(array[leftIndex], array[rightIndex]);
        }

        @Override
        public void swap(int leftIndex, int rightIndex) {
            int temp = array[leftIndex];
            array[leftIndex] = array[rightIndex];
            array[rightIndex] = temp;
        }

        @Override
        public int begin() {
            return 0;
        }

        @Override
        public int end() {
            return array.length;
        }
    }

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
    public void testBinarySearch() {
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

    @Test
    public void testBubbleSort() {
        int[] input = {7, 5, 6, 4, 3, 0, 9, 8, 2, 1};
        new IntArrayIndexable(input).bubbleSort();
        System.out.println(Arrays.toString(input));
        assertArrayEquals(new int[] {0,1,2,3,4,5,6,7,8,9}, input);
    }

    @Test
    public void testHeapSort() {
        int[] input = {7, 5, 6, 4, 3, 0, 9, 8, 2, 1};
        new IntArrayIndexable(input).heapSort();
        System.out.println(Arrays.toString(input));
        assertArrayEquals(new int[] {0,1,2,3,4,5,6,7,8,9}, input);
    }
}
