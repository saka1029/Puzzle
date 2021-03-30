package puzzle;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Streams {

    public static <T1, T2, R> Stream<R> zip(Stream<T1> stream1, Stream<T2> stream2, BiFunction<T1, T2, R> zipper) {
        Iterator<T1> iterator1 = stream1.iterator();
        Iterator<T2> iterator2 = stream2.iterator();
        Iterable<R> iterable = () -> new Iterator<R>() {

            @Override
            public boolean hasNext() {
                return iterator1.hasNext() && iterator2.hasNext();
            }

            @Override
            public R next() {
                return zipper.apply(iterator1.next(), iterator2.next());
            }
        };
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    public static <K, V> Map<K, V> map(Stream<K> keys, Stream<V> values) {
        return zip(keys, values, (k, v) -> Map.entry(k, v))
            .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    public static IntStream primes(int max) {
        IntStream primes = IntStream.range(2, max);
        IntFunction<IntPredicate> sieve = n -> i -> i == n || i % n != 0;
        primes = primes.filter(sieve.apply(2));
        for (int i = 3; i * i <= max; i += 2)
            primes = primes.filter(sieve.apply(i));
        return primes;
    }

}
