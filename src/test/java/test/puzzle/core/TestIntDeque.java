package test.puzzle.core;

import java.util.Arrays;

import org.junit.Test;

public class TestIntDeque {

    static class IntDeque {
        int[] elements;
        int head, tail;

        public IntDeque() {
            elements = new int[17];
        }

        static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

        private int newCapacity(int needed, int jump) {
            int oldCapacity = elements.length, minCapacity;
            if ((minCapacity = oldCapacity + needed) - MAX_ARRAY_SIZE > 0) {
                if (minCapacity < 0)
                    throw new IllegalStateException("sorry, deque too big");
                return Integer.MAX_VALUE;
            }
            if (needed > jump)
                return minCapacity;
            return oldCapacity + jump - MAX_ARRAY_SIZE < 0
                ? oldCapacity + jump : MAX_ARRAY_SIZE;
        }

        private void grow(int needed) {
            int oldCapacity = elements.length;
            int newCapacity;
            int jump = oldCapacity < 64 ? oldCapacity + 2 : oldCapacity >> 1;
            if (jump < needed || (newCapacity = (oldCapacity + jump)) - MAX_ARRAY_SIZE > 0)
                newCapacity = newCapacity(needed, jump);
            int[] es = elements = Arrays.copyOf(elements, newCapacity);
            if (tail < head || (tail == head && es[head] != 0)) {
                int newSpace = newCapacity - oldCapacity;
                System.arraycopy(es, head, es, head + newSpace, oldCapacity - head);
                for (int i = head, to = (head += newSpace); i < to; ++i)
                    es[i] = 0;
            }
        }

        static final int inc(int i, int modulus) {
            return ++i >= modulus ? 0 : i;
        }

        static final int dec(int i, int modulus) {
            return --i < 0 ? modulus - 1 : i;
        }

        public void addFirst(int e) {
            elements[head = dec(head, elements.length)] = e;
            if (head == tail) grow(1);
        }

        public void addLast(int e) {
            elements[tail] = e;
            if (head == (tail = inc(tail, elements.length)))
                grow(1);
        }
    }

    @Test
    public void test() {
        // fail("Not yet implemented");
    }

}
