package puzzle.list;

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
    public String toString() {
        StringBuilder sb = new StringBuilder("(");
        sb.append(car);
        for (TList<T> list = cdr; list != TList.nil(); list = list.cdr())
            sb.append(" ").append(list.car());
        return sb.append(")").toString();
    }
}
