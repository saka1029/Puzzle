package test.puzzle.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.junit.Test;

public class TestPermutationSwap {

    static <T> List<List<T>> permutation(List<T> list) {
        // listが更新可能でない場合があるのでコピーする。
        List<T> input = new ArrayList<>(list);
        List<List<T>> result = new ArrayList<>();
        int length = input.size();
        new Object() {
            void swap(List<T> a, int x, int y) {
                T t = a.get(x);
                a.set(x, a.get(y));
                a.set(y, t);
            }

            void solve(int index) {
                if (index >= length)
                    result.add(new ArrayList<>(input));
                else
                    for (int i = index; i < length; ++i) {
                        swap(input, index, i);
                        solve(index + 1);
                        swap(input, index, i);
                    }
            }
        }.solve(0);
        return result;
    }

    @Test
    public void testPermutation() {
        var r = permutation(List.of("A", "B", "C"));
        for (List<String> a : r)
            System.out.println(a);
    }
    
    static class SwapArray<T> implements Iterable<T> {
        final List<T> list;
        final int[] ref;

        SwapArray(List<T> list, int[] ref) {
            this.list = list;
            this.ref = ref;
        }

        public SwapArray(List<T> list) {
            this(list, IntStream.range(0, list.size()).toArray());
        }

        @SuppressWarnings("unchecked")
        public SwapArray(T... array) {
            this(List.of(array));
        }

        public int size () {
            return list.size();
        }

        public SwapArray<T> swap(int i, int j) {
            int[] nref = ref.clone();
            int t = nref[i];
            nref[i] = nref[j];
            nref[j] = t; 
            return new SwapArray<>(list, nref);
        }

        @Override
        public Iterator<T> iterator() {
            return new Iterator<>() {
                final SwapArray<T> sa = SwapArray.this;
                int index = 0;

                @Override
                public boolean hasNext() {
                    return index < sa.size();
                }

                @Override
                public T next() {
                    return sa.list.get(sa.ref[index++]);
                }
            };
        }

        public Stream<T> stream() {
            return StreamSupport.stream(spliterator(), false);
        }
    }

    @Test
    public void testSwapArray() {
        SwapArray<Integer> sa = new SwapArray<>(1, 2, 3);
        SwapArray<Integer> sb = sa.swap(0, 1);
        SwapArray<Integer> sc = sb.swap(0, 2);
        System.out.println("sa=" + sa.stream().toList());
        System.out.println("sb=" + sb.stream().toList());
        System.out.println("sc=" + sc.stream().toList());
    }

    static <T> List<List<T>> permutationSwapArray(List<T> list) {
        List<List<T>> result = new ArrayList<>();
        int length = list.size();
        new Object() {
            void solve(int index, SwapArray<T> array) {
                if (index >= length)
                    result.add(array.stream().toList());
                else
                    for (int i = index; i < length; ++i)
                        solve(index + 1, array.swap(index, i));
            }
        }.solve(0, new SwapArray<>(list));
        return result;
    }

    @Test
    public void testPermutationSwapArray() {
        var r = permutationSwapArray(List.of("A", "B", "C"));
        for (List<String> a : r)
            System.out.println(a);
    }
}
