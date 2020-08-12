package test.scrap;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import puzzle.Permutation;

class TestPermutaionS {

    static Logger logger = Logger.getLogger(TestPermutaionS.class.getName());

    @Test
    void testIterable4() {
        for (int[] a : Permutation.iterable(4, 2))
            System.out.println(Arrays.toString(a));
    }

    @Test
    void testIterable3_3() {
        List<int[]> all = new ArrayList<>();
        for (int[] a : Permutation.iterable(3, 3))
            all.add(a);
        int[][] expected = {
            {0, 1, 2},
            {0, 2, 1},
            {1, 0, 2},
            {1, 2, 0},
            {2, 0, 1},
            {2, 1, 0}
        };
        assertArrayEquals(expected, all.toArray(int[][]::new));
    }

    @Test
    void testIterable3_2() {
        List<int[]> all = new ArrayList<>();
        for (int[] a : Permutation.iterable(3, 2))
            all.add(a);
        int[][] expected = {
            {0, 1},
            {0, 2},
            {1, 0},
            {1, 2},
            {2, 0},
            {2, 1}
        };
        assertArrayEquals(expected, all.toArray(int[][]::new));
    }

    @Test
    void testIterable3_1() {
        List<int[]> all = new ArrayList<>();
        for (int[] a : Permutation.iterable(3, 1))
            all.add(a);
        int[][] expected = {
            {0},
            {1},
            {2}
        };
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

    @Test
    public void testSendMoreMoney() {
        new Object() {

            int number(int... digits) {
                return IntStream.of(digits).reduce(0, (a, b) -> a * 10 + b);
            }

            void check(int s, int e, int n, int d, int m, int o, int r, int y) {
                if (s == 0 || m == 0) return;
                if (number(s, e, n, d) + number(m, o, r, e) != number(m, o, n, e, y)) return;
                System.out.printf("%d%d%d%d+%d%d%d%d=%d%d%d%d%d%n",
                    s, e, n, d, m, o, r, e, m, o, n, e, y);
            }

            void run() {
                for (int[] a : Permutation.iterable(10, 8))
                    check(a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7]);
            }
        }.run();
    }

}
