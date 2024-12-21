package puzzle.list;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class TCons<T> implements TList<T> {

    public final T car;
    public final TList<T> cdr;

    private TCons(T car, TList<T> cdr) {
        this.car = car;
        this.cdr = cdr;
    }

    public static <T> TCons<T> of(T car, TList<T> cdr) {
        return new TCons<>(car, cdr);
    }

    @SuppressWarnings("unchecked")
    public static <T> TList<T> of(T... elements) {
        TList<T> result = TList.nil();
        for (int i = elements.length - 1; i >= 0; --i)
            result = of(elements[i], result);
        return result;
    }

    @Override
    public T car() {
        return car;
    }

    @Override
    public TList<T> cdr() {
        return cdr;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            TList<T> list = TCons.this;

            @Override
            public boolean hasNext() {
                return !list.isNil();
            }

            @Override
            public T next() {
                if (!hasNext())
                    throw new NoSuchElementException();
                T result = list.car();
                list = list.cdr();
                return result;
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("(");
        sb.append(car);
        for (TList<T> list = cdr; list != TList.nil(); list = list.cdr())
            sb.append(" ").append(list.car());
        return sb.append(")").toString();
    }
}
