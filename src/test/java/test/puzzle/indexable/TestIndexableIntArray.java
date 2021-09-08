package test.puzzle.indexable;

import static org.junit.Assert.assertArrayEquals;

import java.util.logging.Logger;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import puzzle.Common;
import puzzle.indexable.IndexableIntArray;

class TestIndexableIntArray {
    
    static final Logger logger = Common.getLogger(TestIndexableIntArray.class);

    @Test
    void testInsertionSort() {
        int[] array = {5, 7, 3, 2, 9, 4, 0, 8, 1, 6};
        IndexableIntArray.builder(array).build().insertionSort();
        assertArrayEquals(IntStream.range(0, 10).toArray(), array);
    }

    @Test
    void testInsertionSortRange() {
        int[] array = {5, 7, 3, 2, 9, 4, 0, 8, 1, 6};
        IndexableIntArray.builder(array).range(5, array.length).build().insertionSort();
        int[] expected = {5, 7, 3, 2, 9, 0, 1, 4, 6, 8};
        assertArrayEquals(expected, array);
    }

    @Test
    void testQuickSort() {
        int[] array = {5, 7, 3, 2, 9, 4, 0, 8, 1, 6};
        IndexableIntArray.builder(array).build().quickSort();
        assertArrayEquals(IntStream.range(0, 10).toArray(), array);
    }

    @Test
    void testQuickSortRange() {
        int[] array = {5, 7, 3, 2, 9, 4, 0, 8, 1, 6};
        IndexableIntArray.builder(array).range(5, array.length).build().quickSort();
        int[] expected = {5, 7, 3, 2, 9, 0, 1, 4, 6, 8};
        assertArrayEquals(expected, array);
    }

}
