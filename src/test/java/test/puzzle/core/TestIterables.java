package test.puzzle.core;

import static java.util.Comparator.*;
import static java.util.stream.Collectors.*;
import static org.junit.Assert.*;
import static puzzle.core.Iterables.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.Test;

import puzzle.core.Common;
import puzzle.core.Iterables;
import puzzle.core.Iterables.Indexed;

public class TestIterables {

    static final Logger logger = Common.getLogger(TestIterables.class);

    @Test
    public void testRange() {
        assertEquals(list(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), list(range(0, 10)));
        assertEquals(list(0, 2, 4, 6, 8), list(filter(i -> i % 2 == 0, range(0, 10))));
        assertEquals(list(100, 120, 140),
            list(map(i -> i * 10, filter(i -> i % 2 == 0, list(10, 11, 12, 13, 14, 15)))));
        assertEquals(list(10, 8, 6, 4, 2), list(range(10, 0, -2)));
        assertEquals(list(10, 8, 6, 4, 2, 0), list(rangeClosed(10, 0, -2)));
        assertEquals(list(10, 8, 6, 4, 2, 0), list(range(10, -1, -2)));
    }

    @Test
    public void testMap() {
        assertEquals(list(0, 2, 4, 6, 8),
            arrayList(map(i -> i * 2, range(0, 5))));
    }

    @Test
    public void testFlatMap() {
        assertEquals(list(0, 1, 2, 10, 11, 12, 20, 21, 22),
            list(
                flatMap(i -> range(i, i + 3),
                    list(0, 10, 20))));
        assertEquals(list(0, 1, 2, 10, 11, 12, 20, 21, 22),
            IntStream.of(0, 10, 20)
                .flatMap(i -> IntStream.range(i, i + 3))
                .boxed()
                .collect(Collectors.toList()));
        assertEquals(list(0, 1, 2, 20, 21, 22, 40, 41, 42),
            list(
                flatMap(i -> i / 10 % 2 != 0 ? empty() : range(i, i + 3),
                    list(0, 10, 20, 30, 40))));
    }

