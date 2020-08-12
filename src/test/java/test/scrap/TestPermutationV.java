package test.scrap;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import scrap.PermutationV;

class TestPermutationV {

    static Logger logger = Logger.getLogger(TestPermutationV.class.getName());

    @Test
    void testMax() {
        assertEquals(6, PermutationV.size(3, 3));
        assertEquals(6, PermutationV.size(3, 2));
        assertEquals(3, PermutationV.size(3, 1));
        assertEquals(24, PermutationV.size(4, 4));
        assertEquals(24, PermutationV.size(4, 3));
        assertEquals(12, PermutationV.size(4, 2));
        assertEquals(4, PermutationV.size(4, 1));
    }

    static int[] ints(int... a) { return a; }

    @Test
    void testGet() {
        assertArrayEquals(ints(0, 1, 2), PermutationV.get(3, 3, 0));
        assertArrayEquals(ints(0, 2, 1), PermutationV.get(3, 3, 1));
        assertArrayEquals(ints(2, 1, 0), PermutationV.get(3, 3, 5));
        assertArrayEquals(ints(0, 1), PermutationV.get(3, 2, 0));
        assertArrayEquals(ints(0, 2), PermutationV.get(3, 2, 1));
        assertArrayEquals(ints(2, 1), PermutationV.get(3, 2, 5));
        assertArrayEquals(ints(0, 1, 2, 3), PermutationV.get(4, 4, 0));
        assertArrayEquals(ints(0, 1, 3, 2), PermutationV.get(4, 4, 1));
        assertArrayEquals(ints(0, 2, 1, 3), PermutationV.get(4, 4, 2));
        assertArrayEquals(ints(0, 2, 3, 1), PermutationV.get(4, 4, 3));
        assertArrayEquals(ints(0, 3, 1, 2), PermutationV.get(4, 4, 4));
        assertArrayEquals(ints(0, 3, 2, 1), PermutationV.get(4, 4, 5));
        assertArrayEquals(ints(1, 0, 2, 3), PermutationV.get(4, 4, 6));
        assertArrayEquals(ints(1, 0, 3, 2), PermutationV.get(4, 4, 7));
        assertArrayEquals(ints(1, 2, 0, 3), PermutationV.get(4, 4, 8));
        assertArrayEquals(ints(1, 2, 3, 0), PermutationV.get(4, 4, 9));
        assertArrayEquals(ints(1, 3, 0, 2), PermutationV.get(4, 4, 10));
        assertArrayEquals(ints(1, 3, 2, 0), PermutationV.get(4, 4, 11));
        assertArrayEquals(ints(2, 0, 1, 3), PermutationV.get(4, 4, 12));
        assertArrayEquals(ints(2, 0, 3, 1), PermutationV.get(4, 4, 13));
        assertArrayEquals(ints(2, 1, 0, 3), PermutationV.get(4, 4, 14));
        assertArrayEquals(ints(2, 1, 3, 0), PermutationV.get(4, 4, 15));
        assertArrayEquals(ints(2, 3, 0, 1), PermutationV.get(4, 4, 16));
        assertArrayEquals(ints(2, 3, 1, 0), PermutationV.get(4, 4, 17));
        assertArrayEquals(ints(3, 0, 1, 2), PermutationV.get(4, 4, 18));
        assertArrayEquals(ints(3, 0, 2, 1), PermutationV.get(4, 4, 19));
        assertArrayEquals(ints(3, 1, 0, 2), PermutationV.get(4, 4, 20));
        assertArrayEquals(ints(3, 1, 2, 0), PermutationV.get(4, 4, 21));
        assertArrayEquals(ints(3, 2, 0, 1), PermutationV.get(4, 4, 22));
        assertArrayEquals(ints(3, 2, 1, 0), PermutationV.get(4, 4, 23));
    }

    @Test
    public void test3桁の可変進数() {
        int n = 3, r = 3;
        int size = PermutationV.size(n, r);
        for (int i = 0; i < size; ++i)
            logger.info(i + " : " + Arrays.toString(PermutationV.vbn(n, r, i))
                + " -> " + Arrays.toString(PermutationV.get(n, r, i)));
    }

    @Test
    public void testIterable3_3() {
        List<int[]> all = new ArrayList<>();
        for (int[] e : PermutationV.iterable(3, 3))
            all.add(e.clone());
        int[][] array = all.stream().toArray(int[][]::new);
        assertEquals(6, array.length);
        int[][] expected = {{0, 1, 2}, {0, 2, 1}, {1, 0, 2}, {1, 2, 0}, {2, 0, 1}, {2, 1, 0}};
        assertArrayEquals(expected, array);
    }

    @Test
    public void testIterable3_1() {
        List<int[]> all = new ArrayList<>();
        for (int[] e : PermutationV.iterable(3, 1))
            all.add(e.clone());
        int[][] array = all.stream().toArray(int[][]::new);
        assertEquals(3, array.length);
        int[][] expected = {{0}, {1}, {2}};
        assertArrayEquals(expected, array);
    }

    @Test
    public void testStream3_3() {
        int[][] array = PermutationV.stream(3, 3).map(a -> a.clone()).toArray(int[][]::new);
        assertEquals(6, array.length);
        int[][] expected = {{0, 1, 2}, {0, 2, 1}, {1, 0, 2}, {1, 2, 0}, {2, 0, 1}, {2, 1, 0}};
        assertArrayEquals(expected, array);
    }

    static int factorial(int n) {
        return n <= 1 ? 1 : n * factorial(n - 1);
    }

    @Test
    public void testIndexIterator() {
        for (int[] a : PermutationV.iterable(5, 5))
            logger.info(Arrays.toString(a));
        for (int i = 0; i < 10; ++i)
            assertEquals(factorial(i), PermutationV.stream(i, i).count());
    }

    @Test
    public void testArrayIterator() {
        String[] a = {"a", "b", "c"};
        for (String[] e : PermutationV.iterable(a, 3))
            logger.info(Arrays.toString(e));
        for (int i = 0; i < 10; ++i) {
            String[] str = IntStream.range(0, i)
                .mapToObj(j -> Character.toString(j + 'a'))
                .toArray(String[]::new);
            assertEquals(factorial(i), PermutationV.stream(str, i).count());
        }
    }

    @Test
    public void testListIterator() {
        List<String> a = List.of("a", "b", "c", "d");
        for (List<String> e : PermutationV.iterable(a, 4))
            logger.info(e.toString());
        for (int i = 0; i < 10; ++i) {
            List<String> str = IntStream.range(0, i)
                .mapToObj(j -> Character.toString(j + 'a'))
                .collect(Collectors.toList());
            assertEquals(factorial(i), PermutationV.stream(str, i).count());
        }
    }
}
