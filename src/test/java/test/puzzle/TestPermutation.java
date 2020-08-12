package test.puzzle;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import puzzle.Permutation;

class TestPermutation {

    static Logger logger = Logger.getLogger(TestPermutation.class.getName());

    @Test
    void testIterable4() {
        for (int[] a : Permutation.iterable(4, 2))
            logger.info(Arrays.toString(a));
    }

    @Test
    void testIterable3_3() {
        List<int[]> all = new ArrayList<>();
        for (int[] a : Permutation.iterable(3, 3))
            all.add(a);
        int[][] expected = {{0, 1, 2}, {0, 2, 1}, {1, 0, 2}, {1, 2, 0},
            {2, 0, 1}, {2, 1, 0}};
        assertArrayEquals(expected, all.toArray(int[][]::new));
    }

    @Test
    void testIterable3_2() {
        List<int[]> all = new ArrayList<>();
        for (int[] a : Permutation.iterable(3, 2))
            all.add(a);
        int[][] expected = {{0, 1}, {0, 2}, {1, 0}, {1, 2}, {2, 0}, {2, 1}};
        assertArrayEquals(expected, all.toArray(int[][]::new));
    }

    @Test
    void testIterable3_1() {
        List<int[]> all = new ArrayList<>();
        for (int[] a : Permutation.iterable(3, 1))
            all.add(a);
        int[][] expected = {{0}, {1}, {2}};
        assertArrayEquals(expected, all.toArray(int[][]::new));
    }

    static int factorial(int n) {
        return n <= 1 ? 1 : n * factorial(n - 1);
    }

    @Test
    public void testArrayIterator() {
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
                logger.info("start SEND MORE MONEY");
                for (int[] a : Permutation.iterable(10, 8))
                    check(a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7]);
            }

        }.run();
    }

    @Test
    public void testSaveMoreMoney() {
        new Object() {

            List<List<Integer>> expected = List.of(
                List.of(9376, 1086, 10462),
                List.of(9386, 1076, 10462),
                List.of(9476, 1086, 10562),
                List.of(9486, 1076, 10562)
            );

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
            }

            void run() {
                logger.info("start SAVE MORE MONEY");
                for (int[] a : Permutation.iterable(10, 9))
                    check(a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8]);
            }
        }.run();
    }

    /**
     * 2020-08-12T14:49:51.631 情報 count = 479001600 22107msec.
     * 2020-08-12T14:50:40.056 情報 count = 479001600 22854msec.
     * 2020-08-12T14:55:38.434 情報 count = 479001600 23904msec.
     * 2020-08-12T15:15:04.279 情報 count = 479001600 23153msec.
     */
    @Test
    public void test12() {
        int count = 0;
        long start = System.currentTimeMillis();
        for (int[] a : Permutation.iterable(12, 12))
            ++count;
        logger.info("count = " + count + " " + (System.currentTimeMillis() - start) + "msec.");
    }

}
