package test.puzzle;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.BitSet;
import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.Test;

class TestPermutationB {

    static final Logger logger = Logger.getLogger(TestPermutationB.class.getName());

    static int permutationWithBitMap(int n, int r) {
        if (n < 0) throw new IllegalArgumentException("n must be >= 0");
        if (r < 0) throw new IllegalArgumentException("r must be >= 0");
        if (r > n) throw new IllegalArgumentException("r must be <= n");
        if (n > Integer.SIZE) throw new IllegalArgumentException("n must be <= " + Integer.SIZE);
        int[] selected = new int[r];
        int mask = n == Integer.SIZE ? -1 : (1 << n) - 1;
        return new Object() {
            int count = 0;

            void found() {
                ++count;
                // System.out.println(Arrays.toString(selected));
            }

            void permutation(int index, int used) {
                if (index >= r)
                    found();
                else {
                    for (int available = mask & ~used, bit; available != 0; available ^= bit) {
                        bit = Integer.lowestOneBit(available);
                        selected[index] = Integer.numberOfTrailingZeros(bit);
                        permutation(index + 1, used | bit);
                    }
                }
            }

            int run() {
                permutation(0, 0);
                return count;
            }
        }.run();
    }
    static int permutationWithBitMap2(int n, int r) {
        if (n < 0) throw new IllegalArgumentException("n must be >= 0");
        if (r < 0) throw new IllegalArgumentException("r must be >= 0");
        if (r > n) throw new IllegalArgumentException("r must be <= n");
        if (n > Integer.SIZE) throw new IllegalArgumentException("n must be <= " + Integer.SIZE);
        int[] selected = new int[r];
        return new Object() {
            int count = 0;

            void found() {
                ++count;
//                 System.out.println(Arrays.toString(selected));
            }

            void permutation(int index, int available) {
                if (index >= r)
                    found();
                else {
                    for (int rest = available, bit; rest != 0; rest ^= bit) {
                        bit = rest & -rest; // = Integer.lowestOneBit(rest);
                        selected[index] = Integer.numberOfTrailingZeros(bit);
                        permutation(index + 1, available ^ bit);
                    }
                }
            }

            int run() {
                permutation(0, n == Integer.SIZE ? -1 : (1 << n) - 1);
                return count;
            }
        }.run();
    }

    /**
     * 2020-08-16T20:08:10.330 情報 3884msec.
     * 2020-08-16T20:08:30.335 情報 3843msec.
     * 2020-08-16T21:26:21.109 情報 3656msec.
     *
     */
    @Test
    void test12() {
        int n = 12;
        long start = System.currentTimeMillis();
        assertEquals(479001600, permutationWithBitMap2(n, n));
        logger.info((System.currentTimeMillis() - start) + "msec.");
    }

    static class IntSet {

        private IntSet() {
        }

        public static int size(int intSet) {
            return Integer.bitCount(intSet);
        }

        public static boolean isEmpty(int intSet) {
            return intSet == 0;
        }

        public static boolean contains(int intSet, int element) {
            return (intSet & (1 << element)) != 0;
        }

        public static int of(int... elements) {
            int result = 0;
            for (int i : elements)
                result = add(result, i);
            return result;
        }

        public static int add(int intSet, int element) {
            return intSet | (1 << element);
        }

        public static int addAll(int intSet, int anotherIntSet) {
            return intSet | anotherIntSet;
        }

        public static int remove(int intSet, int element) {
            return intSet & ~(1 << element);
        }

        public static int removeAll(int intSet, int anotherIntSet) {
            return intSet & ~anotherIntSet;
        }

        public static String toString(int intSet) {
            return stream(intSet)
                .mapToObj(e -> Integer.toString(e))
                .collect(Collectors.joining(", ", "[", "]"));
        }

        public static PrimitiveIterator.OfInt iterator(int intSet) {
            return new PrimitiveIterator.OfInt() {

                int set = intSet;

                @Override
                public boolean hasNext() {
                    return set != 0;
                }

                /**
                 * "Hacker's Delight" second edition Henry S. Warren, Jr. 2–1
                 * Manipulating Rightmost Bits Some of the formulas in this
                 * section find application in later chapters. Use the following
                 * formula to turn off the rightmost 1-bit in a word, producing
                 * 0 if none (e.g., 01011000 ⇒ 01010000):
                 *
                 * x & (x – 1)
                 *
                 * Use the following formula to isolate the rightmost 1-bit,
                 * producing 0 if none (e.g., 01011000 ⇒ 00001000):
                 *
                 * x & (−x)
                 *
                 */
                @Override
                public int nextInt() {
                    if (!hasNext())
                        throw new NoSuchElementException();
                    int result = Integer.numberOfTrailingZeros(set);
                    set &= set - 1; // 右端の1ビットをクリアする。
                    return result;
                }
            };
        }

        public static Iterable<Integer> iterable(int intSet) {
            return () -> iterator(intSet);
        }

