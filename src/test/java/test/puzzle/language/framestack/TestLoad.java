package test.puzzle.language.framestack;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import puzzle.language.framestack.*;

public class TestLoad {

    static final Symbol STACK = Symbol.of("stack");

    @Test
    public void testLoadStore() {
        Context context = Context.of(10, 10);
        List block = Block.of(0, 1, Int.ZERO, Int.ONE, Int.TWO,
            Load.L1, Load.L2, STACK, Symbol.of("+"), STACK, Store.L0, STACK,
            Load.L0);
        System.out.println("block = " + block);
        block.execute(context);
        assertEquals(1, context.sp);
        assertEquals(Int.THREE, context.pop());
    }

    @Test
    public void testLoadArgs() {
        Context context = Context.of(10, 10);
        List block = Block.of(2, 1, Load.A2, Load.A1, STACK, Symbol.of("+"), STACK);
        System.out.println("block=" + block);
        Int.ONE.execute(context);
        Int.TWO.execute(context);
        block.execute(context);
        assertEquals(1, context.sp);
        assertEquals(Int.THREE, context.pop());
    }

}
