package puzzle.list;

import java.util.Iterator;

public interface TList<T> extends Iterable<T> {

    static TList<?> NIL = new TList<>() {
        @Override
        public String toString() {
            return "()";
        }
    };

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
}
