package puzzle.list;

public class IntCons implements IntList {

    public final int car;
    public final IntList cdr;

    private IntCons(int car, IntList cdr) {
        this.car = car;
        this.cdr = cdr;
    }

    public IntCons of(int car, IntList cdr) {
        return new IntCons(car, cdr);
    }

    public IntList of(int... elements) {
        IntList result = NIL;
        for (int i = elements.length - 1; i >= 0; --i)
            result = of(elements[i], result);
        return result;
    }

    @Override
    public int car() {
        return car;
    }

    @Override
    public IntList cdr() {
        return cdr;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("(");
        sb.append(car);
        for (IntList list = cdr; list != NIL; list = list.cdr())
            sb.append(" ").append(list.car());
        return sb.append(")").toString();
    }
}
