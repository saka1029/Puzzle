package puzzle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Iterables {

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

    static class IntRangeIterator implements Iterator<Integer> {
        int next, to, step;

        IntRangeIterator(int from, int to, int step) {
            this.next = from;
            this.to = to;
            this.step = step;
        }

        IntRangeIterator(int from, int to) {
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

    /*
     * start operations
     */
    public static Iterable<Integer> range(int from, int to) {
        return () -> new IntRangeIterator(from, to);
    }

    public static Iterable<Integer> range(int from, int to, int step) {
        return () -> new IntRangeIterator(from, to, step);
    }

    /*
     * intermediate operations
     */
    public static <T> Iterable<T> filter(Iterable<T> source,
        Predicate<T> predicate) {
        return () -> new FilterIterator<>(source.iterator(), predicate);
    }

    public static <T, U> Iterable<U> map(Iterable<T> source,
        Function<T, U> mapper) {
        return () -> new MapIterator<T, U>(source.iterator(), mapper);
    }

    /*
     * apply
     */
    public static <A, B> B apply(A a, Function<A, B> f1) {
        return f1.apply(a);
    }

    public static <A, B, C> C apply(A a, Function<A, B> f1, Function<B, C> f2) {
        return f2.apply(f1.apply(a));
    }

    public static <A, B, C, D> D apply(A a, Function<A, B> f1,
        Function<B, C> f2, Function<C, D> f3) {
        return f3.apply(f2.apply(f1.apply(a)));
    }

    public static <A, B, C, D, E> E apply(A a, Function<A, B> f1,
        Function<B, C> f2, Function<C, D> f3,
        Function<D, E> f4) {
        return f4.apply(f3.apply(f2.apply(f1.apply(a))));
    }

    public static <A, B, C, D, E, F> F apply(A a, Function<A, B> f1,
        Function<B, C> f2, Function<C, D> f3,
        Function<D, E> f4, Function<E, F> f5) {
        return f5.apply(f4.apply(f3.apply(f2.apply(f1.apply(a)))));
    }

    /*
     * terminal operations
     */
    public static <T> List<T> toList(Iterable<T> source) {
        List<T> result = new ArrayList<>();
        for (T e : source)
            result.add(e);
        return result;
    }

    public static <T> T[] toArray(Iterable<T> source,
        IntFunction<T[]> supplier) {
        return toList(source).toArray(supplier);
    }

    public static <T> int[] toArray(Iterable<Integer> source) {
        // サイズがわからないので一度リストにしてから配列にする必要があります。
        List<Integer> list = toList(source);
        int size = list.size();
        int[] result = new int[size];
        for (int i = 0; i < size; ++i)
            result[i] = list.get(i);
        return result;
    }

    public static <T, K, V> Map<K, V> toMap(Iterable<T> source,
        Supplier<Map<K, V>> supplier, Function<T, K> keyExtractor,
        Function<T, V> valueExtractor) {
        Map<K, V> result = supplier.get();
        for (T t : source)
            result.put(keyExtractor.apply(t), valueExtractor.apply(t));
        return result;
    }

    public static <T, K, V> Map<K, V> toMap(Iterable<T> source,
        Function<T, K> keyExtractor, Function<T, V> valueExtractor) {
        return toMap(source, () -> new HashMap<>(), keyExtractor,
            valueExtractor);
    }

//    public static <K, V> Map<K, V> toMap(Iterable<Entry<K, V>> source,
//        Supplier<Map<K, V>> supplier) {
//        Map<K, V> result = supplier.get();
//        for (Entry<K, V> e : source)
//            result.put(e.getKey(), e.getValue());
//        return result;
//    }
//
//    public static <K, V> Map<K, V> toMap(Iterable<Entry<K, V>> source) {
//        return toMap(source, () -> new HashMap<>());
//    }

}
