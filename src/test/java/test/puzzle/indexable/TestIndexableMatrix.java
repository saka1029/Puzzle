package test.puzzle.indexable;

import static org.junit.Assert.assertArrayEquals;

import java.util.Arrays;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

import puzzle.Common;
import puzzle.indexable.Indexable;

class TestIndexableMatrix {
    
    static final Logger logger = Common.getLogger(TestIndexableMatrix.class);

    @Test
    void testSortRows() {
        logger.info(Common.methodName());
        int[][] array = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}};
        new Indexable() {
            
            @Override
            public int begin() {
                return 0;
            }
            
            @Override
            public int end() {
                return array.length;
            }
            
            @Override
            public int compare(int leftIndex, int rightIndex) {
                return -Integer.compare(array[leftIndex][0], array[rightIndex][0]);
            }
            
            @Override
            public void swap(int leftIndex, int rightIndex) {
                int[] temp = array[leftIndex];
                array[leftIndex] = array[rightIndex];
                array[rightIndex] = temp;
            }
        }.quickSort();
        for (int[] row : array)
            logger.info(Arrays.toString(row));
        int[][] expected = {{6, 7, 8}, {3, 4, 5}, {0, 1, 2}};
        assertArrayEquals(expected, array);
    }

    @Test
    void testSortColumns() {
        logger.info(Common.methodName());
        int[][] array = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}};
        new Indexable() {
            
            final int length = array[0].length;
            
            @Override
            public int begin() {
                return 0;
            }
            
            @Override
            public int end() {
                return length;
            }
            
            @Override
            public int compare(int leftIndex, int rightIndex) {
                return -Integer.compare(array[0][leftIndex], array[0][rightIndex]);
            }
            
            @Override
            public void swap(int leftIndex, int rightIndex) {
                for (int i = 0; i < length; ++i) {
                    int temp = array[i][leftIndex];
                    array[i][leftIndex] = array[i][rightIndex];
                    array[i][rightIndex] = temp;
                }
            }
        }.quickSort();
        for (int[] row : array)
            logger.info(Arrays.toString(row));
        int[][] expected = {{2, 1, 0}, {5, 4, 3}, {8, 7, 6}};
        assertArrayEquals(expected, array);
    }

}
