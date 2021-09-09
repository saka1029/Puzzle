package test.puzzle.indexable;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import puzzle.Common;
import puzzle.indexable.Indexable;
import puzzle.indexable.IndexableString;

class TestIndexableString {

    static final Logger logger = Common.getLogger(TestIndexableString.class);

    @Test
    void testInsertionSort() {
        logger.info(Common.methodName());
        StringBuilder array = new StringBuilder("fhdcjeaibg");
        IndexableString.builder(array).build().insertionSort();
        assertEquals("abcdefghij", array.toString());
    }

    @Test
    void testInsertionSortRange() {
        logger.info(Common.methodName());
        StringBuilder array = new StringBuilder("fhdcjeaibg");
        IndexableString.builder(array).range(5, array.length()).build().insertionSort();
        assertEquals("fhdcjabegi", array.toString());
    }

    @Test
    void testQuickSort() {
        logger.info(Common.methodName());
        StringBuilder array = new StringBuilder("fhdcjeaibg");
        IndexableString.builder(array).build().quickSort();
        assertEquals("abcdefghij", array.toString());
    }

    @Test
    void testQuickSortRange() {
        logger.info(Common.methodName());
        StringBuilder array = new StringBuilder("fhdcjeaibg");
        IndexableString.builder(array).range(5, array.length()).build().quickSort();
        assertEquals("fhdcjabegi", array.toString());
    }

    /**
     * HがSより 僕と君は対等な関係 - YouTube <br>
     * https://www.youtube.com/watch?v=FkiOXGZTmPU
     */
    @Test
    void testNextPermutation() {
        logger.info(Common.methodName());
        StringBuilder array = new StringBuilder("HOSEI");
        Indexable ind = IndexableString.builder(array).build();
        ind.quickSort();
        Pattern pat = Pattern.compile("H.*S");
        // int i = 1;
        int count = 0;
        do {
            boolean match = pat.matcher(array).find();
            if (match)
                ++count;
            // logger.info(i++ + " : " + array.toString() + (match ? " *" : ""));
        } while (ind.nextPermutation());
        assertEquals(60, count);
    }
}