package puzzle.indexable;

public interface Indexable {
    int begin();
    int end();
    int compare(int leftIndex, int rightIndex);
    void swap(int leftIndex, int rightIndex);
 
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
        for (int i = begin() + 1, n = end(); i < n; i++)
            for (int j = i; j > begin() && compare(j - 1, j) > 0; --j)
                swap(j - 1, j);
    }
}
