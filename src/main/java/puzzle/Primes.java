package puzzle;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Primes {

    private Primes() {}

    public static Map<Integer, Integer> primeFactors(int n) {
        Map<Integer, Integer> map = new TreeMap<>();
        for (int i = 2; i <= n; ++i) {
            int count = 0;
            for (; n % i == 0; n /= i)
                ++count;
            if (count > 0)
                map.put(i, count);
        }
        return map;
    }

    public static String toString(Map<Integer, Integer> factors) {
        return factors.entrySet().stream()
            .map(e -> e.getKey() + superscripts(e.getValue()))
            .collect(Collectors.joining("×"));
    }

    public static int multiply(Map<Integer, Integer> factors) {
        int m = 1;
        for (Entry<Integer, Integer> e : factors.entrySet()) {
            int p = e.getKey(), q = e.getValue();
            for (int i = 0; i < q; ++i)
                m *= p;
        }
        return m;
    }

    public static final String[] SUPERSCRIPTS = {
        "\u2070", "\u00b9", "\u00b2", "\u00b3", "\u2074",
        "\u2075", "\u2076", "\u2077", "\u2078", "\u2079",
    };

    public static final String[] SUBSCRIPTS = {
        "\u2080", "\u2081", "\u2082", "\u2083", "\u2084",
        "\u2085", "\u2086", "\u2087", "\u2088", "\u2089",
    };

    public static String translate(int n, String[] t) {
        StringBuilder sb = new StringBuilder();
        if (n == 0)
            sb.append(t[0]);
        else
            for ( ; n > 0; n /= 10)
                sb.append(t[n % 10]);
        return sb.reverse().toString();
    }

    public static String superscripts(int n) {
        return translate(n, SUPERSCRIPTS);
    }

    public static String subscripts(int n) {
        return translate(n, SUBSCRIPTS);
    }

    public static int[] primes(int max) {
        IntStream stream = IntStream.rangeClosed(2, max);
        IntFunction<IntPredicate> sieve = n -> k -> k == n || k % n != 0;
        stream = stream.filter(sieve.apply(2));
        for (int i = 3, n = (int)Math.sqrt(max); i <= n; i += 2)
            stream = stream.filter(sieve.apply(i));
        return stream.toArray();
    }

    public static int[] primes2(int n) {
        return IntStream.rangeClosed(2, (int)Math.sqrt(n))
            .mapToObj(i -> (IntPredicate) k -> k == i || k % i != 0)
            .reduce(IntStream.rangeClosed(2, n),
                (s, p) -> s.filter(p), (a, b) -> IntStream.concat(a, b))
            .toArray();
    }

    public static IntStream primes3(int max) {
        return IntStream.range(2, max)
            .filter(IntStream.rangeClosed(2, (int)Math.sqrt(max)).boxed()
                .reduce(n -> true,
                    (p, n) -> p.and(i -> i == n || i % n != 0), (p, q) -> p));
    }

    public static Set<Integer> factors(int n) {
        return (Set<Integer>) IntStream.rangeClosed(1, (int) Math.sqrt(n))
            .filter(i -> n % i == 0)
            .flatMap(i -> IntStream.of(i, n / i))
            .boxed().collect(Collectors.toCollection(() -> new TreeSet<>()));
//        Set<Integer> factors = new TreeSet<>();
//        int max = (int) Math.sqrt(n);
//        for (int i = 1; i <= max; ++i)
//            if (n % i == 0) {
//                factors.add(i);
//                factors.add(n / i);
//            }
//        return factors;
    }

    public static int sumOfFactors(int n) {
        int max = (int) Math.sqrt(n);
        return IntStream.rangeClosed(1, max)
            .filter(i -> n % i == 0)
            .map(i -> i + n / i)
            .sum() - (max * max == n ? max : 0);
//        int sum = 0;
//        int max = (int) Math.sqrt(n);
//        if (max * max == n)
//            sum -= max;
//        for (int i = 1; i <= max; ++i)
//            if (n % i == 0)
//                sum += i + n / i;
//        return sum;
    }
}
