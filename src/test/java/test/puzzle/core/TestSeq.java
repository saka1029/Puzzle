package test.puzzle.core;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import puzzle.core.Seq;

public class TestSeq {

    @Test
    public void testDrop() {
        Seq<Integer> seq = Seq.of(0, 1, 2, 3, 4);
        assertEquals(List.of(1, 2, 3, 4), seq.drop(0));
        assertEquals(List.of(0, 2, 3, 4), seq.drop(1));
        assertEquals(List.of(0, 1, 3, 4), seq.drop(2));
        assertEquals(List.of(0, 1, 2, 4), seq.drop(3));
        assertEquals(List.of(0, 1, 2, 3), seq.drop(4));
        assertEquals(List.of(0, 1, 2, 3, 4), seq);
        try {
            seq.drop(-1);
            fail();
        } catch (IndexOutOfBoundsException e) {}
        try {
            seq.drop(5);
            fail();
        } catch (IndexOutOfBoundsException e) {}
    }

    @Test
    public void testListSeq() {
        List<Integer> list = Arrays.asList(0, 1, 2, 3, 4);
        Seq<Integer> seq = Seq.of(list);
        assertEquals(list, seq);
        list.set(2, 20);
        assertEquals(List.of(0, 1, 20, 3, 4), list);
        assertEquals(List.of(0, 1, 2, 3, 4), seq);
        List<Integer> to = seq.stream().collect(Collectors.toList());
        assertEquals(seq, to);
        assertEquals(List.of(0, 1, 2, 3, 4), to);
        assertEquals(List.of(0, 1, 2), Seq.ofInt(0, 1, 2));
        int[] ints = {0, 1, 2};
        // Seq<int[]> intSeq1 = Seq.of(ints); // compile error
        Seq<int[]> intSeq2 = Seq.of(ints, ints); // compile ok
        assertEquals(2, intSeq2.size());
        assertArrayEquals(ints, intSeq2.get(0));
        Seq<int[]> intSeq3 = Seq.of(new int[][] {ints}); // compile ok
        assertEquals(1, intSeq3.size());
        assertArrayEquals(ints, intSeq3.get(0));
    }

    @Test
    public void testSubstSeq() {
        Seq<Integer> seq = Seq.of(0, 1, 2, 3, 4);
        Seq<Integer> subst = seq.subst(2, 20);
        assertEquals(List.of(0, 1, 20, 3, 4), subst);
        assertEquals(List.of(0, 1, 2, 3, 4), seq);
        Seq<String> str = Seq.of("a", "b", "c");
        Seq<String> stru = str.subst(1, "B");
        assertEquals(List.of("a", "B", "c"), stru);
        assertEquals(List.of("a", "b", "c"), str);
        try {
            seq.subst(-1, 0);
            fail();
        } catch (IndexOutOfBoundsException e) {}
        try {
            seq.subst(5, 0);
            fail();
        } catch (IndexOutOfBoundsException e) {}
    }

    @Test
    public void testConcatSeqs() {
        Seq<Integer> a = Seq.of(0, 1, 2);
        Seq<Integer> b = Seq.of(3, 4, 5);
        Seq<Integer> c = Seq.concat(a, b);
        assertEquals(List.of(0, 1, 2, 3, 4, 5), c);
        assertEquals(List.of(1, 2, 3, 4, 5), Seq.concat(a.drop(0), b));
        assertEquals(List.of(0, 1, 3, 4, 5), Seq.concat(a.drop(2), b));
        assertEquals(List.of(0, 1, 2, 3, 5), c.drop(4));
    }

    @Test
    public void testConcatElement() {
        assertEquals(List.of(0, 1, 2, 3, 4), Seq.concat(0, Seq.of(1, 2, 3, 4)));
        assertEquals(List.of(0, 1, 2, 3, 4), Seq.concat(Seq.of(0, 1, 2, 3), 4));
        assertEquals(List.of((byte) 0, (byte) 1, (byte) 2, (byte) 3, (byte) 4),
            Seq.concat((byte) 0, Seq.of((byte) 1, (byte) 2, (byte) 3, (byte) 4)));
        assertEquals(List.of(0L, 1L, 2L, 3L, 4L), Seq.concat(0L, Seq.of(1L, 2L, 3L, 4L)));
        assertEquals(List.of('0', '1', '2', '3', '4'), Seq.concat('0', Seq.chars("1234")));
        assertEquals(List.of("0", "1", "2", "3", "4"), Seq.concat("0", Seq.of("1", "2", "3", "4")));
        assertEquals(List.of("0", "1", "2", "3", "4"), Seq.concat(Seq.of("0", "1", "2", "3"), "4"));
    }

    @Test
    public void testCodePoints() {
        assertEquals(List.of((int) 'a', Character.codePointAt("𩸽", 0)), Seq.codePoints("a𩸽"));
    }

    @Test
    public void testSubst() {
    }

    static <T> void permutation(Seq<T> seq, Seq<T> acc, List<List<T>> result) {
        int size = seq.size();
        if (size <= 0)
            result.add(acc); // accは不変なのでコピーせずにaddできます。
        else
            for (int i = 0; i < size; ++i)
                permutation(seq.drop(i), Seq.concat(acc, seq.get(i)), result);
        // permutation(Seq.concat(seq.subSeq(0, i), seq.subSeq(i + 1)),
        //      Seq.concat(acc, seq.get(i)), result);
    }

    static <T> List<List<T>> permutation(Seq<T> seq) {
        List<List<T>> result = new ArrayList<>();
        permutation(seq, Seq.empty(), result);
        return result;
    }

    @Test
    public void test() {
        List<List<String>> result = permutation(Seq.of("a", "b", "c"));
        List<List<String>> expected = List.of(
            List.of("a", "b", "c"),
            List.of("a", "c", "b"),
            List.of("b", "a", "c"),
            List.of("b", "c", "a"),
            List.of("c", "a", "b"),
            List.of("c", "b", "a"));
        assertEquals(expected, result);
        List<String> result2 = permutation(Seq.chars("abc")).stream()
            .map(list -> list.stream()
                .map(c -> Character.toString(c))
                .collect(Collectors.joining()))
            .collect(Collectors.toList());
        assertEquals(List.of("abc", "acb", "bac", "bca", "cab", "cba"), result2);
    }

}
