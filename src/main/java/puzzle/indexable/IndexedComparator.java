import java.util.Comparator;
import java.util.List;

public interface IndexedComparator {
    int compare(int l, int r);

    public default IndexedComparator reverse() {
        return (l, r) -> compare(l, r);
    }

    public static IndexedComparator comparator(int[] array) {
        return (l, r) -> Integer.compare(array[l], array[r]);
    }

    public static <T extends Comparable<T>> IndexedComparator comparator(List<T> list) {
        return (l, r) -> list.get(l).compareTo(list.get(r));
    }

    public static <T> IndexedComparator comparator(List<T> list, Comparator<T> comparator) {
        return (l, r) -> comparator.compare(list.get(l), list.get(r));
    }
}