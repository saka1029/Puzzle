package test.stack;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

class TestCuctus {

    static String LOG_FORMAT_KEY = "java.util.logging.SimpleFormatter.format";
    static String LOG_FORMAT = "%1$tFT%1$tT.%1$tL %4$s %3$s %5$s %6$s%n";
    static {
        System.setProperty(LOG_FORMAT_KEY, LOG_FORMAT);
    }

    static final Logger logger = Logger.getLogger(TestCuctus.class.getSimpleName());

    static class Cuctus {
        final Value value;
        final Cuctus previous;

        Cuctus(Value value, Cuctus previous) {
            this.value = value;
            this.previous = previous;
        }
    }

    interface Executable {
        void execute(Context c);
    }

    static class Context {
        private final Map<String, Value> globals = new HashMap<>();
        private Cuctus stack = null;
        final Consumer<String> trace;

        public Context(Consumer<String> trace) {
            this.trace = trace;
        }

        public Context() {
            this(s -> {});
        }

        public boolean isEmpty() {
            return stack == null;
        }

        public void add(String name, Executable e) {
            globals.put(name, new Value() {
                @Override public void execute(Context c) { e.execute(c); }
                @Override public String toString() { return name; }
            });
        }

        public Value get(String name) {
            return globals.get(name);
        }

        public void push(Value value) {
            stack = new Cuctus(value, stack);
        }

        public Value pop() {
            Value result = stack.value;
            stack = stack.previous;
            return result;
        }

        public void execute(Value v) {
            if (trace == null)
                v.execute(this);
            else {
                String before = toString();
                v.execute(this);
                trace.accept(before + " : " + v + " -> " + this);
            }
        }

        private static void toString(Cuctus c, StringBuilder sb) {
            if (c == null) return;
            toString(c.previous, sb);
            if (c.previous != null) sb.append(" ");
            sb.append(c.value);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            toString(stack, sb);
            sb.append("]");
            return sb.toString();
        }

    }

    static abstract class Value implements Comparable<Value>, Executable {

        public void execute(Context c) { c.push(this); }
        public void run(Context c) { execute(c); }
        @Override public int compareTo(Value o) { throw new UnsupportedOperationException(); }

        public Value add(Value right) { throw new UnsupportedOperationException(); }
        public Value sub(Value right) { throw new UnsupportedOperationException(); }
        public Value mul(Value right) { throw new UnsupportedOperationException(); }

    }

    static class List extends Value {

        final Value[] values;

        private List(Value[] values) {
            this.values = values;
        }

        public static List of(Value... values) {
            return new List(values.clone());
        }

        @Override
        public void run(Context c) {
            for (Value v : values)
                c.execute(v);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (obj == this) return true;
            if (obj.getClass() != getClass()) return false;
            return Arrays.deepEquals(values, ((List)obj).values);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(values);
        }

        @Override
        public int compareTo(Value o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String toString() {
            return Arrays.stream(values)
                .map(Object::toString)
                .collect(Collectors.joining(" ", "[", "]"));
        }

    }

    static class Int extends Value {

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
        public int hashCode() {
            return Integer.hashCode(value);
        }

        @Override
        public int compareTo(Value o) {
            return Integer.compare(value, ((Int)o).value);
        }

        @Override
        public String toString() {
            return Integer.toString(value);
        }

        @Override public Value add(Value right) { return of(value + ((Int)right).value); }
        @Override public Value sub(Value right) { return of(value - ((Int)right).value); }
        @Override public Value mul(Value right) { return of(value * ((Int)right).value); }

    }

    static class Bool extends Value {

        public static final Bool TRUE = of(true);
        public static final Bool FALSE = of(false);

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
        public int hashCode() {
            return Boolean.hashCode(value);
        }

        @Override
        public int compareTo(Value o) {
            return Boolean.compare(value, ((Bool)o).value);
        }

        @Override
        public String toString() {
            return Boolean.toString(value);
        }
    }

    @Test
    void test() {
        Context context = new Context(logger::info);
        context.add("+", c -> { Value r = c.pop(); c.push(c.pop().add(r)); });
        context.add("==", c -> { Value r = c.pop(); c.push(Bool.of(c.pop().equals(r))); });
        context.add("<", c -> { Value r = c.pop(); c.push(Bool.of(c.pop().compareTo(r) < 0)); });
        List block = List.of(Int.of(1), Int.of(2), context.get("+"));
        block.run(context);
        System.out.println(block);
        System.out.println(context);
        assertEquals(Int.of(3), context.pop());
        assertTrue(context.isEmpty());
    }

}
