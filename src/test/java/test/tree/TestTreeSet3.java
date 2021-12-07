package test.tree;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.AbstractSet;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import org.junit.jupiter.api.Test;

class TestTreeSet3 {

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
                    return stack.peek() != null;
                }

                @Override
                public E next() {
                    Node<E> node = stack.pop();
                    advance(node.left);
                    return node.value;
                }
            };
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

    TreeSet<Integer> emptySet = new TreeSet<>();

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
        set.root = new TreeSet.Node<>(0);
        set.size = 1;
        assertEquals("[0]", set.toString());
    }

}
