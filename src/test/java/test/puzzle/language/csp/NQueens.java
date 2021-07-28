package test.puzzle.language.csp;

import static java.lang.Math.*;

import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NQueens {
    public static void solve(Consumer<int[]> callback) {
        int[] _v0_domain = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[] _v1_domain = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[] _v2_domain = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[] _v3_domain = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[] _v4_domain = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[] _v5_domain = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[] _v6_domain = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[] _v7_domain = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[] _v8_domain = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[] _v9_domain = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        for (int v0 : _v0_domain)
            for (int v1 : _v1_domain)
                if (v0 != v1 && abs(v0 - v1) != 1)
                    for (int v2 : _v2_domain)
                        if (v0 != v2 && v1 != v2 && abs(v0 - v2) != 2 && abs(v1 - v2) != 1)
                            for (int v3 : _v3_domain)
                                if (v0 != v3 && v1 != v3 && v2 != v3 && abs(v0 - v3) != 3
                                    && abs(v1 - v3) != 2 && abs(v2 - v3) != 1)
                                    for (int v4 : _v4_domain)
                                        if (v0 != v4 && v1 != v4 && v2 != v4 && v3 != v4
                                            && abs(v0 - v4) != 4 && abs(v1 - v4) != 3
                                            && abs(v2 - v4) != 2 && abs(v3 - v4) != 1)
                                            for (int v5 : _v5_domain)
                                                if (v0 != v5 && v1 != v5 && v2 != v5 && v3 != v5
                                                    && v4 != v5 && abs(v0 - v5) != 5
                                                    && abs(v1 - v5) != 4 && abs(v2 - v5) != 3
                                                    && abs(v3 - v5) != 2 && abs(v4 - v5) != 1)
                                                    for (int v6 : _v6_domain)
                                                        if (v0 != v6 && v1 != v6 && v2 != v6
                                                            && v3 != v6 && v4 != v6 && v5 != v6
                                                            && abs(v0 - v6) != 6
                                                            && abs(v1 - v6) != 5
                                                            && abs(v2 - v6) != 4
                                                            && abs(v3 - v6) != 3
                                                            && abs(v4 - v6) != 2
                                                            && abs(v5 - v6) != 1)
                                                            for (int v7 : _v7_domain)
                                                                if (v0 != v7 && v1 != v7 && v2 != v7
                                                                    && v3 != v7 && v4 != v7
                                                                    && v5 != v7 && v6 != v7
                                                                    && abs(v0 - v7) != 7
                                                                    && abs(v1 - v7) != 6
                                                                    && abs(v2 - v7) != 5
                                                                    && abs(v3 - v7) != 4
                                                                    && abs(v4 - v7) != 3
                                                                    && abs(v5 - v7) != 2
                                                                    && abs(v6 - v7) != 1)
                                                                    for (int v8 : _v8_domain)
                                                                        if (v0 != v8 && v1 != v8
                                                                            && v2 != v8 && v3 != v8
                                                                            && v4 != v8 && v5 != v8
                                                                            && v6 != v8 && v7 != v8
                                                                            && abs(v0 - v8) != 8
                                                                            && abs(v1 - v8) != 7
                                                                            && abs(v2 - v8) != 6
                                                                            && abs(v3 - v8) != 5
                                                                            && abs(v4 - v8) != 4
                                                                            && abs(v5 - v8) != 3
                                                                            && abs(v6 - v8) != 2
                                                                            && abs(v7 - v8) != 1)
                                                                            for (int v9 : _v9_domain)
                                                                                if (v0 != v9
                                                                                    && v1 != v9
                                                                                    && v2 != v9
                                                                                    && v3 != v9
                                                                                    && v4 != v9
                                                                                    && v5 != v9
                                                                                    && v6 != v9
                                                                                    && v7 != v9
                                                                                    && v8 != v9
                                                                                    && abs(v0
                                                                                        - v9) != 9
                                                                                    && abs(v1
                                                                                        - v9) != 8
                                                                                    && abs(v2
                                                                                        - v9) != 7
                                                                                    && abs(v3
                                                                                        - v9) != 6
                                                                                    && abs(v4
                                                                                        - v9) != 5
                                                                                    && abs(v5
                                                                                        - v9) != 4
                                                                                    && abs(v6
                                                                                        - v9) != 3
                                                                                    && abs(v7
                                                                                        - v9) != 2
                                                                                    && abs(v8
                                                                                        - v9) != 1)
                                                                                    callback.accept(
                                                                                        new int[] {
                                                                                            v0, v1,
                                                                                            v2, v3,
                                                                                            v4, v5,
                                                                                            v6, v7,
                                                                                            v8,
                                                                                            v9});
    }

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        System.out.println("v0,v1,v2,v3,v4,v5,v6,v7,v8,v9");
        int[] count = {0};
        Consumer<int[]> callback = a -> {
            ++count[0];
            System.out.println(IntStream.of(a)
                .mapToObj(n -> "" + n)
                .collect(Collectors.joining(",")));
        };
        solve(callback);
        System.err.printf("solutions: " + count[0] + ", elapse: %d msec.%n",
            System.currentTimeMillis() - start);
    }
}
