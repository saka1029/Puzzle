package puzzle;

import java.util.Collections;
import java.util.Comparator;
import java.util.function.Function;

public class Comparators {

    private Comparators() {
    }

    @SafeVarargs
    public static <T> Comparator<T> orderBy(Comparator<T> first, Comparator<? super T>... rest) {
        for (Comparator<? super T> e : rest)
            first = first.thenComparing(e);
        return first;
    }

    public static <T, U extends Comparable<? super U>> Comparator<T> asc(Function<? super T, ? extends U> f) {
        return Comparator.comparing(f);
    }

    public static <T, U extends Comparable<? super U>> Comparator<T> desc(Function<? super T, ? extends U> f) {
        return Collections.reverseOrder(Comparator.comparing(f));
    }
}
