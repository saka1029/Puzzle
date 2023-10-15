package test.puzzle.language.framestack;

import static org.junit.Assert.*;

import org.junit.Test;

import puzzle.language.framestack.*;

public class TestBlock {

    @Test
    public void testExecuteReturn() {
        Context context = Context.of(10, 10);
        List block = List.of(2, 1, Load.A2, Load.A1, Symbol.of("+"));
        System.out.println("block = " + block);
        Int.ONE.execute(context);
        Int.TWO.execute(context);
        block.execute(context);
        assertEquals(1, context.sp);
        assertEquals(Int.THREE, context.pop());
    }

}
