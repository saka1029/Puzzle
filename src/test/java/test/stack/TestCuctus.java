package test.stack;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

/**
 * Cactus stack (Spaghetti stack) ベースのプログラミング言語
 * Cactus stack を使うことで、クロージャを実現することが狙いです。
 */
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
        public Consumer<String> trace;

        public Context(Consumer<String> trace) {
            this.trace = trace;
        }

        public Context() {
            this(s -> {});
        }

        public boolean isEmpty() {
            return stack == null;
        }

        public Context put(String name, Value v) {
            globals.put(name, v);
            return this;
        }

        public Context put(String name, Executable e) {
            put(name, new Value() {
                @Override public void execute(Context c) { e.execute(c); }
                @Override public String toString() { return name; }
            });
            return this;
        }

        public Value get(String name) {
            Value result = globals.get(name);
//            if (result == null)
//                throw new RuntimeException("'" + name + "' not found in context");
            return result;
        }

        public void push(Value value) {
            stack = new Cuctus(value, stack);
        }

        public Value peek(int n) {
            Cuctus c = stack;
            for ( ; n > 0; --n)
                c = c.previous;
            return c.value;
        }

        public Value pop() {
            Value result = stack.value;
            stack = stack.previous;
            return result;
        }

        int nest = 0;

        public void trace(String s) {
            trace.accept("  ".repeat(nest) + s);
        }

        public void execute(Value v) {
            if (trace == null) {
                v.execute(this);
                return;
            }
            trace("> " + v);
            ++nest;
            v.execute(this);
            --nest;
            trace("< " + this);
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
            c.trace("< " + c);
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

    static class IndirectCall extends Value {
        final String name;

        IndirectCall(String name) {
            this.name = name;
        }

        public static IndirectCall of (String name) {
            return new IndirectCall(name);
        }

        @Override
        public void execute(Context c) {
            Value v = c.get(name);
            if (v == null)
                throw new RuntimeException("'" + name + "' not defined in context");
            v.execute(c);
        }

        @Override
        public String toString() {
            return name;
        }
    }

    static void methodName() {
        logger.info("*** " + Thread.currentThread().getStackTrace()[2].getMethodName());
    }

    public static Context context() {
        return new Context(logger::info)
            .put("dup", c -> c.push(c.peek(0)))
            .put("over", c -> c.push(c.peek(1)))
            .put("swap", c -> { Value a = c.pop(), b = c.pop(); c.push(a); c.push(b); })
            .put("drop", c -> c.pop())
            .put("execute", c -> c.pop().run(c))
            .put("if", c -> {
                Value elsePart = c.pop(), thenPart = c.pop(), condition = c.pop();
                if (((Bool)condition).value)
                    thenPart.run(c);
                else
                    elsePart.run(c);
            })
            .put("+", c -> { Value r = c.pop(); c.push(c.pop().add(r)); })
            .put("-", c -> { Value r = c.pop(); c.push(c.pop().sub(r)); })
            .put("==", c -> { Value r = c.pop(); c.push(Bool.of(c.pop().equals(r))); })
            .put("!=", c -> { Value r = c.pop(); c.push(Bool.of(!c.pop().equals(r))); })
            .put("<", c -> { Value r = c.pop(); c.push(Bool.of(c.pop().compareTo(r) < 0)); })
            .put("<=", c -> { Value r = c.pop(); c.push(Bool.of(c.pop().compareTo(r) <= 0)); })
            .put(">", c -> { Value r = c.pop(); c.push(Bool.of(c.pop().compareTo(r) > 0)); })
            .put(">=", c -> { Value r = c.pop(); c.push(Bool.of(c.pop().compareTo(r) >= 0)); })
        ;
    }

    Context context = context();

    @Test
    void testDupDrop() {
        methodName();
        List block = List.of(Int.of(1), Int.of(2), context.get("drop"), context.get("dup"), context.get("drop"));
        block.run(context);
        assertEquals(Int.of(1), context.pop());
        assertTrue(context.isEmpty());
    }

    @Test
    void testOver() {
        methodName();
        List block = List.of(Int.of(1), Int.of(2), context.get("over"));
        block.run(context);
        assertEquals(Int.of(1), context.pop());
        assertEquals(Int.of(2), context.pop());
        assertEquals(Int.of(1), context.pop());
        assertTrue(context.isEmpty());
    }

    @Test
    void testSwap() {
        methodName();
        List block = List.of(Int.of(1), Int.of(2), context.get("swap"));
        block.run(context);
        assertEquals(Int.of(1), context.pop());
        assertEquals(Int.of(2), context.pop());
        assertTrue(context.isEmpty());
    }

    @Test
    void testPlus() {
        methodName();
        List block = List.of(Int.of(1), Int.of(2), context.get("+"));
        block.run(context);
        assertEquals(Int.of(3), context.pop());
        assertTrue(context.isEmpty());
    }

    @Test
    void testIndirectCall() {
        methodName();
        List block = List.of(Int.of(1), Int.of(2), IndirectCall.of("+"));
        block.run(context);
        assertEquals(Int.of(3), context.pop());
        assertTrue(context.isEmpty());
    }

    @Test
    void testExecute() {
        methodName();
        List block = List.of(List.of(Int.of(1), Int.of(2), IndirectCall.of("+")), context.get("execute"));
        block.run(context);
        assertEquals(Int.of(3), context.pop());
        assertTrue(context.isEmpty());
    }

    @Test
    void testIf() {
        methodName();
        List block = List.of(Bool.TRUE, Int.of(1), Int.of(2), context.get("if"));
        block.run(context);
        assertEquals(Int.of(1), context.pop());
        assertTrue(context.isEmpty());
        logger.info("---");
        block = List.of(Bool.FALSE, Int.of(1), Int.of(2), context.get("if"));
        block.run(context);
        assertEquals(Int.of(2), context.pop());
        assertTrue(context.isEmpty());
    }

    @Test
    void testIfFunction() {
        methodName();
        List block = List.of( Int.of(1), Int.of(2),
            Bool.TRUE, List.of(context.get("+")), List.of(context.get("-")), context.get("if"));
        block.run(context);
        assertEquals(Int.of(3), context.pop());
        assertTrue(context.isEmpty());
        logger.info("---");
        block = List.of( Int.of(1), Int.of(2),
            Bool.FALSE, List.of(context.get("+")), List.of(context.get("-")), context.get("if"));
        block.run(context);
        assertEquals(Int.of(-1), context.pop());
        assertTrue(context.isEmpty());
    }

}
