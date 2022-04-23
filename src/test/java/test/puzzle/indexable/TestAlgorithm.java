package test.puzzle.indexable;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.junit.Test;

import puzzle.core.Common;
import puzzle.indexable.Algorithm;
import puzzle.indexable.IndexedComparator;
import puzzle.indexable.IndexedSwapper;

public class TestAlgorithm {

    static final Logger logger = Common.getLogger(TestAlgorithm.class);

    @Test
    public void testQuickSortArrayInt() {
        int[] input = {7, 5, 6, 4, 3, 0, 9, 8, 2, 1};
        Algorithm.quickSort(
            IndexedComparator.comparator(input),
            IndexedSwapper.swapper(input),
            0, input.length);
        assertArrayEquals(new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9}, input);
    }

    @Test
    public void testQuickSortListInteger() {
        List<Integer> input = Arrays.asList(7, 5, 6, 4, 3, 0, 9, 8, 2, 1);
        Algorithm.quickSort(
            IndexedComparator.comparator(input),
            IndexedSwapper.swapper(input),
            0, input.size());
        assertEquals(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), input);
    }

    @Test
    public void testHeapSort() {}
}
