package puzzle;

import java.util.Iterator;

public class Permutation32 {

    static class IndexIterator implements Iterator<int[]> {

        final int n, r;
        final int[] available, rest;
        final int[] selected;
        boolean hasNext;

        IndexIterator(int n, int r) {
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

    public static Iterator<int[]> iterator(int n, int r) {
        return new IndexIterator(n, r);
    }

    public static Iterable<int[]> iterable(int n, int r) {
        return () -> iterator(n, r);
    }
}
