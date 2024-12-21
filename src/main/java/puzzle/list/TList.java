package puzzle.list;

public interface TList<T> {

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

    default T car() {
        throw new RuntimeException("can't car");
    }

    default TList<T> cdr() {
        throw new RuntimeException("can't cdr");
    }
}
