package test.puzzle;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

public class TestSortQiita {

    @Test
    void testSortIntArray() {
        int[] ints = {4, 5, 2, 6, 1, 3, 7, 0};
        Arrays.sort(ints);
        System.out.println(Arrays.toString(ints));
        // [0, 1, 2, 3, 4, 5, 6, 7]
    }

    @Test
    void testSortIntArrayReverseBoxed() {
        int[] ints = {4, 5, 2, 6, 1, 3, 7, 0};
        int[] reverse = IntStream.of(ints)
            .boxed()
            .sorted(Comparator.reverseOrder())
            .mapToInt(i -> i)
            .toArray();
        System.out.println(Arrays.toString(reverse));
        // [7, 6, 5, 4, 3, 2, 1, 0]
    }

    @Test
    void testSortIntArrayReverseNegative() {
        int[] ints = {4, 5, 2, 6, 1, 3, 7, 0};
        int[] reverse = IntStream.of(ints)
            .map(i -> -i)
            .sorted()
            .map(i -> -i)
            .toArray();
        System.out.println(Arrays.toString(reverse));
        // [7, 6, 5, 4, 3, 2, 1, 0]
    }

    @Test
    void testSortIntArrayReverseNegativeMinValue() {
        int[] ints = {Integer.MIN_VALUE, 5, 2, 6, 1, 3, 7, 0};
        int[] reverse = IntStream.of(ints)
            .map(i -> -i)
            .sorted()
            .map(i -> -i)
            .toArray();
        System.out.println(Arrays.toString(reverse));
        // [-2147483648, 7, 6, 5, 3, 2, 1, 0]
    }

    @Test
    void testNegativeMinValue() {
        System.out.println("MIN_VALUE = " + Integer.MIN_VALUE);
        System.out.println("-MIN_VALUE = " + -Integer.MIN_VALUE);
        System.out.println("MAX_VALUE = " + Integer.MAX_VALUE);
        System.out.println("-MAX_VALUE = " + -Integer.MAX_VALUE);
        // MIN_VALUE = -2147483648
        // -MIN_VALUE = -2147483648
        // MAX_VALUE = 2147483647
        // -MAX_VALUE = -2147483647

    }

    @Test
    void testNotMinValue() {
        System.out.println("MIN_VALUE = " + Integer.MIN_VALUE);
        System.out.println("~MIN_VALUE = " + ~Integer.MIN_VALUE);
        System.out.println("MAX_VALUE = " + Integer.MAX_VALUE);
        System.out.println("~MAX_VALUE = " + ~Integer.MAX_VALUE);
        // MIN_VALUE = -2147483648
        // ~MIN_VALUE = 2147483647
        // MAX_VALUE = 2147483647
        // ~MAX_VALUE = -2147483648
    }

    @Test
    void testSortIntArrayReverseNotMinValue() {
        int[] ints = {Integer.MIN_VALUE, 5, 2, 6, 1, 3, 7, 0};
        int[] reverse = IntStream.of(ints)
            .map(i -> ~i)
            .sorted()
            .map(i -> ~i)
            .toArray();
        System.out.println(Arrays.toString(reverse));
        // [7, 6, 5, 3, 2, 1, 0, -2147483648]
    }

    static class IntArrayWrapList extends AbstractList<Integer> {

        int[] ints;

        public IntArrayWrapList(int[] ints) {
            this.ints = ints;
        }

        @Override
        public Integer get(int index) {
            return ints[index];
        }

        @Override
        public Integer set(int index, Integer element) {
            int old = ints[index];
            ints[index] = element;
            return old;
        }

        @Override
        public int size() {
            return ints.length;
        }

    }

    @Test
    public void testIntArrayWrapList() {
        int[] ints = {4, 5, 2, 6, 1, 3, 7, 0};
        new IntArrayWrapList(ints).sort(Comparator.reverseOrder());
        System.out.println(Arrays.toString(ints));
        // [7, 6, 5, 4, 3, 2, 1, 0]
    }

    static int partition0(int arr[], int begin, int end) {
        int pivot = arr[end];
        int i = (begin - 1);
        for (int j = begin; j < end; j++) {
            if (arr[j] <= pivot) {
                i++;
                int swapTemp = arr[i];
                arr[i] = arr[j];
                arr[j] = swapTemp;
            }
        }
        int swapTemp = arr[i + 1];
        arr[i + 1] = arr[end];
        arr[end] = swapTemp;
        return i + 1;
    }

    public static void quickSort0(int arr[], int begin, int end) {
        if (begin < end) {
            int partitionIndex = partition0(arr, begin, end);
            quickSort0(arr, begin, partitionIndex - 1);
            quickSort0(arr, partitionIndex + 1, end);
        }
    }

    @Test
    public void testQuickSort0() {
        int[] ints = {4, 5, 2, 6, 1, 3, 7, 0};
        quickSort0(ints, 0, ints.length - 1);
        System.out.println(Arrays.toString(ints));
        // [0, 1, 2, 3, 4, 5, 6, 7]
    }

//    @FunctionalInterface
//    public interface IntBinaryOperator {
//        int applyAsInt​(int left, int right);
//    }

    @FunctionalInterface
    interface IntBiConsumer {
        void accept(int a, int b);
    }

    static int partition1(IntBinaryOperator comparator, IntBiConsumer swapper, int begin, int end) {
        int pivotIndex = end;
        int i = (begin - 1);
        for (int j = begin; j < end; j++) {
            if (comparator.applyAsInt(j, pivotIndex) <= 0) {
                i++;
                swapper.accept(i, j);
            }
        }
        swapper.accept(i + 1, end);
        return i + 1;
    }

