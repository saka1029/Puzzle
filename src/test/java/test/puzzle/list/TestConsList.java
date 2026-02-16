package test.puzzle.list;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Test;

import puzzle.core.Cons;

public class TestConsList {

    @Test
    public void testToString() {
        Cons<Integer> list = Cons.of(1, 2, 3);
        assertEquals("[1, 2, 3]", list.toString());
    }
    
    @Test
    public void testConsList() {
        Cons<Integer> list = Cons.of(1, 2, 3);
        assertEquals(3, list.size());
        Iterator<Integer> it = list.iterator();
        assertTrue(it.hasNext()); assertEquals(1, (int) it.next());
        assertTrue(it.hasNext()); assertEquals(2, (int) it.next());
        assertTrue(it.hasNext()); assertEquals(3, (int) it.next());
        assertFalse(it.hasNext());
        Cons<Integer> list2 = list.cons(4);
        assertEquals(3, list.size());
        assertEquals(4, list2.size());
        Iterator<Integer> it2 = list2.iterator();
        assertTrue(it2.hasNext()); assertEquals(4, (int) it2.next());
        assertTrue(it2.hasNext()); assertEquals(1, (int) it2.next());
        assertTrue(it2.hasNext()); assertEquals(2, (int) it2.next());
        assertTrue(it2.hasNext()); assertEquals(3, (int) it2.next());
        assertFalse(it2.hasNext());
    }
}
