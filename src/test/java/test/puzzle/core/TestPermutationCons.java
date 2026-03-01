package test.puzzle.core;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.junit.Test;

import puzzle.core.Cons;

public class TestPermutationCons {

    static <T> void permutation(Cons<T> list) {
        permutation(list, Cons.nil());
    }

    static <T> void permutation(Cons<T> list, Cons<T> aux) {
        if (list.isNull())
            System.out.println(aux.reverse());
        else
            for (T x : list)
                permutation(list.remove(x), aux.cons(x));
    }

    @Test
    public void testPermutation() {
        Cons<Integer> list = Cons.of(1, 2, 3, 4);
        permutation(list);
    }

    static <T> List<Cons<T>> permutationsList(Cons<T> list) {
        List<Cons<T>> result = new ArrayList<>();
        permutation(list, Cons.nil(), x -> result.add(x));
        return result;
    }

    static <T> void permutation(Cons<T> list, Cons<T> aux, Consumer<Cons<T>> callback) {
        if (list.isNull())
            callback.accept(aux.reverse());
        else
            for (T x : list)
                permutation(list.remove(x), aux.cons(x), callback);
    }

    @Test
    public void testPermutationsList() {
        Cons<Integer> list = Cons.of(1, 2, 3, 4);
        for (Cons<Integer> e : permutationsList(list))
            System.out.println(e);
    }

    static <T> List<Cons<T>> select(Cons<T> list) {
        return list.stream()
            .map(x -> list.remove(x).cons(x))
            .toList();
    }

    @Test
    public void testSelect() {
        assertEquals(List.of(
            Cons.of(1, 2, 3, 4),
            Cons.of(2, 1, 3, 4),
            Cons.of(3, 1, 2, 4),
            Cons.of(4, 1, 2, 3)),
            select(Cons.of(1,2,3,4)));
        System.out.println(select(Cons.of(2, 3, 4)));
    }
}
