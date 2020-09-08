package test.puzzle;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

class TestIterators {

    static class FilterIterator<T> implements Iterator<T> {

        final Iterator<T> source;
        final Predicate<T> predicate;
        T next = null;

        FilterIterator(Iterator<T> source, Predicate<T> predicate) {
            this.source = source;
            this.predicate = predicate;
            advance();
        }

        void advance() {
            while (source.hasNext()) {
                next = source.next();
                if (predicate.test(next))
                    return;
            }
            next = null;
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public T next() {
            T result = next;
            advance();
            return result;
        }
    }

    static class RangeIterator implements Iterator<Integer> {
        int next, to;

        RangeIterator(int from, int to) {
            this.next = from;
            this.to = to;
        }

        @Override
        public boolean hasNext() {
            return next < to;
        }

        @Override
        public Integer next() {
            if (next >= to)
                throw new NoSuchElementException();
            int result = next;
            ++next;
            return result;
        }
    }

    static Iterable<Integer> range(int from, int to) {
        return () -> new RangeIterator(from, to);
    }

    static <T> Iterable<T> filter(Iterable<T> source, Predicate<T> predicate) {
        return () -> new FilterIterator<>(source.iterator(), predicate);
    }

    @Test
    void testRangeIterator() {
        for (int i : range(0, 10))
            System.out.print(i + " ");
        System.out.println();

        for (int i : filter(range(0, 10), i -> i % 2 == 0))
            System.out.print(i + " ");
        System.out.println();

        for (int i : filter(List.of(10, 11, 12, 13, 14, 15), i -> i % 2 == 0))
            System.out.print(i + " ");
        System.out.println();

        for (int i : (Iterable<Integer>) () -> List.of(10, 11, 12, 13, 14, 15).stream().filter(i -> i % 2 == 0).iterator())
            System.out.print(i + " ");
        System.out.println();
    }

}
