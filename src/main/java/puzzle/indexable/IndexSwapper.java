package puzzle.indexable;

public interface IndexSwapper {
    void swap(int leftIndex, int rightIndex);
    public static IndexSwapper swap(int[] a) {
        return (l, r) -> {
            int temp = a[l];
            a[l] = a[r];
            a[r] = temp;
        };
    }
}
