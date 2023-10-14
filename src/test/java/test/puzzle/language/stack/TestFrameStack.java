package test.puzzle.language.stack;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.Test;

public class TestFrameStack {

    interface Executable {
        void execute(Context c);
    }

    static class Context {
        final Map<String, Executable> globals = new HashMap<>();
        final Executable[] stack;
        final int[] frame;
        int sp = 0, fp = 0;

        Context(int stackSize, int frameSize) {
            this.stack = new Executable[stackSize];
            this.frame = new int[frameSize];
        }
        
        void push(Executable e) {
            stack[sp++] = e;
        }
        
        Object pop() {
            return stack[--sp];
        }
        
        void load(int f, int n) {
            
        }
    }
    
    static class List implements Executable, Iterable<Executable> {
        final ArrayList<Executable> values = new ArrayList<>();

        List(Executable... elements) {
            for (Executable e : elements)
                values.add(e);
        }

        @Override
        public void execute(Context c) {
        }
            
        @Override
        public Iterator<Executable> iterator() {
            return values.iterator();
        }
    }
    
    interface Value extends Executable {
        @Override
        default void execute(Context c) {
            c.push(this);
        }
    }

    static class Int implements Value {
        final int value;
        
        Int(int value) {
            this.value = value;
        }
        
        static Int of(int value) {
            return new Int(value);
        }
        
        @Override
        public int hashCode() {
            return value;
        }
        
        @Override
        public boolean equals(Object obj) {
            return obj instanceof Int i && i.value == value;
        }

        @Override
        public String toString() {
            return "" + value;
        }
    }

    @Test
    public void test() {
        fail("Not yet implemented");
    }

}
