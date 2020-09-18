package puzzle;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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

    public static <A, B, C, D> Function<A, D> and(Function<A, B> b, Function<B, C> c, Function<C, D> d) {
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

    public static <T extends Comparable<T>> Comparator<T> natualOrder() {
        return (a, b) -> a.compareTo(b);
    }

    public static <T extends Comparable<T>> Comparator<T> reverseOrder() {
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
    public static Iterable<Integer> range(int from, int to) {
        return () -> new IntRangeIterator(from, to);
    }

    public static Iterable<Integer> range(int from, int to, int step) {
        return () -> new IntRangeIterator(from, to, step);
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

    public static Iterable<Integer> iterable(int... values) {
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
        return () -> new FilterIterator<>(source.iterator(), predicate);
    }

    public static <T> Iterable<T> exclude(Predicate<T> predicate, Iterable<T> source) {
        return () -> new FilterIterator<>(source.iterator(), not(predicate));
    }

    public static <T, U> Iterable<U> map(Function<T, U> mapper, Iterable<T> source) {
        return () -> iterator(source.iterator(), s -> s.hasNext(), s -> mapper.apply(s.next()));
    }

    public static <T, U> Iterable<U> flatMap(Function<T, Iterable<U>> mapper, Iterable<T> source) {
        return () -> new Iterator<U>() {

            final List<Iterator<U>> iterators = list(
                map(and(mapper, Iterable::iterator), source));
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
            public U next() {
                if (!hasNext())
                    throw new NoSuchElementException();
                U result = iterators.get(index).next();
                hasNext = advance();
                return result;
            }

        };
    }

    @SafeVarargs
    public static <T> Iterable<T> concat(Iterable<T>... sources) {
        return () -> new Iterator<T>() {

            final List<Iterator<T>> iterators = list(map(Iterable::iterator, list(sources)));
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
        return hashSet(source);
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

    /*
     * listのオーバーロード <pre> public static <T> List<T> list(Supplier<List<T>>
     * supplier, Iterable<T> source) public static <T> List<T> list(Iterable<T>
     * source) public static <T> List<T> list() public static <T> List<T>
     * list(T... elements) public static List<Integer> list(int... elements)
     * </pre>
     */
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

    static <T> Set<T> set(Supplier<Set<T>> supplier, Iterable<T> source) {
        Set<T> result = supplier.get();
        for (T e : source)
            result.add(e);
        return result;
    }

    public static <T> Set<T> hashSet(Iterable<T> source) {
        return set(() -> new LinkedHashSet<>(), source);
    }

    public static <T> Set<T> treeSet(Iterable<T> source) {
        return set(() -> new TreeSet<>(), source);
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

    // public static <T, K, V> Map<K, List<V>> grouping(
    // Function<T, K> keyExtractor, Function<T, V> valueExtractor, Iterable<T>
    // source) {
    // Map<K, List<V>> result = new LinkedHashMap<>();
    // for (T e : source)
    // result.computeIfAbsent(keyExtractor.apply(e), k -> new ArrayList<>())
    // .add(valueExtractor.apply(e));
    // return result;
    // }

    public static <T, K, V> Map<K, V> grouping(Function<T, K> keyExtractor,
        Function<Iterable<T>, V> aggregator, Iterable<T> source) {
        Map<K, List<T>> map = new LinkedHashMap<>();
        for (T e : source)
            map.computeIfAbsent(keyExtractor.apply(e), k -> new ArrayList<>()).add(e);
        return linkedHashMap(Entry::getKey, e -> aggregator.apply(e.getValue()), map.entrySet());
    }

    public static <T, K> Map<K, List<T>> grouping(Function<T, K> keyExtractor, Iterable<T> source) {
        // return grouping(keyExtractor, Iterables::list, source);
        Map<K, List<T>> result = new LinkedHashMap<>();
        for (T e : source)
            result.computeIfAbsent(keyExtractor.apply(e), k -> new ArrayList<>()).add(e);
        return result;
    }

    public static <T, K extends Comparable<K>, V> TreeMap<K, V> treeMap(
        Function<T, K> keyExtractor, Function<T, V> valueExtractor,
        Iterable<T> source) {
        return (TreeMap<K, V>) map(() -> new TreeMap<>(), keyExtractor,
            valueExtractor, source);
    }

    public static <T> T reduce(BinaryOperator<T> reducer, Iterable<T> source) {
        Iterator<T> iterator = source.iterator();
        if (!iterator.hasNext())
            return null;
        T prev = iterator.next();
        while (iterator.hasNext())
            prev = reducer.apply(prev, iterator.next());
        return prev;
    }

    public static <T> T reduce(T start, BinaryOperator<T> reducer, Iterable<T> source) {
        for (T e : source)
            start = reducer.apply(start, e);
        return start;
    }

    public static <T> int count(Iterable<T> source) {
        int result = 0;
        for (Iterator<T> i = source.iterator(); i.hasNext(); ++result)
            i.next();
        return result;
    }

    public static int sum(Iterable<Integer> source) {
        int result = 0;
        for (int i : source)
            result += i;
        return result;
    }

    static <T> T selectMin(Comparator<T> comparator, Iterable<T> source) {
        Iterator<T> iterator = source.iterator();
        if (!iterator.hasNext())
            return null;
        T selected = iterator.next();
        while (iterator.hasNext()) {
            T e = iterator.next();
            selected = comparator.compare(e, selected) < 0 ? e : selected;
        }
        return selected;
    }

    public static <T> T min(Comparator<T> comparator, Iterable<T> source) {
        return selectMin(comparator, source);
    }

    public static <T> T max(Comparator<T> comparator, Iterable<T> source) {
        return selectMin(reverse(comparator), source);
    }

    public static <T extends Comparable<T>> T min(Iterable<T> source) {
        return selectMin(Comparator.naturalOrder(), source);
    }

    public static <T extends Comparable<T>> T max(Iterable<T> source) {
        return selectMin(Comparator.reverseOrder(), source);
    }

    public static <T> String join(String separator, Iterable<T> source) {
        String sep = "";
        StringBuilder sb = new StringBuilder();
        for (T e : source) {
            sb.append(sep).append(e);
            sep = separator;
        }
        return sb.toString();
    }
}
