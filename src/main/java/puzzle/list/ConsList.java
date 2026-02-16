package puzzle.list;

import java.util.Iterator;

public class ConsList<T> implements Iterable<T> {

    public static final ConsList<?> NIL = new ConsList<>(null, null);

    @SuppressWarnings("unchecked")
    public static <T> ConsList<T> nil() {
        return (ConsList<T>) NIL;
    }

    final T element;
    final ConsList<T> rest;

    ConsList(T element, ConsList<T> rest) {
        this.element = element;
        this.rest = rest;
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
        for (ConsList<T> list = this; list != nil(); list = list.rest)
            ++size;
        return size;
    }

    public ConsList<T> cons(T element) {
        return new ConsList<>(element, this);
    }

    public ConsList<T> reverse() {
        ConsList<T> result = nil();
        for (ConsList<T> list = this; list != nil(); list = list.rest)
            result = result.cons(list.element);
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
                T e = list.element;
                list = list.rest;
                return e;
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        if (this != nil())
            sb.append(element);
        for (ConsList<T> list = rest; list != nil(); list = list.rest)
            sb.append(", ").append(list.element);
        return sb.append("]").toString();
    }
}
