package puzzle.list;

public interface IntList {

    public static IntList NIL = new IntList() {
        @Override
        public String toString() {
            return "()";
        }
    };

    default int car() {
        throw new RuntimeException("can't car");
    }

    default IntList cdr() {
        throw new RuntimeException("can't cdr");
    }
}
