package test.puzzle.core;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Spliterators;
import java.util.function.IntFunction;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.junit.Test;

public class TestSequenciable {

    interface Sequence<T> {
        T next();
    }

    static abstract class Sequenciable<T> implements Iterable<T> {
        public abstract Sequence<T> sequence();

        public List<T> toList() {
            Sequence<T> sequence = sequence();
            ArrayList<T> list = new ArrayList<>();
            T element;
            while ((element = sequence.next()) != null)
                list.add(element);
            return list;
        }

        public T[] toArray(IntFunction<T[]> generator) {
            return toList().toArray(generator);
        }
        
        public Stream<T> stream() {
            return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator(), 0), false);
        }

        @Override
        public Iterator<T> iterator() {
            return new Iterator<>() {
                Sequence<T> sequence = sequence();
                T element = sequence.next();

                @Override
                public boolean hasNext() {
                    return element != null;
                }

                @Override
                public T next() {
                    if (element == null)
                        throw new NoSuchElementException();
                    T result = element;
                    element = sequence.next();
                    return result;
                }
            };
        }
        
        @Override
        public String toString() {
            Sequence<T> sequence = sequence();
            StringBuilder sb = new StringBuilder("[");
            T element = sequence.next();
            if (element != null) {
                sb.append(element);
                element = sequence.next();
            }
            while (element != null) {
                sb.append(", ").append(element);
                element = sequence.next();
            }
            return sb.append("]").toString();
        }
        
    }

    @Test
    public void test() {
        fail("Not yet implemented");
    }

}
