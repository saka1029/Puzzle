package test.puzzle.parsers;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;

public class TestIntFunctiuon {

    interface IntFunc {
        int eval(Context c);
        
        public static IntFunc thenElse(IntFunc cond, IntFunc then, IntFunc otherwise) {
            return c -> cond.eval(c) != 0 ? then.eval(c) : otherwise.eval(c);
        }
        
        public static IntFunc call(String name) {
            return c -> c.functions.get(name).eval(c);
        }
        
        public static IntFunc function(int argSize, IntFunc body) {
            return c -> {
                c.push(c.fp);
                c.fp = c.sp - 1;
                int result = body.eval(c);
                c.fp = c.pop();
                c.sp -= argSize;
                return result;
            };
        }
    }

    static class Context {
        final int[] stack;
        final Map<String, IntFunc> functions = new HashMap<>();
        int sp = 0, fp = 0;
        
        public Context(int stackSize) {
            this.stack = new int[stackSize];
        }
        
        public int pop() {
            return stack[--sp];
        }
        
        public void push(int value) {
            stack[sp++] = value;
        }
        
        @Override
        public String toString() {
            return IntStream.range(0, sp)
                .mapToObj(i -> Integer.toString(stack[i]))
                .collect(Collectors.joining(" ", "[", "]"));
        }
    }

    @Test
    public void test() {
        Context context = new Context(20);
        context.functions.put("add", IntFunc.function(2, c -> c.stack[c.fp - 1] + c.stack[c.fp - 2]));
        IntFunc adder = c -> {
            c.push(1);
            c.push(2);
            return IntFunc.call("add").eval(c);
        };
        assertEquals(3, adder.eval(context));
        assertEquals(0, context.sp);
    }

}
