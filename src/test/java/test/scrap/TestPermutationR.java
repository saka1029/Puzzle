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

import scrap.PermutationR;

class TestPermutationR {

    static Logger logger = Logger.getLogger(TestPermutationR.class.getName());

    @Test
    public void testIterable3_3() {
        List<int[]> all = new ArrayList<>();
        for (int[] e : PermutationR.iterable(3, 3))
            all.add(e.clone());
        int[][] array = all.stream().toArray(int[][]::new);
        assertEquals(6, array.length);
        int[][] expected = {{0, 1, 2}, {0, 2, 1}, {1, 0, 2}, {1, 2, 0}, {2, 0, 1}, {2, 1, 0}};
        assertArrayEquals(expected, array);
    }

    @Test
    public void testIterable3_1() {
        List<int[]> all = new ArrayList<>();
        for (int[] e : PermutationR.iterable(3, 1))
            all.add(e.clone());
        int[][] array = all.stream().toArray(int[][]::new);
        assertEquals(3, array.length);
        int[][] expected = {{0}, {1}, {2}};
        assertArrayEquals(expected, array);
    }

    @Test
    public void testStream3_3() {
        int[][] array = PermutationR.stream(3, 3).map(a -> a.clone()).toArray(int[][]::new);
        assertEquals(6, array.length);
        int[][] expected = {{0, 1, 2}, {0, 2, 1}, {1, 0, 2}, {1, 2, 0}, {2, 0, 1}, {2, 1, 0}};
        assertArrayEquals(expected, array);
    }

    static int factorial(int n) {
        return n <= 1 ? 1 : n * factorial(n - 1);
    }

    @Test
    public void testIndexIterator() {
        for (int[] a : PermutationR.iterable(5, 5))
            logger.info(Arrays.toString(a));
        for (int i = 0; i < 10; ++i)
            assertEquals(factorial(i), PermutationR.stream(i, i).count());
    }

    @Test
    public void testArrayIterator() {
        String[] a = {"a", "b", "c"};
        for (String[] e : PermutationR.iterable(a, 3))
            logger.info(Arrays.toString(e));
        for (int i = 0; i < 10; ++i) {
            String[] str = IntStream.range(0, i)
                .mapToObj(j -> Character.toString(j + 'a'))
                .toArray(String[]::new);
            assertEquals(factorial(i), PermutationR.stream(str, i).count());
        }
    }

    @Test
    public void testListIterator() {
        List<String> a = List.of("a", "b", "c", "d");
        for (List<String> e : PermutationR.iterable(a, 4))
            logger.info(e.toString());
        for (int i = 0; i < 10; ++i) {
            List<String> str = IntStream.range(0, i)
                .mapToObj(j -> Character.toString(j + 'a'))
                .collect(Collectors.toList());
            assertEquals(factorial(i), PermutationR.stream(str, i).count());
        }
    }
}
