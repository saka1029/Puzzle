package test.puzzle;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import puzzle.Permutation;

class TestPermutation {

    static Logger logger = Logger.getLogger(TestPermutation.class.getName());

    static int factorial(int n) {
        return n <= 1 ? 1 : n * factorial(n - 1);
    }

    @Test
    public void testIndexIterator() {
        for (int[] a : Permutation.iterable(5))
            logger.info(Arrays.toString(a));
        for (int i = 0; i < 10; ++i)
            assertEquals(factorial(i), Permutation.stream(i).count());
    }

    @Test
    public void testArrayIterator() {
        String[] a = {"a", "b", "c"};
        for (String[] e : Permutation.iterable(a))
            logger.info(Arrays.toString(e));
        for (int i = 0; i < 10; ++i) {
            String[] str = IntStream.range(0, i)
                .mapToObj(j -> Character.toString(j + 'a'))
                .toArray(String[]::new);
            assertEquals(factorial(i), Permutation.stream(str).count());
        }
    }

    @Test
    public void testListIterator() {
        List<String> a = List.of("a", "b", "c", "d");
        for (List<String> e : Permutation.iterable(a))
            logger.info(e.toString());
        for (int i = 0; i < 10; ++i) {
            List<String> str = IntStream.range(0, i)
                .mapToObj(j -> Character.toString(j + 'a'))
                .collect(Collectors.toList());
            assertEquals(factorial(i), Permutation.stream(str).count());
        }
    }

}
