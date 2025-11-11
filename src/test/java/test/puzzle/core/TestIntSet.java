package test.puzzle.core;

import java.util.Iterator;

public class TestIntSet {

    static class IntSet {
        static final int MAX_ELEMENT = 31;

        static void check(int e) {
            if (e < 0 || e > MAX_ELEMENT)
                throw new IllegalArgumentException();
        }

        public static int add(int self, int e) {
            check(e);
            return self | e;
        }

        public static int remove(int self, int e) {
            check(e);
            return self & ~e;
        }

        public static Iterator<Integer> iterator(int self) {
            throw new UnsupportedOperationException("Unimplemented method 'iterator'");
        }
    }

}
