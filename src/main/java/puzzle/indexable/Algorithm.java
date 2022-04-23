package puzzle.indexable;

import java.util.Comparator;
import java.util.List;

public class Algorithm {

    public static void quickSort(IndexedComparator comparator, IndexedSwapper swapper,
        int begin, int end) {
        new Object() {
            int partition(int begin, int end) {
                int pivotIndex = end;
                int i = (begin - 1);
                for (int j = begin; j < end; j++)
                    if (comparator.compare(j, pivotIndex) <= 0)
                        swapper.swap(++i, j);
                swapper.swap(i + 1, end);
                return i + 1;
            }

            void sort(int begin, int end) {
                if (begin >= end)
                    return;
                int partitionIndex = partition(begin, end);
                sort(begin, partitionIndex - 1);
                sort(partitionIndex + 1, end);
            }
        }.sort(begin, end - 1);
    }

    public static void quickSort(int[] array, int begin, int end) {
        quickSort(IndexedComparator.comparator(array),
            IndexedSwapper.swapper(array), begin, end);
    }

    public static void quickSort(int[] array) {
        quickSort(array, 0, array.length);
    }

    public static <T extends Comparable<T>> void quickSort(List<T> list, int begin, int end) {
        quickSort(IndexedComparator.comparator(list),
            IndexedSwapper.swapper(list), begin, end);
    }

    public static <T extends Comparable<T>> void quickSort(List<T> list) {
        quickSort(list, 0, list.size());
    }

    public static <T> void quickSort(List<T> list, Comparator<T> comparator, int begin, int end) {
        quickSort(IndexedComparator.comparator(list, comparator),
            IndexedSwapper.swapper(list), begin, end);
    }

    public static <T> void quickSort(List<T> list, Comparator<T> comparator) {
        quickSort(list, comparator, 0, list.size());
    }
}
