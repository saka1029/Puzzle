package test.puzzle.core;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.Test;

public class TestZyunkaisu {

    static Map<Integer, Long> count0(int n) {
        return Integer.toString(n).chars()
            .mapToObj(c -> c - '0')
            .collect(Collectors.groupingBy(Function.identity(),
                Collectors.counting()));
    }

    static int[] count(int n) {
        return Integer.toString(n).chars()
            .map(c -> c - '0')
            .collect(() -> new int[10],
                (a, i) -> a[i]++,
                (a, b) -> {});
    }

    static boolean equalsOtherThanFirst(int[] a, int[] b) {
        for (int i = 1, max = a.length; i < max; ++i)
            if (a[i] != b[i])
                return false;
        return true;
    }

    @Test
    public void testCount() {
        for (int i = 1; i < 1000000; ++i) {
            int[] ci = count(i);
            if (equalsOtherThanFirst(count(2 * i), ci))
                System.out.println(i + " " + 2 * i);
        }

    }
}