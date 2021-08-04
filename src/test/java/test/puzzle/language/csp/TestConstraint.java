package test.puzzle.language.csp;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import puzzle.Common;

class TestConstraint {

    static Logger logger = Common.getLogger(TestConstraint.class);

    static int number(int... digits) {
        return IntStream.of(digits).reduce(0, (n, d) -> n * 10 + d);
    }

    Set<Integer> range(int start, int end) {
        Set<Integer> set = new HashSet<>();
        for (int i = start; i <= end; ++i)
            set.add(i);
        return set;
    }

    @SafeVarargs
    static boolean[] remove(int n, Set<Integer>... sets) {
        boolean[] b = new boolean[sets.length];
        int i = 0;
        for (Set<Integer> set : sets)
            b[i++] = set.remove(n);
        return b;
    }

    @SafeVarargs
    static void add(int n, boolean[] b, Set<Integer>... sets) {
        int i = 0;
        for (Set<Integer> set : sets)
            if (b[i++])
                set.add(n);
    }

    @Test
    void testSendMoreMoneySet() {
        logger.info(Common.methodName() + " start");
        long start = System.currentTimeMillis();
        Set<Integer> sd = range(1, 9);
        Set<Integer> ed = range(0, 9);
        Set<Integer> nd = range(0, 9);
        Set<Integer> dd = range(0, 9);
        Set<Integer> md = range(1, 9);
        Set<Integer> od = range(0, 9);
        Set<Integer> rd = range(0, 9);
        Set<Integer> yd = range(0, 9);
        for (int s : sd) {
            boolean[] sb = remove(s, ed, nd, dd, md, od, rd, yd);
            for (int e : ed) {
                boolean[] eb = remove(e, nd, dd, md, od, rd, yd);
                for (int n : nd) {
                    boolean[] nb = remove(n, dd, md, od, rd, yd);
                    for (int d : dd) {
                        boolean[] db = remove(d, md, od, rd, yd);
                        for (int m : md) {
                            boolean[] mb = remove(m, od, rd, yd);
                            for (int o : od) {
                                boolean[] ob = remove(o, rd, yd);
                                for (int r : rd) {
                                    boolean[] rb = remove(r, yd);
                                    for (int y : yd) {
                                        if (number(s, e, n, d)
                                          + number(m, o, r, e)
                                         == number(m, o, n, e, y))
                                            logger.info(String.format(
                                                "%d %d %d %d %d %d %d %d",
                                                s, e, n, d, m, o, r, y));
                                    }
                                    add(r, rb, yd);
                                }
                                add(o, ob, rd, yd);
                            }
                            add(m, mb, od, rd, yd);
                        }
                        add(d, db, md, od, rd, yd);
                    }
                    add(n, nb, dd, md, od, rd, yd);
                }
                add(e, eb, nd, dd, md, od, rd, yd);
            }
            add(s, sb, ed, nd, dd, md, od, rd, yd);
        }
        logger.info("elapsed " + (System.currentTimeMillis() - start) + "msec");
    }

    static final Map<Long, Integer> VALUES = new HashMap<>();
    static {
        long m = 1L;
        for (int i = 0; i < Long.SIZE; ++i, m <<= 1)
            VALUES.put(m, i);
    }

    static class Set64 implements Iterable<Integer> {
        long value = 0;

        static Set64 range(int start, int end) {
            Set64 s = new Set64();
            for (int i = start; i <= end; ++i)
                s.add(i);
            return s;
        }

        static Set64 of(int... elements) {
            Set64 s = new Set64();
            for (int i : elements)
                s.add(i);
            return s;
        }

//        int size() {
//            return Long.bitCount(value);
//        }

        void add(int n) {
            value |= 1L << n;
        }

        boolean remove(int n) {
            long m = 1L << n;
            if ((value & m) == 0)
                return false;
            value &= ~m;
            return true;
        }

        @Override
        public Iterator<Integer> iterator() {
            return new Iterator<Integer>() {
                long v = value;

                @Override
                public boolean hasNext() {
                    return v != 0;
                }

                @Override
                public Integer next() {
                    long bit = Long.lowestOneBit(v);
                    v &= ~bit;
                    return VALUES.get(bit);
                }
            };
        }
    }

    @Test
    public void testSet64() {
        Set64 s = Set64.range(1, 6);
//        Set64 s = Set64.of(1, 2, 3, 4, 5, 6);
        Set<Integer> si = new HashSet<>();
        for (int i : s)
            si.add(i);
        assertEquals(Set.of(1, 2, 3, 4, 5, 6), si);
    }

    static boolean[] remove(int n, Set64... sets) {
        boolean[] b = new boolean[sets.length];
        int i = 0;
        for (Set64 set : sets)
            b[i++] = set.remove(n);
        return b;
    }

    static void add(int n, boolean[] b, Set64... sets) {
        int i = 0;
        for (Set64 set : sets)
            if (b[i++])
                set.add(n);
    }

    @Test
    void testSendMoreMoneySet64() {
        logger.info(Common.methodName() + " start");
        long start = System.currentTimeMillis();
        Set64 sd = Set64.range(1, 9);
        Set64 ed = Set64.range(0, 9);
        Set64 nd = Set64.range(0, 9);
        Set64 dd = Set64.range(0, 9);
        Set64 md = Set64.range(1, 9);
        Set64 od = Set64.range(0, 9);
        Set64 rd = Set64.range(0, 9);
        Set64 yd = Set64.range(0, 9);
        for (int s : sd) {
            boolean[] sb = remove(s, ed, nd, dd, md, od, rd, yd);
            for (int e : ed) {
                boolean[] eb = remove(e, nd, dd, md, od, rd, yd);
                for (int n : nd) {
                    boolean[] nb = remove(n, dd, md, od, rd, yd);
                    for (int d : dd) {
                        boolean[] db = remove(d, md, od, rd, yd);
                        for (int m : md) {
                            boolean[] mb = remove(m, od, rd, yd);
                            for (int o : od) {
                                boolean[] ob = remove(o, rd, yd);
                                for (int r : rd) {
                                    boolean[] rb = remove(r, yd);
                                    for (int y : yd) {
                                        if (number(s, e, n, d)
                                          + number(m, o, r, e)
                                         == number(m, o, n, e, y))
                                            logger.info(String.format(
                                                "%d %d %d %d %d %d %d %d",
                                                s, e, n, d, m, o, r, y));
                                    }
                                    add(r, rb, yd);
                                }
                                add(o, ob, rd, yd);
                            }
                            add(m, mb, od, rd, yd);
                        }
                        add(d, db, md, od, rd, yd);
                    }
                    add(n, nb, dd, md, od, rd, yd);
                }
                add(e, eb, nd, dd, md, od, rd, yd);
            }
            add(s, sb, ed, nd, dd, md, od, rd, yd);
        }
        logger.info("elapsed " + (System.currentTimeMillis() - start) + "msec");
    }

}