    @Test
    public void testConcat() {
        assertEquals(list(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
            list(concat(list(0, 1, 2), list(), list(3), list(4, 5, 6), list(), list(),
                list(7, 8, 9))));
    }

    @Test
    public void testSkip() {
        Iterable<Integer> skipped;
        assertEquals(list(5, 6, 7, 8, 9), list(skipped = skip(5, range(0, 10))));
        assertEquals(list(5, 6, 7, 8, 9), list(skipped));
    }

    @Test
    public void testLimit() {
        Iterable<Integer> limited;
        assertEquals(list(0, 1, 2, 3, 4), list(limit(5, range(0, 10))));
        assertEquals(list(3, 4, 5), list(limited = limit(3, skip(3, range(0, 10)))));
        assertEquals(list(3, 4, 5), arrayList(limited));
    }

    @Test
    public void testArray() {
        assertArrayEquals(new Integer[] {0, 1, 2, 3},
            array(Integer[]::new, range(0, 4)));
        assertArrayEquals(new int[] {0, 1, 2, 3}, array(range(0, 4)));
    }

    @Test
    public void testNoApply() {
        assertEquals(list(0, 2, 4, 6, 8),
            arrayList(filter(i -> i % 2 == 0, range(0, 10))));
        assertEquals(list(100, 120, 140),
            arrayList(map(i -> i * 10, filter(i -> i % 2 == 0, list(10, 11, 12, 13, 14, 15)))));
    }

    static Iterable<Integer> primes(int max) {
        Iterable<Integer> primes = range(2, max);
        IntFunction<Predicate<Integer>> sieve = n -> i -> i == n || i % n != 0;
        primes = filter(sieve.apply(2), primes);
        for (int i = 3; i * i <= max; i += 2)
            primes = filter(sieve.apply(i), primes);
        return primes;
    }

    @Test
    public void testPrimes() {
        List<Integer> primes = list(primes(100));
        assertEquals(25, count(primes));
        assertEquals(list(2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43,
            47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97), primes);
    }

    @Test
    public void test上2桁と下2桁の差が素数であるナンバーの数() {
        // 上2桁と下2桁の差が素数であるような4桁の数の個数
        Set<Integer> primes = hashSet(primes(100));
        int primeDiff = count(
            flatMap(a -> map(b -> a * 100 + b,
                filter(b -> primes.contains(Math.abs(a - b)),
                    range(0, 100))),
                range(0, 100)));
        assertEquals(2880, primeDiff);
    }

    @Test
    public void testList() {
        // list(int...)
        assertEquals(List.of(0, 1, 2), list(0, 1, 2));
        assertEquals(List.of(0, 1, 2), intList(0, 1, 2));
        assertEquals(List.of(0, 1, 2),
            list(Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(2)));
        // list(T...)
        assertEquals(List.of(0, 1, 2), list(new Integer[] {0, 1, 2}));
        // int[]のリストはこう書く必要があります。
        List<int[]> listInt1 = list(new int[][] {{0, 1, 2}});
        assertArrayEquals(new int[] {0, 1, 2}, listInt1.get(0));
        List<int[]> listInt2 = list(new int[] {0, 1, 2}, new int[] {3, 4});
        assertArrayEquals(new int[] {0, 1, 2}, listInt2.get(0));
        assertArrayEquals(new int[] {3, 4}, listInt2.get(1));
        assertArrayEquals(new Integer[][] {{0, 1, 2}, {3, 4}},
            array(Integer[][]::new,
                list(new Integer[] {0, 1, 2}, new Integer[] {3, 4})));
        // list(Iterable<T>)
        assertEquals(List.of(0, 1, 2), list(list(0, 1, 2)));
    }

    // @Test
    // public void testHashMap() {
    //     List<String> list = List.of("zero", "one", "two");
    //     assertEquals(Map.of(0, "zero", 1, "one", 2, "two"),
    //         hashMap(Function.identity(), list::get, range(0, list.size())));
    //     assertEquals(Map.of(0, "zero", 1, "one", 2, "two"),
    //         hashMap(obj -> obj.key, obj -> obj.value, map(i -> new Object() {
    //             @SuppressWarnings("unused")
    //             final int key = i; // The value of the field new Object(){}.key
    //                                // is not used
    //             @SuppressWarnings("unused")
    //             final String value = list.get(i); // The value of the field new
    //                                               // Object(){}.value is not
    //                                               // used
    //         }, range(0, list.size()))));
    //     var s = map(i -> new Object() {
    //         int k = i;
    //         String v = list.get(i);
    //     }, range(0, list.size()));
    //     assertEquals(Map.of(0, "zero", 1, "one", 2, "two"),
    //         hashMap(obj -> obj.k, obj -> obj.v, s));
    // }

    @Test
    public void testReduce() {
        assertEquals(10, (int)reduce(0, (a, b) -> a + b, range(0, 5)));
        assertEquals(10, (int)reduce(0, Integer::sum, range(0, 5)));
        assertEquals(120, (int)reduce(1, (a, b) -> a * b, range(1, 6)));
        assertEquals(0, (int)reduce(0, (a, b) -> a + b, range(0, 0)));
        assertEquals(10, (int)reduce(0, Integer::sum, range(0, 5)));
        assertEquals(BigInteger.TEN,
            reduce(BigInteger.ZERO, (a, b) -> a.add(BigInteger.valueOf((long) b)), range(0, 5)));
    }

    @Test
    public void testSum() {
        assertEquals(10, sum(range(0, 5)));
        assertEquals(0, sum(list()));
    }

    @Test
    public void testMin() {
        assertEquals(-2, (int)min(null, list(3, 2, -1, -2, 0)));
        assertEquals(3, (int)min(null, list(3)));
        assertNull(min(null, range(0, 0)));
        assertEquals(-2, (int)min(null, (a, b) -> Integer.compare(a, b), list(3, 2, -1, -2, 0)));
        assertEquals(3, (int)min(null, (a, b) -> Integer.compare(a, b), list(3)));
        assertNull(min(null, (a, b) -> Integer.compare(a, b), range(0, 0)));
    }

    @Test
    public void testMax() {
        assertEquals(3, (int)max(null, list(3, 2, -1, -2, 0)));
        assertEquals(3, (int)max(null, list(3)));
        assertNull(max(null, range(0, 0)));
        assertEquals(3, (int)max(null, (a, b) -> Integer.compare(a, b), list(3, 2, -1, -2, 0)));
        assertEquals(3, (int)max(null, (a, b) -> Integer.compare(a, b), list(3)));
        assertNull(max(null, (a, b) -> Integer.compare(a, b), range(0, 0)));
    }

    @Test
    public void testJoin() {
        assertEquals("0, 1, 2, 3, 4", join(", ", range(0, 5)));
        assertEquals("[0, 1, 2, 3, 4]", join(", ", "[", "]", range(0, 5)));
        assertEquals("", join(", ", list()));
    }

    @Test
    public void testPeek() {
        List<String> list = new ArrayList<>();
        List<String> result = list(peek(e -> list.add(e),
            filter(e -> !e.equals("b"),
                list("a", "b", "c"))));
        assertEquals(list("a", "c"), list);
        assertEquals(list("a", "c"), result);
        List<String> doubleList = new ArrayList<>();
        list(peek(e -> doubleList.add(e),
            peek(e -> doubleList.add(e),
                list("p", "q", "r"))));
        assertEquals(list("p", "p", "q", "q", "r", "r"), doubleList);
        Iterable<String> inter;
        list(inter = peek(x -> {
        }, list("p", "q", "r")));
        assertEquals(list("p", "q", "r"), list(inter));
    }

    @Test
    public void testCombination() {
        assertArrayEquals(new int[][] {
            {0, 1},
            {0, 2},
            {0, 3},
            {1, 2},
            {1, 3},
            {2, 3}}, array(int[][]::new, combination(4, 2)));
        assertEquals(List.of(
            List.of("a", "b"),
            List.of("a", "c"),
            List.of("a", "d"),
            List.of("b", "c"),
            List.of("b", "d"),
            List.of("c", "d")), list(combination(2, list("a", "b", "c", "d"))));
    }

    @Test
    public void testPermutation() {
        List<List<String>> expected3 = list(
            list("a", "b", "c"),
            list("a", "c", "b"),
            list("b", "a", "c"),
            list("b", "c", "a"),
            list("c", "a", "b"),
            list("c", "b", "a"));
        assertEquals(expected3, list(permutation(3, list("a", "b", "c"))));
        List<List<Integer>> expected2 = list(
            list(0, 1),
            list(0, 2),
            list(1, 0),
            list(1, 2),
            list(2, 0),
            list(2, 1));
        assertEquals(expected2, list(permutation(2, list(0, 1, 2))));
        int[][] expected0 = {
            {0, 1},
            {0, 2},
            {1, 0},
            {1, 2},
            {2, 0},
            {2, 1}
        };
        assertArrayEquals(expected0, array(int[][]::new, permutation(3, 2)));
        assertEquals(expected2, list(map(intArray -> intList(intArray), permutation(3, 2))));
    }

    static int number(int... digits) {
        return reduce(0, (a, b) -> a * 10 + b, ints(digits));
    }

    static boolean checkSendMoreMoney(int s, int e, int n, int d, int m, int o, int r, int y) {
        if (s == 0 || m == 0)
            return false;
        int send = number(s, e, n, d);
        int more = number(m, o, r, e);
        int money = number(m, o, n, e, y);
        return send + more == money;
    }

    @Test
    public void testSendMoreMoney() {
        String[] variables = {"S", "E", "N", "D", "M", "O", "R", "Y"};
        Predicate<List<Integer>> check = a -> checkSendMoreMoney(
            a.get(0), a.get(1), a.get(2), a.get(3), a.get(4), a.get(5), a.get(6), a.get(7));
        List<List<Integer>> answers = list(filter(check, permutation(8, range(0, 10))));
        assertEquals(List.of(List.of(9, 5, 6, 7, 1, 0, 8, 2)), answers);
        assertEquals("S=9, E=5, N=6, D=7, M=1, O=0, R=8, Y=2",
            join(", ",
                map((s, i) -> s + "=" + i,
                    list(variables),
                    answers.get(0))));
    }

    @Test
    public void testSendMoreMoneyIntArray() {
        List<int[]> answers =
            list(
                filter(
                    a -> checkSendMoreMoney(a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7]),
                    permutation(10, 8)));
        assertEquals(1, answers.size());
        assertArrayEquals(new int[] {9, 5, 6, 7, 1, 0, 8, 2}, answers.get(0));
    }

