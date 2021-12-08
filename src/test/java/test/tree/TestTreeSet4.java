package test.tree;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.AbstractSet;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Objects;

import org.junit.jupiter.api.Test;

class TestTreeSet4 {

    public class TreeSet<E extends Comparable<E>> extends AbstractSet<E> {

        private int size = 0;
        private Node<E> root = null;

        private static class Node<E> {
            E value;
            Node<E> left = null, right = null;

            Node(E value) {
                this.value = value;
            }
        }

        @Override
        public Iterator<E> iterator() {
            return new Iterator<E>() {

                Deque<Node<E>> stack = new ArrayDeque<>(size);
                {
                    advance(root);
                }

                void advance(Node<E> top) {
                    for (Node<E> node = top; node != null; node = node.left)
                        stack.push(node);
                }

                @Override
                public boolean hasNext() {
                    return !stack.isEmpty();
                }

                @Override
                public E next() {
                    Node<E> node = stack.pop();
                    advance(node.right);
                    return node.value;
                }
            };
        }

        @Override
        public int size() {
            return size;
        }

        Node<E> add(Node<E> current, E value) {
            if (current == null) {
                ++size;
                return new Node<E>(value);
            }
            int compare = value.compareTo(current.value);
            if (compare < 0)
                current.left = add(current.left, value);
            else if (compare > 0)
                current.right = add(current.right, value);
            return current;
        }

        @Override
        public boolean add(E e) {
            int oldSize = size;
            root = add(root, e);
            return size != oldSize;
        }

        Node<E> getEntry(Object value) {
            Node<E> current = root;
            Objects.requireNonNull(value);
            @SuppressWarnings("unchecked")
            Comparable<? super E> v = (Comparable<? super E>)value;
            while (current != null) {
                int compare = v.compareTo(current.value);
                if (compare < 0)
                    current = current.left;
                else if (compare > 0)
                    current = current.right;
                else
                    return current;
            }
            return current;
        }

        @Override
        public boolean contains(Object o) {
            return  getEntry(o) != null;
        }
    }

    TreeSet<Integer> emptySet = new TreeSet<>();

    @Test
    void testSizeEmpty() {
        assertEquals(0, emptySet.size());
    }

    @Test
    void testIsEmptyEmpty() {
        assertTrue(emptySet.isEmpty());
    }

    @Test
    void testIteratorEmpty() {
        assertFalse(emptySet.iterator().hasNext());
    }

    @Test
    void testToStringEmpty() {
        assertEquals("[]", emptySet.toString());
    }

    @Test
    void testToArrayEmpty() {
        assertArrayEquals(new Integer[] {}, emptySet.toArray());
    }

    @Test
    void testToContainsEmpty() {
        assertFalse(emptySet.contains(0));
    }

    @Test
    void testAdd() {
        TreeSet<Integer> set = new TreeSet<Integer>();
        assertTrue(set.add(1));
        assertTrue(set.add(0));
        assertTrue(set.add(2));
        assertFalse(set.add(0));
        assertEquals(3, set.size());
        assertEquals(1, set.root.value);
        assertEquals(0, set.root.left.value);
        assertEquals(2, set.root.right.value);
        assertTrue(set.contains(0));
        assertTrue(set.contains(1));
        assertTrue(set.contains(2));
        assertEquals("[0, 1, 2]", set.toString());
    }

    @Test
    void testAdd2() {
        TreeSet<Integer> set = new TreeSet<Integer>();
        assertTrue(set.add(0));
        assertTrue(set.add(1));
        assertTrue(set.add(2));
        assertEquals(3, set.size());
        assertEquals(0, set.root.value);
        assertEquals(1, set.root.right.value);
        assertEquals(2, set.root.right.right.value);
        assertEquals("[0, 1, 2]", set.toString());
    }

    @Test
    void testContains() {
        TreeSet<Integer> set = new TreeSet<Integer>();
        assertTrue(set.add(1));
        assertTrue(set.add(0));
        assertTrue(set.add(2));
        assertFalse(set.contains(-1));
        assertTrue(set.contains(0));
        assertTrue(set.contains(1));
        assertTrue(set.contains(2));
        assertFalse(set.contains(3));
    }
}