    public static void quickSort1(IntBinaryOperator comparator, IntBiConsumer swapper, int begin,
        int end) {
        if (begin >= end)
            return;
        int partitionIndex = partition1(comparator, swapper, begin, end);
        quickSort1(comparator, swapper, begin, partitionIndex - 1);
        quickSort1(comparator, swapper, partitionIndex + 1, end);
    }

    @Test
    public void testQuickSort1() {
        int[] a = {4, 5, 2, 6, 1, 3, 7, 0};
        quickSort1(
            (l, r) -> Integer.compare(a[l], a[r]),
            (l, r) -> {
                int t = a[l];
                a[l] = a[r];
                a[r] = t;
            },
            0, a.length - 1);
        System.out.println(Arrays.toString(a));
        // [0, 1, 2, 3, 4, 5, 6, 7]
    }

    @Test
    public void testQuickSort1IntsReverse() {
        int[] a = {4, 5, 2, 6, 1, 3, 7, 0};
        quickSort1(
            (l, r) -> -Integer.compare(a[l], a[r]),
            (l, r) -> {
                int t = a[l];
                a[l] = a[r];
                a[r] = t;
            },
            0, a.length - 1);
        System.out.println(Arrays.toString(a));
        // [7, 6, 5, 4, 3, 2, 1, 0]
    }

    @Test
    public void testQuickSort1Doubles() {
        double[] a = {4D, 5D, 2D, 6D, 1D, 3D, 7D, 0};
        quickSort1(
            (l, r) -> Double.compare(a[l], a[r]),
            (l, r) -> {
                double t = a[l];
                a[l] = a[r];
                a[r] = t;
            },
            0, a.length - 1);
        System.out.println(Arrays.toString(a));
        // [0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0]
    }

    @Test
    public void testQuickSort1List() {
        List<Double> list = Arrays.asList(4D, 5D, 2D, 6D, 1D, 3D, 7D, 0D);
        quickSort1(
            (l, r) -> Double.compare(list.get(l), list.get(r)),
            (l, r) -> {
                double t = list.get(l);
                list.set(l, list.get(r));
                list.set(r, t);
            },
            0, list.size() - 1);
        System.out.println(list);
        // [0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0]
    }

    public static void quickSort2(IntBinaryOperator comparator, IntBiConsumer swapper, int begin, int end) {
        new Object() {

            int partition(int begin, int end) {
                int pivotIndex = end;
                int i = (begin - 1);
                for (int j = begin; j < end; j++) {
                    if (comparator.applyAsInt(j, pivotIndex) <= 0) {
                        i++;
                        swapper.accept(i, j);
                    }
                }
                swapper.accept(i + 1, end);
                return i + 1;
            }

            void sort(int begin, int end) {
                if (begin >= end)
                    return;
                int partitionIndex = partition(begin, end);
                sort(begin, partitionIndex - 1);
                sort(partitionIndex + 1, end);
            }
        }.sort(begin, end);
    }

    public static void quickSort2(IntBinaryOperator comparator, IntBiConsumer swapper, int size) {
        quickSort2(comparator, swapper, 0, size - 1);
    }

    public static <T> void sort(List<T> list, Comparator<T> comparator) {
        quickSort2(
            (l, r) -> comparator.compare(list.get(l), list.get(r)),
            (l, r) -> { T t = list.get(l); list.set(l, list.get(r)); list.set(r, t); },
            0, list.size() - 1);
    }

    public static void sort(int[] ints, Comparator<Integer> comparator) {
        quickSort2(
            (l, r) -> comparator.compare(ints[l], ints[r]),
            (l, r) -> { int t = ints[l]; ints[l] = ints[r]; ints[r] = t; },
            0, ints.length - 1);
    }

    public static void sort(int[] ints, IntBinaryOperator comparator) {
        quickSort2(
            (l, r) -> comparator.applyAsInt(ints[l], ints[r]),
            (l, r) -> { int t = ints[l]; ints[l] = ints[r]; ints[r] = t; },
            0, ints.length - 1);
    }

    @Test
    public void testSortIntsWithIntBinaryOperator() {
        int[] a = {4, 5, 2, 6, 1, 3, 7, 0};
        sort(a, (IntBinaryOperator)(l, r) -> -Integer.compare(l, r));
        System.out.println(Arrays.toString(a));
        // [7, 6, 5, 4, 3, 2, 1, 0]
    }

    static IntBiConsumer swap(IntUnaryOperator get, IntBiConsumer set) {
        return (l, r) -> {
            int temp = get.applyAsInt(l);
            set.accept(l, get.applyAsInt(r));
            set.accept(r, temp);
        };
    }

    @Test
    public void testSortMatrixColumn() {
        int[][] m = {
            {24, 9, 10, 11, 12},
            {23, 8, 1, 2, 13},
            {22, 7, 0, 3, 14},
            {21, 6, 5, 4, 15},
            {20, 19, 18, 17, 16},
        };
        int sortCol = 2;
        quickSort2(
            (l, r) -> Integer.compare(m[l][sortCol], m[r][sortCol]),
            swap(i -> m[i][sortCol], (i, v) -> m[i][sortCol] = v),
//            (l, r) -> {
//                int t = m[l][sortCol];
//                m[l][sortCol] = m[r][sortCol];
//                m[r][sortCol] = t; },
            0, m[sortCol].length - 1);
        for (int[] row : m)
            System.out.println(Arrays.toString(row));
        // [24, 9, 0, 11, 12]
        // [23, 8, 1, 2, 13]
        // [22, 7, 5, 3, 14]
        // [21, 6, 10, 4, 15]
        // [20, 19, 18, 17, 16]

    }

}
