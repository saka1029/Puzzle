package puzzle.indexable;

import java.util.Random;

public interface Indexable {
    int compare(int leftIndex, int rightIndex);

    void swap(int leftIndex, int rightIndex);

    int begin();

    int end();

    public default void quickSort() {
        new Object() {
            int partition(int begin, int end) {
                int pivotIndex = end;
                int i = (begin - 1);
                for (int j = begin; j < end; j++)
                    if (compare(j, pivotIndex) <= 0)
                        swap(++i, j);
                swap(i + 1, end);
                return i + 1;
            }

            void sort(int begin, int end) {
                if (begin >= end)
                    return;
                int partitionIndex = partition(begin, end);
                sort(begin, partitionIndex - 1);
                sort(partitionIndex + 1, end);
            }
        }.sort(begin(), end() - 1);
    }

    public default void insertionSort() {
        int begin = begin(), end = end();
        for (int i = begin + 1, n = end; i < n; i++)
            for (int j = i; j > begin && compare(j - 1, j) > 0; --j)
                swap(j - 1, j);
    }

    public default void bubbleSort() {
        boolean swapped;
        do {
            swapped = false;
            for (int i = begin() + 1; i < end(); ++i) {
                if (compare(i - 1, i) > 0) {
                    /* swap them and remember something changed */
                    swap(i - 1, i);
                    swapped = true;
                }
            }
        } while (swapped);
    }

    /**
     * マージソートを行うためにはget(index), set(index, value)が必要となる。
     * get(index)の戻り値は要素の型なので、抽象化する必要がある。
     */
    public default void mergeSort() {
        throw new RuntimeException("mergeSort() not implemented");
    }

    public default void shuffle() {
        Random random = new Random();
        int begin = begin(), end = end();
        for (int i = end - 1; i > begin; --i) {
            int j = random.nextInt(i - begin + 1) + begin;
            swap(i, j);
        }
    }

    public default void reverse() {
        for (int i = begin(), j = end() - 1; i < j; ++i, --j)
            swap(i, j);
    }

    public default boolean nextPermutation() {
        int begin = begin(), end = end();
        int i = end - 2;
        while (i >= begin && compare(i, i + 1) >= 0)
            --i;
        if (i < begin)
            return false;
        int j = end - 1;
        while (compare(i, j) >= 0)
            --j;
        swap(i, j);
        for (int p = i + 1, q = end - 1; p < q; ++p, --q)
            swap(p, q);
        return true;
    }

    public default int binarySearch() {
        int low = begin();
        int high = end() - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            // T midVal = l.get(mid);
            int cmp = compare(-1, mid);
            if (cmp > 0)
                low = mid + 1;
            else if (cmp < 0)
                high = mid - 1;
            else
                return mid; // key found
        }
        return -(low + 1); // key not found
    }

}
