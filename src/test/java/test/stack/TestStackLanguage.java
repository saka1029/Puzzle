package test.stack;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

public class TestStackLanguage {

    public interface Value extends Comparable<Value>, Iterable<Value> {

        default Iterator<Value> iterator() {
            throw new UnsupportedOperationException();
        }

        @Override
        default int compareTo(Value o) {
            throw new UnsupportedOperationException();
        }

        default Value eq(Value right) {
            return Bool.of(equals(right));
        }

        default Value ne(Value right) {
            return Bool.of(!equals(right));
        }

        default Value add(Value right) {
            throw new UnsupportedOperationException();
        }

        default Value mul(Value right) {
            throw new UnsupportedOperationException();
        }

        default Value mod(Value right) {
            throw new UnsupportedOperationException();
        }

        default Value map(Function<Value, Value> mapper) {
            return new Value() {

                @Override
                public Iterator<Value> iterator() {
                    return new Iterator<Value>() {

                        final Iterator<Value> iterator = Value.this.iterator();

                        @Override
                        public boolean hasNext() {
                            return iterator.hasNext();
                        }

                        @Override
                        public Value next() {
                            return mapper.apply(iterator.next());
                        }
                    };
                }
            };
        }

        default Value filter(Function<Value, Value> filter) {
            return new Value() {

                @Override
                public Iterator<Value> iterator() {
                    return new Iterator<Value>() {

                        final Iterator<Value> iterator = Value.this.iterator();
                        boolean hasNext = advance();
                        Value next;

                        boolean advance() {
                            while (iterator.hasNext()) {
                                next = iterator.next();
                                if (((Bool)filter.apply(next)).value)
                                    return true;
                            }
                            return false;
                        }

                        @Override
                        public boolean hasNext() {
                            return hasNext;
                        }

                        @Override
                        public Value next() {
                            Value result = next;
                            hasNext = advance();
                            return result;
                        }
                    };
                }
            };
        }

    }

    public static class Bool implements Value {

        public static final Bool TRUE = new Bool(true);
        public static final Bool FALSE = new Bool(false);

        public final boolean value;

        private Bool(boolean value) {
            this.value = value;
        }

        public static Bool of(boolean value) {
            return value ? TRUE : FALSE;
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj;
        }

        @Override
        public int compareTo(Value o) {
            return Boolean.compare(value, ((Bool)o).value);
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

    }

    public static class Int implements Value {

        public static final Int ZERO = new Int(0);
        public static final Int ONE = new Int(1);
        public static final Int TWO = new Int(2);

        public final int value;

        private Int(int value) {
            this.value = value;
        }

        public static Int of(int value) {
            return new Int(value);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (obj == this) return true;
            if (obj.getClass() != getClass()) return false;
            return value == ((Int)obj).value;
        }

        @Override
        public int compareTo(Value o) {
            return Integer.compare(value, ((Int)o).value);
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @Override
        public Value add(Value right) {
            return of(value + ((Int)right).value);
        }

        @Override
        public Value mul(Value right) {
            return of(value * ((Int)right).value);
        }

        @Override
        public Value mod(Value right) {
            return of(value % ((Int)right).value);
        }
    }

    public static class List implements Value {

        final Value[] values;

        private List(Value... values) {
            this.values = values;
        }

        public static List of(Value... values) {
            return new List(values.clone());
        }

        @Override
        public Iterator<Value> iterator() {
            return Arrays.asList(values).iterator();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (obj == this) return true;
            if (obj.getClass() != getClass()) return false;
            return Arrays.equals(values, ((List)obj).values);
        }

        @Override
        public int compareTo(Value o) {
            return Arrays.compare(values, ((List)o).values);
        }

        @Override
        public String toString() {
            return Arrays.stream(values)
                .map(Object::toString)
                .collect(Collectors.joining(" ", "[", "]"));
        }
    }

    @Test
    void testList() {
        List list = List.of(Int.of(0), Int.of(1), Int.of(2), Int.of(3));
        System.out.println(list);
        for (Value v : list)
            System.out.println(v);
        Value mapped = list.map(n -> n.mul(Int.of(10)));
        System.out.println(mapped);
        for (Value v : mapped)
            System.out.println(v);
        for (Value v : list.filter(n -> n.mod(Int.TWO).eq(Int.ZERO)))
            System.out.println(v);
    }

}
