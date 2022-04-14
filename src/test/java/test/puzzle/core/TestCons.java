package test.puzzle.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.junit.Test;

public class TestCons {

    static class CList<T> implements Iterable<T> {
        final Node<T> root;

        CList(Node<T> root) {
            this.root = root;
        }

        @SafeVarargs
        public static <T> CList<T> of(T... values) {
            Node<T> result = null;
            for (int i = values.length - 1; i >= 0; --i)
                result = new Node<>(values[i], result);
            return new CList<>(result);
        }

        public static <T> CList<T> of(List<T> list) {
            Node<T> result = null;
            for (int i = list.size() - 1; i >= 0; --i)
                result = new Node<>(list.get(i), result);
            return new CList<>(result);
        }
        
        public T head() {
            if (root == null) throw new UnsupportedOperationException();
            return root.value;
        }
        
        public CList<T> tail() {
            if (root == null) throw new UnsupportedOperationException();
            return new CList<>(root.next);
        }

        public CList<T> cons(T value) {
            return new CList<>(new Node<>(value, root));
        }
        
        public boolean isEmpty() {
            return root == null;
        }
        
        public int size() {
            int result = 0;
            for (Node<T> node = root; node != null; node = node.next)
                ++result;
            return result;
        }
        
        public T get(int index) {
            if (index < 0)
                throw new IndexOutOfBoundsException("index");
            Node<T> node = root;
            for ( ; node != null && index > 0; node = node.next)
                --index;
            if (node != null)
                return node.value;
            else
                throw new IndexOutOfBoundsException("index");
        }
        
        public List<T> toList() {
            List<T> result = new ArrayList<>();
            for (Node<T> node = root; node != null; node = node.next)
                result.add(node.value);
            return result;
        }
  
        public CList<T> reverse() {
            Node<T> reverse = null;
            for (Node<T> node = root; node != null; node = node.next)
                reverse = new Node<>(node.value, reverse);
            return new CList<>(reverse);
        }
        
        @Override
        public Iterator<T> iterator() {
            return new Iterator<T>() {
                
                Node<T> node = root;

                @Override
                public boolean hasNext() {
                    return node != null;
                }

                @Override
                public T next() {
                    T result = node.value;
                    node = node.next;
                    return result;
                }
            };
        }
        
        public Stream<T> stream() {
            return StreamSupport.stream(((Iterable<T>) () -> iterator()).spliterator(), false);
        }
  
        @Override
        public int hashCode() {
            return Objects.hashCode(root);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            CList<?> other = (CList<?>) obj;
            return Objects.equals(root, other.root);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            Node<T> node = root;
            if (node != null) {
                sb.append(node.value);
                for (node = node.next; node != null; node = node.next)
                    sb.append(" ").append(node.value);
            }
            sb.append(")");
            return sb.toString();
        }
        
        static class Node<V> {

            final V value;
            final Node<V> next;
            
            Node(V value, Node<V> next) {
                this.value = value;
                this.next = next;
            }

            @Override
            public int hashCode() {
                final int prime = 31;
                int result = 1;
                result = prime * result
                    + ((next == null) ? 0 : next.hashCode());
                result = prime * result
                    + ((value == null) ? 0 : value.hashCode());
                return result;
            }

            @Override
            public boolean equals(Object obj) {
                if (this == obj)
                    return true;
                if (obj == null)
                    return false;
                if (getClass() != obj.getClass())
                    return false;
                Node<?> other = (Node<?>) obj;
                return Objects.equals(value, other.value)
                    && Objects.equals(next, other.next);
            }
        }

    }

    @Test
    public void testHeadTail() {
        CList<Integer> clist = CList.of(1, 2, 3, 4);
        assertEquals(1, (int)clist.head());
        assertEquals(2, (int)clist.tail().head());
        assertEquals(3, (int)clist.tail().tail().head());
        assertEquals(4, (int)clist.tail().tail().tail().head());
        assertEquals(CList.of(2, 3, 4), clist.tail());
    }
   
    @Test
    public void testSize() {
        CList<Integer> clist = CList.of(1, 2, 3, 4);
        assertEquals(4, clist.size());
        assertEquals(0, CList.of().size());
    }
   
    @Test
    public void testGet() {
        CList<Integer> clist = CList.of(1, 2, 3, 4);
        assertEquals(1, (int)clist.get(0));
        assertEquals(2, (int)clist.get(1));
        assertEquals(3, (int)clist.get(2));
        assertEquals(4, (int)clist.get(3));
    }
   
    @Test
    public void testToList() {
        CList<Integer> clist = CList.of(1, 2, 3, 4);
        assertEquals(List.of(1, 2, 3, 4), clist.toList());
    }
    
    @Test
    public void testIterable() {
        CList<Integer> clist = CList.of(1, 2, 3, 4);
        Iterator<Integer> it = clist.iterator();
        assertTrue(it.hasNext()); assertEquals(1, (int)it.next());
        assertTrue(it.hasNext()); assertEquals(2, (int)it.next());
        assertTrue(it.hasNext()); assertEquals(3, (int)it.next());
        assertTrue(it.hasNext()); assertEquals(4, (int)it.next());
        assertFalse(it.hasNext());
        assertFalse(CList.of().iterator().hasNext());
    }
    
    @Test
    public void testReverse() {
        assertEquals(CList.of(4, 3, 2, 1), CList.of(1, 2, 3, 4).reverse());
    }
        
    @Test
    public void testStream() {
        CList<Integer> clist = CList.of(1, 2, 3, 4);
        assertEquals(10, clist.stream().mapToInt(Integer::intValue).sum());
    }
    
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
