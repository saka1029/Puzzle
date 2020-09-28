package test.qiita.newObject;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

class TestIterableAsStream {

    public static <T, U> Iterable<U> map0(Function<T, U> mapper, Iterable<T> source) {
        List<U> result = new ArrayList<>();
        for (T element : source)
            result.add(mapper.apply(element));
        return result;
    }

    public static <T, U> Iterable<U> map(Function<T, U> mapper, Iterable<T> source) {
        return () -> new Iterator<U>() {

            final Iterator<T> iterator = source.iterator();

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public U next() {
                return mapper.apply(iterator.next());
            }

        };
    }

    public static <T> List<T> toList(Iterable<T> source) {
        List<T> result = new ArrayList<>();
        for (T element : source)
            result.add(element);
        return result;
    }

    @Test
    void testMap() {
        List<String> actual = toList(
            map(String::toUpperCase,
                List.of("a", "b", "c")));
        List<String> expected = List.of("A", "B", "C");
        assertEquals(actual, expected);

        List<String> stream = List.of("a", "b", "c").stream()
            .map(String::toUpperCase)
            .collect(Collectors.toList());
        assertEquals(stream, expected);
    }

    public static <T> Iterable<T> filter(Predicate<T> selector, Iterable<T> source) {
        return () -> new Iterator<T>() {

            final Iterator<T> iterator = source.iterator();
            boolean hasNext = advance();
            T next;

            boolean advance() {
                while (iterator.hasNext())
                    if (selector.test(next = iterator.next()))
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
    }

    @Test
    void testFilter() {
        List<Integer> actual = toList(
            map(i -> i * 10,
                filter(i -> i % 2 == 0,
                    List.of(0, 1, 2, 3, 4, 5))));
        List<Integer> expected = List.of(0, 20, 40);
        assertEquals(expected, actual);

        List<Integer> stream = List.of(0, 1, 2, 3, 4, 5).stream()
            .filter(i -> i % 2 == 0)
            .map(i -> i * 10)
            .collect(Collectors.toList());
        assertEquals(stream, actual);
    }

    @Test
    void testSaveFilter() {
        Iterable<Integer> saved;
        List<Integer> actual = toList(
            map(i -> i * 10,
                saved = filter(i -> i % 2 == 0,
                    List.of(0, 1, 2, 3, 4, 5))));
        List<Integer> expected = List.of(0, 20, 40);
        assertEquals(expected, actual);

        assertEquals(List.of(0, 2, 4), toList(saved));
    }

    public static <T, K, V> Map<K, V> toMap(Function<T, K> keyExtractor,
        Function<T, V> valueExtractor, Iterable<T> source) {
        Map<K, V> result = new LinkedHashMap<>();
        for (T element : source)
            result.put(keyExtractor.apply(element), valueExtractor.apply(element));
        return result;
    }

    public static <T, K> Map<K, List<T>> groupingBy(Function<T, K> keyExtractor,
        Iterable<T> source) {
        Map<K, List<T>> result = new LinkedHashMap<>();
        for (T e : source)
            result.computeIfAbsent(keyExtractor.apply(e), k -> new ArrayList<>()).add(e);
        return result;
    }

    @Test
    public void testGroupingBy() {
        Map<Integer, List<String>> actual = groupingBy(String::length,
            List.of("one", "two", "three", "four", "five"));
        Map<Integer, List<String>> expected = Map.of(
            3, List.of("one", "two"),
            5, List.of("three"),
            4, List.of("four", "five"));
        assertEquals(expected, actual);

        Map<Integer, List<String>> stream = List.of("one", "two", "three", "four", "five").stream()
            .collect(Collectors.groupingBy(String::length));
        assertEquals(stream, actual);
    }

    static <T, K, V> Map<K, V> groupingBy(Function<T, K> keyExtractor,
        Function<Iterable<T>, V> valueAggregator, Iterable<T> source) {
        return toMap(Entry::getKey, e -> valueAggregator.apply(e.getValue()),
            groupingBy(keyExtractor, source).entrySet());
    }

    public static <T> long count(Iterable<T> source) {
        long count = 0;
        for (@SuppressWarnings("unused")
        T e : source)
            ++count;
        return count;
    }

    @Test
    public void testGroupingByCount() {
        Map<Integer, Long> actual = groupingBy(String::length, s -> count(s),
            List.of("one", "two", "three", "four", "five"));
        Map<Integer, Long> expected = Map.of(3, 2L, 5, 1L, 4, 2L);
        assertEquals(expected, actual);

        Map<Integer, Long> stream = List.of("one", "two", "three", "four", "five").stream()
            .collect(Collectors.groupingBy(String::length, Collectors.counting()));
        assertEquals(stream, actual);
    }

    public static <T, U> Iterable<U> flatMap(Function<T, Iterable<U>> flatter, Iterable<T> source) {
        return () -> new Iterator<U>() {

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
                U result = next;
                hasNext = advance();
                return result;
            }

        };
    }

    @Test
    public void testFlatMap() {
        List<Integer> actual = toList(
            flatMap(i -> List.of(i, i),
                List.of(0, 1, 2, 3)));
        List<Integer> expected = List.of(0, 0, 1, 1, 2, 2, 3, 3);
        assertEquals(expected, actual);

        List<Integer> stream = List.of(0, 1, 2, 3).stream()
            .flatMap(i -> Stream.of(i, i))
            .collect(Collectors.toList());
        assertEquals(stream, actual);
    }

    static <T, U, V> Iterable<V> zip(BiFunction<T, U, V> zipper, Iterable<T> source1,
        Iterable<U> source2) {
        return () -> new Iterator<V>() {

            final Iterator<T> iterator1 = source1.iterator();
            final Iterator<U> iterator2 = source2.iterator();

            @Override
            public boolean hasNext() {
                return iterator1.hasNext() && iterator2.hasNext();
            }

            @Override
            public V next() {
                return zipper.apply(iterator1.next(), iterator2.next());
            }

        };
    }

    @Test
    void testZip() {
        List<String> actual = toList(
            zip((x, y) -> x + "-" + y,
                List.of(0, 1, 2),
                List.of("zero", "one", "two")));
        List<String> expected = List.of("0-zero", "1-one", "2-two");
        assertEquals(expected, actual);
    }

    public static <T, U> Iterable<U> cumulative(U unit, BiFunction<U, T, U> function,
        Iterable<T> source) {
        return () -> new Iterator<U>() {

            Iterator<T> iterator = source.iterator();
            U accumlator = unit;

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public U next() {
                return accumlator = function.apply(accumlator, iterator.next());
            }

        };
    }

    @Test
    public void testCumalative() {
        List<Integer> actual = toList(
            cumulative(0, (x, y) -> x + y,
                List.of(0, 1, 2, 3, 4, 5)));
        List<Integer> expected = List.of(0, 1, 3, 6, 10, 15);
        assertEquals(expected, actual);
    }

}
