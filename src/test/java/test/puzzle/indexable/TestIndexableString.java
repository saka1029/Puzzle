package test.puzzle.indexable;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

import puzzle.Common;
import puzzle.indexable.IndexableString;

class TestIndexableString {

    static final Logger logger = Common.getLogger(TestIndexableString.class);

    @Test
    void testInsertionSort() {
        StringBuilder array = new StringBuilder("fhdcjeaibg");
        IndexableString.builder(array).build().insertionSort();
        assertEquals("abcdefghij", array.toString());
    }

    @Test
    void testInsertionSortRange() {
        StringBuilder array = new StringBuilder("fhdcjeaibg");
        IndexableString.builder(array).range(5, array.length()).build().insertionSort();
        assertEquals("fhdcjabegi", array.toString());
    }

    @Test
    void testQuickSort() {
        StringBuilder array = new StringBuilder("fhdcjeaibg");
        IndexableString.builder(array).build().quickSort();
        assertEquals("abcdefghij", array.toString());
    }

    @Test
    void testQuickSortRange() {
        StringBuilder array = new StringBuilder("fhdcjeaibg");
        IndexableString.builder(array).range(5, array.length()).build().quickSort();
        assertEquals("fhdcjabegi", array.toString());
    }

}
