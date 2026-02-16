package puzzle.list;

import java.util.Iterator;

public class ConsList<T> implements Iterable<T> {

    public static final ConsList<?> NIL = new ConsList<>(null, null);

    @SuppressWarnings("unchecked")
    public static <T> ConsList<T> nil() {
        return (ConsList<T>) NIL;
    }

    final T car;
    final ConsList<T> cdr;

    ConsList(T car, ConsList<T> cdr) {
        this.car = car;
        this.cdr = cdr;
    }

    @SuppressWarnings("unchecked")
    public static <T> ConsList<T> of(T... elements) {
        ConsList<T> result = nil();
        for (int i = elements.length - 1; i >= 0; --i)
            result = result.cons(elements[i]);
        return result;
    }

    public int size() {
        int size = 0;
        for (ConsList<T> list = this; list != nil(); list = list.cdr)
            ++size;
        return size;
    }

    public ConsList<T> cons(T car) {
        return new ConsList<>(car, this);
    }

    public T car() {
        return car;
    }

    public ConsList<T> cdr() {
        return cdr;
    }

    public ConsList<T> reverse() {
        ConsList<T> result = nil();
        for (ConsList<T> list = this; list != nil(); list = list.cdr)
            result = result.cons(list.car);
        return result;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            ConsList<T> list = ConsList.this;

            @Override
            public boolean hasNext() {
                return list != nil();
            }

            @Override
            public T next() {
                T car = list.car;
                list = list.cdr;
                return car;
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        if (this != nil())
            sb.append(car);
        for (ConsList<T> list = cdr; list != nil(); list = list.cdr)
            sb.append(", ").append(list.car);
        return sb.append("]").toString();
    }
}
