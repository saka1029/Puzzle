package test.puzzle.list;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import java.util.Iterator;
import org.junit.Test;
import puzzle.list.TList;

public class TestTList {

    @Test
    public void testSize() {
        TList<Integer> list = TList.of(1, 2, 3);
        assertEquals(3, list.size());
    }

    @Test
    public void testOf() {
        TList<Integer> list = TList.of(1, 2, 3);
        assertEquals(1, (int)list.car());
        assertEquals(2, (int)list.cdr().car());
        assertEquals(3, (int)list.cdr().cdr().car());
        assertEquals(TList.nil(), list.cdr().cdr().cdr());
    }

    @Test
    public void testIterator() {
        TList<Integer> list = TList.of(1, 2, 3);
        Iterator<Integer> it = list.iterator();
        assertEquals(1, (int)it.next());
        assertEquals(2, (int)it.next());
        assertEquals(3, (int)it.next());
        assertFalse(it.hasNext());
    }

    @Test
    public void testStream() {
        TList<Integer> list = TList.of(1, 2, 3);
        assertArrayEquals(new int[] {1, 2, 3}, list.stream().mapToInt(Integer::valueOf).toArray());
    }

    @Test
    public void testHashCode() {
        assertEquals(0, TList.nil().hashCode());
        assertEquals(TList.of(1, 2, 3).hashCode(), TList.cons(1, TList.of(2, 3)).hashCode());
    }

    @Test
    public void testEquals() {
        assertEquals(TList.nil(), TList.of());
        assertEquals(TList.nil(), TList.of(1).cdr());
        assertEquals(TList.of(1, 2, 3), TList.cons(1, TList.of(2, 3)));
    }

}
