package test.puzzle;

import static org.junit.jupiter.api.Assertions.*;
import static puzzle.Primes.*;

import java.math.BigInteger;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import puzzle.Primes;

class TestPrimes {

    static void testPrimeFactor(int n) {
        Map<Integer, Integer> factors = primeFactors(n);
        System.out.println(n + " = " + Primes.toString(factors));
        assertEquals(n, multiply(factors));
    }

    @Test
    void testPrimeFactorization() {
        testPrimeFactor(360);
        testPrimeFactor(1001);
        testPrimeFactor(1729);
        testPrimeFactor(123456789);
        testPrimeFactor(987654321);
        testPrimeFactor(3571);
        testPrimeFactor(999975);
    }

    @Test
    void testSuperscripts() {
        assertEquals("⁰¹²³⁴⁵⁶⁷⁸⁹", String.join("", SUPERSCRIPTS));
        assertEquals("₀₁₂₃₄₅₆₇₈₉", String.join("", SUBSCRIPTS));
        assertEquals("¹²³", superscripts(123));
        assertEquals("⁰", superscripts(0));
        assertEquals("₁₂₃", subscripts(123));
        assertEquals("₀", subscripts(0));
    }

    static final int[] PRIMES1000 = {
            2, 3, 5, 7, 11, 13, 17, 19, 23, 29,
            31, 37, 41, 43, 47, 53, 59, 61, 67, 71,
            73, 79, 83, 89, 97, 101, 103, 107, 109, 113,
            127, 131, 137, 139, 149, 151, 157, 163, 167, 173,
            179, 181, 191, 193, 197, 199, 211, 223, 227, 229,
            233, 239, 241, 251, 257, 263, 269, 271, 277, 281,
            283, 293, 307, 311, 313, 317, 331, 337, 347, 349,
            353, 359, 367, 373, 379, 383, 389, 397, 401, 409,
            419, 421, 431, 433, 439, 443, 449, 457, 461, 463,
            467, 479, 487, 491, 499, 503, 509, 521, 523, 541,
            547, 557, 563, 569, 571, 577, 587, 593, 599, 601,
            607, 613, 617, 619, 631, 641, 643, 647, 653, 659,
            661, 673, 677, 683, 691, 701, 709, 719, 727, 733,
            739, 743, 751, 757, 761, 769, 773, 787, 797, 809,
            811, 821, 823, 827, 829, 839, 853, 857, 859, 863,
            877, 881, 883, 887, 907, 911, 919, 929, 937, 941,
            947, 953, 967, 971, 977, 983, 991, 997};

    @Test
    void testPrimes() {
        assertArrayEquals(PRIMES1000, primes(1000));
    }

    @Test
    void testPrimes2() {
        assertArrayEquals(PRIMES1000, primes2(1000));
    }

    @Test
    void testFactors() {
        assertEquals(
            Set.of(1, 2, 3, 4, 5, 6, 8, 9, 10, 12, 15, 18, 20, 24, 30, 36, 40, 45, 60, 72, 90, 120, 180, 360),
            factors(360));
    }

    @Test
    void testSumOfFactors() {
        for (int i = 1; i <= 10000; ++i)
            assertEquals(factors(i).stream().mapToInt(Integer::intValue).sum(), sumOfFactors(i));
    }

    @Test
    void testPrdocutOfFactors() {
        BigInteger p = factors(120).stream()
            .map(i -> BigInteger.valueOf(i))
            .reduce(BigInteger.ONE, (a, b) -> a.multiply(b));
        System.out.println(p);
        BigInteger n = BigInteger.valueOf(120);
        int count = 0;
        BigInteger i = p;
        for ( ; i.remainder(n).equals(BigInteger.ZERO); i = i.divide(n))
            ++count;
        System.out.println(p + " = " + n + "^" + count + " remainder=" + i);
    }
}
