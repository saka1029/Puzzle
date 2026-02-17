package test.puzzle.core;

import org.junit.Test;

import puzzle.core.Cons;

public class TestPermutation2 {

    static <T> void permutation(Cons<T> list) {
        permutation(list, Cons.nil());
    }

    static <T> void permutation(Cons<T> list, Cons<T> aux) {
        if (list.isEmpty())
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
}
