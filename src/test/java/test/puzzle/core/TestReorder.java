package test.puzzle.core;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.IntStream;

import org.junit.Test;

public class TestReorder {

    interface Reorder {

        int compare(int indexA, int indexB);

        void swap(int indexA, int indexB);

        int size();

        public default void quickSort() {
            new Object() {
                int partition(int begin, int end) {
                    int pivotIndex = end;
                    int i = (begin - 1);
                    for (int j = begin; j < end; j++)
                        if (compare(j, pivotIndex) <= 0)
                            swap(++i, j);
                    swap(i + 1, end);
                    return i + 1;
                }

                void sort(int begin, int end) {
                    if (begin >= end)
                        return;
                    int partitionIndex = partition(begin, end);
                    sort(begin, partitionIndex - 1);
                    sort(partitionIndex + 1, end);
                }
            }.sort(0, size() - 1);
        }

        public default void insertionSort() {
            int end = size();
            for (int i = 1, n = end; i < n; i++)
                for (int j = i; j > 0 && compare(j - 1, j) > 0; --j)
                    swap(j - 1, j);
        }

        public default void bubbleSort() {
            int end = size();
            boolean swapped;
            do {
                swapped = false;
                for (int i = 1; i < end; ++i) {
                    if (compare(i - 1, i) > 0) {
                        swap(i - 1, i);
                        swapped = true;
                    }
                }
            } while (swapped);
        }

        public default void heapSort() {
            int end = size();
            new Object() {

                void heapify(int n, int i) {
                    int largest = i; // Initialize largest as root
                    int l = 2 * i + 1; // left = 2*i + 1
                    int r = 2 * i + 2; // right = 2*i + 2
                    if (l < n && compare(l, largest) > 0)
                        largest = l;
                    if (r < n && compare(r, largest) > 0)
                        largest = r;
                    if (largest != i) {
                        swap(i, largest);
                        heapify(n, largest);
                    }
                }

                void sort() {
                    for (int i = end / 2 - 1; i >= 0; i--)
                        heapify(end, i);
                    for (int i = end - 1; i > 0; i--) {
                        swap(0, i);
                        heapify(i, 0);
                    }
                }
            }.sort();
        }

        public default void reverse() {
            for (int i = 0, j = size() - 1; i < j; ++i, --j)
                swap(i, j);
        }

        public default void shuffle() {
            Random random = new Random();
            int end = size();
            for (int i = end - 1; i > 0; --i) {
                int j = random.nextInt(i + 1);
                swap(i, j);
            }
        }

        public default Reorder reverseOrder() {
            final Reorder origin = this;
            return new Reorder() {

                @Override
                public int compare(int indexA, int indexB) {
                    return -origin.compare(indexA, indexB);
                }

                @Override
                public void swap(int indexA, int indexB) {
                    origin.swap(indexA, indexB);
                }

                @Override
                public int size() {
                    return origin.size();
                }

            };
        }

        public default Reorder subset(int offset, int size) {
            final Reorder origin = this;
            if (offset < 0 || offset > origin.size())
                throw new IllegalArgumentException("offset");
            if (size < 0 || offset + size > origin.size())
                throw new IllegalArgumentException("size");
            return new Reorder() {

                @Override
                public int compare(int indexA, int indexB) {
                    return origin.compare(indexA + offset, indexB + offset);
                }

                @Override
                public void swap(int indexA, int indexB) {
                    origin.swap(indexA + offset, indexB + offset);
                }

                @Override
                public int size() {
                    return size;
                }

            };
        }

        public static Reorder of(int[] array) {
            return new Reorder() {

                @Override
                public int compare(int indexA, int indexB) {
                    return Integer.compare(array[indexA], array[indexB]);
                }

                @Override
                public void swap(int indexA, int indexB) {
                    int temp = array[indexA];
                    array[indexA] = array[indexB];
                    array[indexB] = temp;
                }

                @Override
                public int size() {
                    return array.length;
                }
            };
        }
        
        public static <T> Reorder of(T[] array, Comparator<T> comparator) {
            return new Reorder() {

                @Override
                public int compare(int indexA, int indexB) {
                    return comparator.compare(array[indexA], array[indexB]);
                }

                @Override
                public void swap(int indexA, int indexB) {
                    T temp = array[indexA];
                    array[indexA] = array[indexB];
                    array[indexB] = temp;
                }

                @Override
                public int size() {
                    return array.length;
                }
                
            };
        }

        public static <T extends Comparable<T>> Reorder of(T[] array) {
            return of(array, Comparator.naturalOrder());
        }

