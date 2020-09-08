package puzzle;

import java.util.Comparator;
import java.util.function.Function;

public class Functions {

    /*
     * Comparator functions
     */

    @SafeVarargs
    public static <T> Comparator<T> and(Comparator<T> first, Comparator<T>... rest) {
        for (Comparator<T> c : rest)
            first = first.thenComparing(c);
        return first;
    }

    public static <T> Comparator<T> reversed(Comparator<T> comparator) {
        return (a, b) -> -comparator.compare(a, b);
    }

    public static <U extends Comparable<? super U>, T> Comparator<T> comparing​(Function<? super T, ? extends U> keyExtractor) {
        return Comparator.comparing(keyExtractor);
    }

    /*
     * Function
     */

    public static <A, B, C> Function<A, C> compose(Function<A, B> first, Function<B, C> second) {
        return a -> second.apply(first.apply(a));
    }
}
