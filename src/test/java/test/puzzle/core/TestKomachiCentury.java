package test.puzzle.core;

import java.util.stream.IntStream;

import org.junit.Test;

import puzzle.core.Permutation;
import puzzle.core.Rational;

public class TestKomachiCentury {

    static int subDigits(int[] digits, int start, int end) {
        return IntStream.range(start, end).map(i -> digits[i]).reduce(0, (x, y) -> 10 * x + y);
    }
    static void solve() {
        Rational goal = Rational.of(100);
        int[] digits = IntStream.rangeClosed(1, 9).toArray();
        for (int[] array : Permutation.iterable(digits, 9)) {
            for (int ae = 0; ae <= 3; ++ae) {
                int a = subDigits(array, 0, ae);
                for (int be = ae + 1; be < 8; ++be) {
                    int b = subDigits(array, ae, be);
                    int c = subDigits(array, be, 9);
                    if (Rational.of(a).add(Rational.of(b, c)).equals(goal))
                        System.out.printf("%d + %d/%d == %s%n",a, b, c, goal);
                }
            }
        }
    }

    @Test
    public void testCentury() {
        solve();
    }

}
