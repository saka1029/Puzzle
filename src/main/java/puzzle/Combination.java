package puzzle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Combination {

    private Combination() {}

//    @SuppressWarnings("unchecked")
//    public static <T> List<T> newFixedSizeList(int n, T initialValue) {
//        T[] array = (T[])new Object[n];
//        Arrays.fill(array, initialValue);
//        return Arrays.asList(array);
//    }
//    public static <T> List<T> newFixedSizeList(int n) {
//        return newFixedSizeList(n, null);
//    }

//    public static <T> List<Entry<T, Integer>> histogram(List<T> list) {
//        Map<T, Integer> histogram = new LinkedHashMap<>(list.size());
//        for (T e : list)
//            histogram.compute(e, (k, v) -> v == null ? 1 : v + 1);
//        return new ArrayList<>(histogram.entrySet());
//    }

    @SuppressWarnings("unchecked")
    public static <T> Entry<T, Integer>[] histogramArray(List<T> list) {
        Map<T, Integer> histogram = new LinkedHashMap<>(list.size());
        for (T e : list)
            histogram.compute(e, (k, v) -> v == null ? 1 : v + 1);
        return histogram.entrySet().toArray(Entry[]::new);
    }

    static <T> List<T> generateList(Entry<T, Integer>[] histogram, int[] selected) {
        int size = selected.length;
        List<T> list = new ArrayList<>(size);
        for (int i = 0; i < size; ++i) {
            T element = histogram[i].getKey();
            for (int j = 0, count = selected[i]; j < count; ++j)
                list.add(element);
        }
        return list;
    }

    public static <T> void callback(List<T> list, int n, Consumer<List<T>> callback) {
        if (n < 0) throw new IllegalArgumentException("n must be >= 0");
        Entry<T, Integer>[] histogram = histogramArray(list);
        int size = histogram.length;
        int[] selectedCount = new int[size];
        new Object() {
            void generate(int index, int rest) {
                if (index >= size) {
                    if (rest == 0)
                        callback.accept(generateList(histogram, selectedCount));
                } else {
                    for (int i = Math.min(histogram[index].getValue(), rest); i >= 0; --i) {
                        selectedCount[index] = i;
                        generate(index + 1, rest - i);
                    }
                }
            }
        }.generate(0, n);
    }

    static class CombinationIterator<T> implements Iterator<List<T>> {
        static final int UNDEF = Integer.MIN_VALUE;
        final int n;
        final Entry<T, Integer>[] histogram;
        final int size;
        final int[] selected;
        boolean hasNext = true;
        int rest;

        CombinationIterator(List<T> list, int n) {
            if (n < 0) throw new IllegalArgumentException("n must be >= 0");
            this.n = n;
            this.histogram = histogramArray(list);
            this.size = histogram.length;
            this.selected = new int[size];
            Arrays.fill(this.selected, UNDEF);
            this.rest = n;
            if (n > 0)
                forward(0);
        }

        void forward(int index) {
            if (!hasNext) return;
            while (true) {
                int count = selected[index];
                if (count == 0) {
                    if (index == 0) {
                        hasNext = false;
                        return;
                    }
                    selected[index--] = UNDEF;
                } else {
                    if (count == UNDEF) {
                        count = selected[index] = Math.min(histogram[index].getValue(), rest);
                        rest -= count;
                    } else {
                        --selected[index];
                        ++rest;
                    }
                    if (index + 1 >= size) {
                        if (rest == 0) return;
                    } else
                        ++index;
                }
            }
        }

        @Override
        public boolean hasNext() {
            return hasNext;
        }

        @Override
        public List<T> next() {
            if (!hasNext)
                throw new NoSuchElementException();
            List<T> r = generateList(histogram, selected);
            if (size > 0)
                forward(size - 1);
            else
                hasNext = false;
            return r;
        }
    }

    public static <T> Iterator<List<T>> iterator(List<T> list, int n) {
        if (n < 0) throw new IllegalArgumentException("n must be >= 0");
        return new CombinationIterator<>(list, n);
    }

    public static <T> Iterable<List<T>> iterable(List<T> list, int n) {
        if (n < 0) throw new IllegalArgumentException("n must be >= 0");
        return () -> iterator(list, n);
    }

    public static <T> Stream<List<T>> stream(List<T> list, int n, boolean parallel) {
        if (n < 0) throw new IllegalArgumentException("n must be >= 0");
        return StreamSupport.stream(iterable(list, n).spliterator(), parallel);
    }

    public static <T> Stream<List<T>> stream(List<T> list, int n) {
        return stream(list, n, false);
    }

}
