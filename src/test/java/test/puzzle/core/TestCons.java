package test.puzzle.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.Test;

import puzzle.core.Cons;

public class TestCons {

    @Test
    public void testCarCdr() {
        Cons<Integer> clist = Cons.of(1, 2, 3, 4);
        assertEquals(1, (int)clist.car());
        assertEquals(2, (int)clist.cdr().car());
        assertEquals(3, (int)clist.cdr().cdr().car());
        assertEquals(4, (int)clist.cdr().cdr().cdr().car());
        assertEquals(Cons.of(2, 3, 4), clist.cdr());
    }
   
    @Test
    public void testSize() {
        Cons<Integer> clist = Cons.of(1, 2, 3, 4);
        assertEquals(4, clist.size());
        assertEquals(0, Cons.of().size());
    }

    @Test
    public void testAppend() {
        Cons<Integer> a = Cons.of(1, 2, 3), b = Cons.of(4, 5);
        assertEquals(Cons.of(1, 2, 3, 4, 5), a.append(b));
    }

    @Test
    public void testRemove() {
        Cons<Integer> a = Cons.of(1, 2, 3, 2, 4, 5, 2, 8);
        assertEquals(Cons.of(1, 3, 4, 5, 8), a.remove(2));
    }
   
    // @Test
    // public void testGet() {
    //     Cons<Integer> clist = Cons.of(1, 2, 3, 4);
    //     assertEquals(1, (int)clist.get(0));
    //     assertEquals(2, (int)clist.get(1));
    //     assertEquals(3, (int)clist.get(2));
    //     assertEquals(4, (int)clist.get(3));
    // }
   
    // @Test
    // public void testToList() {
    //     Cons<Integer> clist = Cons.of(1, 2, 3, 4);
    //     assertEquals(List.of(1, 2, 3, 4), clist.toList());
    // }
    
    @Test
    public void testIterable() {
        Cons<Integer> clist = Cons.of(1, 2, 3, 4);
        Iterator<Integer> it = clist.iterator();
        assertTrue(it.hasNext()); assertEquals(1, (int)it.next());
        assertTrue(it.hasNext()); assertEquals(2, (int)it.next());
        assertTrue(it.hasNext()); assertEquals(3, (int)it.next());
        assertTrue(it.hasNext()); assertEquals(4, (int)it.next());
        assertFalse(it.hasNext());
        assertFalse(Cons.of().iterator().hasNext());
    }
    
    @Test
    public void testReverse() {
        assertEquals(Cons.of(4, 3, 2, 1), Cons.of(1, 2, 3, 4).reverse());
    }
        
    // @Test
    // public void testStream() {
    //     Cons<Integer> clist = Cons.of(1, 2, 3, 4);
    //     assertEquals(10, clist.stream().mapToInt(Integer::intValue).sum());
    // }
    
    static <T> List<T> cat(List<T> a, List<T> b) {
        List<T> result = new ArrayList<>(a);
        result.addAll(b);
        return result;
    }
    
    static <T> List<T> cons(T t, List<T> b) {
        List<T> result = new ArrayList<>();
        result.add(t);
        result.addAll(b);
        return result;
    }

    static <T> void perm(List<T> a, List<T> b) {
        if (a.isEmpty())
            System.out.println(b);
        else
            for (int i = 0; i < a.size(); ++i)
                perm(cat(a.subList(0, i), a.subList(i + 1, a.size())), cons(a.get(i), b));
    }

    @Test
    public void testSublist() {
        List<Integer> list = List.of(1, 2, 3);
//        perm(list, Collections.emptyList());
        int[] rev = IntStream.range(0, list.size())
            .map(i -> list.get(list.size() - i - 1))
            .toArray();
        System.out.println(Arrays.toString(rev));

    }

}
