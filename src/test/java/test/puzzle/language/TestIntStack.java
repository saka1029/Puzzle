package test.puzzle.language;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class TestIntStack {

    public interface Primitive {
        void eval(Context context);
    }

    public static class EvalException extends RuntimeException {
        private static final long serialVersionUID = 1L;
        EvalException(String message) {
            super(message);
        }
    }
    // masks
    public static final int TAG = 0xF0000000;
    public static final int DATA = 0x0FFFFFFF;
    // tags
    public static final int PRIM = 0x00000000;
    public static final int CALL = 0x10000000;
    public static final int PUSHCALL = 0x20000000;
    public static final int BRANCH = 0x30000000;
    public static final int INT = 0x40000000;
    public static final int STRING = 0x50000000;
    // int
    public static final int INTSIGN = 0x01000000;
    public static final int INTMINUS = 0xF0000000;

    public static class Context {

        final int[] stack;
        int sp = 0;
        final int[] code;
        int cp = 0;
        final int[] rs;
        int rp = 0;
        final Primitive[] prim;
        int pp = 0;
        
        int pc = 0;
        final Map<String, Integer> names = new HashMap<>();

        Context(int stackSize, int codeSize, int retSize, int natSize) {
            this.stack = new int[stackSize];
            this.code = new int[codeSize];
            this.rs = new int[retSize];
            this.prim = new Primitive[natSize];
        }

        void push(int value) {
            stack[sp++] = value;
        }

        int pop() {
            return stack[--sp];
        }
        
        void pushCode(int value) {
            code[cp++] = value;
        }
        
        void pushCode(String name) {
            Integer index = names.get(name);
            if (index == null)
                throw error("native %s undefined", name);
            code[cp++] = primValue(index);
        }

        void eval(String name) {
            Integer index = names.get(name);
            if (index == null)
                throw error("native %s undefined", name);
            prim[index & DATA].eval(this);
        }
        
        void run(int start) {
            pc = start;
            MAIN: while (true) {
                int c = code[pc], tag = c & TAG, data = c & DATA;
                switch (tag) {
                    case PRIM:
                        ++pc;
                        prim[data].eval(this);
                        if (pc < 0)
                            break MAIN;
                        break;
                    case CALL:
                        rs[rp++] = pc;
                        pc = data;
                        break;
                    case PUSHCALL:
                        ++pc;
                        push(CALL | data);
                        break;
                    case BRANCH:
                        pc = data;
                        break;
                    case INT:
                    case STRING:
                        ++pc;
                        push(c);
                        break;
                    default:
                        throw error("unknown tag %08x", tag);
                }
            }
        }
        
        void addPrim(String name, Primitive n) {
            int index = pp;
            prim[pp++] = n;
            names.put(name, primValue(index));
        }

        public static void addStandardNative(Context context) {
            context.addPrim("end", c -> c.pc = -1);
            context.addPrim("return", c -> c.pc = c.rs[--c.rp]);
            context.addPrim("exec", c -> {
                int value = c.pop(), tag = value & TAG, data = value & DATA;
                switch (tag) {
                    case PRIM:
                        c.prim[data].eval(c);
                        break;
                    case CALL:
                        c.rs[c.rp++] = c.pc;
                        c.pc = data;
                        break;
                    case INT:
                    case STRING:
                        c.push(value);
                        break;
                    default:
                        throw error("cant exec tag %08x", tag);
                }
            });
            context.addPrim("+", c -> {
                int r = intData(c.pop());
                c.push(intValue(intData(c.pop()) + r));
            });
        }

        public static Context of(int stackSize, int codeSize, int retSize, int natSize) {
            Context context = new Context(stackSize, codeSize, retSize, natSize);
            addStandardNative(context);
            return context;
        }
    }

    public static EvalException error(String format, Object... args) {
        return new EvalException(format.formatted(args));
    }

    public static int intValue(int data) {
        return INT | data & DATA;
    }

    public static int primValue(int data) {
        return PRIM | data & DATA;
    }

    public static int intData(int value) {
        if ((value & TAG) != INT)
            throw error("INT expected but %08x", value & TAG);
        return value & DATA;
    }

    public static int primData(int value) {
        if ((value & TAG) != PRIM)
            throw error("PRIM expected but %08x", value & TAG);
        return value & DATA;
    }

    public static class ParseException extends RuntimeException {
        private static final long serialVersionUID = 1L;
        ParseException(String message) {
            super(message);
        }
    }

    @Test
    public void testNative() {
        Context context = Context.of(100, 100, 100, 100);
        context.push(intValue(1));
        context.push(intValue(2));
        context.eval("+");
        assertEquals(3, intData(context.pop()));
    }
    
    @Test
    public void testCode() {
        Context context = Context.of(100, 100, 100, 100);
        context.pushCode(intValue(1));
        context.pushCode(intValue(2));
        context.pushCode("+");
        context.pushCode("end");
        context.run(0);
        assertEquals(3, intData(context.pop()));
    }
    
    @Test
    public void testCallReturn() {
        Context context = Context.of(100, 100, 100, 100);
        context.pushCode(INT | 1);
        context.pushCode(PUSHCALL | 3);
        context.pushCode(BRANCH | 5);
        context.pushCode(INT | 2);
        context.pushCode("return");
        context.pushCode("exec");
        context.pushCode("+");
        context.pushCode("end");
        context.run(0);
        assertEquals(3, intData(context.pop()));
    }
}
