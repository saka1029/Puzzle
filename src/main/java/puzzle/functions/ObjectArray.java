package puzzle.functions;

import java.util.Arrays;

public class ObjectArray {

    private final Object[] array;

    public ObjectArray(Object... arguments) {
        this.array = arguments.clone();
    }

    public int size() {
        return array.length;
    }

    /*
     * Immutableでなくなるので削除
     */
//    public Object get(int index) {
//        return array[index];
//    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(array);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        return Arrays.deepEquals(array, ((ObjectArray) obj).array);
    }

    @Override
    public String toString() {
        return Arrays.deepToString(array);
    }
}
