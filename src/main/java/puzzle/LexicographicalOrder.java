package puzzle;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class LexicographicalOrder {
    
    public static class IntsIterator implements Iterator<int[]> {

        final int n, r;
        final int[] array;
        boolean hasNext = true;

        public IntsIterator(int n, int r) {
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
        
        void swap(int i, int j) {
            int temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
        
        void reverse(int from, int to) {
            for (int i = from, j = to - 1; i < j; ++i, --j)
                swap(i, j);
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
                    swap(i, m);
                    reverse(i + 1, n);
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
    
    public class AppearanceOrderComparator<T> implements Comparator<T> {
        
        final T[] uniq;
        final Map<T, Integer> map;
        
        public AppearanceOrderComparator(T[] array) {
            this.uniq = Stream.of(array)
                .distinct()
                .toArray(n -> Arrays.copyOf(array, n));
            this.map = IntStream.range(0, uniq.length)
                .boxed()
                .collect(Collectors.toMap(i -> array[i], i -> i));
        }

        @Override
        public int compare(T o1, T o2) {
            return Integer.compare(map.get(o1), map.get(o2));
        }
        
        public T[] decode(int[] indexes) {
            return IntStream.of(indexes)
                .mapToObj(i -> uniq[i])
                .toArray(n -> Arrays.copyOf(uniq, n));
        }
    }


}
