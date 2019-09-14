package puzzle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Permutation {

    private Permutation() {
    }

    static <T> Map<T, Integer> histogram(List<T> list) {
        Map<T, Integer> result = new HashMap<>(list.size());
        for (T e : list)
            result.compute(e, (k, v) -> v == null ? 1 : v + 1);
        return result;
    }

//    static <T> List<T> cons(T e, List<T> list) {
//        List<T> r = new LinkedList<>();
//        r.add(e);
//        r.addAll(list);
//        return r;
//    }
//
//    static <T> List<T> remove(List<T> list, T e) {
//        List<T> r = new LinkedList<>(list);
//        r.remove(e);
//        return r;
//    }
//
//    public static <T> Stream<List<T>> permutation(List<T> list) {
//        return permutation(list, list.size());
//    }
//
//    public static <T> Stream<List<T>> permutation(List<T> list, int n) {
//        if (n <= 0)
//            return Stream.of(Collections.emptyList());
//        return list.stream()
//            .flatMap(h -> permutation(remove(list, h), n - 1)
//                .map(t -> cons(h, t)));
//    }

    private static <T> void callback(Map<T, Integer> histogram, int n, T[] selected, Consumer<List<T>> consumer, int index) {
        if (index >= n)
            consumer.accept(List.of(selected));
        else
            for (T element : histogram.keySet()) {
                int count = histogram.get(element);
                if (count <= 0) continue;
                selected[index] = element;
                histogram.put(element, count - 1);
                callback(histogram, n, selected, consumer, index + 1);
                histogram.put(element, count);
            }
    }

    public static <T> void callback(List<T> list, int n, Consumer<List<T>> callback) {
        if (n < 0) throw new IllegalArgumentException("n must be >= 0");
        int size = list.size();
        if (n > size) return;
        Map<T, Integer> histogram = histogram(list);
        @SuppressWarnings("unchecked")
        T[] selected = (T[]) new Object[n];
        callback(histogram, n, selected, callback, 0);
    }

    public static <T> void callback(List<T> list, Consumer<List<T>> callback) {
        callback(list, list.size(), callback);
    }

    static class PermutationIterator<T> implements Iterator<List<T>> {
        final int n;
        final Map<T, Integer> histogram;
        final Set<T> elements;
        final int[] backup;
        final T[] selected;
        final Iterator<T>[] iterators;
        boolean hasNext = true;

        @SuppressWarnings("unchecked")
        PermutationIterator(List<T> list, int n) {
            if (n < 0) throw new IllegalArgumentException("n must be >= 0");
            this.n = n;
            this.histogram = histogram(list);
            this.elements = new HashSet<>(histogram.keySet());
            this.backup = new int[n];
            this.selected = (T[])new Object[n];
            this.iterators = (Iterator<T>[])new Iterator[n];
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
