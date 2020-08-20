package puzzle;

import java.util.Iterator;

public class Permutation64 {

    static class IndexIterator implements Iterator<int[]> {

        final int n, r;
        final long[] available, rest;
        final int[] selected;
        boolean hasNext;

        IndexIterator(int n, int r) {
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
                    long bit = resti & -resti;
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
        return new IndexIterator(n, r);
    }

    public static Iterable<int[]> iterable(int n, int r) {
        return () -> iterator(n, r);
    }
}
