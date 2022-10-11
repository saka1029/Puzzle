package test.puzzle.core;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.junit.Test;

import puzzle.core.ArrayReorder;

public class TestArrayReorder {


    @Test
    public void testQuickSort() {
        int[] a = {5, 3, 2, 1, 4, 0};
        ArrayReorder.of(a).quickSort();
        assertArrayEquals(new int[] {0, 1, 2, 3, 4, 5}, a);
        List<String> s = Arrays.asList("b", "f", "c", "a", "d", "e");
        ArrayReorder.of(s).quickSort();
        assertEquals(List.of("a", "b", "c", "d", "e", "f"), s);
    }

    @Test
    public void testInsertionSort() {
        int[] a = {5, 3, 2, 1, 4, 0};
        ArrayReorder.of(a).insertionSort();
        assertArrayEquals(new int[] {0, 1, 2, 3, 4, 5}, a);
        List<String> s = Arrays.asList("b", "f", "c", "a", "d", "e");
        ArrayReorder.of(s).insertionSort();
        assertEquals(List.of("a", "b", "c", "d", "e", "f"), s);
    }

    @Test
    public void testBubbleSort() {
        int[] a = {5, 3, 2, 1, 4, 0};
        ArrayReorder.of(a).bubbleSort();
        assertArrayEquals(new int[] {0, 1, 2, 3, 4, 5}, a);
        List<String> s = Arrays.asList("b", "f", "c", "a", "d", "e");
        ArrayReorder.of(s).bubbleSort();
        assertEquals(List.of("a", "b", "c", "d", "e", "f"), s);
    }

    @Test
    public void testHeapSort() {
        int[] a = {5, 3, 2, 1, 4, 0};
        ArrayReorder.of(a).heapSort();
        assertArrayEquals(new int[] {0, 1, 2, 3, 4, 5}, a);
        List<String> s = Arrays.asList("b", "f", "c", "a", "d", "e");
        ArrayReorder.of(s).heapSort();
        assertEquals(List.of("a", "b", "c", "d", "e", "f"), s);
    }

    @Test
    public void testReverseOrder() {
        int[] a = {5, 3, 2, 1, 4, 0};
        ArrayReorder.of(a).reverseOrder().quickSort();
        assertArrayEquals(new int[] {5, 4, 3, 2, 1, 0}, a);
    }

    @Test
    public void testReverse() {
        int[] a = {0, 1, 2, 3, 4};
        ArrayReorder.of(a).reverse();
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
            ArrayReorder.of(list).shuffle();
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
    public void testNextPermutation() {
        int[] a = {0, 1, 2, 3, 4};
        assertTrue(ArrayReorder.of(a).nextPermutation());
        assertArrayEquals(new int[] {0, 1, 2, 4, 3}, a);
        assertTrue(ArrayReorder.of(a).nextPermutation());
        assertArrayEquals(new int[] {0, 1, 3, 2, 4}, a);
        assertTrue(ArrayReorder.of(a).nextPermutation());
        assertArrayEquals(new int[] {0, 1, 3, 4, 2}, a);
        assertTrue(ArrayReorder.of(a).nextPermutation());
        assertArrayEquals(new int[] {0, 1, 4, 2, 3}, a);
        int[] b = {4, 3, 2, 1, 0};
        assertFalse(ArrayReorder.of(b).nextPermutation());
    }

    @Test
    public void testIntArray() {
        int[] a = {0, 1, 2, 3, 4};
        ArrayReorder.of(a).reverse();
        assertArrayEquals(new int[] {4, 3, 2, 1, 0}, a);
    }

    @Test
    public void testStringArray() {
        String[] array = {"b", "f", "c", "a", "d", "e"};
        ArrayReorder.of(array).quickSort();
        assertArrayEquals(new String[] {"a", "b", "c", "d", "e", "f"}, array);
    }

    @Test
    public void testTArray() {
        record P(int id, String name) implements Comparable<P> {
            static Comparator<P> comparator = Comparator.comparing(P::id).thenComparing(P::name);
            @Override public int compareTo(P o) { return comparator.compare(this, o); }
        }
        P[] array = { new P(2, "a"), new P(1, "c"), new P(2, "b")};
        ArrayReorder.of(array).quickSort();
        assertArrayEquals(new P[] {new P(1, "c"), new P(2, "a"), new P(2, "b")}, array);
    }

    @Test
    public void testTArrayWithComparator() {
        record P(int id, String name) {}
        Comparator<P> comparator = Comparator.comparing(P::id).thenComparing(P::name);
        P[] array = { new P(2, "a"), new P(1, "c"), new P(2, "b")};
        ArrayReorder.of(array, comparator).quickSort();
        assertArrayEquals(new P[] {new P(1, "c"), new P(2, "a"), new P(2, "b")}, array);
    }

    @Test
    public void testIntegerList() {
        List<Integer> a = Arrays.asList(0, 1, 2, 3, 4);
        ArrayReorder.of(a).reverse();
        assertEquals(List.of(4, 3, 2, 1, 0), a);
    }
    
    @Test
    public void testSubset() {
        int[] a = {0, 1, 2, 3, 4, 5, 6};
        ArrayReorder.of(a).subset(3, 3).reverse();
        assertArrayEquals(new int[] {0, 1, 2, 5, 4, 3, 6}, a);
        ArrayReorder.of(a).subset(2, 3).quickSort();
        assertArrayEquals(new int[] {0, 1, 2, 4, 5, 3, 6}, a);
    }
    
    @Test
    public void testTraceReverse() {
        int[] array = {4, 3, 2, 1, 0};
        ArrayReorder re = ArrayReorder.of(array);
        List<List<Integer>> swapTrace = new ArrayList<>();
        ArrayReorder.Trace2i compare = (a, b) -> { throw new RuntimeException(); };
        ArrayReorder.Trace2i swap = (a, b) -> swapTrace.add(List.of(a, b));
        re.trace(compare, swap).reverse();
        assertArrayEquals(new int[] {0, 1, 2, 3, 4}, array);
        assertEquals(List.of(List.of(0, 4), List.of(1, 3)), swapTrace);
    }
    
    @Test
    public void testTraceQuickSort() {
        int[] array = {5, 4, 3, 2, 1, 0};
        List<List<Integer>> swapHistory = new ArrayList<>();
        ArrayReorder.of(array).trace(
            (a, b) -> System.out.printf("compare %d %d%n", a, b),
            (a, b) -> {
                System.out.printf("swap %d %d%n", a, b);
                swapHistory.add(List.of(a, b));
            }).quickSort();
        assertEquals(List.of(List.of(0, 5), List.of(1, 4), List.of(2, 3)), swapHistory);
    }
}