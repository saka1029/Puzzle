package test.puzzle.indexable;


import static org.junit.Assert.assertEquals;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.junit.Test;
import puzzle.core.Common;
import puzzle.indexable.IndexComparator;
import puzzle.indexable.IndexSwapper;
import puzzle.indexable.Indexable;
import puzzle.indexable.SimpleIndexable;

public class TestIndexableString {

    static final Logger logger = Common.getLogger(TestIndexableString.class);

    static final IndexComparator comp(StringBuilder array) {
        return (l, r) -> Character.compare(array.charAt(l), array.charAt(r));
    }

    static final IndexSwapper swapper(StringBuilder array) {
        return (l, r) -> {
            char temp = array.charAt(l);
            array.setCharAt(l, array.charAt(r));
            array.setCharAt(r, temp);
        };
    }

    @Test
    public void testInsertionSort() {
        logger.info(Common.methodName());
        StringBuilder array = new StringBuilder("fhdcjeaibg");
        new SimpleIndexable(
            comp(array),
            swapper(array),
            0, array.length()).insertionSort();
        assertEquals("abcdefghij", array.toString());
    }

    @Test
    public void testInsertionSortRange() {
        logger.info(Common.methodName());
        StringBuilder array = new StringBuilder("fhdcjeaibg");
        new SimpleIndexable(
            comp(array),
            swapper(array),
            5, array.length()).insertionSort();
        assertEquals("fhdcjabegi", array.toString());
    }

    @Test
    public void testQuickSort() {
        logger.info(Common.methodName());
        StringBuilder array = new StringBuilder("fhdcjeaibg");
        new SimpleIndexable(
            comp(array),
            swapper(array),
            0, array.length()).quickSort();
        assertEquals("abcdefghij", array.toString());
    }

    @Test
    public void testQuickSortRange() {
        logger.info(Common.methodName());
        StringBuilder array = new StringBuilder("fhdcjeaibg");
        new SimpleIndexable(
            comp(array),
            swapper(array),
            5, array.length()).quickSort();
        assertEquals("fhdcjabegi", array.toString());
    }

    /**
     * HがSより 僕と君は対等な関係 - YouTube <br>
     * https://www.youtube.com/watch?v=FkiOXGZTmPU
     */
    @Test
    public void testNextPermutation() {
        logger.info(Common.methodName());
        StringBuilder array = new StringBuilder("HOSEI");
        Indexable ind = new SimpleIndexable(
            comp(array),
            swapper(array),
            0, array.length());
        ind.quickSort();
        Pattern pat = Pattern.compile("H.*S");
//        int i = 1;
        int count = 0;
        do {
            boolean match = pat.matcher(array).find();
            if (match)
                ++count;
//            logger.info(i++ + " : " + array.toString() + (match ? " *" : ""));
        } while (ind.nextPermutation());
        assertEquals(60, count);
    }
}