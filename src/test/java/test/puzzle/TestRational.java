package test.puzzle;

import org.junit.jupiter.api.Test;

import puzzle.Rational;

class TestRational {

    @Test
    void test() {
        Rational r = Rational.ZERO;
        for (int i = 1; i <= 10; ++i)
            r = r.add(new Rational(1, i));
        System.out.println(r);
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

}
