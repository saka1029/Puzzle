package puzzle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Permutation {

    static class IndexIterator implements Iterator<int[]> {

        private final int[] array;
        private boolean hasNext;

        IndexIterator(int size) {
            this.array = IntStream.range(0, size).toArray();
            this.hasNext = true;
        }

        static void swap(int[] array, int i, int j) {
            int temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }

        private boolean forward() {
            if (!hasNext)
                return false;
            int length = array.length;
            if (length < 2)
                return false;
            for (int i = length - 2; i >= 0; --i)
                if (array[i] < array[i + 1])
                    for (int j = length - 1; true; --j)
                        if (array[i] < array[j]) {
                            swap(array, i, j);
                            for (int k = i + 1, l = length - 1; k < l; ++k, --l)
                                swap(array, k, l);
                            return true;
                        }
            return false;
        }

        @Override
        public boolean hasNext() {
            return hasNext;
        }

        @Override
        public int[] next() {
            if (!hasNext)
                throw new NoSuchElementException();
            int[] result = array.clone();
            hasNext = forward();
            return result;
        }
    }

    public static Iterable<int[]> iterable(int size) {
        return () -> new IndexIterator(size);
    }

    public static Stream<int[]> stream(int size) {
        return StreamSupport.stream(iterable(size).spliterator(), false);
    }

    static class ArrayIterator<T> implements Iterator<T[]> {

        private final T[] array;
        private final IndexIterator indexes;

        ArrayIterator(T[] array) {
            this.array = array.clone();
            this.indexes = new IndexIterator(array.length);
        }

        @Override
        public boolean hasNext() {
            return indexes.hasNext();
        }

        @Override
        public T[] next() {
            T[] result = array.clone();
            int i = 0;
            for (int j : indexes.next())
                result[i++] = array[j];
            return result;
        }
    }

    public static <T> Iterable<T[]> iterable(T[] array) {
        return () -> new ArrayIterator<>(array);
    }

    public static <T> Stream<T[]> stream(T[] array) {
        return StreamSupport.stream(iterable(array).spliterator(), false);
    }

    static class ListIterator<T> implements Iterator<List<T>> {

        private final List<T> list;
        private final IndexIterator indexes;

        ListIterator(List<T> list) {
            this.list = new ArrayList<>(list);
            this.indexes = new IndexIterator(list.size());
        }

        @Override
        public boolean hasNext() {
            return indexes.hasNext();
        }

        @Override
        public List<T> next() {
            List<T> result = new ArrayList<>(list.size());
            for (int i : indexes.next())
                result.add(list.get(i));
            return result;
        }
    }

    public static <T> Iterable<List<T>> iterable(List<T> list) {
        return () -> new ListIterator<>(list);
    }

    public static <T> Stream<List<T>> stream(List<T> list) {
        return StreamSupport.stream(iterable(list).spliterator(), false);
    }
}