    @Test
    public void testStream() {
        List<Integer> list = list(
            filter(i -> i % 2 == 0, () -> IntStream.range(0, 5).boxed().iterator()));
        assertEquals(List.of(0, 2, 4), list);
        Iterable<Integer> iterable1;
        List<Integer> random = list(
            iterable1 = () -> new Random(0).ints(4, 1, 7).boxed().iterator());
        assertEquals(List.of(1, 5, 2, 6), random);
        // このIterableは再利用できます。
        assertEquals(List.of(1, 5, 2, 6), list(iterable1));
        Stream<Integer> stream = new Random(0).ints(4, 1, 7).boxed();
        Iterable<Integer> iterable2;
        assertEquals(List.of(1, 5, 2, 6), list(iterable2 = () -> stream.iterator()));
        try {
            // このIterableは再利用できません。
            assertEquals(List.of(1, 5, 2, 6), list(iterable2));
            fail();
        } catch (IllegalStateException e) {
        }
    }

    @Test
    public void testGrouping() {
        Map<String, Integer> map = build(new LinkedHashMap<>(),
            m -> m.put("いち", 1),
            m -> m.put("に", 2),
            m -> m.put("さん", 3),
            m -> m.put("one", 1),
            m -> m.put("three", 3));

        assertEquals(Map.of(
            1, List.of("いち", "one"),
            2, List.of("に"),
            3, List.of("さん", "three")
        ), grouping(Entry::getValue, s -> list(map(Entry::getKey, s)), map.entrySet()));

        assertEquals(Map.of(
            1, List.of("いち", "one"),
            2, List.of("に"),
            3, List.of("さん", "three")
        ), map.entrySet().stream()
            .collect(Collectors.groupingBy(Entry::getValue,
                Collectors.mapping(Entry::getKey, Collectors.toList()))));

        assertEquals(Map.of(1, 2, 2, 1, 3, 2),
            grouping(Entry::getValue, Iterables::count, map.entrySet()));
    }

