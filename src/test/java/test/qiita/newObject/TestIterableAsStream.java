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

import org.junit.jupiter.api.Test;


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
 * <li>静的メソッドで実現します。</li>
 * <li>値がない時（空のリストに対して最小値を求めるなど）はデフォルト値を指定します。</li>
 * </ul>
 * <ul>
 */

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
        assertEquals(List.of("A", "B", "C"),
            toList(map(String::toUpperCase, List.of("a", "b", "c"))));
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
        assertEquals(List.of(0, 20, 40),
            toList(map(i -> i * 10, filter(i -> i % 2 == 0, List.of(0, 1, 2, 3, 4, 5)))));
    }

    static <T, U, V> Iterable<V> zip(BiFunction<T, U, V> zipper, Iterable<T> source1, Iterable<U> source2) {
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
        assertEquals(List.of("0-zero", "1-one", "2-two"),
            toList(zip((a, b) -> a + "-" + b, List.of(0, 1, 2), List.of("zero", "one", "two"))));
    }

    public static <T> int count(Iterable<T> source) {
        int count = 0;
        for (@SuppressWarnings("unused") T e : source)
            ++count;
        return count;
    }

    public static <T, K, V> Map<K, V> toMap(Function<T, K> keyExtractor, Function<T, V> valueExtractor, Iterable<T> source) {
        Map<K, V> result = new LinkedHashMap<>();
        for (T element : source)
            result.put(keyExtractor.apply(element), valueExtractor.apply(element));
        return result;
    }

    public static <T, K> Map<K, List<T>> groupingBy(Function<T, K> keyExtractor, Iterable<T> source) {
        Map<K, List<T>> result = new LinkedHashMap<>();
        for (T e : source)
            result.computeIfAbsent(keyExtractor.apply(e), k -> new ArrayList<>()).add(e);
        return result;
    }

    static <T, K, V> Map<K, V> groupingBy(Function<T, K> keyExtractor, Function<Iterable<T>, V> valueAggregator, Iterable<T> source) {
        return toMap(Entry::getKey, e -> valueAggregator.apply(e.getValue()),
            groupingBy(keyExtractor, source).entrySet());
    }

    @Test
    public void testGroupingBy() {
        List<String> list = List.of("one", "two", "three", "four", "five");

        assertEquals(Map.of(3, List.of("one", "two"), 5, List.of("three"), 4, List.of("four", "five")),
            groupingBy(String::length, list));

        assertEquals(Map.of(3, 2, 5, 1, 4, 2),
            groupingBy(String::length, s -> count(s), list));
    }

}
