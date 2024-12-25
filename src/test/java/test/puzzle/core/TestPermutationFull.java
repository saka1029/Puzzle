package test.puzzle.core;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import org.junit.Test;

public class TestPermutationFull {

    static final List<List<Integer>> EXPECTED = List.of(
        List.of(0, 1, 2),
        List.of(0, 2, 1),
        List.of(1, 0, 2),
        List.of(1, 2, 0),
        List.of(2, 0, 1),
        List.of(2, 1, 0)
    );

    static void permutation(int n, int r, Consumer<int[]> callback) {
        new Object() {
            boolean[] used = new boolean[n];
            int[] result = new int[r];

            void solve(int i) {
                if (i >= r)
                    callback.accept(result);
                else {
                    for (int j = 0; j < n; ++j) {
                        if (used[j])
                            continue;
                        result[i] = j;
                        used[j] = true;
                        solve(i + 1);
                        used[j] = false;
                    }
                }
            }
        }.solve(0);
    }

    @Test
    public void testSlowPermutation() {
        List<List<Integer>> all = new ArrayList<>();
        permutation(3, 3,
            a -> all.add(IntStream.of(a).mapToObj(i -> i).toList()));
        assertEquals(EXPECTED, all);
    }
}
