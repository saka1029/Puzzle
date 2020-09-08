package test.puzzle;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
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

    static class MapIterator<T, U> implements Iterator<U> {

        final Iterator<T> source;
        final Function<T, U> mapper;

        MapIterator(Iterator<T> source, Function<T, U> mapper) {
            this.source = source;
            this.mapper = mapper;
        }

        @Override
        public boolean hasNext() {
            return source.hasNext();
        }

        @Override
        public U next() {
            if (!hasNext())
                throw new NoSuchElementException();
            return mapper.apply(source.next());
        }
    }

    static class RangeIterator implements Iterator<Integer> {
        int next, to, step;

        RangeIterator(int from, int to, int step) {
            this.next = from;
            this.to = to;
            this.step = step;
        }

        RangeIterator(int from, int to) {
            this(from, to, 1);
        }

        @Override
        public boolean hasNext() {
            return step > 0 ? next < to : next > to;
        }

        @Override
        public Integer next() {
            if (next >= to)
                throw new NoSuchElementException();
            int result = next;
            next += step;
            return result;
        }
    }

    static Iterable<Integer> range(int from, int to) {
        return () -> new RangeIterator(from, to);
    }

    static Iterable<Integer> range(int from, int to, int step) {
        return () -> new RangeIterator(from, to, step);
    }

    static <T> Iterable<T> filter(Iterable<T> source, Predicate<T> predicate) {
        return () -> new FilterIterator<>(source.iterator(), predicate);
    }

    static <T, U> Iterable<U> map(Iterable<T> source, Function<T, U> mapper) {
        return () -> new MapIterator<T, U>(source.iterator(), mapper);
    }

    static <A, B> B apply(A a, Function<A, B> f1) {
        return f1.apply(a);
    }

    static <A, B, C> C apply(A a, Function<A, B> f1, Function<B, C> f2) {
        return f2.apply(f1.apply(a));
    }

    static <T> List<T> list(Iterable<T> source) {
        List<T> result = new ArrayList<>();
        for (T e : source)
            result.add(e);
        return result;
    }

    @Test
    void testRangeIterator() {
        assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), list(range(0, 10)));
        assertEquals(List.of(0, 2, 4, 6, 8),
            list(filter(range(0, 10), i -> i % 2 == 0)));
        assertEquals(List.of(10, 12, 14),
            list(filter(List.of(10, 11, 12, 13, 14, 15), i -> i % 2 == 0)));
    }

    @Test
    void testMapIterator() {
        assertEquals(List.of(0, 2, 4, 6, 8),
            list(map(range(0, 5), i -> i * 2)));
    }

    @Test
    void testApply() {
        assertEquals(List.of(0, 2, 4, 6, 8),
            list(apply(range(0, 10), r -> filter(r, i -> i % 2 == 0))));
        assertEquals(List.of(10, 12, 14),
            list(apply(List.of(10, 11, 12, 13, 14, 15),
                r -> filter(r, i -> i % 2 == 0))));
    }

    static Iterable<Integer> primes(int max) {
        Iterable<Integer> primes = range(2, max);
        Function<Integer, Predicate<Integer>> sieve = n -> i -> i == n || i % n != 0;
        primes = filter(primes, sieve.apply(2));
        for (int i = 3; i * i <= max; i += 2)
            primes = filter(primes, sieve.apply(i));
        return primes;
    }

    @Test
    void testPrimes() {
        assertEquals(List.of(2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43,
            47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97), list(primes(100)));
    }

}
