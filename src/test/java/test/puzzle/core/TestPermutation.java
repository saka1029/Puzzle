package test.puzzle.core;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Test;

import puzzle.core.Common;
import puzzle.core.Permutation;

public class TestPermutation {

    static final Logger logger = Common.getLogger(TestPermutation.class);

    @Test
    public void testCount() {
        assertEquals(1, Permutation.count(3, 0));
        assertEquals(3, Permutation.count(3, 1));
        assertEquals(6, Permutation.count(3, 2));
        assertEquals(6, Permutation.count(3, 3));
        assertEquals(1, Permutation.count(4, 0));
        assertEquals(4, Permutation.count(4, 1));
        assertEquals(12, Permutation.count(4, 2));
        assertEquals(24, Permutation.count(4, 3));
        assertEquals(24, Permutation.count(4, 4));
    }

    static final List<List<Integer>> EXPECTED_4_2 = List.of(
            List.of(0, 1),
            List.of(0, 2),
            List.of(0, 3),
            List.of(1, 0),
            List.of(1, 2),
            List.of(1, 3),
            List.of(2, 0),
            List.of(2, 1),
            List.of(2, 3),
            List.of(3, 0),
            List.of(3, 1),
            List.of(3, 2));

    @Test
    public void testIteratorInt_N_R() {
        List<List<Integer>> actual = new ArrayList<>();
        for (Iterator<int[]> i = Permutation.iterator(4, 2); i.hasNext();)
            actual.add(IntStream.of(i.next()).boxed().toList());
        assertEquals(Permutation.count(4, 2), actual.size());
        assertEquals(EXPECTED_4_2, actual);
    }

    @Test
    public void testIterableInt_N_R() {
        List<List<Integer>> actual = new ArrayList<>();
        for (int[] a : Permutation.iterable(4, 2))
            actual.add(IntStream.of(a).boxed().toList());
        assertEquals(Permutation.count(4, 2), actual.size());
        assertEquals(EXPECTED_4_2, actual);
    }

    @Test
    public void testStreamInt_N_R() {
        List<List<Integer>> actual = Permutation.stream(4, 2)
            .map(a -> IntStream.of(a).boxed().toList())
            .toList();
        assertEquals(Permutation.count(4, 2), actual.size());
        assertEquals(EXPECTED_4_2, actual);
    }

    @Test
    public void testIteratorIntArray_N_R() {
        List<List<Integer>> actual = new ArrayList<>();
        int[] array = IntStream.range(0, 4).toArray();
        for (Iterator<int[]> i = Permutation.iterator(array, 2); i.hasNext();)
            actual.add(IntStream.of(i.next()).boxed().toList());
        assertEquals(Permutation.count(4, 2), actual.size());
        assertEquals(EXPECTED_4_2, actual);
    }

    @Test
    public void testIterableIntArray_N_R() {
        List<List<Integer>> actual = new ArrayList<>();
        int[] array = IntStream.range(0, 4).toArray();
        for (int[] a : Permutation.iterable(array, 2))
            actual.add(IntStream.of(a).boxed().toList());
        assertEquals(Permutation.count(4, 2), actual.size());
        assertEquals(EXPECTED_4_2, actual);
    }

    @Test
    public void testStreamIntArray_N_R() {
        int[] array = IntStream.range(0, 4).toArray();
        List<List<Integer>> actual = Permutation.stream(array, 2)
            .map(a -> IntStream.of(a).boxed().toList())
            .toList();
        assertEquals(Permutation.count(4, 2), actual.size());
        assertEquals(EXPECTED_4_2, actual);
    }

    @Test
    public void testIteratorIntegerArray_N_R() {
        List<List<Integer>> actual = new ArrayList<>();
        Integer[] array = IntStream.range(0, 4).boxed().toArray(Integer[]::new);
        for (Iterator<Integer[]> i = Permutation.iterator(array, 2); i.hasNext();)
            actual.add(Arrays.asList(i.next()));
        assertEquals(Permutation.count(4, 2), actual.size());
        assertEquals(EXPECTED_4_2, actual);
    }
    
    static int number(int... digits) {
        int r = 0;
        for (int i : digits)
            r = r * 10 + i;
        return r;
    }

    static boolean isSendMoreMoney(int[] a) {
        int s = a[0], e = a[1], n = a[2], d = a[3];
        int m = a[4], o = a[5], r = a[6], y = a[7];
        if (s == 0 || m == 0)
            return false;
        return number(s, e, n, d) + number(m, o, r, e) == number(m, o, n, e, y);
    }

