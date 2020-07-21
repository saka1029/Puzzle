package experiment;

import static org.junit.jupiter.api.Assertions.*;

import java.io.Serializable;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;

import org.junit.jupiter.api.Test;


class TestRecord {

    @Test
    void testRecordIntInt() {
        record IntInt(int a, int b) {
        }
        assertEquals(new IntInt(1, 2), new IntInt(1, 2));
    }

    @Test
    void testRecordIntVarArgs() {
        record IntVarArgs(int... array) {
        }
        assertNotEquals(new IntVarArgs(1, 2), new IntVarArgs(1, 2));
    }

    @Test
    void testRecordIntArray() {
        record IntArray(int[] array) {
        }
        assertNotEquals(new IntArray(new int[] {1, 2}), new IntArray(new int[] {1, 2}));
    }

    @Test
    void testRecordIntegerArray() {
        record IntegerArray(Integer[] array) {
        }
        assertNotEquals(new IntegerArray(new Integer[] {1, 2}), new IntegerArray(new Integer[] {1, 2}));
    }

    @Test
    void testRecordIntegerVarArgs() {
        record IntegerVarArgs(Integer... array) {
        }
        assertNotEquals(new IntegerVarArgs(1, 2), new IntegerVarArgs(1, 2));
    }

    /**
     * hashCodeとequalsをoverrideすれば配列の中まで比較対象となる。
     */
    @Test
    void testRecordIntArrayOverrideEquals() {
        record IntArray(int[] a) {

            @Override
            public int hashCode() {
                return Arrays.hashCode(a);
            }

            @Override
            public boolean equals(Object obj) {
                if (obj == this)
                    return true;
                if (obj == null)
                    return false;
                if (obj.getClass() != getClass())
                    return false;
                return Arrays.equals(((IntArray) obj).a, a);
            }
        }
        assertEquals(new IntArray(new int[] {1, 2}), new IntArray(new int[] {1, 2}));
    }

    /**
     * hashCodeとequalsをoverrideすれば配列の中まで比較対象となる。
     */
    @Test
    void testRecordIntVarArgsOverrideEquals() {
        record IntVarArgs(int... a) {

            @Override
            public int hashCode() {
                return Arrays.hashCode(a);
            }

            @Override
            public boolean equals(Object obj) {
                if (obj == this)
                    return true;
                if (obj == null)
                    return false;
                if (obj.getClass() != getClass())
                    return false;
                return Arrays.equals(((IntVarArgs) obj).a, a);
            }

            // @Override
            // public String toString() {
            // return "IntVarArgs[a=" + Arrays.toString(a) + "]";
            // }
        }
        assertEquals(new IntVarArgs(1, 2), new IntVarArgs(1, 2));
        System.out.println(new IntVarArgs(1, 2));
        System.out.println(new IntVarArgs());
    }

    @Test
    public void testGetClass() {
        record Rec(int a) {}
        System.out.println(Rec.class.getName());
        System.out.println(new Rec(1).getClass().getName());
        System.out.println(new Rec(3).a);
        Rec r = new Rec(0);
        System.out.println(r instanceof Object);
        Object o = r;
        System.out.println(r instanceof Rec);
//        System.out.println(r instanceof Serializable);
        System.out.println(o instanceof Serializable);
        record Foo(int a) implements Comparable<Foo> {
            @Override
            public int compareTo(Foo o) {
                return Integer.compare(a, o.a);
            }
        }
    }

    @Test
    public void testGetRecordComponents() {
        record R(int a, String s) {}
        R r = new R(3, "foo");
        System.out.println(Arrays.toString(r.getClass().getRecordComponents()));
        RecordComponent[] comps = r.getClass().getRecordComponents();
        System.out.println(comps[0].getAccessor());
    }

}
