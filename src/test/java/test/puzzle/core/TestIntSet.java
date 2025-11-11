package test.puzzle.core;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.Test;

public class TestIntSet {

    static class IntSet {
        static final int MAX_ELEMENT = 31;

        static void check(int e) {
            if (e < 0 || e > MAX_ELEMENT)
                throw new IllegalArgumentException();
        }

        public static int numberOfTrailingZeros(int x) {
            if (x == 0) return(32);
            int n = 1;
            if ((x & 0x0000FFFF) == 0) {n = n +16; x = x >>16;}
            if ((x & 0x000000FF) == 0) {n = n + 8; x = x >> 8;}
            if ((x & 0x0000000F) == 0) {n = n + 4; x = x >> 4;}
            if ((x & 0x00000003) == 0) {n = n + 2; x = x >> 2;}
            return n - (x & 1);
        }

        public static int int2bit(int i) {
            check(i);
            return 1 << i;
        }

        public static int bit2int(int i) {
            check(i);
            return numberOfTrailingZeros(i);
        }

        public static int add(int self, int... elements) {
            for (int e : elements)
                self = self | int2bit(e);
            return self;
        }

        public static int remove(int self, int... elements) {
            for (int e : elements)
                self = self & ~int2bit(e);
            return self;
        }

        public static Iterable<Integer> bits(int self) {
            return () -> new Iterator<Integer>() {

                int bits = self;

                @Override
                public boolean hasNext() {
                    return bits != 0;
                }

                @Override
                public Integer next() {
                    int bit = -bits & bits;
                    bits ^= bit;
                    return bit;
                }
            };
        }

        public static Iterable<Integer> elements(int self) {
            return () -> new Iterator<Integer>() {

                int bits = self;

                @Override
                public boolean hasNext() {
                    return bits != 0;
                }

                @Override
                public Integer next() {
                    int bit = -bits & bits;
                    bits ^= bit;
                    return numberOfTrailingZeros(bit);
                }
            };
        }
    }

    @Test
    public void testIntSet() {
        int intSet = 0;
        intSet = IntSet.add(intSet, 0, 1, 2, 3, 4, 5, 6, 7);
        assertEquals(0b1111_1111, intSet);
        intSet = IntSet.remove(intSet, 1, 3, 5, 7);
        assertEquals(0b0101_0101, intSet);
        Set<Integer> bits = new HashSet<>();
        for (int i : IntSet.bits(intSet))
            bits.add(i);
        assertEquals(Set.of(1, 4, 16, 64), bits);
        Set<Integer> elements = new HashSet<>();
        for (int i : IntSet.elements(intSet))
            elements.add(i);
        assertEquals(Set.of(0, 2, 4, 6), elements);
    }
}
