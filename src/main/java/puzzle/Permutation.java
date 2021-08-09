package puzzle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Permutation {

    private Permutation() {
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
