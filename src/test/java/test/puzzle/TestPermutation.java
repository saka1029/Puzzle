package test.puzzle;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;

import puzzle.Permutation;

public class TestPermutation {

    static List<Integer> origin = List.of(1, 2, 3, 4);

    static Set<List<Integer>> expect5 = Set.of();

    static Set<List<Integer>> expect4 = Set.of(
        List.of(1, 2, 3, 4),
        List.of(1, 2, 4, 3),
        List.of(1, 3, 2, 4),
        List.of(1, 3, 4, 2),
        List.of(1, 4, 2, 3),
        List.of(1, 4, 3, 2),
        List.of(2, 1, 3, 4),
        List.of(2, 1, 4, 3),
        List.of(2, 3, 1, 4),
        List.of(2, 3, 4, 1),
        List.of(2, 4, 1, 3),
        List.of(2, 4, 3, 1),
        List.of(3, 1, 2, 4),
        List.of(3, 1, 4, 2),
        List.of(3, 2, 1, 4),
        List.of(3, 2, 4, 1),
        List.of(3, 4, 1, 2),
        List.of(3, 4, 2, 1),
        List.of(4, 1, 2, 3),
        List.of(4, 1, 3, 2),
        List.of(4, 2, 1, 3),
        List.of(4, 2, 3, 1),
        List.of(4, 3, 1, 2),
        List.of(4, 3, 2, 1));

    static Set<List<Integer>> expect3 = Set.of(
        List.of(1, 2, 3),
        List.of(1, 2, 4),
        List.of(1, 3, 2),
        List.of(1, 3, 4),
        List.of(1, 4, 2),
        List.of(1, 4, 3),
        List.of(2, 1, 3),
        List.of(2, 1, 4),
        List.of(2, 3, 1),
        List.of(2, 3, 4),
        List.of(2, 4, 1),
        List.of(2, 4, 3),
        List.of(3, 1, 2),
        List.of(3, 1, 4),
        List.of(3, 2, 1),
        List.of(3, 2, 4),
        List.of(3, 4, 1),
        List.of(3, 4, 2),
        List.of(4, 1, 2),
        List.of(4, 1, 3),
        List.of(4, 2, 1),
        List.of(4, 2, 3),
        List.of(4, 3, 1),
        List.of(4, 3, 2));

    static Set<List<Integer>> expect2 = Set.of(
        List.of(1, 2),
        List.of(1, 3),
        List.of(1, 4),
        List.of(2, 1),
        List.of(2, 3),
        List.of(2, 4),
        List.of(3, 1),
        List.of(3, 2),
        List.of(3, 4),
        List.of(4, 1),
        List.of(4, 2),
        List.of(4, 3));

    static Set<List<Integer>> expect1 = Set.of(
        List.of(1),
        List.of(2),
        List.of(3),
        List.of(4));

    static Set<List<Integer>> expect0 = Set.of(
        List.of());

    static List<Integer> originDup = List.of(1, 2, 2);

    static Set<List<Integer>> expectDup3 = Set.of(
        List.of(1, 2, 2),
        List.of(2, 1, 2),
        List.of(2, 2, 1));

    static Set<List<Integer>> expectDup2 = Set.of(
        List.of(1, 2),
        List.of(2, 1),
        List.of(2, 2));

    static Set<List<Integer>> expectDup1 = Set.of(
        List.of(1),
        List.of(2));

    static Set<List<Integer>> expectDup0 = Set.of(
        List.of());

    static Set<List<Integer>> testCallback(List<Integer> list, int n) {
        Set<List<Integer>> result = new HashSet<>();
        Permutation.callback(list, n, e -> result.add(e));
        return result;
    }

    static Set<List<Integer>> testCallback(List<Integer> list) {
        Set<List<Integer>> result = new HashSet<>();
        Permutation.callback(list, e -> result.add(e));
        return result;
    }

