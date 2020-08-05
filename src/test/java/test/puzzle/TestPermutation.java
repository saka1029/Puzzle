package test.puzzle;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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

    static void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    static boolean next(int[] array) {
        int length = array.length;
        if (length < 2)
            return false;
        for (int i = length - 2; i >= 0; --i)
            if (array[i] < array[i + 1])
                for (int j = length - 1; true; --j)
                    if (array[i] < array[j]) {
                        swap(array, i, j);
                        for (int k = i + 1, l = length - 1; k < l; ++k, --l)
                            swap(array, k, l);
                        return true;
                    }
        return false;
    }

    @Test
    public void testNext() {
        int[] a = {2, 2, 1, 3};
        List<int[]> list = new ArrayList<>();
        for (boolean f = true; f; f = next(a))
            list.add(a.clone());
        int[][] result = list.toArray(int[][]::new);
        int[][] expected = {
            {2, 2, 1, 3},
            {2, 2, 3, 1},
            {2, 3, 1, 2},
            {2, 3, 2, 1},
            {3, 1, 2, 2},
            {3, 2, 1, 2},
            {3, 2, 2, 1},
        };
        assertArrayEquals(expected, result);
    }


    static class PermIterator implements Iterator<int[]> {

        final int[] array;
        final int[] sub;
        final int sel;
        boolean hasNext = true;

        PermIterator(int[] array, int sel) {
            this.array = array;
            this.sub = new int[sel];
            this.sel = sel;
        }

        boolean forward(int length) {
            for (int i = length - 2; i >= 0; --i)
                if (array[i] < array[i + 1])
                    for (int j = length - 1; true; --j)
                        if (array[i] < array[j]) {
                            swap(array, i, j);
                            for (int k = i + 1, l = length - 1; k < l; ++k, --l)
                                swap(array, k, l);
                            return true;
                        }
            return false;
        }

        boolean forward() {
            if (!hasNext) return false;
            int length = array.length;
            if (length < 2)
                return false;
            System.arraycopy(array, 0, sub, 0, sel);
            while (Arrays.compare(array, 0, sel, sub, 0, sel) == 0)
                if (!forward(length))
                    return false;
            return true;
        }

        @Override
        public boolean hasNext() {
            return hasNext;
        }

        @Override
        public int[] next() {
            int[] r = Arrays.copyOf(array, sel);
            hasNext = forward();
            return r;
        }

        static Iterable<int[]> iterable(int[] array, int sel) {
            return () -> new PermIterator(array, sel);
        }

    }
    @Test
    public void testNextSelect() {
        int[] a = {0, 1, 2, 3};
        List<List<Integer>> list = new ArrayList<>();
        for (int[] e : PermIterator.iterable(a, 3))
            list.add(IntStream.of(e).boxed().collect(Collectors.toList()));
        for (List<Integer> e : list)
            System.out.println(e);
    }

    static class PermutationIndexIterator implements Iterator<int[]> {

        private final int n;
        private final int max;
        private final boolean[] used;
        private final int[] next;

        private int order;
        private boolean hasNext;

        PermutationIndexIterator(int n, int r) {
            if (n < 0) throw new IllegalArgumentException("n must be >= 0");
            if (r < 0) throw new IllegalArgumentException("r must be >= 0");
            if (r > n) throw new IllegalArgumentException("r must be <= n");
            this.n = n;
            this.max = (int)Math.pow(n, r);
            this.used = new boolean[n];
            this.next = new int[r];
            this.order = 0;
            this.hasNext = advance();
        }

        boolean uniq() {
            Arrays.fill(used, false);
            for (int i : next)
                if (used[i])
                    return false;
                else
                    used[i] = true;
            return true;
        }

        boolean advance() {
            for (; order < max; ++order) {
                for (int k = order, i = next.length - 1; k > 0; k /= n, --i)
                    next[i] = k % n;
                if (uniq()) {
                    ++order;
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean hasNext() {
            return hasNext;
        }

        @Override
        public int[] next() {
            int[] result = next.clone();
            hasNext = advance();
            return result;
        }

        public static Iterable<int[]> iterable(int n, int r) {
            return () -> new PermutationIndexIterator(n, r);
        }

        public static Stream<int[]> stream(int n, int r) {
            return StreamSupport.stream(iterable(n, r).spliterator(), false);
        }

//        public static Stream<int[]> stream(int[] array, int r) {
//            return stream(array.length, r).map(a -> IntStream.of(a).map(i -> array[i]).toArray());
//        }
//
//        public static Iterable<int[]> iterable(int[] array, int r) {
//            return () -> stream(array, r).iterator();
//        }

        public static <T> Stream<List<T>> stream(List<T> list, int r) {
            int size = list.size();
            return stream(size, r)
                .map(a -> {
                    List<T> perm = new ArrayList<>(r);
                    for (int i = 0; i < r; ++i)
                        perm.add(list.get(a[i]));
                    return perm;
                });
//                .map(a -> IntStream.of(a)
//                    .mapToObj(i -> list.get(i))
//                    .collect(Collectors.toList()));
        }

        public static <T> Iterable<List<T>> iterable(List<T> list, int r) {
            return () -> stream(list, r).iterator();
        }
    }

    @Test
    public void testPermutationIndexIterator() {
        for (int[] e : PermutationIndexIterator.iterable(4, 2))
            System.out.println(Arrays.toString(e));
        PermutationIndexIterator.stream(4, 2)
            .forEach(a -> System.out.println(Arrays.toString(a)));
        for (List<String> e : PermutationIndexIterator.iterable(List.of("a", "b", "c", "d"), 2))
            System.out.println(e);
        PermutationIndexIterator.stream(List.of("a", "b", "c", "d"), 2)
            .forEach(list -> System.out.println(list));
    }

    /**
     * 10進数から可変進数への変換
     *
     * 可変進数は下第n桁がn進数であるような数である。
     * <pre>
     * 桁      進数  取りうる値
     * 第1桁  1進数  {0}
     * 第2桁  2進数  {0, 1}
     * 第3桁  3進数  {0, 1, 2}
     * 第4桁  4進数  {0, 1, 2, 3}
     * 第5桁  5進数  {0, 1, 2, 3, 4}
     * .....
     * <pre>
     */
    List<Integer> variableBaseNumber(int n) {
        LinkedList<Integer> result = new LinkedList<>();
        for (int b = 1; n > 0; n /= b++)
            result.add(n % b);
        Collections.reverse(result);
        return result;
    }

    @Test
    public void testVariableBaseNumber() {
        for (int i = 0; i <= 24; ++i)
            System.out.println(variableBaseNumber(i));
    }

    static int VBN(final int n) {
        int size = 1;
        for (int i = 2; i <= n; ++i)
            size *= i;
//        int size = IntStream.rangeClosed(1, n).reduce((a, b) -> a * b).getAsInt();
        int[] digits = new int[n];
        for (int i = 0; i < size; ++i) {
            for (int b = 1, j = n - 1, k = i; k > 0; k /= b++, --j)
                digits[j] = k % b;
            System.out.println(Arrays.toString(digits));
        }
        return size;
    }

    @Test
    public void testVBN() {
        VBN(6);
    }




}
