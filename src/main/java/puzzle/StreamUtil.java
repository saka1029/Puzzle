package puzzle;

import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StreamUtil {
    
    public static <T1, T2, R> Stream<R> zip(Stream<T1> stream1, Stream<T2> stream2, BiFunction<T1, T2, R> zipper) {
        Iterator<T1> iterator1 = stream1.iterator();
        Iterator<T2> iterator2 = stream2.iterator();
        Iterable<R> iterable = () -> new Iterator<>() {

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

}
