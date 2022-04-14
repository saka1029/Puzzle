package puzzle.core;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;

/**
 * Iterableを使用したStreamライクな関数群です。
 *
 * Streamライブラリの問題点
 * <ul>
 * <li>map()やfilter()などのStream操作メソッドはインスタンスメソッドになっており、容易に拡張することができません。</li>
 * <li>IntStreamはありますが、CharStreamやByteStreamはありません。</li>
 * <li>配列にするにはstream.toArray()でよいが、リストにするときはstream.collec(Collectors.toList())とする必要があります。</li>
 * <li>String.chars()はStream&lt;Character&gt;ではなく、Stream&lt;Integer&gt;を返します。</li>
 * </ul>
 * 設計方針
 * <ul>
 * <li>値がない時（空のリストに対して最小値を求めるなど）はデフォルト値を指定します。</li>
 * </ul>
 * <ul>
 */
public class Iterables {

    /*
     * Function
     */
    public static <T> Function<T, T> identity() {
        return t -> t;
    }

    public static <A, B, C> Function<A, C> and(Function<A, B> b, Function<B, C> c) {
        return a -> c.apply(b.apply(a));
    }

    public static <A, B, C, D> Function<A, D> and(Function<A, B> b, Function<B, C> c,
        Function<C, D> d) {
        return a -> d.apply(c.apply(b.apply(a)));
    }

    @SafeVarargs
    public static <T> T build(T t, Consumer<T>... consumers) {
        for (Consumer<T> c : consumers)
            c.accept(t);
        return t;
    }

    public static <T> Predicate<T> not(Predicate<T> predicate) {
        return t -> !predicate.test(t);
    }

    static <T> BinaryOperator<T> min(Comparator<? super T> comparator) {
        return (a, b) -> a == null && b == null ? null
            : a == null && b != null ? b
                : a != null && b == null ? a
                    : comparator.compare(a, b) <= 0 ? a : b;
    }

    static <T> BinaryOperator<T> max(Comparator<? super T> comparator) {
        return (a, b) -> a == null && b == null ? null
            : a == null && b != null ? b
                : a != null && b == null ? a
                    : comparator.compare(a, b) >= 0 ? a : b;
    }

    @FunctionalInterface
    public interface TriFunction<T, U, V, R> {
        R apply(T t, U u, V v);
    }

    /*
     * Comparator
     */
    public static <T, U extends Comparable<U>> Comparator<T> asc(
        Function<? super T, ? extends U> extractor) {
        return (a, b) -> extractor.apply(a).compareTo(extractor.apply(b));
    }

    public static <T, U extends Comparable<U>> Comparator<T> desc(
        Function<? super T, ? extends U> extractor) {
        return (a, b) -> extractor.apply(b).compareTo(extractor.apply(a));
    }

    public static <T> Comparator<T> reverse(Comparator<T> comparator) {
        return (a, b) -> comparator.compare(b, a);
    }

    public static <T extends Comparable<? super T>> Comparator<T> naturalOrder() {
        return (a, b) -> a.compareTo(b);
    }

    public static <T extends Comparable<? super T>> Comparator<T> reverseOrder() {
        return (a, b) -> b.compareTo(a);
    }

    @SafeVarargs
    public static <T> Comparator<T> and(Comparator<T> first, Comparator<T>... rest) {
        for (Comparator<T> c : rest)
            first = first.thenComparing(c);
        return first;
    }

    /*
     * Common Iterator
     */
    // static class FilterIterator<T> implements Iterator<T> {
    //
    // final Iterator<T> source;
    // final Predicate<T> predicate;
    // boolean hasNext;
    // T next = null;
    //
    // FilterIterator(Iterator<T> source, Predicate<T> predicate) {
    // this.source = source;
    // this.predicate = predicate;
    // hasNext = advance();
    // }
    //
    // boolean advance() {
    // while (source.hasNext())
    // if (predicate.test(next = source.next()))
    // return true;
    // return false;
    // }
    //
    // @Override
    // public boolean hasNext() {
    // return hasNext;
    // }
    //
    // @Override
    // public T next() {
    // if (!hasNext())
    // throw new NoSuchElementException();
    // T result = next;
    // hasNext = advance();
    // return result;
    // }
    // }

    static class IntRangeIterator implements Iterator<Integer> {

        int next, to, step;

