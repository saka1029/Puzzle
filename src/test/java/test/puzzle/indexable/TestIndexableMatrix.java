package test.puzzle.indexable;

import static org.junit.Assert.assertArrayEquals;

import java.util.Arrays;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

import puzzle.Common;
import puzzle.indexable.SimpleIndexable;

class TestIndexableMatrix {

    static final Logger logger = Common.getLogger(TestIndexableMatrix.class);

    @Test
    void testSortRows() {
        logger.info(Common.methodName());
        int[][] array = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}};
        new SimpleIndexable(
            (l, r) -> -Integer.compare(array[l][0], array[r][0]),
            (l, r) -> {
                int[] temp = array[l];
                array[l] = array[r];
                array[r] = temp;
            },
            0, array.length).quickSort();
        for (int[] row : array)
            logger.info(Arrays.toString(row));
        int[][] expected = {{6, 7, 8}, {3, 4, 5}, {0, 1, 2}};
        assertArrayEquals(expected, array);
    }

    @Test
    void testSortColumns() {
        logger.info(Common.methodName());
        int[][] array = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}};
        int length = array[0].length;
        new SimpleIndexable(
            (l, r) -> -Integer.compare(array[0][l], array[0][r]),
            (l, r) -> {
                for (int i = 0; i < length; ++i) {
                    int temp = array[i][l];
                    array[i][l] = array[i][r];
                    array[i][r] = temp;
                }
            },
            0, length).quickSort();
        for (int[] row : array)
            logger.info(Arrays.toString(row));
        int[][] expected = {{2, 1, 0}, {5, 4, 3}, {8, 7, 6}};
        assertArrayEquals(expected, array);
    }

    @Test
    void testSortColumnsSimpleIndexable() {
        logger.info(Common.methodName());
        int[][] array = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}};
        int length = array[0].length;
        new SimpleIndexable(
            (l, r) -> - Integer.compare(array[0][l], array[0][r]),
            (l, r) -> {
                for (int i = 0; i < length; ++i) {
                    int temp = array[i][l];
                    array[i][l] = array[i][r];
                    array[i][r] = temp;
                }
            },
            0, length).quickSort();
        for (int[] row : array)
            logger.info(Arrays.toString(row));
        int[][] expected = {{2, 1, 0}, {5, 4, 3}, {8, 7, 6}};
        assertArrayEquals(expected, array);
    }

}
