package puzzle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Iterables {

    static class FilterIterator<T> implements Iterator<T> {

        final Iterator<T> source;
        final Predicate<T> predicate;
        boolean hasNext;
        T next = null;

        FilterIterator(Iterator<T> source, Predicate<T> predicate) {
            this.source = source;
            this.predicate = predicate;
            hasNext = advance();
        }

        boolean advance() {
            while (source.hasNext())
                if (predicate.test(next = source.next()))
                    return true;
            return false;
        }

        @Override
        public boolean hasNext() {
            return hasNext;
        }

        @Override
        public T next() {
            T result = next;
            hasNext = advance();
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
            this(from, to, from <= to ? 1 : -1);
        }

        @Override
        public boolean hasNext() {
            return step > 0 ? next < to : next > to;
        }

        @Override
        public Integer next() {
            if (!hasNext())
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

    public static <T extends Comparable<T>> Iterable<T> sorted(
        Iterable<T> source) {
        List<T> result = list(source);
        Collections.sort(result);
        return result;
    }

    public static <T> Iterable<T> sorted(Iterable<T> source,
        Comparator<T> comparator) {
        List<T> result = list(source);
        Collections.sort(result, comparator);
        return result;
    }

    public static <T> Iterable<T> reverse(Iterable<T> source) {
        List<T> result = list(source);
        Collections.reverse(result);
        return result;
    }

    public static <T> Iterable<T> skip(Iterable<T> source, int skip) {
        Iterator<T> iterator = source.iterator();
        for (int i = 0; i < skip && iterator.hasNext(); ++i)
            iterator.next();
        return () -> iterator;
    }

    public static <T> Iterable<T> limit(Iterable<T> source, int limit) {
        return () -> new Iterator<T>() {

            int i = 0;
            final Iterator<T> iterator = source.iterator();

            @Override
            public boolean hasNext() {
                return iterator.hasNext() && i < limit;
            }

            @Override
            public T next() {
                if (!hasNext())
                    throw new NoSuchElementException();
                ++i;
                return iterator.next();
            }

        };
    }

    /*
     * terminal operations
     */
    public static <T> List<T> list(Iterable<T> source) {
        List<T> result = new ArrayList<>();
        for (T e : source)
            result.add(e);
        return result;
    }

    public static <T> T[] array(Iterable<T> source,
        IntFunction<T[]> supplier) {
        return list(source).toArray(supplier);
    }

    public static <T> int[] array(Iterable<Integer> source) {
        // サイズがわからないので一度リストにしてから配列にする必要があります。
        List<Integer> list = list(source);
        int size = list.size();
        int[] result = new int[size];
        for (int i = 0; i < size; ++i)
            result[i] = list.get(i);
        return result;
    }

    static <T, K, V> Map<K, V> map(Iterable<T> source,
        Supplier<Map<K, V>> supplier, Function<T, K> keyExtractor,
        Function<T, V> valueExtractor) {
        Map<K, V> result = supplier.get();
        for (T t : source)
            result.put(keyExtractor.apply(t), valueExtractor.apply(t));
        return result;
    }

    public static <T, K, V> HashMap<K, V> hashMap(Iterable<T> source,
        Function<T, K> keyExtractor, Function<T, V> valueExtractor) {
        return (HashMap<K, V>) map(source, () -> new HashMap<>(), keyExtractor,
            valueExtractor);
    }

    public static <T, K, V> LinkedHashMap<K, V> linkedHashMap(
        Iterable<T> source,
        Function<T, K> keyExtractor, Function<T, V> valueExtractor) {
        return (LinkedHashMap<K, V>) map(source, () -> new LinkedHashMap<>(),
            keyExtractor, valueExtractor);
    }

    public static <T, K extends Comparable<K>, V> TreeMap<K, V> treeMap(
        Iterable<T> source,
        Function<T, K> keyExtractor, Function<T, V> valueExtractor) {
        return (TreeMap<K, V>) map(source, () -> new TreeMap<>(), keyExtractor,
            valueExtractor);
    }

    // public static <K, V> Map<K, V> toMap(Iterable<Entry<K, V>> source,
    // Supplier<Map<K, V>> supplier) {
    // Map<K, V> result = supplier.get();
    // for (Entry<K, V> e : source)
    // result.put(e.getKey(), e.getValue());
    // return result;
    // }
    //
    // public static <K, V> Map<K, V> toMap(Iterable<Entry<K, V>> source) {
    // return toMap(source, () -> new HashMap<>());
    // }

    public static <T> T reduce(Iterable<T> source, T start,
        BinaryOperator<T> reducer) {
        for (T e : source)
            start = reducer.apply(start, e);
        return start;
    }

    public static int count(Iterable<Integer> source) {
        int result = 0;
        for (int i : source)
            ++result;
        return result;
    }

    public static int sum(Iterable<Integer> source) {
        int result = 0;
        for (int i : source)
            result += i;
        return result;
    }

    public static String join(Iterable<Integer> source, String separator) {
        String sep = "";
        StringBuilder sb = new StringBuilder();
        for (int e : source) {
            sb.append(sep).append(e);
            sep = separator;
        }
        return sb.toString();
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
}