    @Test
    public void testSendMoreMoney() {
        for (int[] a : Permutation.iterable(10, 8))
            if (isSendMoreMoney(a))
                assertArrayEquals(new int[] {9, 5, 6, 7, 1, 0, 8, 2}, a);
    }

    @Test
    public void testIterableIntegerArray_N_R() {
        List<List<Integer>> actual = new ArrayList<>();
        Integer[] array = IntStream.range(0, 4).boxed().toArray(Integer[]::new);
        for (Integer[] a : Permutation.iterable(array, 2))
            actual.add(Arrays.asList(a));
        assertEquals(Permutation.count(4, 2), actual.size());
        assertEquals(EXPECTED_4_2, actual);
    }

    @Test
    public void testStreamIntegerArray_N_R() {
        Integer[] array = IntStream.range(0, 4).boxed().toArray(Integer[]::new);
        List<List<Integer>> actual = Permutation.stream(array, 2)
            .map(a -> Arrays.asList(a))
            .toList();
        assertEquals(Permutation.count(4, 2), actual.size());
        assertEquals(EXPECTED_4_2, actual);
    }

    @Test
    public void testStream_33_2() {
        List<List<Integer>> actual = Permutation.stream(33, 2)
            .limit(32)
            .map(a -> IntStream.of(a).boxed().toList())
            .toList();
        assertEquals(32, actual.size());
        // expected = [[0, 1], [0, 2], ... , [0, 32]]
        List<List<Integer>> expected = IntStream.range(1, 33)
            .mapToObj(i -> List.of(0, i))
            .toList();
        assertEquals(expected, actual);
    }

    @Test
    public void testStream_65_5() {
        try {
            @SuppressWarnings("unused")
            Stream<int[]> stream = Permutation.stream(65, 5);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("n must b <= 64", e.getMessage());
        }
    }

    @Test
    public void testNextInt_3() {
        int[] array = IntStream.range(0, 3).toArray();
        List<List<Integer>> actual = new ArrayList<>();
        do {
            actual.add(IntStream.of(array).boxed().toList());
        } while (Permutation.next(array));
        List<List<Integer>> expected = List.of(
            List.of(0, 1, 2),
            List.of(0, 2, 1),
            List.of(1, 0, 2),
            List.of(1, 2, 0),
            List.of(2, 0, 1),
            List.of(2, 1, 0));
        assertEquals(expected, actual);
    }

    @Test
    public void testNextInt_3_String() {
        String str = "abc";
        int[] array = str.codePoints().toArray();
        List<String> actual = new ArrayList<>();
        do {
            actual.add(new String(array, 0, array.length));
        } while (Permutation.next(array));
        List<String> expected = List.of("abc", "acb", "bac", "bca", "cab", "cba");
        assertEquals(expected, actual);
    }

    @Test
    public void testNextString() {
        String str = "abc";
        List<String> actual = new ArrayList<>();
        do {
            actual.add(str);
            str = Permutation.next(str);
        } while (str != null);
        List<String> expected = List.of("abc", "acb", "bac", "bca", "cab", "cba");
        assertEquals(expected, actual);
        assertEquals("acb", Permutation.next("abc"));
        assertEquals("bab", Permutation.next("abb"));
        assertEquals(null, Permutation.next("cba"));
        assertEquals(null, Permutation.next("aaa"));
    }

    @Test
    public void testNextStringArrayComparator() {
        Comparator<String> comparator = Comparator.reverseOrder();
        String[] array1 = {"c", "b", "a"};
        Permutation.next(array1, comparator);
        assertArrayEquals(new String[] {"c", "a", "b"}, array1);
        String[] array2 = {"b", "b", "a"};
        Permutation.next(array2, comparator);
        assertArrayEquals(new String[] {"b", "a", "b"}, array2);
        String[] array3 = {"a", "b", "c"};
        assertFalse(Permutation.next(array3, comparator));
        String[] array4 = {"a", "a", "a"};
        assertFalse(Permutation.next(array4, comparator));
    }

    @Test
    public void testNextStringArray() {
        String[] array1 = {"a", "b", "c"};
        Permutation.next(array1);
        assertArrayEquals(new String[] {"a", "c", "b"}, array1);
        String[] array2 = {"a", "b", "b"};
        Permutation.next(array2);
        assertArrayEquals(new String[] {"b", "a", "b"}, array2);
        String[] array3 = {"c", "b", "a"};
        assertFalse(Permutation.next(array3));
        String[] array4 = {"a", "a", "a"};
        assertFalse(Permutation.next(array4));
    }
}
