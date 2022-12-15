package test.puzzle.language;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.junit.Test;

public class TestIntLisp {
    
    public interface Obj {
    }
    
    public static class Cons implements Obj{
        public final Obj car, cdr;
        
        private Cons(Obj car, Obj cdr) {
            this.car = car;
            this.cdr = cdr;
        }
        
        public static Cons of(Obj car, Obj cdr) {
            return new Cons(car, cdr);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(car, cdr);
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj
                || obj instanceof Cons c
                    && c.car.equals(car)
                    && c.cdr.equals(cdr);
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("(");
            sb.append(car);
            Obj tail = cdr;
            for (; tail instanceof Cons c; tail = c.cdr)
                sb.append(" ").append(c.car);
            if (tail != NIL)
                sb.append(" . ").append(tail);
            sb.append(")");
            return sb.toString();
        }
    }

    public interface Atom extends Obj {
    }
    
    
    public static class Symbol implements Atom {
        public final String name;
        static final Map<String, Symbol> ALL = new HashMap<>();
        
        private Symbol(String name) {
            this.name = name;
        }
        
        public static Symbol of(String name) {
            return ALL.computeIfAbsent(name, k -> new Symbol(k));
        }
        
        @Override
        public String toString() {
            return name;
        }
    }

    public static final Symbol NIL = new Symbol("nil");
    
    public static class Int implements Atom {
        public final int value;
        
        private Int(int value) {
            this.value = value;
        }
        
        public static Int of(int value) {
            return new Int(value);
        }

        @Override
        public int hashCode() {
            return value;
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj
                || obj instanceof Int i
                    && i.value == value;
        }

        @Override
        public String toString() {
            return "" + value;
        }
    }
    
    public static Cons cons(Obj car, Obj cdr) {
        return Cons.of(car, cdr);
    }

    public static Obj list(Obj... elements) {
        Obj result = NIL;
        for (int i = elements.length - 1; i >= 0; --i)
            result = Cons.of(elements[i], result);
        return result;
    }
    
    public static Symbol sym(String name) {
        return Symbol.of(name);
    }

    public static Int i(int value) {
        return Int.of(value);
    }
    
    public class RuntimeContext {
        final int[] stack;
        int sp = 0, bp = 0;
        
        private RuntimeContext(int stackSize) {
            this.stack = new int[stackSize];
        }
        
        public void push(int value) {
            stack[sp++] = value;
        }

        public int pop() {
            return stack[--sp];
        }
    }
    
    @FunctionalInterface
    public interface Code {
        void execute(RuntimeContext c);
    }
    
    public static class CompileContext {
        
    }

    @Test
    public void testConsToString() {
        assertEquals("(a . 2)", cons(sym("a"), i(2)).toString());
        assertEquals("(a 2)", list(sym("a"), i(2)).toString());
        assertEquals(list(sym("a"), i(2)), cons(sym("a"), cons(i(2), NIL)));
        assertEquals(NIL, list());
    }
}