    @Test
    public void testDistinct() {
        assertEquals(list(1, 2, 3), list(distinct(list(1, 2, 3, 2, 2, 1, 2, 3))));
    }

    @Test
    public void testAddNumbersInString() {
        // java - I want to extract integers from a input string
        // "a2re45pr456#$#$80" - Stack Overflow
        // https://stackoverflow.com/questions/63950029/i-want-to-extract-integers-from-a-input-string-a2re45pr45680
        String input = "a2re45pr456#$#$80";
        // I should get a output like below
        // output : 2+45+456+80 = 583
        // int sum = sum(map(Integer::valueOf, filter(not(String::isEmpty),
        // iterable(input.split("\\D+")))));
        assertEquals(583,
            sum(map(Integer::valueOf, exclude(String::isEmpty, iterable(input.split("\\D+"))))));
    }

    @Test
    public void testCumulative() {
        assertEquals(List.of(0, 1, 3, 6, 10, 15, 21, 28, 36, 45),
            list(accumlate(0, (a, b) -> a + b, range(0, 10))));
        assertEquals(
            list(map(i -> BigInteger.valueOf((long) i),
                iterable(1, 2, 6, 24, 120, 720, 5040, 40320, 362880))),
            list(accumlate(BigInteger.ONE, (a, b) -> a.multiply(BigInteger.valueOf((long) b)),
                range(1, 10))));
    }

