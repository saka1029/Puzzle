package test.puzzle.core;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;

public class TestSeries {

    interface Series<E> extends List<E> {

        Series<E> subst(int index, E value);
        Series<E> drop(int index);

        /*
         * static methods
         */
        @SafeVarargs
        static <E> Series<E> of(E... elements) {
            return new ArraySeries<>(elements);
        }

        @SuppressWarnings("unchecked")
        static <E> Series<E> of(List<E> list) {
            return ArraySeries.concat(list);
        }

        @SafeVarargs
        static <E> Series<E> concat(List<E>... lists) {
            return ArraySeries.concat(lists);
        }

        static <E> Series<E> concat(E head, List<E> tail) {
            return ArraySeries.concat(head, tail);
        }

        static <E> Series<E> concat(List<E> head, E tail) {
            return ArraySeries.concat(head, tail);
        }

    }

    static class ArraySeries<E> extends AbstractList<E> implements Series<E> {

        final Object[] array;

        private ArraySeries(Object[] array) {
            this.array = array;
        }

        @SuppressWarnings("unchecked")
        @Override
        public E get(int index) {
            return (E)array[index];
        }

        @Override
        public int size() {
            return array.length;
        }

        @Override
        public Series<E> subst(int index, E value) {
            Object[] n = Arrays.copyOf(array, array.length);
            n[index] = value;
            return new ArraySeries<>(n);
        }

        @Override
        public Series<E> drop(int index) {
            int length = array.length;
            if (index < 0 || index >= length)
                throw new IndexOutOfBoundsException("index");
            Object[] n = new Object[length - 1];
            System.arraycopy(array, 0, n, 0, index);
            System.arraycopy(array, index + 1, n, index, length - index - 1);
            return new ArraySeries<>(n);
        }

        @SuppressWarnings("unchecked")
        static <E> Series<E> concat(List<E>... lists) {
            int length = Arrays.stream(lists).mapToInt(List::size).sum();
            Object[] n = new Object[length];
            int i = 0;
            for (List<E> list : lists)
                for (E e : list)
                    n[i++] = e;
            return new ArraySeries<>(n);
        }

        static <E> Series<E> concat(E head, List<E> tail) {
            int length = tail.size();
            Object[] n = new Object[length + 1];
            int i = 0;
            n[i++] = head;
            for (E e : tail)
                n[i++] = e;
            return new ArraySeries<>(n);
        }

        static <E> Series<E> concat(List<E> head, E tail) {
            int length = head.size();
            Object[] n = new Object[length + 1];
            int i = 0;
            for (E e : head)
                n[i++] = e;
            n[i++] = tail;
            return new ArraySeries<>(n);
        }
    }

}