package test.puzzle.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TestForthBlock {

    static class Stack<T> {
        ArrayList<T> list = new ArrayList<>();

        public boolean isEmpty() { return list.isEmpty(); }
        public int size() { return list.size(); }
        public T get(int index) { return list.get(index); }
        public void set(int index, T element) { list.set(index, element); }
        int top() { return list.size() - 1; }
        public T peek() { return get(top()); }
        public T peek(int index) { return get(top() - index); }
        public void push(T element) { list.add(element); }
        public T pop() { return list.remove(top()); }
        public void drop() { pop(); }
        public void dup() { push(peek()); }
        @Override public String toString() { return list.toString(); }
    }

    static class Context {
        final Stack<Object> stack = new Stack<>();
        final Stack<Integer> frames = new Stack<>();
        Object local(int frameNo, int offset) {
            return stack.get(frames.peek(frameNo) + offset);
        }
        void load(int frameNo, int offset) {
            stack.push(local(frameNo, offset));
        }
        void store(int frameNo, int offset) {
            stack.set(frames.peek(frameNo) + offset, stack.pop());
        }
        @Override
        public String toString() {
            return "Context[stack=" + stack + " frames=" + frames + "]";
        }
    }

    interface Executable {
        void execute(Context context);
    }

    static class Block implements Executable {
        final List<Executable> list;

        Block(Executable... executables) {
            this.list = List.of(executables);
        }

        @Override
        public void execute(Context context) {
            context.stack.push(this);
        }

        public void run(Context context) {
            context.frames.push(context.stack.size());
            for (Executable e : list)
                e.execute(context);
            context.frames.pop();
        }
        @Override
        public String toString() {
            return list.toString();
        }
    }

    static final Executable PLUS = c -> c.stack.push((int)c.stack.pop() + (int)c.stack.pop());
    @SuppressWarnings({"rawtypes", "unchecked"})
    static final Executable LE = c -> c.stack.push(((Comparable)c.stack.pop()).compareTo((Comparable)c.stack.pop()) >= 0);
    static final Executable EXEC = c -> ((Block)c.stack.pop()).run(c);
    static final Executable IF = c -> {
        Block otherwise = (Block)c.stack.pop();
        Block then = (Block)c.stack.pop();
        if ((boolean)c.stack.pop())
            then.run(c);
        else
            otherwise.run(c);
    };
    static final Executable WHILE = c -> {
        Block body = (Block)c.stack.pop();
        Block cond = (Block)c.stack.pop();
        while (true) {
            cond.run(c);
            if (!(boolean)c.stack.pop())
                break;
            body.run(c);
        }
    };
    static Executable push(Object element) { return c -> c.stack.push(element); }
    static Executable load(int frameNo, int offset) { return c -> c.load(frameNo, offset); }
    static Executable store(int frameNo, int offset) { return c -> c.store(frameNo, offset); }

    @Test
    public void testForth() {
        Context context = new Context();
        Block block = new Block(
            push(1),
            push(2),
            c -> assertEquals(1, c.local(0, 0)),
            c -> assertEquals(2, c.local(0, 1)),
            PLUS,
            new Block(
                push(4),
                c -> assertEquals(4, c.local(0, 0)),
                c -> assertEquals(3, c.local(1, 0)),
                PLUS
            ),
            EXEC,
            c -> assertEquals(7, c.local(0, 0)),
            c -> assertEquals(7, c.stack.pop())
        );
        block.run(context);
        assertTrue(context.stack.isEmpty());
    }

    @Test
    public void testIfThen() {
        Context context = new Context();
        Block block = new Block(
            push(true),
            new Block(push(1)),
            new Block(push(0)),
            IF,
            c -> assertEquals(1, c.stack.pop())
        );
        block.run(context);
        assertTrue(context.stack.isEmpty());
    }

    @Test
    public void testIfElse() {
        Context context = new Context();
        Block block = new Block(
            push(false),
            new Block(push(1)),
            new Block(push(0)),
            IF,
            c -> assertEquals(0, c.stack.pop())
        );
        block.run(context);
        assertTrue(context.stack.isEmpty());
    }

    @Test
    public void testWhile() {
        Context context = new Context();
        Block block = new Block(                              // [
            push(0),                                          //     sum
            push(0),                                          //     i
            new Block(load(1, 1), push(10), LE),              //     [i 10 <=]
            new Block(                                        //     [
                load(1, 0), load(1, 1), PLUS, store(1, 0),    //         sum i + =sum
                load(1, 1), push(1), PLUS, store(1, 1)        //         i 1 + =i
            ),                                                //     ]
            WHILE,                                            //     while
            c -> assertEquals(11, (int)c.stack.pop()),        //     i == 11
            c -> assertEquals(55, (int)c.stack.pop())         //     sum == 55
        );                                                    // ]
        block.run(context);
        assertTrue(context.stack.isEmpty());
    }

}