    @Test
    public void testDropWhile() {
        assertEquals(List.of(3, 4, 5),
            List.of(0, 1, 2, 3, 4, 5).stream().dropWhile(i -> i < 3).collect(Collectors.toList()));
        assertEquals(List.of(3, 2, 1),
            List.of(0, 1, 2, 3, 2, 1).stream().dropWhile(i -> i < 3).collect(Collectors.toList()));
        assertEquals(List.of(3, 2, 1),
            List.of(3, 2, 1).stream().dropWhile(i -> i < 3).collect(Collectors.toList()));
        assertEquals(List.of(3, 4, 5), list(dropWhile(i -> i < 3, iterable(0, 1, 2, 3, 4, 5))));
        assertEquals(List.of(3, 2, 1), list(dropWhile(i -> i < 3, iterable(0, 1, 2, 3, 2, 1))));
        assertEquals(List.of(3, 2, 1), list(dropWhile(i -> i < 3, iterable(3, 2, 1))));
    }

    @Test
    public void testTakeWhile() {
        assertEquals(List.of(0, 1, 2),
            List.of(0, 1, 2, 3, 4, 5).stream().takeWhile(i -> i < 3).collect(Collectors.toList()));
        assertEquals(List.of(0, 1, 2),
            List.of(0, 1, 2, 3, 2, 1).stream().takeWhile(i -> i < 3).collect(Collectors.toList()));
        assertEquals(List.of(),
            List.of(3, 2, 1).stream().takeWhile(i -> i < 3).collect(Collectors.toList()));
        assertEquals(List.of(0, 1, 2), list(takeWhile(i -> i < 3, iterable(0, 1, 2, 3, 4, 5))));
        assertEquals(List.of(0, 1, 2), list(takeWhile(i -> i < 3, iterable(0, 1, 2, 3, 2, 1))));
        assertEquals(List.of(), list(takeWhile(i -> i < 3, iterable(3, 2, 1))));
    }

    @Test
    public void testIndexed() {
        assertEquals(List.of(new Indexed<>(0, "a"), new Indexed<>(1, "b"), new Indexed<>(2, "c")),
            list(indexed(list("a", "b", "c"))));
    }

    static int num(int... digits) {
        return reduce(0, (a, b) -> a * 10 + b, ints(digits));
    }

    static boolean check(int a, int b, int c, int d, int e) {
        if (a == 0 || e == 0)
            return false;
        int abcde = num(a, b, c, d, e);
        int eeeeee = num(e, e, e, e, e, e);
        return abcde * a == eeeeee;
    }

    /**
     * Hard Problem For 9 Year Olds In Taiwan - YouTube
     * https://www.youtube.com/watch?v=gaDiyWowbUc
     */
    // @Test
    // public void testHardProblemFor9YearOldsInTaiwan() {
    //     List<List<Integer>> results = list(
    //         map(a -> list(a),
    //             filter(a -> check(a[0], a[1], a[2], a[3], a[4]),
    //                 permutation(10, 5))));
    //     logger.info("" + results);
    // }

    static record Foo(int i, String s) {}

    static List<Foo> foos = List.of(
        new Foo(2, "c"),
        new Foo(1, "b"),
        new Foo(3, "c"),
        new Foo(2, "b"),
        new Foo(3, "b"),
        new Foo(2, "a"),
        new Foo(1, "c"),
        new Foo(3, "a"),
        new Foo(1, "a"));

