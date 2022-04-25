package puzzle.indexable;

import java.util.List;

public interface IndexedSwapper {
    void swap(int l, int r);

    public static IndexedSwapper swapper(int[] array) {
        return (l, r) -> {
            int temp = array[l];
            array[l] = array[r];
            array[r] = temp;
        };
    }

    public static <T> IndexedSwapper swapper(List<T> list) {
        return (l, r) -> list.set(r, list.set(l, list.get(r)));
    }
}