        IntRangeIterator(int from, int to, int step) {
            if (step == 0)
                throw new IllegalArgumentException("step");
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

    static class CombinationIndexIterator implements Iterator<int[]> {

        final int n, r;
        final int[] selection;
        boolean hasNext;

        CombinationIndexIterator(int n, int r) {
            if (n < 0)
                throw new IndexOutOfBoundsException("n must be >= 0");
            Objects.checkIndex(r, n + 1);
            this.n = n;
            this.r = r;
            this.selection = IntStream.range(0, r).toArray();
            this.hasNext = true;
        }

        boolean advance() {
            for (int i = r - 1; i >= 0;)
                if (++selection[i] >= n)
                    --i;
                else if (i + 1 >= r)
                    return true;
                else
                    selection[i + 1] = selection[i++];
            return false;
        }

        @Override
        public boolean hasNext() {
            return hasNext;
        }

        @Override
        public int[] next() {
            int[] result = selection.clone();
            hasNext = advance();
            return result;
        }

    }

    static class PermutationIndexIterator implements Iterator<int[]> {

        final int n, r;
        final long[] available, rest;
        final int[] selected;
        boolean hasNext;

        PermutationIndexIterator(int n, int r) {
            Objects.checkIndex(n, Long.SIZE + 1);
            Objects.checkIndex(r, n + 1);
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

    /*
     * start operations
     */
    public static <T> Iterable<T> empty() {
        class Empty implements Iterator<T> {

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public T next() {
                throw new NoSuchElementException();
            }
        }
        return () -> new Empty();
    }

    public static Iterable<Integer> range(int from, int to) {
        return () -> new IntRangeIterator(from, to);
    }

    public static Iterable<Integer> range(int from, int to, int step) {
        return () -> new IntRangeIterator(from, to, step);
    }

    public static Iterable<Integer> rangeClosed(int from, int to) {
        return () -> new IntRangeIterator(from, to + 1);
    }

    public static Iterable<int[]> combination(int n, int r) {
        return () -> new CombinationIndexIterator(n, r);
    }

    public static Iterable<int[]> permutation(int n, int r) {
        return () -> new PermutationIndexIterator(n, r);
    }

    @SafeVarargs
    public static <T> Iterable<T> iterable(T... values) {
        return List.of(values);
    }

    public static Iterable<Integer> ints(int... values) {
        return () -> iterator(new Object() {
            int[] elements = values.clone();
            int index = 0;
        }, c -> c.index < c.elements.length, c -> c.elements[c.index++]);
    }

    public static Iterable<Character> chars(String s) {
        return () -> s.chars().mapToObj(i -> (char) i).iterator();
    }

    public static Iterable<Integer> codePoints(String s) {
        return () -> s.codePoints().iterator();
    }

    /*
     * intermediate operations
     */
    public static <T> Iterable<T> filter(Predicate<T> predicate, Iterable<T> source) {
        class Filter implements Iterator<T> {

            final Iterator<T> iterator = source.iterator();
            boolean hasNext = advance();
            T next;

            boolean advance() {
                while (iterator.hasNext())
                    if (predicate.test(next = iterator.next()))
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
        };
        return () -> new Filter();
        // return () -> new FilterIterator<>(source.iterator(), predicate);
    }

    public static <T> Iterable<T> exclude(Predicate<T> predicate, Iterable<T> source) {
        return filter(not(predicate), source);
    }

    public static <T, U> Iterable<U> map(Function<T, U> mapper, Iterable<T> source) {
        return () -> iterator(source.iterator(), s -> s.hasNext(), s -> mapper.apply(s.next()));
    }

    public static <T, U, R> Iterable<R> map(BiFunction<T, U, R> mapper, Iterable<T> a, Iterable<U> b) {
        class Zip implements Iterator<R> {
            Iterator<T> aa = a.iterator();
            Iterator<U> bb = b.iterator();

            @Override
            public boolean hasNext() {
                return aa.hasNext() && bb.hasNext();
            }

            @Override
            public R next() {
                return mapper.apply(aa.next(), bb.next());
            }
        }
        return () -> new Zip();
    }

    public static <T, U, V, R> Iterable<R> map(TriFunction<T, U, V, R> mapper,
        Iterable<T> a, Iterable<U> b, Iterable<V> c) {
        class Zip3 implements Iterator<R> {
            Iterator<T> aa = a.iterator();
            Iterator<U> bb = b.iterator();
            Iterator<V> cc = c.iterator();

            @Override
            public boolean hasNext() {
                return aa.hasNext() && bb.hasNext() && cc.hasNext();
            }

            @Override
            public R next() {
                return mapper.apply(aa.next(), bb.next(), cc.next());
            }
        }
        return () -> new Zip3();
    }


    // public static <T, U> Iterable<U> flatMap(Function<T, Iterable<U>> mapper,
    // Iterable<T> source) {
    // return () -> new Iterator<U>() {
    //
    // final List<Iterator<U>> iterators = list(
    // map(and(mapper, Iterable::iterator), source));
    // int index = 0;
    // boolean hasNext = advance();
    //
    // boolean advance() {
    // for (int size = iterators.size(); index < size; ++index)
    // if (iterators.get(index).hasNext())
    // return true;
    // return false;
    // }
    //
    // @Override
    // public boolean hasNext() {
    // return hasNext;
    // }
    //
    // @Override
    // public U next() {
    // if (!hasNext())
    // throw new NoSuchElementException();
    // U result = iterators.get(index).next();
    // hasNext = advance();
    // return result;
    // }
    //
    // };
    // }

    public static <T, U> Iterable<U> flatMap(Function<T, Iterable<U>> flatter, Iterable<T> source) {
        class FlatMap implements Iterator<U> {

            final Iterator<T> parent = source.iterator();
            Iterator<U> child = null;
            boolean hasNext = advance();
            U next;

            boolean advance() {
                while (true) {
                    if (child == null) {
                        if (!parent.hasNext())
                            return false;
                        child = flatter.apply(parent.next()).iterator();
                    }
                    if (child.hasNext()) {
                        next = child.next();
                        return true;
                    }
                    child = null;
                }
            }

            @Override
            public boolean hasNext() {
                return hasNext;
            }

            @Override
            public U next() {
                if (!hasNext())
                    throw new NoSuchElementException();
                U result = next;
                hasNext = advance();
                return result;
            }

        }
        return () -> new FlatMap();
    }

    @SafeVarargs
    public static <T> Iterable<T> concat(Iterable<T>... sources) {
        class Concat implements Iterator<T> {

            final List<Iterator<T>> iterators = list(map(Iterable::iterator, iterable(sources)));
            int index = 0;
            boolean hasNext = advance();

            boolean advance() {
                for (int size = iterators.size(); index < size; ++index)
                    if (iterators.get(index).hasNext())
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
                T result = iterators.get(index).next();
                hasNext = advance();
                return result;
            }
        };
        return () -> new Concat();
    }

    public static <T> Iterable<T> peek(Consumer<T> consumer, Iterable<T> source) {
        return () -> iterator(source.iterator(), x -> x.hasNext(),
            x -> build(x.next(), consumer));
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

    public static <T> Iterable<T> distinct(Iterable<T> source) {
        return linkedHashSet(source);
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
        class Limit implements Iterator<T> {
            int index = 0;
            Iterator<T> iterator = source.iterator();

            @Override
            public boolean hasNext() {
                return iterator.hasNext() && index < limit;
            }

            @Override
            public T next() {
                index++;
                return iterator.next();
            }

        }
        return () -> new Limit();
//        return () -> iterator(new Object() {
//            int index = 0;
//            Iterator<T> iterator = source.iterator();
//        },
//        c -> c.iterator.hasNext() && c.index < limit,
//            c -> {
//                c.index++;
//                return c.iterator.next();
//            });
    }

    public static <T> Iterable<T> dropWhile(Predicate<? super T> predicate, Iterable<T> source) {
        class DropWhile implements Iterator<T> {

            final Iterator<T> iterator = source.iterator();
            boolean hasNext = init();
            T next;

            boolean init() {
                while (iterator.hasNext())
                    if (!predicate.test(next = iterator.next()))
                        return true;
                return false;
            }

            boolean advance() {
                if (!iterator.hasNext())
                    return false;
                next = iterator.next();
                return true;
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

        };
        return () -> new DropWhile();
    }

    public static <T> Iterable<T> takeWhile(Predicate<? super T> predicate, Iterable<T> source) {
        class TakeWhile implements Iterator<T> {

            final Iterator<T> iterator = source.iterator();
            T next;
            boolean hasNext = true;
            {
                hasNext = advance();
            }

            boolean advance() {
                if (!hasNext)
                    return false;
                while (iterator.hasNext())
                    if (predicate.test(next = iterator.next()))
                        return true;
                    else
                        return false;
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
        };
        return () -> new TakeWhile();
    }

    public static record Indexed<T> (int index, T value) {
    }

    public static <T> Iterable<Indexed<T>> indexed(Iterable<T> source) {
        class Ind implements Iterator<Indexed<T>> {
            Iterator<T> iterator = source.iterator();
            int index = 0;

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Indexed<T> next() {
                return new Indexed<>(index++, iterator.next());
            }
        }
        return () -> new Ind();
    }

    public static <T> Iterable<List<T>> combination(int r, Iterable<T> source) {
        List<T> list = arrayList(source);
        int size = list.size();
        return map(indexes -> {
            List<T> result = new ArrayList<>(size);
            for (int i : indexes)
                result.add(list.get(i));
            return result;
        }, combination(list.size(), r));
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

    public static <T> Iterable<List<T>> permutation(Iterable<T> source) {
        List<T> list = arrayList(source);
        return permutation(list.size(), list);
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

    public static <T> List<T> list(Supplier<List<T>> supplier, Iterable<T> source) {
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

    public static List<Integer> intlist(int... elements) {
        return new AbstractList<Integer>() {

            final int[] copy = elements.clone();

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

    static <T> Set<T> set(Supplier<Set<T>> supplier, Iterable<T> source) {
        Set<T> result = supplier.get();
        for (T e : source)
            result.add(e);
        return result;
    }

    public static <T> Set<T> hashSet(Iterable<T> source) {
        return set(HashSet::new, source);
    }

    public static <T> Set<T> linkedHashSet(Iterable<T> source) {
        return set(LinkedHashSet::new, source);
    }

    public static <T> Set<T> treeSet(Iterable<T> source) {
        return set(TreeSet::new, source);
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

    static <T, K, V> Map<K, V> map(Supplier<Map<K, V>> supplier, Function<T, K> keyExtractor,
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

    public static <T, K, V> LinkedHashMap<K, V> linkedHashMap(Function<T, K> keyExtractor,
        Function<T, V> valueExtractor, Iterable<T> source) {
        return (LinkedHashMap<K, V>) map(() -> new LinkedHashMap<>(),
            keyExtractor, valueExtractor, source);
    }

    // public static <K, V> LinkedHashMap<K, V> linkedHashMap(Iterable<Entry<K,
    // V>> source) {
    // LinkedHashMap<K, V> result = new LinkedHashMap<>();
    // for (Entry<K, V> e : source)
    // result.put(e.getKey(), e.getValue());
    // return result;
    // }

    public static <T> void forEach(Consumer<T> body, Iterable<T> source) {
        for (T e : source)
            body.accept(e);
    }

    public static <T, K> Map<K, List<T>> grouping(Function<T, K> keyExtractor, Iterable<T> source) {
        Map<K, List<T>> result = new LinkedHashMap<>();
        for (T e : source)
            result.computeIfAbsent(keyExtractor.apply(e), k -> new ArrayList<>()).add(e);
        return result;
    }

    public static <T, K, V> Map<K, V> grouping(Function<T, K> keyExtractor,
        Function<Iterable<T>, V> aggregator, Iterable<T> source) {
        return linkedHashMap(Entry::getKey,
            e -> aggregator.apply(e.getValue()),
            grouping(keyExtractor, source).entrySet());
    }

    public static <T, K extends Comparable<K>, V> TreeMap<K, V> treeMap(
        Function<T, K> keyExtractor, Function<T, V> valueExtractor,
        Iterable<T> source) {
        return (TreeMap<K, V>) map(() -> new TreeMap<>(), keyExtractor,
            valueExtractor, source);
    }

    public static <T, U> U reduce(U unit, BiFunction<U, T, U> reducer, Iterable<T> source) {
        for (T e : source)
            unit = reducer.apply(unit, e);
        return unit;
    }

    public static <T, U> Iterable<U> accumlate(U unit, BiFunction<U, T, U> function, Iterable<T> source) {
        class Cumulative implements Iterator<U> {

            Iterator<T> iterator = source.iterator();
            U accumlator = unit;

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public U next() {
                if (!hasNext())
                    throw new NoSuchElementException();
                return accumlator = function.apply(accumlator, iterator.next());
            }

        }
        return () -> new Cumulative();
    }

    public static <T> int count(Iterable<T> source) {
        return reduce(0, (a, b) -> a + 1, source);
    }

    public static int sum(Iterable<Integer> source) {
        return reduce(0, Integer::sum, source);
    }

    public static <T> T min(T defaultValue, Comparator<T> comparator, Iterable<T> source) {
        return reduce(defaultValue, min(comparator), source);
    }

    public static <T> T max(T defaultValue, Comparator<T> comparator, Iterable<T> source) {
        return reduce(defaultValue, max(comparator), source);
    }

    public static <T extends Comparable<? super T>> T min(T defaultValue, Iterable<T> source) {
        return reduce(defaultValue, min(naturalOrder()), source);
    }

    public static <T extends Comparable<T>> T max(T defaultValue, Iterable<T> source) {
        return reduce(defaultValue, max(naturalOrder()), source);
    }

    public static <T> String join(String separator, Iterable<T> source) {
        return reduce(new StringBuilder(),
            (sb, e) -> (sb.length() == 0 ? sb : sb.append(separator)).append(e), source).toString();
    }

    public static <T> String join(String separator, String begin, String end, Iterable<T> source) {
        return begin + join(separator, source) + end;
    }
}
