package test.tree;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.AbstractSet;
import java.util.Iterator;

import org.junit.jupiter.api.Test;

class TestTreeSet1 {

    public class TreeSet<E extends Comparable<E>> extends AbstractSet<E> {

        private int size = 0;

        @Override
        public Iterator<E> iterator() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public int size() {
            return size;
        }
    }

    @Test
    void testSizeEmpty() {
        TreeSet<Integer> set = new TreeSet<>();
        assertEquals(0, set.size());
    }

    @Test
    void testIsEmptyEmpty() {
        TreeSet<Integer> set = new TreeSet<>();
        assertTrue(set.isEmpty());
    }
}
