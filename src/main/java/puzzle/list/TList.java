package puzzle.list;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface TList<T> extends Iterable<T> {

    static TList<?> NIL = new TList<>() {

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public String toString() {
            return "()";
        }
    };

    @SuppressWarnings("unchecked")
    public static <T> TList<T> of(T... elements) {
        TList<T> result = TList.nil();
        for (int i = elements.length - 1; i >= 0; --i)
            result = TCons.of(elements[i], result);
        return result;
    }

    public static <T> TList<T> of(List<T> elements) {
        TList<T> result = TList.nil();
        for (int i = elements.size() - 1; i >= 0; --i)
            result = TCons.of(elements.get(i), result);
        return result;
    }

    @SuppressWarnings("unchecked")
    public static <T> TList<T> nil() {
        return (TList<T>)NIL;
    }

    default boolean isNil() {
        return this == NIL;
    }

    default T car() {
        throw new RuntimeException("can't car");
    }

    default TList<T> cdr() {
        throw new RuntimeException("can't cdr");
    }

    @Override
    default Iterator<T> iterator() {
        return new Iterator<T>() {

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public T next() {
                throw new UnsupportedOperationException("Unimplemented method 'next'");
            }
        };
    }

    default Stream<T> stream() {
        return StreamSupport.stream(this.spliterator(), false);
    }

    static <T> TCons<T> cons(T car, TList<T> cdr) {
        return TCons.of(car, cdr);
    }

    default TList<T> reverse() {
        TList<T> result = nil();
        for (T e : this)
            result = TCons.of(e, result);
        return result;
    }

    default int size() {
        int result = 0;
        for (@SuppressWarnings("unused") T e : this)
            ++result;
        return result;
    }
}
