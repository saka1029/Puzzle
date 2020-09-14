package puzzle;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
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
            if (!hasNext())
                throw new NoSuchElementException();
            T result = next;
            hasNext = advance();
            return result;
        }
    }

    static class IntRangeIterator implements Iterator<Integer> {

        int next, to, step;

        IntRangeIterator(int from, int to, int step) {
            if (step == 0)
                throw new IllegalArgumentException("step");
            if (Math.signum(to - from) != Math.signum(step))
                throw new IllegalArgumentException();
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

    static class PermutationIndexIterator implements Iterator<int[]> {

        final int n, r;
        final long[] available, rest;
        final int[] selected;
        boolean hasNext;

        PermutationIndexIterator(int n, int r) {
            if (n < 0)
                throw new IllegalArgumentException("n must be >= 0");
            if (r < 0)
                throw new IllegalArgumentException("r must be >= 0");
            if (r > n)
                throw new IllegalArgumentException("r must be <= n");
            if (n > Long.SIZE)
                throw new IllegalArgumentException("n must be <= " + Long.SIZE);
            this.n = n;
            this.r = r;
            this.available = new long[r];
            this.rest = new long[r];
            this.selected = new int[r];
            long allOne = n == Long.SIZE ? -1L : (1L << n) - 1L;
            if (r == 0)
                hasNext = true;
            else {
                available[0] = rest[0] = allOne;
                hasNext = advance(0);
            }
        }

        boolean advance(int i) {
            while (i >= 0) {
                long resti = rest[i];
                if (resti == 0)
                    --i;
                else {
                    // bit = Long.lowestOneBit(resti);
                    long bit = resti & -resti;
                    rest[i] ^= bit;
                    selected[i] = Long.numberOfTrailingZeros(bit);
                    if (++i >= r)
                        return true;
                    available[i] = rest[i] = available[i - 1] ^ bit;
                }
            }
            return false;
        }

        @Override
        public boolean hasNext() {
            return hasNext;
        }

        @Override
        public int[] next() {
            int[] result = selected.clone();
            hasNext = advance(r - 1);
            return result;
        }
    }

    static <T, X> Iterator<T> iterator(X x, Predicate<X> hasNext, Function<X, T> next) {
        return new Iterator<T>() {

            @Override
            public boolean hasNext() {
                return hasNext.test(x);
            }

            @Override
            public T next() {
                return next.apply(x);
            }

        };
    }

    @SafeVarargs
    static <T> T consume(T t, Consumer<T>... consumers) {
        for (Consumer<T> c : consumers)
            c.accept(t);
        return t;
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

    public static Iterable<int[]> permutation(int n, int r) {
        return () -> new PermutationIndexIterator(n, r);
    }

    @SafeVarargs
    public static <T> Iterable<T> iterable(T... values) {
        return List.of(values);
    }

    public static Iterable<Integer> iterable(int... values) {
        return () -> iterator(new Object() {
            int[] elements = values.clone();
            int index = 0;
        }, c -> c.index < c.elements.length, c -> c.elements[c.index++]);
    }

    /*
     * intermediate operations
     */
    public static <T> Iterable<T> filter(Predicate<T> predicate,
        Iterable<T> source) {
        return () -> new FilterIterator<>(source.iterator(), predicate);
    }

    public static <T, U> Iterable<U> map(Function<T, U> mapper, Iterable<T> source) {
        return () -> iterator(source.iterator(), s -> s.hasNext(), s -> mapper.apply(s.next()));
    }

    public static <T> Iterable<T> peek(Consumer<T> consumer, Iterable<T> source) {
        return () -> iterator(source.iterator(), x -> x.hasNext(),
            x -> consume(x.next(), consumer));
    }

    public static <T extends Comparable<T>> Iterable<T> sorted(Iterable<T> source) {
        List<T> result = arrayList(source);
        Collections.sort(result);
        return result;
    }

    public static <T> Iterable<T> sorted(
        Comparator<T> comparator, Iterable<T> source) {
        List<T> result = arrayList(source);
        Collections.sort(result, comparator);
        return result;
    }

    public static <T> Iterable<T> reverse(Iterable<T> source) {
        List<T> result = arrayList(source);
        Collections.reverse(result);
        return result;
    }

    public static <T> Iterable<T> skip(int skip, Iterable<T> source) {
        return () -> {
            Iterator<T> iterator = source.iterator();
            for (int i = 0; i < skip && iterator.hasNext(); ++i)
                iterator.next();
            return iterator;
        };
    }

    public static <T> Iterable<T> limit(int limit, Iterable<T> source) {
        return () -> iterator(new Object() {
            int index = 0;
            Iterator<T> iterator = source.iterator();
        }, c -> c.iterator.hasNext() && c.index < limit,
            c -> {
                c.index++;
                return c.iterator.next();
            });
    }

    public static <T> Iterable<List<T>> permutation(int r, Iterable<T> source) {
        List<T> list = arrayList(source);
        int size = list.size();
        return map(indexes -> {
            List<T> result = new ArrayList<>(size);
            for (int i : indexes)
                result.add(list.get(i));
            return result;
        }, permutation(list.size(), r));
    }

    public static <T, U, R> Iterable<R> zip(BiFunction<T, U, R> zipper, Iterable<T> a,
        Iterable<U> b) {
        return () -> iterator(new Object() {
            Iterator<T> aa = a.iterator();
            Iterator<U> bb = b.iterator();
        }, c -> c.aa.hasNext() && c.bb.hasNext(), c -> zipper.apply(c.aa.next(), c.bb.next()));
    }

    /*
     * terminal operations
     */
    public static <T> boolean anyMatch(Predicate<T> predicate, Iterable<T> source) {
        for (T t : source)
            if (predicate.test(t))
                return true;
        return false;
    }

    public static <T> boolean allMatch(Predicate<T> predicate, Iterable<T> source) {
        for (T t : source)
            if (!predicate.test(t))
                return false;
        return true;
    }

    public static <T> List<T> list(Supplier<List<T>> supplier,
        Iterable<T> source) {
        List<T> result = supplier.get();
        for (T e : source)
            result.add(e);
        return result;
    }

    public static <T> List<T> arrayList(Iterable<T> source) {
        return list(() -> new ArrayList<>(), source);
    }

    public static <T> List<T> list(Iterable<T> source) {
        return arrayList(source);
    }

    public static <T> List<T> list() {
        return Collections.emptyList();
    }

    @SafeVarargs
    public static <T> List<T> list(T... elements) {
        return List.of(elements);
    }

    public static List<Integer> list(int... elements) {
        int[] copy = elements.clone();
        return new AbstractList<Integer>() {

            @Override
            public Integer get(int index) {
                return copy[index];
            }

            @Override
            public int size() {
                return copy.length;
            }
        };

    }

    public static <T> List<T> linkedList(Iterable<T> source) {
        return list(() -> new LinkedList<>(), source);
    }

    public static <T> T[] array(IntFunction<T[]> supplier, Iterable<T> source) {
        return arrayList(source).toArray(supplier);
    }

    public static <T> int[] array(Iterable<Integer> source) {
        // サイズがわからないので一度リストにしてから配列にする必要があります。
        List<Integer> list = arrayList(source);
        int size = list.size();
        int[] result = new int[size];
        for (int i = 0; i < size; ++i)
            result[i] = list.get(i);
        return result;
    }

    static <T, K, V> Map<K, V> map(Supplier<Map<K, V>> supplier,
        Function<T, K> keyExtractor,
        Function<T, V> valueExtractor, Iterable<T> source) {
        Map<K, V> result = supplier.get();
        for (T t : source)
            result.put(keyExtractor.apply(t), valueExtractor.apply(t));
        return result;
    }

    public static <T, K, V> HashMap<K, V> hashMap(Function<T, K> keyExtractor,
        Function<T, V> valueExtractor, Iterable<T> source) {
        return (HashMap<K, V>) map(() -> new HashMap<>(), keyExtractor,
            valueExtractor, source);
    }

    public static <T, K, V> LinkedHashMap<K, V> linkedHashMap(
        Function<T, K> keyExtractor, Function<T, V> valueExtractor,
        Iterable<T> source) {
        return (LinkedHashMap<K, V>) map(() -> new LinkedHashMap<>(),
            keyExtractor, valueExtractor, source);
    }

    public static <T, K extends Comparable<K>, V> TreeMap<K, V> treeMap(
        Function<T, K> keyExtractor, Function<T, V> valueExtractor,
        Iterable<T> source) {
        return (TreeMap<K, V>) map(() -> new TreeMap<>(), keyExtractor,
            valueExtractor, source);
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

    public static <T> T reduce(T start, BinaryOperator<T> reducer,
        Iterable<T> source) {
        for (T e : source)
            start = reducer.apply(start, e);
        return start;
    }

    public static int count(Iterable<Integer> source) {
        int result = 0;
        for (@SuppressWarnings("unused")
        int i : source)
            ++result;
        return result;
    }

    public static int sum(Iterable<Integer> source) {
        int result = 0;
        for (int i : source)
            result += i;
        return result;
    }

    public static String join(String separator, Iterable<Integer> source) {
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

    public static <A, B, C, D> D apply(A a, Function<A, B> f1, Function<B, C> f2,
        Function<C, D> f3) {
        return f3.apply(f2.apply(f1.apply(a)));
    }

    public static <A, B, C, D, E> E apply(A a, Function<A, B> f1, Function<B, C> f2,
        Function<C, D> f3, Function<D, E> f4) {
        return f4.apply(f3.apply(f2.apply(f1.apply(a))));
    }

    public static <A, B, C, D, E, F> F apply(A a, Function<A, B> f1, Function<B, C> f2,
        Function<C, D> f3, Function<D, E> f4, Function<E, F> f5) {
        return f5.apply(f4.apply(f3.apply(f2.apply(f1.apply(a)))));
    }
}
