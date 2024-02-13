package puzzle.sequence;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class Sequenciable<T> implements Iterable<T> {

    public abstract Sequence<T> sequence();

    @Override
    public int hashCode() {
        Sequence<T> s = sequence();
        T element;
        int hash = 7;
        while ((element = s.next()) != null)
            hash = hash * 13 + element.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Sequenciable right))
            return false;
        @SuppressWarnings("unchecked")
        Sequence<T> leftSequence = sequence(), rightSequence = right.sequence();
        T l = leftSequence.next(), r = rightSequence.next();
        for (; l != null && r != null; l = leftSequence.next(), r = rightSequence.next())
            if (!l.equals(r))
                return false;
        if (l == null && r == null)
            return true;
        return false;
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

    @SafeVarargs
    public static <T> Sequenciable<T> seq(T... args) {
        int size = args.length;
        return new Sequenciable<>() {
            @Override
            public Sequence<T> sequence() {
                int[] i = {0};
                return () -> i[0] < size ? args[i[0]++] : null;
            }
        };
    }

    public static <T> Sequenciable<T> seq(Iterable<T> iterable) {
        return new Sequenciable<>() {
            @Override
            public Sequence<T> sequence() {
                Iterator<T> it = iterable.iterator();
                return () -> 
                    it.hasNext() ? it.next() : null;
            }
        };
    }

//    public static Sequenciable<Integer> sequence(int... args) {
//        int size = args.length;
//        return new Sequenciable<>() {
//            @Override
//            public Sequence<Integer> sequence() {
//                int[] i = {0};
//                return () -> i[0] < size ? args[i[0]++] : null;
//            }
//        };
//    }

    public static <T> List<T> list(Sequenciable<T> s) {
        Sequence<T> sequence = s.sequence();
        ArrayList<T> list = new ArrayList<>();
        T element;
        while ((element = sequence.next()) != null)
            list.add(element);
        return list;
    }

    public static <T> T[] array(IntFunction<T[]> generator, Sequenciable<T> s) {
        return list(s).toArray(generator);
    }

    public static <T> Stream<T> stream(Sequenciable<T> s) {
        return StreamSupport.stream(
            Spliterators.spliteratorUnknownSize(s.iterator(), 0), false);
    }

    public static <T> Sequenciable<T> map(Function<T, T> function, Sequenciable<T> s) {
        return new Sequenciable<>() {
            @Override
            public Sequence<T> sequence() {
                Sequence<T> sequence = s.sequence();
                return () -> {
                    T element = sequence.next();
                    return element == null ? null : function.apply(element);
                };
            }
        };
    }
}
