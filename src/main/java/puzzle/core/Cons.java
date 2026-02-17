package puzzle.core;

import java.util.Iterator;
import java.util.Objects;

public class Cons<T> implements Iterable<T> {

    public static final Cons<?> NIL = new Cons<>(null, null);

    @SuppressWarnings("unchecked")
    public static <T> Cons<T> nil() {
        return (Cons<T>) NIL;
    }

    final T car;
    final Cons<T> cdr;

    Cons(T car, Cons<T> cdr) {
        this.car = car;
        this.cdr = cdr;
    }

    @SuppressWarnings("unchecked")
    public static <T> Cons<T> of(T... elements) {
        Cons<T> result = nil();
        for (int i = elements.length - 1; i >= 0; --i)
            result = result.cons(elements[i]);
        return result;
    }

    public boolean isEmpty() {
        return this == NIL;
    }

    public int size() {
        int size = 0;
        for (Cons<T> list = this; list != nil(); list = list.cdr)
            ++size;
        return size;
    }

    public Cons<T> cons(T car) {
        return new Cons<>(car, this);
    }

    public T car() {
        return car;
    }

    public Cons<T> cdr() {
        return cdr;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Cons c
            && Objects.equals(car, c.car)
            && Objects.equals(cdr, c.cdr);
    }

    public Cons<T> append(Cons<T> list) {
        return isEmpty() ? list : cdr.append(list).cons(car);
    }

    public Cons<T> remove(T element) {
        if (isEmpty())
            return this;
        else if (car.equals(element))
            return cdr.remove(element);
        else
            return cdr.remove(element).cons(car);
    }

    public Cons<T> reverse() {
        Cons<T> result = nil();
        for (Cons<T> list = this; list != nil(); list = list.cdr)
            result = result.cons(list.car);
        return result;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            Cons<T> list = Cons.this;

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
        for (Cons<T> list = cdr; list != nil(); list = list.cdr)
            sb.append(", ").append(list.car);
        return sb.append("]").toString();
    }
}
