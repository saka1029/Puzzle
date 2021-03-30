package test.puzzle;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;


class TestSieveOfEratoschenes {

    static final List<Integer> PRIMES1000 = List.of(
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
        947, 953, 967, 971, 977, 983, 991, 997);

    static boolean[] primesByLoop(int n) {
        int max = n + 1;
        boolean[] isPrime = new boolean[max];
        Arrays.fill(isPrime, 2, max, true);
        for (int i = 2; i < max; ++i)
            for (int j = i + i; j < max; j += i)
                isPrime[j] = false;
        return isPrime;
    }

    @Test
    void testPrimesByLoop() {
        boolean[] isPrime = primesByLoop(1000);
        java.util.List<Integer> actual = new ArrayList<>();
        for (int i = 0; i < isPrime.length; ++i)
            if (isPrime[i])
                actual.add(i);
        assertEquals(PRIMES1000, actual);
    }

    static IntStream primeStreamByLoop(int n) {
        IntStream stream = IntStream.rangeClosed(2, n);
        for (int i = 2; i <= n; ++i) {
            final int k = i;
            stream = stream.filter(j -> j == k || j % k != 0);
        }
        return stream;
    }

    @Test
    void testPrimeStreamByLoop() {
        List<Integer> actual = primeStreamByLoop(1000)
            .boxed().collect(Collectors.toList());
        assertEquals(PRIMES1000, actual);
    }

    static IntStream primeStream(int n) {
        return IntStream.rangeClosed(2, n)
            .mapToObj(i -> (IntPredicate) j -> j == i || j % i != 0)
            .reduce(IntStream.rangeClosed(2, n),
                (s, p) -> s.filter(p), (a, b) -> IntStream.concat(a, b));
    }

    @Test
    void testPrimeStream() {
        List<Integer> actual = primeStream(1000)
            .boxed().collect(Collectors.toList());
        assertEquals(PRIMES1000, actual);
    }
}
