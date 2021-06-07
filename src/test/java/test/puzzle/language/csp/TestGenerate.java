package test.puzzle.language.csp;

import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestGenerate {
    public static void solve(Consumer<int[]> callback) {
        int[] _a_domain = {1, 2, 3};
        int[] _b_domain = {1, 2, 3};
        int[] _c_domain = {1, 2};
        for (int a : _a_domain)
            for (int b : _b_domain)
                if (a < b)
                    for (int c : _c_domain)
                        if (b < c && a * a + b * b == c * c)
                            callback.accept(new int[] {a, b, c});
    }

    static final Consumer<int[]> CALLBACK = a -> System.out.println(IntStream.of(a)
        .mapToObj(n -> "" + n)
        .collect(Collectors.joining(",")));

    public static void main(String[] args) throws Exception {
        System.out.println("a,b,c");
        solve(CALLBACK);
    }
}
