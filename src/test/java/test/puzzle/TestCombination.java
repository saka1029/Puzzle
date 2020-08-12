package test.puzzle;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.junit.Test;

import puzzle.Combination;

public class TestCombination {

    static final List<String> input0 = Collections.emptyList();
    static final Set<List<String>> output0_0 = Set.of(
        Collections.emptyList());

    static final List<String> input1 = List.of("a", "b", "c", "d");
    static final Set<List<String>> output1_0 = Set.of(
        Collections.emptyList());
    static final Set<List<String>> output1_1 = Set.of(
        List.of("a"),
        List.of("b"),
        List.of("c"),
        List.of("d"));
    static final Set<List<String>> output1_2 = Set.of(
        List.of("a", "b"),
        List.of("a", "c"),
        List.of("a", "d"),
        List.of("b", "c"),
        List.of("b", "d"),
        List.of("c", "d"));
    static final Set<List<String>> output1_3 = Set.of(
        List.of("a", "b", "c"),
        List.of("a", "b", "d"),
        List.of("a", "c", "d"),
        List.of("b", "c", "d"));
    static final Set<List<String>> output1_4 = Set.of(
        List.of("a", "b", "c", "d"));

    static final List<Integer> input2 = List.of(1, 2, 2);
    static final Set<List<Integer>> output2_0 = Set.of(
        Collections.emptyList());
    static final Set<List<Integer>> output2_1 = Set.of(
        List.of(1),
        List.of(2));
    static final Set<List<Integer>> output2_2 = Set.of(
        List.of(1, 2),
        List.of(2, 2));
    static final Set<List<Integer>> output2_3 = Set.of(
        List.of(1, 2, 2));

    static <T> Set<List<T>> callback(List<T> list, int n) {
        Set<List<T>> result = new HashSet<>();
        Combination.callback(list, n, e -> result.add(e));
        return result;
    }

    @Test
    public void testCallback() {
        assertEquals(output0_0, callback(input0, 0));
        assertEquals(output1_0, callback(input1, 0));
        assertEquals(output1_1, callback(input1, 1));
        assertEquals(output1_2, callback(input1, 2));
        assertEquals(output1_3, callback(input1, 3));
        assertEquals(output1_4, callback(input1, 4));
        assertEquals(output2_0, callback(input2, 0));
        assertEquals(output2_1, callback(input2, 1));
        assertEquals(output2_2, callback(input2, 2));
        assertEquals(output2_3, callback(input2, 3));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCallbackError() {
        callback(input1, -1);
    }

    static <T> Set<List<T>> iterator(List<T> list, int n) {
        Set<List<T>> result = new HashSet<>();
        for (Iterator<List<T>> i = Combination.iterator(list, n); i.hasNext();)
            result.add(i.next());
        return result;
    }

    @Test
    public void testIterator() {
        assertEquals(output0_0, iterator(input0, 0));
        assertEquals(output1_0, iterator(input1, 0));
        assertEquals(output1_1, iterator(input1, 1));
        assertEquals(output1_2, iterator(input1, 2));
        assertEquals(output1_3, iterator(input1, 3));
        assertEquals(output1_4, iterator(input1, 4));
        assertEquals(output2_0, iterator(input2, 0));
        assertEquals(output2_1, iterator(input2, 1));
        assertEquals(output2_2, iterator(input2, 2));
        assertEquals(output2_3, iterator(input2, 3));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIteratorError() {
        iterator(input1, -1);
    }

    static <T> Set<List<T>> iterable(List<T> list, int n) {
        Set<List<T>> result = new HashSet<>();
        for (List<T> e : Combination.iterable(list, n))
            result.add(e);
        return result;
    }

    @Test
    public void testIterable() {
        assertEquals(output0_0, iterable(input0, 0));
        assertEquals(output1_0, iterable(input1, 0));
        assertEquals(output1_1, iterable(input1, 1));
        assertEquals(output1_2, iterable(input1, 2));
        assertEquals(output1_3, iterable(input1, 3));
        assertEquals(output1_4, iterable(input1, 4));
        assertEquals(output2_0, iterable(input2, 0));
        assertEquals(output2_1, iterable(input2, 1));
        assertEquals(output2_2, iterable(input2, 2));
        assertEquals(output2_3, iterable(input2, 3));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIterableError() {
        iterable(input1, -1);
    }
    
    static class CombinationIterator implements Iterator<int[]> {
        
        final int n, r;
        final int[] selection;
        boolean hasNext;
        
        public CombinationIterator(int n, int r) {
            if (n < 0) throw new IllegalArgumentException("n must be >= 0");
            if (r < 0) throw new IllegalArgumentException("r must be >= 0");
            if (r > n) throw new IllegalArgumentException("r must be <= n");
            this.n = n;
            this.r = r;
            this.selection = IntStream.range(0, r).toArray();
            this.hasNext = true;
        }
        
        boolean advance() {
            for (int i = r - 1; i >= 0; )
                if (++selection[i] >= n)
                    --i;
                else if (i + 1 < r)
                    selection[i + 1] = selection[i++];
                else
                    return true;
            return false;
        }

        @Override
        public boolean hasNext() {
            return hasNext;
        }

        @Override
        public int[] next() {
            int[] result = selection.clone();
            hasNext = advance();
            return result;
        }

    }
    
    static Iterable<int[]> iterable(int n, int r) {
        return () -> new CombinationIterator(n, r);
    }

    static Stream<int[]> stream(int n, int r) {
        return StreamSupport.stream(iterable(n, r).spliterator(), false);
    }

    
    @Test
    public void testCombinationIterator() {
//        for (int[] a : iterable(5, 3))
//            System.out.println(Arrays.toString(a));
        stream(5, 3)
            .forEach(a -> System.out.println(Arrays.toString(a)));
    }

}
