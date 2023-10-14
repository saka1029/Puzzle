package test.puzzle.language.framestack;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import puzzle.language.framestack.*;

public class TestLoad {

    static final Symbol STACK = Symbol.of("stack");

    @Test
    public void testLoadStore() {
        Context context = Context.of(10, 10);
        List block = List.of(Int.ZERO, Int.ONE, Int.TWO,
            Load.of(0, 1), Load.of(0, 2), STACK, Symbol.of("+"), STACK, Store.of(0, 0), STACK,
            Load.of(0, 0), Return.ONE);
        block.execute(context);
        assertEquals(1, context.sp);
        assertEquals(Int.THREE, context.pop());
    }

}
