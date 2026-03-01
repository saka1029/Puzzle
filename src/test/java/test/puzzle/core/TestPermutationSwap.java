package test.puzzle.core;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TestPermutationSwap {

    static <T> List<List<T>> permutation(List<T> list) {
        // listが更新可能でない場合があるのでコピーする。
        List<T> input = new ArrayList<>(list);
        List<List<T>> result = new ArrayList<>();
        new Object() {
            int length = input.size();

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

}
