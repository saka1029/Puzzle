package test.puzzle;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.IntBinaryOperator;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import puzzle.Common;
import puzzle.Permutation;

class TestPermutation {

    static Logger logger = Common.getLogger(TestPermutation.class);

    @Test
    void testNextInts() {
        int[] array = {0, 1, 2};
        List<int[]> list = new ArrayList<>();
        do {
            list.add(array.clone());
        } while (Permutation.next(array));
        int[][] expected = {
            {0, 1, 2},
            {0, 2, 1},
            {1, 0, 2},
            {1, 2, 0},
            {2, 0, 1},
            {2, 1, 0},
        };
        assertArrayEquals(expected, list.stream().toArray(int[][]::new));
    }

    @Test
    void testNextIntsReverse() {
        int[] array = {2, 1, 0};
        List<int[]> list = new ArrayList<>();
        IntBinaryOperator reverse = (a, b) -> -Integer.compare(a, b);
        do {
            list.add(array.clone());
        } while (Permutation.next(array, reverse));
        int[][] expected = {
            {2, 1, 0},
            {2, 0, 1},
            {1, 2, 0},
            {1, 0, 2},
            {0, 2, 1},
            {0, 1, 2},
        };
        assertArrayEquals(expected, list.stream().toArray(int[][]::new));
    }

    @Test
    void testNextStrings() {
        String[] array = {"a", "b", "c"};
        List<String[]> list = new ArrayList<>();
        do {
            list.add(array.clone());
        } while (Permutation.next(array));
        String[][] expected = {
            {"a", "b", "c"},
            {"a", "c", "b"},
            {"b", "a", "c"},
            {"b", "c", "a"},
            {"c", "a", "b"},
            {"c", "b", "a"},
        };
        assertArrayEquals(expected, list.stream().toArray(String[][]::new));
    }

    @Test
    void testNextStringsReverse() {
        String[] array = {"c", "b", "a"};
        List<String[]> list = new ArrayList<>();
        do {
            list.add(array.clone());
        } while (Permutation.next(array, Comparator.reverseOrder()));
        String[][] expected = {
            {"c", "b", "a"},
            {"c", "a", "b"},
            {"b", "c", "a"},
            {"b", "a", "c"},
            {"a", "c", "b"},
            {"a", "b", "c"},
        };
        assertArrayEquals(expected, list.stream().toArray(String[][]::new));
    }

    @Test
    void testIterable4_0() {
        logger.info(Common.methodName());
        for (int[] a : Permutation.iterable(4, 0))
            logger.info(Arrays.toString(a));
    }

    @Test
    void testIterable4() {
        logger.info(Common.methodName());
        for (int[] a : Permutation.iterable(4, 2))
            logger.info(Arrays.toString(a));
    }

    @Test
    void testIterable3_3() {
        logger.info(Common.methodName());
        List<int[]> all = new ArrayList<>();
        for (int[] a : Permutation.iterable(3, 3))
            all.add(a);
        int[][] expected = {{0, 1, 2}, {0, 2, 1}, {1, 0, 2}, {1, 2, 0},
            {2, 0, 1}, {2, 1, 0}};
        assertArrayEquals(expected, all.toArray(int[][]::new));
    }

    @Test
    void testIterable3_2() {
        logger.info(Common.methodName());
        List<int[]> all = new ArrayList<>();
        for (int[] a : Permutation.iterable(3, 2))
            all.add(a);
        int[][] expected = {{0, 1}, {0, 2}, {1, 0}, {1, 2}, {2, 0}, {2, 1}};
        assertArrayEquals(expected, all.toArray(int[][]::new));
    }

    @Test
    void testIterable3_1() {
        logger.info(Common.methodName());
        List<int[]> all = new ArrayList<>();
        for (int[] a : Permutation.iterable(3, 1))
            all.add(a);
        int[][] expected = {{0}, {1}, {2}};
        assertArrayEquals(expected, all.toArray(int[][]::new));
    }

