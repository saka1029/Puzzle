package test.puzzle.core;

import org.junit.Test;

import puzzle.core.Cons;

public class TestPermutation2 {

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

    static <T> Cons<Cons<T>> select(Cons<T> list) {
        Cons<Cons<T>> result = Cons.nil();
        for (T x : list)
            result.cons(x.

        return result;
    }

    @Test
    public void testSelect() {
    }
}
