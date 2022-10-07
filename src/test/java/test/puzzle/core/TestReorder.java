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

        public default void shuffle() {
            Random random = new Random();
            int end = size();
            for (int i = end - 1; i > 0; --i) {
                int j = random.nextInt(i + 1);
                swap(i, j);
            }
        }

        public default void reverse() {
            for (int i = 0, j = size() - 1; i < j; ++i, --j)
                swap(i, j);
        }

        default Reorder subset(int offset, int size) {
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
    public void testIntArray() {
        int[] a = {0, 1, 2, 3, 4};
        Reorder.of(a).reverse();
        assertArrayEquals(new int[] {4, 3, 2, 1, 0}, a);
    }

    @Test
    public void testIntList() {
        List<Integer> a = Arrays.asList(0, 1, 2, 3, 4);
        Reorder.of(a).reverse();
        assertEquals(List.of(4, 3, 2, 1, 0), a);
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

}
