package test.puzzle.list;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Test;

public class TestNDList {

    static class NDList<T> implements Iterable<T> {

        static final NDList<?> NIL = new NDList<>(null, null);

        @SuppressWarnings("unchecked")
        static <T> NDList<T> nil() {
            return (NDList<T>) NIL;
        }

        final T element;
        final NDList<T> rest;

        NDList(T element, NDList<T> rest) {
            this.element = element;
            this.rest = rest;
        }

        @SuppressWarnings("unchecked")
        public static <T> NDList<T> of(T... elements) {
            NDList<T> result = nil();
            for (T e : elements)
                result = result.addLast(e);
            return result;
        }

        public int size() {
            int size = 0;
            for (NDList<T> list = this; list != nil(); list = list.rest)
                ++size;
            return size;
        }

        public NDList<T> addLast(T element) {
            return new NDList<>(element, this);
        }

        public NDList<T> reverse() {
            NDList<T> result = nil();
            for (NDList<T> list = this; list != nil(); list = list.rest)
                result = result.addLast(list.element);
            return result;
        }

        public Iterator<T> descendingIterator() {
            return new Iterator<>() {
                NDList<T> list = NDList.this;

                @Override
                public boolean hasNext() {
                    return list != nil();
                }

                @Override
                public T next() {
                    T e = list.element;
                    list = list.rest;
                    return e;
                }
            };
        }

        @Override
        public Iterator<T> iterator() {
            return reverse().descendingIterator();
        }
    }

    @Test
    public void testNDList() {
        NDList<Integer> list = NDList.of(1, 2, 3);
        assertEquals(3, list.size());
        Iterator<Integer> it = list.iterator();
        assertTrue(it.hasNext()); assertEquals(1, (int)it.next());
        assertTrue(it.hasNext()); assertEquals(2, (int)it.next());
        assertTrue(it.hasNext()); assertEquals(3, (int)it.next());
        assertFalse(it.hasNext());
        NDList<Integer> list2 = list.addLast(4);
        assertEquals(3, list.size());
        assertEquals(4, list2.size());
        Iterator<Integer> it2 = list2.iterator();
        assertTrue(it2.hasNext()); assertEquals(1, (int)it2.next());
        assertTrue(it2.hasNext()); assertEquals(2, (int)it2.next());
        assertTrue(it2.hasNext()); assertEquals(3, (int)it2.next());
        assertTrue(it2.hasNext()); assertEquals(4, (int)it2.next());
        assertFalse(it2.hasNext());
    }

}