    @Test
    public void testCallback() {
        assertEquals(expect4, testCallback(origin));
        assertEquals(expect5, testCallback(origin, 5));
        assertEquals(expect4, testCallback(origin, 4));
        assertEquals(expect3, testCallback(origin, 3));
        assertEquals(expect2, testCallback(origin, 2));
        assertEquals(expect1, testCallback(origin, 1));
        assertEquals(expect0, testCallback(origin, 0));
        assertEquals(expectDup3, testCallback(originDup));
        assertEquals(expectDup3, testCallback(originDup, 3));
        assertEquals(expectDup2, testCallback(originDup, 2));
        assertEquals(expectDup1, testCallback(originDup, 1));
        assertEquals(expectDup0, testCallback(originDup, 0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCallbackError() {
        Permutation.callback(origin, -1, x -> System.out.println(x));
    }

    static Set<List<Integer>> testIterator(List<Integer> list, int n) {
        Set<List<Integer>> result = new HashSet<>();
        for (var i = Permutation.iterator(list, n); i.hasNext();)
            result.add(i.next());
        return result;
    }

    static Set<List<Integer>> testIterator(List<Integer> list) {
        Set<List<Integer>> result = new HashSet<>();
        for (var i = Permutation.iterator(list); i.hasNext();)
            result.add(i.next());
        return result;
    }

    @Test
    public void testIterator() {
        assertEquals(expect4, testIterator(origin));
        assertEquals(expect5, testIterator(origin, 5));
        assertEquals(expect4, testIterator(origin, 4));
        assertEquals(expect3, testIterator(origin, 3));
        assertEquals(expect2, testIterator(origin, 2));
        assertEquals(expect1, testIterator(origin, 1));
        assertEquals(expect0, testIterator(origin, 0));
        assertEquals(expectDup3, testIterator(originDup));
        assertEquals(expectDup3, testIterator(originDup, 3));
        assertEquals(expectDup2, testIterator(originDup, 2));
        assertEquals(expectDup1, testIterator(originDup, 1));
        assertEquals(expectDup0, testIterator(originDup, 0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIteratorError() {
        Permutation.iterator(origin, -1);
    }

    static Set<List<Integer>> testIterable(List<Integer> list, int n) {
        Set<List<Integer>> result = new HashSet<>();
        for (var i : Permutation.iterable(list, n))
            result.add(i);
        return result;
    }

    static Set<List<Integer>> testIterable(List<Integer> list) {
        Set<List<Integer>> result = new HashSet<>();
        for (var i : Permutation.iterable(list))
            result.add(i);
        return result;
    }

    @Test
    public void testIterable() {
        assertEquals(expect4, testIterable(origin));
        assertEquals(expect5, testIterable(origin, 5));
        assertEquals(expect4, testIterable(origin, 4));
        assertEquals(expect3, testIterable(origin, 3));
        assertEquals(expect2, testIterable(origin, 2));
        assertEquals(expect1, testIterable(origin, 1));
        assertEquals(expect0, testIterable(origin, 0));
        assertEquals(expectDup3, testIterable(originDup));
        assertEquals(expectDup3, testIterable(originDup, 3));
        assertEquals(expectDup2, testIterable(originDup, 2));
        assertEquals(expectDup1, testIterable(originDup, 1));
        assertEquals(expectDup0, testIterable(originDup, 0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIterableError() {
        Permutation.iterable(origin, -1);
    }

    static Set<List<Integer>> testStream(List<Integer> list, int n) {
        return Permutation.stream(list, n).collect(Collectors.toSet());
    }

    static Set<List<Integer>> testStream(List<Integer> list) {
        return Permutation.stream(list).collect(Collectors.toSet());
    }

    @Test
    public void testStream() {
        assertEquals(expect4, testStream(origin));
        assertEquals(expect5, testStream(origin, 5));
        assertEquals(expect4, testStream(origin, 4));
        assertEquals(expect3, testStream(origin, 3));
        assertEquals(expect2, testStream(origin, 2));
        assertEquals(expect1, testStream(origin, 1));
        assertEquals(expect0, testStream(origin, 0));
        assertEquals(expectDup3, testStream(originDup));
        assertEquals(expectDup3, testStream(originDup, 3));
        assertEquals(expectDup2, testStream(originDup, 2));
        assertEquals(expectDup1, testStream(originDup, 1));
        assertEquals(expectDup0, testStream(originDup, 0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStreamError() {
        Permutation.stream(origin, -1);
    }

    @Test
    public void testNext() {
        List<String> list = Arrays.asList("a", "b", "c");
        assertEquals(List.of("a", "c", "b"), list = Permutation.next(list));
        assertEquals(List.of("b", "a", "c"), list = Permutation.next(list));
        assertEquals(List.of("b", "c", "a"), list = Permutation.next(list));
        assertEquals(List.of("c", "a", "b"), list = Permutation.next(list));
        assertEquals(List.of("c", "b", "a"), list = Permutation.next(list));
        assertNull(Permutation.next(list));
    }

    @Test
    public void testNextReverse() {
        Comparator<String> c = Comparator.reverseOrder();
        List<String> list = Arrays.asList("c", "b", "a");
        assertEquals(List.of("c", "a", "b"), list = Permutation.next(list, c));
        assertEquals(List.of("b", "c", "a"), list = Permutation.next(list, c));
        assertEquals(List.of("b", "a", "c"), list = Permutation.next(list, c));
        assertEquals(List.of("a", "c", "b"), list = Permutation.next(list, c));
        assertEquals(List.of("a", "b", "c"), list = Permutation.next(list, c));
        assertNull(Permutation.next(list, c));
    }

    static List<Integer> expectSendMoreMoney = List.of(9, 5, 6, 7, 1, 0, 8, 2);

    static int number(int... digits) {
        return IntStream.of(digits).reduce(0, (a, b) -> 10 * a + b);
    }

    static boolean checkSendMoreMoney(int s, int e, int n, int d, int m, int o, int r, int y) {
        if (s == 0 || m == 0) return false;
        int send = number(s, e, n, d);
        int more = number(m, o, r, e);
        int money = number(m, o, n, e, y);
        if (send + more != money) return false;
        return true;
    }

    @Test
    public void testSendMoreMoneyCallback() {
        Permutation.callback(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), 8,
            l -> {
                if (checkSendMoreMoney(l.get(0), l.get(1), l.get(2), l.get(3),
                    l.get(4), l.get(5), l.get(6), l.get(7)))
                    assertEquals(expectSendMoreMoney, l);
            });
    }

    @Test
    public void testSendMoreMoneyIterator() {
        List<Integer> list = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        for (Iterator<List<Integer>> i = Permutation.iterator(list, 8); i.hasNext();) {
            List<Integer> l = i.next();
            if (checkSendMoreMoney(l.get(0), l.get(1), l.get(2), l.get(3),
                l.get(4), l.get(5), l.get(6), l.get(7)))
                assertEquals(expectSendMoreMoney, l);
        }
    }

    @Test
    public void testSendMoreMoneyIterable() {
        for (List<Integer> l : Permutation.iterable(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), 8))
            if (checkSendMoreMoney(l.get(0), l.get(1), l.get(2), l.get(3),
                l.get(4), l.get(5), l.get(6), l.get(7)))
                assertEquals(expectSendMoreMoney, l);
    }

    @Test
    public void testSendMoreMoneyStream() {
        Permutation.stream(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), 8)
            .filter(l -> checkSendMoreMoney(
                l.get(0), l.get(1), l.get(2), l.get(3),
                l.get(4), l.get(5), l.get(6), l.get(7)))
            .forEach(l -> assertEquals(expectSendMoreMoney, l));
    }
}
