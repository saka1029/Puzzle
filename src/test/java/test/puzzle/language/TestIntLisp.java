package test.puzzle.language;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.IntBinaryOperator;

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

        public int length() {
            int length = 0;
            for (Obj o = this; o instanceof Cons c; o = c.cdr)
                ++length;
            return length;
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
    
    @FunctionalInterface
    public interface Compiler {
        void compile(Obj obj, CompilerContext context);
    }
    
    public static class CompilerContext {
        public final List<Code> codes = new ArrayList<>();
        public final Map<Symbol, Compiler> compilers = new HashMap<>();
        
        public void add(Symbol symbol, Compiler compiler) {
            compilers.put(symbol, compiler);
        }

        public void add(Code code) {
            codes.add(code);
        }
        
        public void compile(Obj obj) {
            if (obj instanceof Int i)
                codes.add(c -> c.push(i.value));
            else if (obj instanceof Cons c) {
                Compiler compiler = compilers.get(c.car);
                if (compiler != null)
                    compiler.compile(c.cdr, this);
                else
                    throw new RuntimeException("unknown function " + c.car);
            } else
                throw new RuntimeException("can't compile " + obj);
        }
        
        public void run(RuntimeContext rc) {
            for (Code code : codes)
                code.execute(rc);
        }
        
        public int compileGo(Obj obj, RuntimeContext rc) {
            codes.clear();
            compile(obj);
            run(rc);
            return rc.pop();
        }
        
    }
    
    /**
     * 2項演算子operatorを可変長引数に適用する。
     * +や*はこちらを使う。
     * @param unit 単位元。+の時は0、*の時は1
     * @param operator 二項演算子。
     * @return
     */
    public static Compiler compileBinary(Obj unit, IntBinaryOperator operator) {
        return (args, cc) -> {
            cc.compile(unit);
            for (Obj tail = args; tail instanceof Cons c0; tail = c0.cdr) {
                cc.compile(c0.car);
                cc.add(c -> { int r = c.pop(); c.push(operator.applyAsInt(c.pop(), r)); });
            }
        };
    }
    
    /**
     * 2項演算子operatorを可変長引数に適用する。
     * -や/はこちらを使う。
     * @param unit 単位元。-の時は0、/の時は1
     * @param operator 二項演算子。
     * @return
     */
    public static Compiler compileBinary2(Obj unit, IntBinaryOperator operator) {
        Code op = c -> { int r = c.pop(); c.push(operator.applyAsInt(c.pop(), r)); };
        return (args, cc) -> {
            if (args instanceof Cons c0) {
                if (c0.cdr instanceof Cons c1) {
                    cc.compile(c0.car);
                    for (Obj tail = c1; tail instanceof Cons c2; tail = c2.cdr) {
                        cc.compile(c2.car);
                        cc.add(op);
                    }
                } else {
                    cc.compile(unit);
                    cc.compile(c0.car);
                    cc.add(op);
                }
            } else
                throw new RuntimeException("insufficient arguments");
        };
    }

    @Test
    public void testConsToString() {
        assertEquals("(a . 2)", cons(sym("a"), i(2)).toString());
        assertEquals("(a 2)", list(sym("a"), i(2)).toString());
        assertEquals(list(sym("a"), i(2)), cons(sym("a"), cons(i(2), NIL)));
        assertEquals(NIL, list());
    }
    
    @Test
    public void testCompileInt() {
        CompilerContext cc = new CompilerContext();
        cc.compile(i(3));
        RuntimeContext context = new RuntimeContext(20);
        for (Code c : cc.codes)
            c.execute(context);
        assertEquals(3, context.pop());
    }
    
    @Test
    public void testCompileBinary() {
        CompilerContext compilerContext = new CompilerContext();
        compilerContext.add(sym("+"), compileBinary(i(0), (a, b) -> a + b));
        compilerContext.add(sym("-"), compileBinary2(i(0), (a, b) -> a - b));
        compilerContext.add(sym("*"), compileBinary(i(1), (a, b) -> a * b));
        compilerContext.add(sym("/"), compileBinary2(i(1), (a, b) -> a / b));
        RuntimeContext rc = new RuntimeContext(20);
        assertEquals(0, compilerContext.compileGo(list(sym("+")), rc));
        assertEquals(1, compilerContext.compileGo(list(sym("+"), i(1)), rc));
        assertEquals(3, compilerContext.compileGo(list(sym("+"), i(1), i(2)), rc));
        assertEquals(6, compilerContext.compileGo(list(sym("+"), i(1), i(2), i(3)), rc));
        try {
            assertEquals(0, compilerContext.compileGo(list(sym("-")), rc));
            fail();
        } catch (RuntimeException e) {
            assertEquals("insufficient arguments", e.getMessage());
        }
        assertEquals(-1, compilerContext.compileGo(list(sym("-"), i(1)), rc));
        assertEquals(-1, compilerContext.compileGo(list(sym("-"), i(1), i(2)), rc));
        assertEquals(-4, compilerContext.compileGo(list(sym("-"), i(1), i(2), i(3)), rc));
        assertEquals(1, compilerContext.compileGo(list(sym("*")), rc));
        assertEquals(1, compilerContext.compileGo(list(sym("*"), i(1)), rc));
        assertEquals(2, compilerContext.compileGo(list(sym("*"), i(1), i(2)), rc));
        assertEquals(6, compilerContext.compileGo(list(sym("*"), i(1), i(2), i(3)), rc));
        assertEquals(24, compilerContext.compileGo(list(sym("*"), i(1), i(2), i(3), i(4)), rc));
        try {
            assertEquals(1, compilerContext.compileGo(list(sym("/")), rc));
            fail();
        } catch (RuntimeException e) {
            assertEquals("insufficient arguments", e.getMessage());
        }
        assertEquals(1, compilerContext.compileGo(list(sym("/"), i(1)), rc));
        assertEquals(0, compilerContext.compileGo(list(sym("/"), i(2)), rc));
        assertEquals(2, compilerContext.compileGo(list(sym("/"), i(4), i(2)), rc));
        assertEquals(1, compilerContext.compileGo(list(sym("/"), i(8), i(4), i(2)), rc));
    }
}
