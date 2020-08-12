package puzzle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
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

    /**
     * 辞書的順序生成アルゴリズムを改善した順列生成Iteratorです。
     * n個のなかからr個選ぶ順列の生成ができます。
     */
    static class IndexIterator implements Iterator<int[]> {

        final int n, r;
        final int[] array;
        boolean hasNext = true;

        IndexIterator(int n, int r) {
            if (r > n)
                throw new IllegalArgumentException("r must be <= n");
            this.n = n;
            this.r = r;
            this.array = new int[n];
            for (int i = 0; i < n; ++i)
                array[i] = i;
        }

        @Override
        public boolean hasNext() {
            return hasNext;
        }

        boolean advance() {
            for (int i = r - 1; i >= 0; --i) {
                int ai = array[i];
                int m = -1;
                for (int j = i + 1, min = Integer.MAX_VALUE; j < n; ++j) {
                    int aj = array[j];
                    if (aj > ai && aj < min) {
                        m = j;
                        min = aj;
                    }
                }
                if (m >= 0) {
                    swap(array, i, m);
                    Arrays.sort(array, i + 1, n);
                    return true;
                }
            }
            return false;

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
        return () -> stream(array.length, r)
            .map(a -> {
                int[] sub = new int[r];
                for (int i = 0; i < r; ++i)
                    sub[i] = array[a[i]];
                return sub;
            }).iterator();
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