        public static IntStream stream(int intSet) {
            return StreamSupport.intStream(Spliterators.spliterator(iterator(intSet), size(intSet),
                Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SORTED | Spliterator.DISTINCT), false);
        }
    }

    @Test
    public void testIntSetAddRemove() {
        int intSet = 0;
        assertTrue(IntSet.isEmpty(intSet));
        intSet = IntSet.add(intSet, 0);
        intSet = IntSet.add(intSet, 3);
        assertEquals(2, IntSet.size(intSet));
        assertFalse(IntSet.isEmpty(intSet));
        assertTrue(IntSet.contains(intSet, 0));
        assertTrue(IntSet.contains(intSet, 3));
        assertFalse(IntSet.contains(intSet, 2));
        intSet = IntSet.remove(intSet, 0);
        assertEquals(1, IntSet.size(intSet));
        assertFalse(IntSet.contains(intSet, 0));
        assertEquals("[3]", IntSet.toString(intSet));
    }

    @Test
    public void testIntSetIterable() {
        int intSet = 0;
        intSet = IntSet.of(0, 1, 3, 5, 9, 10, 12);
        assertEquals(7, IntSet.size(intSet));
        for (int i : IntSet.iterable(intSet))
            System.out.println(i);
    }

    @Test
    public void testIntSetStream() {
        int[] elements = {0, 1, 3, 5, 9, 10, 12};
        int set = IntSet.of(elements);
        int[] result = IntSet.stream(set).toArray();
        System.out.println(Arrays.toString(result));
        assertArrayEquals(elements, result);
    }

    /**
     * 2020-08-15T17:58:57.699 情報 count=479001600 7528msec.
     */
    @Test
    public void testLoop() {
        long start = System.currentTimeMillis();
        int max = 12;
        int count = 0;
        int intSet = 0;
        for (int i = 0; i < max; ++i)
            intSet = IntSet.add(intSet, i);
        int za, zb, zc, zd, ze, zf, zg, zh, zi, zj, zk, zl;
        for (int a : IntSet.iterable(za = intSet))
            for (int b : IntSet.iterable(zb = IntSet.remove(za, a)))
                for (int c : IntSet.iterable(zc = IntSet.remove(zb, b)))
                    for (int d : IntSet.iterable(zd = IntSet.remove(zc, c)))
                        for (int e : IntSet.iterable(ze = IntSet.remove(zd, d)))
                            for (int f : IntSet.iterable(zf = IntSet.remove(ze, e)))
                                for (int g : IntSet.iterable(zg = IntSet.remove(zf, f)))
                                    for (int h : IntSet.iterable(zh = IntSet.remove(zg, g)))
                                        for (int i : IntSet.iterable(zi = IntSet.remove(zh, h)))
                                            for (int j : IntSet.iterable(zj = IntSet.remove(zi, i)))
                                                for (int k : IntSet.iterable(zk = IntSet.remove(zj, j)))
                                                    for (int l : IntSet.iterable(zl = IntSet.remove(zk, k)))
                                                        ++count;
        logger.info("count=" + count + " " + (System.currentTimeMillis() - start) + "msec.");
    }

    static int permutationWithBiSet(int n, int r) {
        int[] selected = new int[r];
        BitSet available = new BitSet(n);
        available.set(0, n);
        return new Object() {

            int count = 0;

            void permute(int index) {
                if (index >= r) {
                    ++count;
//                    System.out.println(Arrays.toString(selected));
                    return;
                }
                for (int i = available.nextSetBit(0); i >= 0; i = available.nextSetBit(i + 1)) {
                    selected[index] = i;
                    available.clear(i);
                    permute(index + 1);
                    available.set(i);
                }
            }

            int run() {
                permute(0);
                return count;
            }
        }.run();
    }

    @Test
    public void testBitSet() {
        int max = 4;
        BitSet b = new BitSet(max);
        System.out.println(b + " length=" + b.length() + " size=" + b.size());
        for (int i = max - 1; i >= 0; --i) {
            b.set(i);
            System.out.println(b + " length=" + b.length() + " size=" + b.size() + " cardinality=" + b.cardinality());
        }
    }

    /**
     * 2020-08-16T20:09:44.912 情報 11960msec.
     * 2020-08-16T20:10:14.683 情報 12527msec.
     */
//    @Test
    public void testPermutationWithBitSet() {
        assertEquals(24, permutationWithBiSet(4, 4));
        assertEquals(24, permutationWithBiSet(4, 3));
        assertEquals(12, permutationWithBiSet(4, 2));
        assertEquals(4, permutationWithBiSet(4, 1));
        assertEquals(1, permutationWithBiSet(4, 0));
        long start = System.currentTimeMillis();
        assertEquals(479001600, permutationWithBiSet(12, 12));
        logger.info(System.currentTimeMillis() - start + "msec.");
    }
}
