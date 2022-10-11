package puzzle.core;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public interface ArrayReorder {

    int compare(int indexA, int indexB);

    void swap(int indexA, int indexB);

    int size();

    String string(int index);

    public default String string() {
        StringBuilder sb = new StringBuilder();
        String sep = "";
        for (int i = 0, size = size(); i < size; ++i, sep = ", ")
            sb.append(sep).append(string(i));
        return sb.toString();
    }

    public static ArrayReorder of(int[] array) {
        return new ArrayReorder() {

            @Override
            public int compare(int indexA, int indexB) {
                return Integer.compare(array[indexA], array[indexB]);
            }

            @Override
            public void swap(int indexA, int indexB) {
                int temp = array[indexA];
                array[indexA] = array[indexB];
                array[indexB] = temp;
            }

            @Override
            public int size() {
                return array.length;
            }

            @Override
            public String string(int index) {
                return "" + array[index];
            }
        };
    }

    public static ArrayReorder of(short[] array) {
        return new ArrayReorder() {

            @Override
            public int compare(int indexA, int indexB) {
                return Short.compare(array[indexA], array[indexB]);
            }

            @Override
            public void swap(int indexA, int indexB) {
                short temp = array[indexA];
                array[indexA] = array[indexB];
                array[indexB] = temp;
            }

            @Override
            public int size() {
                return array.length;
            }

            @Override
            public String string(int index) {
                return "" + array[index];
            }
        };
    }

    public static ArrayReorder of(byte[] array) {
        return new ArrayReorder() {

            @Override
            public int compare(int indexA, int indexB) {
                return Byte.compare(array[indexA], array[indexB]);
            }

            @Override
            public void swap(int indexA, int indexB) {
                byte temp = array[indexA];
                array[indexA] = array[indexB];
                array[indexB] = temp;
            }

            @Override
            public int size() {
                return array.length;
            }

            @Override
            public String string(int index) {
                return "" + array[index];
            }
        };
    }

    public static ArrayReorder of(char[] array) {
        return new ArrayReorder() {

            @Override
            public int compare(int indexA, int indexB) {
                return Character.compare(array[indexA], array[indexB]);
            }

            @Override
            public void swap(int indexA, int indexB) {
                char temp = array[indexA];
                array[indexA] = array[indexB];
                array[indexB] = temp;
            }

            @Override
            public int size() {
                return array.length;
            }

            @Override
            public String string(int index) {
                return "" + array[index];
            }
        };
    }

    public static ArrayReorder of(float[] array) {
        return new ArrayReorder() {

            @Override
            public int compare(int indexA, int indexB) {
                return Float.compare(array[indexA], array[indexB]);
            }

            @Override
            public void swap(int indexA, int indexB) {
                float temp = array[indexA];
                array[indexA] = array[indexB];
                array[indexB] = temp;
            }

            @Override
            public int size() {
                return array.length;
            }

            @Override
            public String string(int index) {
                return "" + array[index];
            }
        };
    }

    public static ArrayReorder of(double[] array) {
        return new ArrayReorder() {

            @Override
            public int compare(int indexA, int indexB) {
                return Double.compare(array[indexA], array[indexB]);
            }

            @Override
            public void swap(int indexA, int indexB) {
                double temp = array[indexA];
                array[indexA] = array[indexB];
                array[indexB] = temp;
            }

            @Override
            public int size() {
                return array.length;
            }

            @Override
            public String string(int index) {
                return "" + array[index];
            }
        };
    }

    public static <T> ArrayReorder of(T[] array, Comparator<T> comparator) {
        return new ArrayReorder() {

            @Override
            public int compare(int indexA, int indexB) {
                return comparator.compare(array[indexA], array[indexB]);
            }

            @Override
            public void swap(int indexA, int indexB) {
                T temp = array[indexA];
                array[indexA] = array[indexB];
                array[indexB] = temp;
            }

            @Override
            public int size() {
                return array.length;
            }

            @Override
            public String string(int index) {
                return "" + array[index];
            }
        };
    }

    public static <T extends Comparable<T>> ArrayReorder of(T[] array) {
        return of(array, Comparator.naturalOrder());
    }

    public static <T> ArrayReorder of(List<T> list, Comparator<T> comparator) {
        return new ArrayReorder() {

            @Override
            public int compare(int indexA, int indexB) {
                return comparator.compare(list.get(indexA), list.get(indexB));
            }

            @Override
            public void swap(int indexA, int indexB) {
                Collections.swap(list, indexA, indexB);
            }

            @Override
            public int size() {
                return list.size();
            }

            @Override
            public String string(int index) {
                return "" + list.get(index);
            }
        };
    }

    public static <T extends Comparable<T>> ArrayReorder of(List<T> list) {
        return of(list, Comparator.naturalOrder());
    }

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
        }.sort(0, size() - 1);
    }

    public default void insertionSort() {
        int end = size();
        for (int i = 1, n = end; i < n; i++)
            for (int j = i; j > 0 && compare(j - 1, j) > 0; --j)
                swap(j - 1, j);
    }

    public default void bubbleSort() {
        int end = size();
        boolean swapped;
        do {
            swapped = false;
            for (int i = 1; i < end; ++i) {
                if (compare(i - 1, i) > 0) {
                    swap(i - 1, i);
                    swapped = true;
                }
            }
        } while (swapped);
    }

    public default void heapSort() {
        int end = size();
        new Object() {

            void heapify(int n, int i) {
                int largest = i; // Initialize largest as root
                int l = 2 * i + 1; // left = 2*i + 1
                int r = 2 * i + 2; // right = 2*i + 2
                if (l < n && compare(l, largest) > 0)
                    largest = l;
                if (r < n && compare(r, largest) > 0)
                    largest = r;
                if (largest != i) {
                    swap(i, largest);
                    heapify(n, largest);
                }
            }

            void sort() {
                for (int i = end / 2 - 1; i >= 0; i--)
                    heapify(end, i);
                for (int i = end - 1; i > 0; i--) {
                    swap(0, i);
                    heapify(i, 0);
                }
            }
        }.sort();
    }

    public default void reverse() {
        for (int i = 0, j = size() - 1; i < j; ++i, --j)
            swap(i, j);
    }

    public default void shuffle() {
        Random random = new Random();
        int end = size();
        for (int i = end - 1; i > 0; --i) {
            int j = random.nextInt(i + 1);
            swap(i, j);
        }
    }

    public default boolean nextPermutation() {
        int end = size();
        int i = end - 2;
        while (i >= 0 && compare(i, i + 1) >= 0)
            --i;
        if (i < 0)
            return false;
        int j = end - 1;
        while (compare(i, j) >= 0)
            --j;
        swap(i, j);
        for (int p = i + 1, q = end - 1; p < q; ++p, --q)
            swap(p, q);
        return true;
    }

    public default ArrayReorder reverseOrder() {
        final ArrayReorder origin = this;
        return new ArrayReorder() {

            @Override
            public int compare(int indexA, int indexB) {
                return -origin.compare(indexA, indexB);
            }

            @Override
            public void swap(int indexA, int indexB) {
                origin.swap(indexA, indexB);
            }

            @Override
            public int size() {
                return origin.size();
            }

            @Override
            public String string(int index) {
                return origin.string(index);
            }
        };
    }

    public default ArrayReorder subset(int offset, int size) {
        final ArrayReorder origin = this;
        if (offset < 0 || offset > origin.size())
            throw new IllegalArgumentException("offset");
        if (size < 0 || offset + size > origin.size())
            throw new IllegalArgumentException("size");
        return new ArrayReorder() {

            @Override
            public int compare(int indexA, int indexB) {
                return origin.compare(indexA + offset, indexB + offset);
            }

            @Override
            public void swap(int indexA, int indexB) {
                origin.swap(indexA + offset, indexB + offset);
            }

            @Override
            public int size() {
                return size;
            }

            @Override
            public String string(int index) {
                return origin.string(index);
            }
        };
    }

    public interface Trace2i {
        void trace(int indexA, int indexB);
    }

    public default ArrayReorder trace(Trace2i traceCompare, Trace2i traceSwap) {
        ArrayReorder origin = this;
        return new ArrayReorder() {

            @Override
            public int compare(int indexA, int indexB) {
                traceCompare.trace(indexA, indexB);
                return origin.compare(indexA, indexB);
            }

            @Override
            public void swap(int indexA, int indexB) {
                traceSwap.trace(indexA, indexB);
                origin.swap(indexA, indexB);
            }

            @Override
            public int size() {
                return origin.size();
            }

            @Override
            public String string(int index) {
                return origin.string(index);
            }
        };
    }
}