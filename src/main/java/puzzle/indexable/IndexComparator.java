package puzzle.indexable;

public interface IndexComparator {
    int compare(int leftIndex, int rightIndex);

    public default IndexComparator reverse() {
        return (l, r) -> compare(r, l);
    }
}
