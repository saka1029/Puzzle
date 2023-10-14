package puzzle.language.framestack;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Context {
    final Map<Symbol, Executable> globals = new HashMap<>();
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

    Executable pop() {
        return stack[--sp];
    }

    Executable peek(int index) {
        return stack[sp - index - 1];
    }

    Executable set(int index, Executable e) {
        return stack[sp - index - 1] = e;
    }

    void load(int frameNo, int index) {
        push(peek(fpeek(frameNo) + index));
    }

    void store(int frameNo, int index) {
        set(fpeek(frameNo) + index, pop());
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
    
    @Override
    public String toString() {
        return IntStream.range(0, sp)
            .mapToObj(i -> stack[i].toString())
            .collect(Collectors.joining(", ", "[", "]"));
    }
}
