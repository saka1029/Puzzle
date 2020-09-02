package test.puzzle;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import puzzle.Seq;

class TestSeq {

    @Test
    void testListSeq() {
        List<Integer> list = Arrays.asList(0, 1, 2, 3, 4);
        Seq<Integer> seq = Seq.of(list);
        assertEquals(list, seq);
        list.set(2, 20);
        assertEquals(List.of(0, 1, 20, 3, 4), list);
        assertEquals(List.of(0, 1, 2, 3, 4), seq);
        List<Integer> to = seq.stream().collect(Collectors.toList());
        assertEquals(seq, to);
        assertEquals(List.of(0, 1, 2, 3, 4), to);
        assertEquals(List.of(0, 1, 2), Seq.of(new int[] {0, 1, 2}));
    }

    @Test
    void testSubstSeq() {
        Seq<Integer> seq = Seq.of(0, 1, 2, 3, 4);
        Seq<Integer> subst = seq.subst(2, 20);
        assertEquals(List.of(0, 1, 20, 3, 4), subst);
        assertEquals(List.of(0, 1, 2, 3, 4), seq);
    }

    @Test
    void testConcatElement() {
        assertEquals(List.of(0, 1, 2, 3, 4), Seq.concat(0, Seq.of(1, 2, 3, 4)));
        assertEquals(List.of(0, 1, 2, 3, 4), Seq.concat(Seq.of(0, 1, 2, 3), 4));
        assertEquals(List.of((byte)0, (byte)1, (byte)2, (byte)3, (byte)4), Seq.concat((byte)0, Seq.of((byte)1, (byte)2, (byte)3, (byte)4)));
        assertEquals(List.of(0L, 1L, 2L, 3L, 4L), Seq.concat(0L, Seq.of(1L, 2L, 3L, 4L)));
        assertEquals(List.of('0', '1', '2', '3', '4'), Seq.concat('0', Seq.chars("1234")));
        assertEquals(List.of("0", "1", "2", "3", "4"), Seq.concat("0", Seq.of("1", "2", "3", "4")));
        assertEquals(List.of("0", "1", "2", "3", "4"), Seq.concat(Seq.of("0", "1", "2", "3"), "4"));
    }

    @Test
    void testCodePoints() {
        assertEquals(List.of((int)'a', Character.codePointAt("𩸽", 0)), Seq.codePoints("a𩸽"));
    }

    static <T> void permutation(Seq<T> seq, Seq<T> acc, List<List<T>> result) {
        int size = seq.size();
        if (size <= 0)
            result.add(acc);    // accは不変なのでコピーせずにaddできます。
        else
            for (int i = 0; i < size; ++i)
                permutation(Seq.concat(seq.subSeq(0, i), seq.subSeq(i + 1)), Seq.concat(acc, seq.get(i)), result);
    }

    static <T> List<List<T>> permutation(Seq<T> seq) {
        List<List<T>> result = new ArrayList<>();
        permutation(seq, Seq.empty(), result);
        return result;
    }

    @Test
    void test() {
        List<List<String>> result = permutation(Seq.of("a", "b", "c"));
        List<List<String>> expected = List.of(
            List.of("a", "b", "c"),
            List.of("a", "c", "b"),
            List.of("b", "a", "c"),
            List.of("b", "c", "a"),
            List.of("c", "a", "b"),
            List.of("c", "b", "a")
        );
        assertEquals(expected, result);
        List<String> result2 = permutation(Seq.chars("abc")).stream()
            .map(list -> list.stream()
                .map(c -> Character.toString(c))
                .collect(Collectors.joining()))
            .collect(Collectors.toList());
        assertEquals(List.of("abc", "acb", "bac", "bca", "cab", "cba"), result2);
    }

}
