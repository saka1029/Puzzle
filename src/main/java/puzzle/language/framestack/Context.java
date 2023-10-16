package puzzle.language.framestack;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Context {
    final Map<Symbol, Executable> globals = new HashMap<>();
    final Executable[] stack;
    final int[] frame;
    public int sp = 0, fp = 0;

    Context(int stackSize, int frameSize) {
        this.stack = new Executable[stackSize];
        this.frame = new int[frameSize];
    }
    
    public static Context of(int stackSize, int frameSize) {
        return new Context(stackSize, frameSize);
    }

    public void push(Executable e) {
        stack[sp++] = e;
    }

    public Executable pop() {
        return stack[--sp];
    }

    public Executable peek(int index) {
        return stack[sp - index - 1];
    }
    
    void dup() {
        push(peek(0));
    }

    void fpush(int e) {
        frame[fp++] = e;
    }

    int fpop() {
        return frame[--fp];
    }

    int fpeek(int index) {
        return frame[fp - index - 1];
    }

    public void load(int frameNo, int index) {
        push(stack[fpeek(frameNo) + index]);
    }

    public void store(int frameNo, int index) {
        stack[fpeek(frameNo) + index] = pop();
    }
    
    public void run(String s) {
        
    }
    
    @Override
    public String toString() {
        return IntStream.range(0, sp)
            .mapToObj(i -> stack[i].toString())
            .collect(Collectors.joining(" ", "[", "]"));
    }
    
    void add(String name, Executable value) {
        globals.put(Symbol.of(name), value);
    }
    
    {
        add("dup", c -> c.dup());
        add("+", c -> c.push(Int.of(((Int)c.pop()).value + ((Int)c.pop()).value)));
        add("*", c -> c.push(Int.of(((Int)c.pop()).value * ((Int)c.pop()).value)));
        add("==", c -> c.push(Bool.of(c.pop().equals(c.pop()))));
        add("<=", c -> c.push(Bool.of(((Ordered)c.pop()).compareTo((Ordered)c.pop()) >= 0)));
        add("stack", c -> System.out.println(c));
        add("if", c -> {
            Executable otherwise = c.pop(), then = c.pop();
            if (c.pop() != Bool.FALSE)
                then.execute(c);
            else
                otherwise.execute(c);
        });
        add("define", c -> { Symbol name = (Symbol)c.pop(); c.globals.put(name, c.pop()); });
    }
}
