package test.puzzle.language.framestack;

import static org.junit.Assert.*;

import org.junit.Test;

import puzzle.language.framestack.*;

public class TestList {

    @Test
    public void testExecute() {
        Context context = Context.of(10, 10);
        List block = List.of(Int.ONE, Int.TWO, Symbol.of("+"));
        System.out.println("block = " + block);
        block.execute(context);
        assertEquals(1, context.sp);
        assertEquals(Int.THREE, context.pop());
    }

}
