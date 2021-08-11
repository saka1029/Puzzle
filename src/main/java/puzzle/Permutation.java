package puzzle;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Permutation {
    
    private Permutation() {
    }
    
    /**
     * nPr（n個からr個取り出す順列の総数）を計算します。
     * @param n 全体の要素数を指定します。
     * @param r 取り出す要素数を指定します。
     * @return n個からr個取り出す順列の総数を返します。
     */
    public static int count(int n, int r) {
        int permutation = 1;
        for (; r > 0; --r, --n)
            permutation *= n;
        return permutation;
    }

    static class PermutationIterator32 implements Iterator<int[]> {

        final int n, r;
        final int[] available, rest;
        final int[] selected;
        boolean hasNext;

        PermutationIterator32(int n, int r) {
            if (n < 0) throw new IllegalArgumentException("n must be >= 0");
            if (r < 0) throw new IllegalArgumentException("r must be >= 0");
            if (r > n) throw new IllegalArgumentException("r must be <= n");
            if (n > Integer.SIZE) throw new IllegalArgumentException("n must be <= " + Integer.SIZE);
            this.n = n;
            this.r = r;
            this.available = new int[r];
            this.rest = new int[r];
            this.selected = new int[r];
            int allOne = n == Integer.SIZE ? -1 : (1 << n) - 1;
            if (r == 0)
                hasNext = true;
            else {
                available[0] = rest[0] = allOne;
                hasNext = advance(0);
            }
        }

        boolean advance(int i) {
            while (i >= 0) {
                int resti = rest[i];
                if (resti == 0)
                    --i;
                else {
                    int bit = resti & -resti;
                    rest[i] ^= bit;
                    selected[i] = Integer.numberOfTrailingZeros(bit);
                    if (++i >= r) return true;
                    available[i] = rest[i] = available[i - 1] ^ bit;
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
            int[] result = selected.clone();
            hasNext = advance(r - 1);
            return result;
        }
    }
    
    static class PermutationIterator64 implements Iterator<int[]> {

        final int n, r;
        final long[] available, rest;
        final int[] selected;
        boolean hasNext;

        PermutationIterator64(int n, int r) {
            if (n < 0) throw new IllegalArgumentException("n must be >= 0");
            if (r < 0) throw new IllegalArgumentException("r must be >= 0");
            if (r > n) throw new IllegalArgumentException("r must be <= n");
            if (n > Long.SIZE) throw new IllegalArgumentException("n must be <= " + Long.SIZE);
            this.n = n;
            this.r = r;
            this.available = new long[r];
            this.rest = new long[r];
            this.selected = new int[r];
            long allOne = n == Long.SIZE ? -1L : (1L << n) - 1L;
            if (r == 0)
                hasNext = true;
            else {
                available[0] = rest[0] = allOne;
                hasNext = advance(0);
            }
        }

        boolean advance(int i) {
            while (i >= 0) {
                long resti = rest[i];
                if (resti == 0)
                    --i;
                else {
                    long bit = resti & -resti;   // bit = Long.lowestOneBit(resti);
                    rest[i] ^= bit;
                    selected[i] = Long.numberOfTrailingZeros(bit);
                    if (++i >= r) return true;
                    available[i] = rest[i] = available[i - 1] ^ bit;
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
            int[] result = selected.clone();
            hasNext = advance(r - 1);
            return result;
        }
    }
    
    public static Iterator<int[]> iterator(int n, int r) {
        if (n <= Integer.SIZE)
            return new PermutationIterator32(n, r);
        else if (n <= Long.SIZE)
            return new PermutationIterator64(n, r);
        else
            throw new IllegalArgumentException("n must b <= " + Long.SIZE);
    }
    
    public static Iterable<int[]> iterable(int n, int r) {
        return () -> iterator(n, r);
    }
    
    public static Stream<int[]> stream(int n, int r) {
        return StreamSupport.stream(iterable(n, r).spliterator(), false);
    }
    
    public static Iterator<int[]> iterator(int[] array, int r) {
        return stream(array, r).iterator();
    }
    
    public static Iterable<int[]> iterable(int[] array, int r) {
        return () -> iterator(array, r);
    }

    public static Stream<int[]> stream(int[] array, int r) {
        return stream(array.length, r)
            .map(a -> IntStream.of(a)
                .map(i -> array[i])
                .toArray());
    }
    
    public static <T> Iterator<T[]> iterator(T[] array, int r) {
        return stream(array, r).iterator();
    }

    public static <T> Iterable<T[]> iterable(T[] array, int r) {
        return () -> iterator(array, r);
    }
    
    public static <T> Stream<T[]> stream(T[] array, int r) {
        int n = array.length;
        return stream(IntStream.range(0, n).toArray(), r)
            .map(a -> IntStream.of(a)
                .mapToObj(i -> array[i])
                .toArray(size -> Arrays.copyOf(array, size)));
    }
    
}
