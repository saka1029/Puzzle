package scrap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class PermutationR {

    private PermutationR() {}

    /**
     * 再帰呼び出しによる順列の生成をIteratorとして書き換えたもの。
     * もとのプログラムは以下のとおり。
     *
     * <pre>
     * <code>
     *     static void permutation(int n, int r) {
     *         int[] selection = new int[r];
     *         boolean[] used = new boolean[n];
     *         new Object() {
     *
     *             void found() {
     *                 System.out.println(Arrays.toString(selection));
     *             }
     *
     *             void permutation(int index) {
     *                 if (index >= r)
     *                     found();
     *                 else
     *                     for (int i = 0; i < n; ++i) {
     *                         if (used[i]) continue;
     *                         used[i] = true;
     *                         selection[index] = i;
     *                         permutation(index + 1);
     *                         used[i] = false;
     *                     }
     *             }
     *
     *         }.permutation(0);
     *     }
     * </code>
     * </pre>
     *
     *
     */
    static class IndexIterator implements Iterator<int[]> {

        final int n, r;
        final int[] selection;
        final boolean[] used;
        boolean hasNext;

        IndexIterator(int n, int r) {
            if (n < 0) throw new IllegalArgumentException("n must be > 0");
            if (r < 0) throw new IllegalArgumentException("r must be > 0");
            if (r > n) throw new IllegalArgumentException("r must be <= n");
            this.n = n;
            this.r = r;
            this.selection = new int[r];
            this.used = new boolean[n];
            for (int i = 0; i < r; ++i) {
                selection[i] = i;
                used[i] = true;
            }
            this.hasNext = true;
        }

        boolean advance() {
            for (int i = r - 1; i >= 0;) {
                if (i >= r)
                    return true;
                int si = selection[i];
                int j = si + 1;
                while (j < n && used[j])
                    ++j;
                if (si >= 0)
                    used[si] = false;
                if (j >= n)
                    --i;
                else {
                    selection[i] = j;
                    used[j] = true;
                    if (++i < r)
                        selection[i] = -1;
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
            int[] result = selection.clone();
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
