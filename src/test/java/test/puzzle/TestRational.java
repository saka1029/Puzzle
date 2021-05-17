package test.puzzle;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import puzzle.Rational;

class TestRational {

    @Test
    void test() {
        Rational r = Rational.ZERO;
        for (int i = 1; i <= 20; ++i) {
            Rational add = Rational.of(1, i);
            Rational sum = r.add(add);
            System.out.println(r + " + " + add + " -> " + sum);
            r = sum;
        }
        System.out.println(r + " ≒ " + r.doubleValue());
        System.out.println(r.numerator % 11);
    }

    @Test
    void test2() {
        long den = 1;
        for (int i = 2; i <= 10; ++i)
            den *= i;
        System.out.println(den);
        long num = 0;
        for (int i = 1; i <= 10; ++i)
            num += den / i;
        System.out.println(num + "/" + den);
        for (int i = 2; i <= num; ++i)
            if (num % i == 0 && den % i == 0) {
                num /= i;
                den /= i;
                --i;
            }
        System.out.println(num + "/" + den);
    }

    /**
     * 【中学受験算数】何秒で解ける？ちょっと変わったキセル算！　【毎日１題！中学受験算数７】 - YouTube
     * https://www.youtube.com/watch?v=84IXyRluoEM
     *
     * <pre>
     *  1     1     1      1
     * --- + --- + --- + ----
     * 3×5   5×7   7×9   9×11
     *
     * = (1/2)(1/3 - 1/5 + 1/5 - 1/7 + 1/7 - 1/9 + 1/9 - 1/11)
     * = (1/2)(1/3 - 1/11)
     * = 4/33
     * </pre>
     */
    @Test
    public void testキセル算() {
        Rational r = Rational.ZERO;
        for (int i = 3; i <= 9; i += 2) {
            Rational e = Rational.of(1, i * (i + 2));
            Rational x = r.add(e);
//            System.out.println(r + " + " + e + " -> " + x);
            r = x;
        }
        assertEquals(Rational.of(4, 33), r);
    }

    @Test
    public void testGCD() {
        assertEquals(29, Rational.gcd(493, 667));
    }

    /**
     * いい感じの難易度に仕上げた(?)大小比較パズル - YouTube
     * https://www.youtube.com/watch?v=LjQeOP-nZr4
     */
    @Test
    public void testYoutube() {
        Rational left = Rational.ONE;
        for (int n = 1; n < 24; n += 2) {
            Rational t = Rational.of(n, n + 1);
            Rational m = left.multiply(Rational.of(n, n + 1));
            System.out.printf("%s = %s * %s = %s%n", m, left, t, m.doubleValue());
            left = m;
        }
        System.out.printf("left = %s = %s%n", left, left.doubleValue());
    }

}
