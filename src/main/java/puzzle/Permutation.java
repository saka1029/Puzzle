package puzzle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Permutation {

    private Permutation() {
    }

    static class Counter<T> {

        final T key;
        int count;

        Counter(Entry<T, Integer> entry) {
            this.key = entry.getKey();
            this.count = entry.getValue();
        }
    }

    static <T> Map<T, Integer> histogram(List<T> list) {
        Map<T, Integer> result = new LinkedHashMap<>(list.size());
        for (T e : list)
            result.compute(e, (k, v) -> v == null ? 1 : v + 1);
        return result;
    }

    @SuppressWarnings("unchecked")
    static <T> Counter<T>[] histogramArray(Map<T, Integer> histogram) {
        return histogram.entrySet().stream().map(Counter::new).toArray(Counter[]::new);
    }

    static <T> Counter<T>[] histogramArray(List<T> list) {
        return histogramArray(histogram(list));
    }

    private static <T> void callback(Counter<T>[] histogram, int n, T[] selected, Consumer<List<T>> consumer, int index) {
        if (index >= n)
            consumer.accept(List.of(selected));
        else
            for (Counter<T> element : histogram) {
                if (element.count <= 0) continue;
                selected[index] = element.key;
                --element.count;
                callback(histogram, n, selected, consumer, index + 1);
                ++element.count;
            }
    }

    public static <T> void callback2(List<T> list, int n, Consumer<List<T>> callback) {
        if (n < 0) throw new IllegalArgumentException("n must be >= 0");
        int size = list.size();
        if (n > size) return;
        Counter<T>[] histogram = histogramArray(list);
        @SuppressWarnings("unchecked")
        T[] selected = (T[]) new Object[n];
        callback(histogram, n, selected, callback, 0);
    }

    public static <T> void callback(List<T> list, int n, Consumer<List<T>> callback) {
        if (n < 0) throw new IllegalArgumentException("n must be >= 0");
        int size = list.size();
        if (n > size) return;
        Counter<T>[] h = histogramArray(list);
        int m = h.length;
        @SuppressWarnings("unchecked")
        T[] selected = (T[]) new Object[n];
        Deque<Integer> stack = new LinkedList<>();
        new Object() {
            void generate() {
                int index = 0;
                int i = 0;
                boolean init = true;
                L: while (true) {
                    if (index >= n) {
                        callback.accept(List.of(selected));
                    } else {
                        if (init)
                            i = 0;
                        while (i < m) {
                            if (h[i].count > 0) {
                                selected[index] = h[i].key;
                                --h[i].count;
                                stack.push(i); ++index;
                                init = true;
                                continue L;
                            }
                            ++i;
                        }
                    }
                    if (stack.isEmpty()) break;
                    i = stack.pop(); --index;
                    ++h[i].count;
                    ++i;
                    init = false;
                }
            }
        }.generate();
    }

    public static <T> void callback(List<T> list, Consumer<List<T>> callback) {
        callback(list, list.size(), callback);
    }

    static class PermutationIterator<T> implements Iterator<List<T>> {
        final int m, n;
        final Map<T, Integer> histogram;
        final Set<T> elements;
        final Iterator<T>[] iterators;
        final int[] backup;
        final T[] selected;
        final int[] indexes;
        boolean hasNext = true;

        PermutationIterator(List<T> list, int n) {
            if (n < 0) throw new IllegalArgumentException("n must be >= 0");
            this.n = n;
            this.histogram = histogram(list);
            this.m = histogram.size();
            this.elements = new HashSet<>(histogram.keySet());
            this.iterators = (Iterator<T>[]) new Iterator[n];
            this.backup = new int[n];
            this.selected = (T[])new Object[n];
            this.indexes = new int[n];
            Arrays.fill(indexes, -1);
            forward(0);
        }

        void forward(int index) {
            if (!hasNext) return;
            while (index < n) {
                Iterator<T> iterator = iterators[index];
                if (iterator == null)
                    iterators[index] = iterator = elements.iterator();
                if (iterator.hasNext()) {
                    T e = iterator.next();
                    int c = histogram.get(e);
                    if (c > 0) {
                        backup[index] = c;
                        selected[index] = e;
                        histogram.put(e, c - 1);
                        ++index;
                    }
                } else {
                    iterators[index] = null;
                    histogram.put(selected[index], backup[index]);
                    if (--index < 0) {
                        hasNext = false;
                        return;
                    }
                    histogram.put(selected[index], backup[index]);
                }
            }
        }

        @Override
        public boolean hasNext() {
            return hasNext;
        }

        @Override
        public List<T> next() {
            List<T> next = List.of(selected);
            if (n > 0) {
                histogram.put(selected[n - 1], backup[n - 1]);
                forward(n - 1);
            } else
                hasNext = false;
            return next;
        }

    }

    public static <T> Iterator<List<T>> iterator(List<T> list, int n) {
        return new PermutationIterator<>(list, n);
    }

    public static <T> Iterator<List<T>> iterator(List<T> list) {
        return iterator(list, list.size());
    }

    public static <T> Iterable<List<T>> iterable(List<T> list, int n) {
        if (n < 0) throw new IllegalArgumentException("n must be >= 0");
        return () -> iterator(list, n);
    }

    public static <T> Iterable<List<T>> iterable(List<T> list) {
        return iterable(list, list.size());
    }

    static <T> Stream<List<T>> stream(List<T> list, int n, boolean parallel) {
        if (n < 0) throw new IllegalArgumentException("n must be >= 0");
        return StreamSupport.stream(iterable(list, n).spliterator(), parallel);
    }

    public static <T> Stream<List<T>> stream(List<T> list, int n) {
        return stream(list, n, false);
    }

    public static <T> Stream<List<T>> stream(List<T> list) {
        return stream(list, list.size());
    }

    public static <T> List<T> next(List<T> list, Comparator<T> comparator) {
        int length = list.size();
        for (int i = length - 2; i >= 0; --i) {
            T left = list.get(i);
            if (comparator.compare(left, list.get(i + 1)) < 0) {
                for (int j = length - 1; true; --j) {
                    if (comparator.compare(left, list.get(j)) < 0) {
                        List<T> result = new ArrayList<>(list);
                        Collections.swap(result, i, j);
                        for (int k = i + 1, l = length - 1; k < l; ++k, --l)
                            Collections.swap(result, k, l);
                        return result;
                    }
                }
            }
        }
        return null;
    }

    public static <T extends Comparable<T>> List<T> next(List<T> list) {
        return next(list, Comparator.naturalOrder());
    }
}
