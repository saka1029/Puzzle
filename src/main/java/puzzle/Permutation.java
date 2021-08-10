package puzzle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.IntBinaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Permutation {

    private Permutation() {
    }

    static void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    static <T> void swap(T[] array, int i, int j) {
        T temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    static void reverse(int[] array, int i, int j) {
        while (i < j)
            swap(array, i++, j--);
    }

    static <T> void reverse(T[] array, int i, int j) {
        while (i < j)
            swap(array, i++, j--);
    }

    public static boolean next(int[] array) {
        return next(array, Integer::compare);
//        int length = array.length;
//        int i = length - 2;
//        while (i >= 0 && array[i] >= array[i + 1])
//            --i;
//        if (i < 0)
//            return false;
//        int j = array.length - 1;
//        while (array[i] >= array[j])
//            --j;
//        swap(array, i, j);
//        reverse(array, i + 1, length - 1);
//        return true;
    }

    public static boolean next(int[] array, IntBinaryOperator comparator) {
        int length = array.length;
        int i = length - 2;
        while (i >= 0 && comparator.applyAsInt(array[i], array[i + 1]) >= 0)
            --i;
        if (i < 0)
            return false;
        int j = array.length - 1;
        while (comparator.applyAsInt(array[i], array[j]) >= 0)
            --j;
        swap(array, i, j);
        reverse(array, i + 1, length - 1);
        return true;
    }

    public static <T extends Comparable<T>> boolean next(T[] array) {
        return next(array, Comparator.naturalOrder());
//        int length = array.length;
//        int i = length - 2;
//        while (i >= 0 && array[i].compareTo(array[i + 1]) >= 0)
//            --i;
//        if (i < 0)
//            return false;
//        int j = array.length - 1;
//        while (array[i].compareTo(array[j]) >= 0)
//            --j;
//        swap(array, i, j);
//        reverse(array, i + 1, length - 1);
//        return true;
    }

    public static <T> boolean next(T[] array, Comparator<T> comparator) {
        int length = array.length;
        int i = length - 2;
        while (i >= 0 && comparator.compare(array[i], array[i + 1]) >= 0)
            --i;
        if (i < 0)
            return false;
        int j = array.length - 1;
        while (comparator.compare(array[i], array[j]) >= 0)
            --j;
        swap(array, i, j);
        reverse(array, i + 1, length - 1);
        return true;
    }

    /**
     * 辞書的順序生成アルゴリズムを改善した順列生成Iteratorです。
     * n個の中からr個選ぶ順列の生成ができます。
     */
    static class IndexIterator implements Iterator<int[]> {

        final int n, r;
        final int[] array;
        boolean hasNext = true;

        IndexIterator(int[] array, int r) {
            if (r > array.length)
                throw new IllegalArgumentException("array length must be <= n");
            this.n = array.length;
            this.r = r;
            this.array = array.clone();
        }

        IndexIterator(int n, int r) {
            this(IntStream.range(0, n).toArray(), r);
        }

        void swap(int i, int j) {
            int temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }

        boolean advance() {
            for (int i = r - 1; i >= 0; --i) {
                int ai = array[i];
                int m = -1;
                for (int j = i + 1, am = 0; j < n; ++j) {
                    int aj = array[j];
                    if (ai < aj && (m == -1 || aj < am)) {
                        m = j;
                        am = aj;
                    }
                }
                if (m >= 0) {
                    swap(i, m);
                    Arrays.sort(array, i + 1, n);
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
            int[] result = Arrays.copyOf(array, r);
            hasNext = advance();
            return result;
        }
    }

    public static Iterable<int[]> iterable(int n, int r) {
        return () -> new IndexIterator(n, r);
    }

    public static Stream<int[]> stream(int n, int r) {
        return StreamSupport.stream(iterable(n, r).spliterator(), false);
    }

    public static Iterable<int[]> iterable(int[] array, int r) {
        return () -> new IndexIterator(array, r);
    }
//    public static Iterable<int[]> iterable(int[] array, int r) {
//        return () -> stream(array.length, r)
//            .map(a -> {
//                int[] sub = new int[r];
//                for (int i = 0; i < r; ++i)
//                    sub[i] = array[a[i]];
//                return sub;
//            }).iterator();
//    }

    public static Stream<int[]> stream(int[] array, int r) {
        return StreamSupport.stream(iterable(array, r).spliterator(), false);
    }

    public static <T> Stream<T[]> stream(T[] array, int r) {
        return stream(array.length, r)
            .map(a -> {
                T[] sub = Arrays.copyOf(array, r);
                for (int i = 0; i < r; ++i)
                    sub[i] = array[a[i]];
                return sub;
            });
    }

    public static <T> Iterable<T[]> iterable(T[] array, int r) {
        return () -> stream(array, r).iterator();
    }

    public static <T> Stream<List<T>> stream(List<T> list, int r) {
        return stream(list.size(), r)
            .map(a -> {
                List<T> sub = new ArrayList<>();
                for (int i : a)
                    sub.add(list.get(i));
                return sub;
            });
    }

    public static <T> Iterable<List<T>> iterable(List<T> list, int r) {
        return () -> stream(list, r).iterator();
    }

}
