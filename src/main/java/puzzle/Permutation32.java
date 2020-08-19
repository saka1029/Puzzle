package puzzle;

import java.util.Iterator;

public class Permutation32 {

    static class IndexIterator implements Iterator<Integer> {

        final int n, r;
        final int[] available, rest;
        final int[] selected;
        boolean hasNext;

        IndexIterator(int n, int r) {
            this.n = n;
            this.r = r;
            this.available = new int[n];
            this.rest = new int[n];
            this.selected = new int[r];
            int mask = n == Integer.SIZE ? -1 : (1 << n);
            available[0] = rest[0] = mask;
            hasNext = advance(0);
        }

        boolean advance(int i) {
            while (i >= 0) {

            }
            return false;
        }

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Integer next() {
            // TODO Auto-generated method stub
            return null;
        }

    }
}
