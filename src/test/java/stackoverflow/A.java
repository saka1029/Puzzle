package stackoverflow;

import org.junit.jupiter.api.Test;

import puzzle.Rational;

class A {

    @Test
    public void testGCD() {
        // 25m + 17n = 1623
        long gcd = Rational.gcd(25, 17);
        long lcm = 25 * 17 / gcd;
        System.out.println("gcd=" + gcd + " lcm=" + lcm);
        System.out.println("[1623 / lcm]=" + (1623/lcm) + " 1623 mod lcm=" + (1623 % lcm));
        for (int m = 0, max = 348 / 25; m <= max; ++m) {
            int n = (1623 - 25 * m) / 17;
            if (25 * m + 17 * n == 1623)
                System.out.println("m=" + m + " n=" + n);
        }
        int min = -1000, max = 1000;
        for (int m = min; m <= max; ++m)
            for (int n = min; n <= max; ++n)
                if (25 * m + 17 * n == 1623)
                    System.out.printf("m=%d n=%d 25m+17n=%d%n", m, n, (25 * m + 17 * n));
    }

}