    @Test
    public void testAscDesc() {
        List<Foo> expected0 = List.of(
            new Foo(1, "a"),
            new Foo(1, "b"),
            new Foo(1, "c"),
            new Foo(2, "a"),
            new Foo(2, "b"),
            new Foo(2, "c"),
            new Foo(3, "a"),
            new Foo(3, "b"),
            new Foo(3, "c"));
        assertEquals(expected0,
            list(sorted(and(asc(Foo::i), asc(Foo::s)), foos)));
        assertEquals(expected0,
            foos.stream()
                .sorted(comparing(Foo::i).thenComparing(Foo::s))
                .collect(toList()));
        List<Foo> expected1 = List.of(
            new Foo(1, "c"),
            new Foo(1, "b"),
            new Foo(1, "a"),
            new Foo(2, "c"),
            new Foo(2, "b"),
            new Foo(2, "a"),
            new Foo(3, "c"),
            new Foo(3, "b"),
            new Foo(3, "a"));
        assertEquals(expected1,
            list(sorted(and(asc(Foo::i), desc(Foo::s)), foos)));
        assertEquals(expected1,
            foos.stream()
                .sorted(comparing(Foo::i).thenComparing(comparing(Foo::s).reversed()))
                .collect(toList()));
    }

    @Test
    public void testReverse() {
        List<Foo> expected1 = List.of(
            new Foo(1, "c"),
            new Foo(1, "b"),
            new Foo(1, "a"),
            new Foo(2, "c"),
            new Foo(2, "b"),
            new Foo(2, "a"),
            new Foo(3, "c"),
            new Foo(3, "b"),
            new Foo(3, "a"));
        assertEquals(expected1,
            list(sorted(and(asc(Foo::i), reverse(asc(Foo::s))), foos)));
    }

    @Test
    public void testSortNumberString() {
        List<String> numbers = list(map(i -> "No." + i, range(1, 100)));
        List<String> random = list(numbers);
        Collections.shuffle(random);
        assertEquals(numbers,
            list(sorted(and(asc(String::length), asc(identity())), random)));
        assertEquals(numbers,
            random.stream()
                .sorted(Comparator.comparing(String::length).thenComparing(Function.identity()))
                .collect(Collectors.toList()));
    }

    /**
     * java - Why is the DP solution not working, Trapping Rain Water? - Stack Overflow
     * https://stackoverflow.com/questions/66911147/why-is-the-dp-solution-not-working-trapping-rain-water
     *
     * Given n non-negative integers representing an elevation map where the width of each bar is 1,
     * compute how much water it can trap after raining.
     * example: Input: height = [0,1,0,2,1,0,1,3,2,1,2,1]
     *         Output: 6
     *         Explanation: The above elevation map (black section) is represented
     *         by array [0,1,0,2,1,0,1,3,2,1,2,1]. In this case,
     *         6 units of rain water (blue section) are being trapped.
     */
    // static int trap(int[] height) {
    //     List<Integer> list = list(height);
    //     return sum(map((h, l, r) -> Math.min(l, r) - h,
    //             list,
    //             accumlate(-1, Math::max, list),
    //             reverse(accumlate(-1, Math::max, reverse(list)))));
    // }

    // @Test
    // void testTrappingRainWater() {
    //     assertEquals(6, trap(new int[] {0,1,0,2,1,0,1,3,2,1,2,1}));
    //     assertEquals(2, trap(new int[] {2,0,2}));
    // }

    @Test
    public void testMakeMapEntry() {
        List<Integer> keys = intList(1, 2, 3);
        List<String> values = list("one", "two", "three");
        Map<Integer, String> expected = Map.of(1, "one", 2, "two", 3, "three");
        assertEquals(expected,
            hashMap(Entry::getKey, Entry::getValue,
                map(Map::entry, keys, values)));
        Map<Integer, String> map = new HashMap<>();
        for (var e : map(Map::entry, keys, values))
            map.put(e.getKey(), e.getValue());
        assertEquals(expected, map);
    }

    @Test
    public void testMakeMapByZip() {
        Map<Integer, String> map = new HashMap<>();
        List<Integer> keys = intList(1, 2, 3);
        List<String> values = list("one", "two", "three");
        forEach((k, v) -> map.put(k, v), keys, values);
        assertEquals(Map.of(1, "one", 2, "two", 3, "three"), map);
        assertEquals(Map.of(1, "one", 2, "two", 3, "three"), hashMap(keys, values));
        assertEquals(Map.of(1, "one", 2, "two", 3, "three"), linkedHashMap(keys, values));
    }
}