        public static <T> Reorder of(List<T> list, Comparator<T> comparator) {
            return new Reorder() {

                @Override
                public int compare(int indexA, int indexB) {
                    return comparator.compare(list.get(indexA), list.get(indexB));
                }

                @Override
                public void swap(int indexA, int indexB) {
                    Collections.swap(list, indexA, indexB);
                }

                @Override
                public int size() {
                    return list.size();
                }

            };
        }

        public static <T extends Comparable<T>> Reorder of(List<T> list) {
            return of(list, Comparator.naturalOrder());
        }
    }

    @Test
    public void testQuickSort() {
        int[] a = {5, 3, 2, 1, 4, 0};
        Reorder.of(a).quickSort();
        assertArrayEquals(new int[] {0, 1, 2, 3, 4, 5}, a);
        List<String> s = Arrays.asList("b", "f", "c", "a", "d", "e");
        Reorder.of(s).quickSort();
        assertEquals(List.of("a", "b", "c", "d", "e", "f"), s);
    }

    @Test
    public void testInsertionSort() {
        int[] a = {5, 3, 2, 1, 4, 0};
        Reorder.of(a).insertionSort();
        assertArrayEquals(new int[] {0, 1, 2, 3, 4, 5}, a);
        List<String> s = Arrays.asList("b", "f", "c", "a", "d", "e");
        Reorder.of(s).insertionSort();
        assertEquals(List.of("a", "b", "c", "d", "e", "f"), s);
    }

    @Test
    public void testBubbleSort() {
        int[] a = {5, 3, 2, 1, 4, 0};
        Reorder.of(a).bubbleSort();
        assertArrayEquals(new int[] {0, 1, 2, 3, 4, 5}, a);
        List<String> s = Arrays.asList("b", "f", "c", "a", "d", "e");
        Reorder.of(s).bubbleSort();
        assertEquals(List.of("a", "b", "c", "d", "e", "f"), s);
    }

    @Test
    public void testHeapSort() {
        int[] a = {5, 3, 2, 1, 4, 0};
        Reorder.of(a).heapSort();
        assertArrayEquals(new int[] {0, 1, 2, 3, 4, 5}, a);
        List<String> s = Arrays.asList("b", "f", "c", "a", "d", "e");
        Reorder.of(s).heapSort();
        assertEquals(List.of("a", "b", "c", "d", "e", "f"), s);
    }

    @Test
    public void testReverseOrder() {
        int[] a = {5, 3, 2, 1, 4, 0};
        Reorder.of(a).reverseOrder().quickSort();
        assertArrayEquals(new int[] {5, 4, 3, 2, 1, 0}, a);
    }

    @Test
    public void testReverse() {
        int[] a = {0, 1, 2, 3, 4};
        Reorder.of(a).reverse();
        assertArrayEquals(new int[] {4, 3, 2, 1, 0}, a);
    }

    @Test
    public void testShuffle() {
        List<Integer> org = Arrays.asList(0, 1, 2, 3, 4);
        Map<List<Integer>, Integer> hist = new HashMap<>();
        int permutation = IntStream.range(1, org.size() + 1).reduce(1, (a, b) -> a * b);
        int average = 100;
        int count = permutation * average;
        for (int i = 0; i < count; ++i) {
            List<Integer> list = new ArrayList<>(org);
            Reorder.of(list).shuffle();
            hist.compute(list, (k, v) -> v == null ? 1 : v + 1);
        }
        int min = average / 2, max = average + min;
        for (var e : hist.entrySet()) {
//            System.out.println(e);
            assertTrue(e.getValue() >= min && e.getValue() <= max);
        }
        assertEquals(120, hist.size());
    }

    @Test
    public void testIntArray() {
        int[] a = {0, 1, 2, 3, 4};
        Reorder.of(a).reverse();
        assertArrayEquals(new int[] {4, 3, 2, 1, 0}, a);
    }

    @Test
    public void testStringArray() {
        String[] array = {"b", "f", "c", "a", "d", "e"};
        Reorder.of(array).quickSort();
        assertArrayEquals(new String[] {"a", "b", "c", "d", "e", "f"}, array);
    }

    @Test
    public void testTArray() {
        record P(int id, String name) {}
        Comparator<P> comparator = Comparator.comparing(P::id).thenComparing(P::name);
        P[] array = { new P(2, "a"), new P(1, "c"), new P(2, "b")};
        Reorder.of(array, comparator).quickSort();
        assertArrayEquals(new P[] {new P(1, "c"), new P(2, "a"), new P(2, "b")}, array);
    }

    @Test
    public void testIntegerList() {
        List<Integer> a = Arrays.asList(0, 1, 2, 3, 4);
        Reorder.of(a).reverse();
        assertEquals(List.of(4, 3, 2, 1, 0), a);
    }

}