    /**
     * 2020-08-14T19:32:11.959 情報 count=479001600 23966msec.
     * 2020-08-14T19:32:59.735 情報 count=479001600 23720msec.
     */
//    @Test
    void testIterable12() {
        logger.info(Common.methodName());
        long start = System.currentTimeMillis();
        int count = 0;
        for (int[] a : Permutation.iterable(12, 12))
            ++count;
        logger.info("count=" + count + " " + (System.currentTimeMillis() - start) + "msec.");
        assertEquals(479001600, count);
    }

    static int factorial(int n) {
        return n <= 1 ? 1 : n * factorial(n - 1);
    }

    @Test
    public void testArrayIterator() {
        logger.info(Common.methodName());
        String[] a = {"a", "b", "c"};
        for (String[] e : Permutation.iterable(a, 3))
            logger.info(Arrays.toString(e));
        for (int i = 0; i < 10; ++i) {
            String[] str = IntStream.range(0, i)
                .mapToObj(j -> Character.toString(j + 'a'))
                .toArray(String[]::new);
            assertEquals(factorial(i), Permutation.stream(str, i).count());
        }
    }

    @Test
    public void testListIterator() {
        logger.info(Common.methodName());
        List<String> a = List.of("a", "b", "c", "d");
        for (List<String> e : Permutation.iterable(a, 4))
            logger.info(e.toString());
        for (int i = 0; i < 10; ++i) {
            List<String> str = IntStream.range(0, i)
                .mapToObj(j -> Character.toString(j + 'a'))
                .collect(Collectors.toList());
            assertEquals(factorial(i), Permutation.stream(str, i).count());
        }
    }

    static int number(int... digits) {
        return IntStream.of(digits).reduce(0, (a, b) -> a * 10 + b);
    }

    @Test
    public void testSendMoreMoney() {
        logger.info(Common.methodName());
        new Object() {

            void check(int s, int e, int n, int d, int m, int o, int r, int y) {
                if (s == 0 || m == 0)
                    return;
                int send = number(s, e, n, d);
                int more = number(m, o, r, e);
                int money = number(m, o, n, e, y);
                if (send + more != money)
                    return;
                logger.info(send + "+" + more + "=" + money);
                assertEquals(9567, send);
                assertEquals(1085, more);
                assertEquals(10652, money);
            }

            void run() {
                for (int[] a : Permutation.iterable(10, 8))
                    check(a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7]);
            }

        }.run();
    }

    @Test
    public void testSaveMoreMoney() {
        logger.info(Common.methodName());
        new Object() {
            int count = 0;

            List<List<Integer>> expected = List.of(
                List.of(9376, 1086, 10462),
                List.of(9386, 1076, 10462),
                List.of(9476, 1086, 10562),
                List.of(9486, 1076, 10562));

            void check(int s, int a, int v, int e, int m, int o, int r, int n,
                int y) {
                if (s == 0 || m == 0)
                    return;
                int save = number(s, a, v, e);
                int more = number(m, o, r, e);
                int money = number(m, o, n, e, y);
                if (save + more != money)
                    return;
                logger.info(save + "+" + more + "=" + money);
                assertTrue(expected.contains(List.of(save, more, money)));
                ++count;
            }

            void run() {
                for (int[] a : Permutation.iterable(10, 9))
                    check(a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8]);
                assertEquals(4, count);
            }
        }.run();
    }

    /**
     * 2020-08-12T14:49:51.631 情報 count = 479001600 22107msec.
     * 2020-08-12T14:50:40.056 情報 count = 479001600 22854msec.
     * 2020-08-12T14:55:38.434 情報 count = 479001600 23904msec.
     * 2020-08-12T15:15:04.279 情報 count = 479001600 23153msec.
     */
//    @Test
    public void test12() {
        logger.info(Common.methodName());
        int count = 0;
        long start = System.currentTimeMillis();
        for (int[] a : Permutation.iterable(12, 12))
            ++count;
        logger.info("count = " + count + " " + (System.currentTimeMillis() - start) + "msec.");
    }

    /**
     * 2020-08-12T18:59:52.829 情報 count = 479001600 76028msec.
     */
