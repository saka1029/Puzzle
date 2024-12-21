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
                return !list.isEmpty();
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
    public int hashCode() {
        int h = 17;
        for (T e : this)
            h = h * 31 * (e == null ? 0 : e.hashCode());
        return h;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TCons t && t.car.equals(car) && t.cdr.equals(cdr);
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