//    @Test
    public void test12Loop() {
        logger.info(Common.methodName());
        int count = 0;
        long start = System.currentTimeMillis();
        for (int a = 0; a < 12; ++a) {
            for (int b = 0; b < 12; ++b) {
                if (b == a)
                    continue;
                for (int c = 0; c < 12; ++c) {
                    if (c == a || c == b)
                        continue;
                    for (int d = 0; d < 12; ++d) {
                        if (d == a || d == b || d == c)
                            continue;
                        for (int e = 0; e < 12; ++e) {
                            if (e == a || e == b || e == c || e == d)
                                continue;
                            for (int f = 0; f < 12; ++f) {
                                if (f == a || f == b || f == c || f == d || f == e)
                                    continue;
                                for (int g = 0; g < 12; ++g) {
                                    if (g == a || g == b || g == c || g == d || g == e || g == f)
                                        continue;
                                    for (int h = 0; h < 12; ++h) {
                                        if (h == a || h == b || h == c || h == d || h == e || h == f || h == g)
                                            continue;
                                        for (int i = 0; i < 12; ++i) {
                                            if (i == a || i == b || i == c || i == d || i == e || i == f || i == g
                                                || i == h)
                                                continue;
                                            for (int j = 0; j < 12; ++j) {
                                                if (j == a || j == b || j == c || j == d || j == e || j == f || j == g
                                                    || j == h || j == i)
                                                    continue;
                                                for (int k = 0; k < 12; ++k) {
                                                    if (k == a || k == b || k == c || k == d || k == e || k == f
                                                        || k == g || k == h || k == i || k == j)
                                                        continue;
                                                    for (int l = 0; l < 12; ++l) {
                                                        if (l == a || l == b || l == c || l == d || l == e || l == f
                                                            || l == g || l == h || l == i || l == j || l == k)
                                                            continue;
                                                        ++count;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        logger.info("count = " + count + " " + (System.currentTimeMillis() - start) + "msec.");
    }

    static Set<Integer> remove(Set<Integer> set, int i) {
        Set<Integer> result = new HashSet<>(set);
        result.remove(i);
        return result;
    }

    /**
     * 計測不能
     */
//    @Test
    public void test12LoopSet() {
        logger.info(Common.methodName());
        int max = 12;
        int count = 0;
        long start = System.currentTimeMillis();
        Set<Integer> as = IntStream.range(0, max).boxed().collect(Collectors.toSet());
        Set<Integer> bs, cs, ds, es, fs, gs, hs, is, js, ks, ls;
        for (int a : as)
            for (int b : bs = remove(as, a))
                for (int c : cs = remove(bs, b))
                    for (int d : ds = remove(cs, c))
                        for (int e : es = remove(ds, d))
                            for (int f : fs = remove(es, e))
                                for (int g : gs = remove(fs, f))
                                    for (int h : hs = remove(gs, g))
                                        for (int i : is = remove(hs, h))
                                            for (int j : js = remove(is, i))
                                                for (int k : ks = remove(js, j))
                                                    for (int l : ls = remove(ks, k))
                                                        ++count;
        logger.info("count = " + count + " " + (System.currentTimeMillis() - start) + "msec.");
    }

    static int[] remove(int[] array, int i) {
        return IntStream.of(array)
            .filter(v -> v != i)
            .toArray();
    }

    /**
     * 2020-08-12T19:20:10.766 情報 count = 479001600 67803msec.
     * 2020-08-12T19:21:42.758 情報 count = 479001600 68696msec.
     */
//    @Test
    public void test12LoopArray() {
        logger.info(Common.methodName());
        int max = 12;
        int count = 0;
        long start = System.currentTimeMillis();
        int[] as = IntStream.range(0, max).toArray();
        int[] bs, cs, ds, es, fs, gs, hs, is, js, ks, ls;
        for (int a : as)
            for (int b : bs = remove(as, a))
                for (int c : cs = remove(bs, b))
                    for (int d : ds = remove(cs, c))
                        for (int e : es = remove(ds, d))
                            for (int f : fs = remove(es, e))
                                for (int g : gs = remove(fs, f))
                                    for (int h : hs = remove(gs, g))
                                        for (int i : is = remove(hs, h))
                                            for (int j : js = remove(is, i))
                                                for (int k : ks = remove(js, j))
                                                    for (int l : ls = remove(ks, k))
                                                        ++count;
        logger.info("count = " + count + " " + (System.currentTimeMillis() - start) + "msec.");
    }

    @Test
    public void testTokyo2020() {
        logger.info(Common.methodName());
        int[] array = {0, 0, 1, 2, 2};
        int length = array.length;
        int count = 0;
        for (int[] a : Permutation.iterable(array, length)) {
            ++count;
//            logger.info(count + " " + Arrays.toString(a));
        }
        int expectedCount = 5 * 4 * 3 * 2 * 1 / 2 / 2;
        assertEquals(expectedCount, count);
        assertEquals(expectedCount, Permutation.stream(array, length).count());

//        String tokyo = "Tokyo2020";
//        int[] tokyoArray = tokyo.codePoints().sorted().toArray();
//        System.out.println(Arrays.toString(tokyoArray));
//        long tokyoCount = Permutation.stream(tokyoArray, tokyoArray.length)
//            .count();
//        long expectedTokyoCount = 9 * 8 * 7 * 6 * 5 * 4 * 3 * 2 * 1 / 2 / 2 / 2;
//        assertEquals(expectedTokyoCount, tokyoCount);
        //        Integer[] s = {(int)'T', (int)'o', (int)'k', (int)'y', (int)'o', (int)'2', (int)'0', (int)'2', (int)'0'};
//        long count = Permutation.stream(s, s.length).count();
//        System.out.println(count);
    }

    /**
     * Lexicographically next permutation in C++ - GeeksforGeeks
     * https://www.geeksforgeeks.org/find-the-next-lexicographically-greater-word-than-a-given-word/
     * @param array
     * @return
     */
    static boolean nextPermutation(int[] s) {
        return new Object() {

            int len = s.length;

            void swap(int i, int j) {
                int temp = s[i];
                s[i] = s[j];
                s[j] = temp;
            }

            void rev(int l, int r) {
                while (l < r)
                    swap(l++, r--);
            }

            /**
             *
             * @param l 検索範囲の最小インデックス
             * @param r 検索範囲の最大インデックス
             * @param key 検索する値
             * @return 検索されたインデックス値。見つからない場合は-1を返す。
             */
            int bsearch(int l, int r, int key) {
                int index = -1;
                while (l <= r) {
                    int mid = l + (r - l) / 2;
                    if (s[mid] <= key)
                        r = mid - 1;
                    else {
                        l = mid + 1;
                        if (index == -1 || s[index] >= s[mid])
                            index = mid;
                    }
                }
                return index;
            }

            boolean next() {
                int i = len - 2;
                while (i >= 0 && s[i] >= s[i + 1])
                    --i;
                if (i < 0)
                    return false;
                else {
                    int index = bsearch(i + 1, len - 1, s[i]);
                    // index = -1 の場合は例外が発生する。
                    swap(i, index);
                    rev(i + 1, len - 1);
                    return true;
                }
            }
        }.next();
    }

    @Test
    public void testNextPermutation() {
        logger.info(Common.methodName());
        int[] array = IntStream.range(0, 5).toArray();
        do {
            logger.info(Arrays.toString(array));
        } while (nextPermutation(array));
    }

    static int fact(int n) {
        int r = 1;
        for (int i = 1; i <= n; ++i)
            r *= i;
        return r;
    }

    @Test
    public void testNextPermutationDuplicatedValues() {
        logger.info(Common.methodName());
        List<int[]> result = new ArrayList<>();
        int[] array = {0, 0, 1, 2};
        do {
            result.add(array.clone());
        } while (nextPermutation(array));
        int[][] expected = {
            {0, 0, 1, 2},
            {0, 0, 2, 1},
            {0, 1, 0, 2},
            {0, 1, 2, 0},
            {0, 2, 0, 1},
            {0, 2, 1, 0},
            {1, 0, 0, 2},
            {1, 0, 2, 0},
            {1, 2, 0, 0},
            {2, 0, 0, 1},
            {2, 0, 1, 0},
            {2, 1, 0, 0},
        };
        assertArrayEquals(expected, result.toArray(int[][]::new));
    }
}
