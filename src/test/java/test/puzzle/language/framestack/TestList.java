package test.puzzle.language.framestack;

import static org.junit.Assert.*;

import org.junit.Test;

import puzzle.language.framestack.*;

public class TestList {

    @Test
    public void testExecute() {
        Context context = Context.of(10, 10);
        List block = List.of(Int.of(1), Int.of(2), c -> c.push(Int.of(((Int)c.pop()).value + ((Int)c.pop()).value)));
        block.execute(context);
        assertEquals(0, context.sp);
    }

    @Test
    public void testExecuteReturn() {
        Context context = Context.of(10, 10);
        List block = List.of(Int.of(1), Int.of(2), c -> c.push(Int.of(((Int)c.pop()).value + ((Int)c.pop()).value)), Return.ONE);
        block.execute(context);
        assertEquals(1, context.sp);
        assertEquals(Int.of(3), context.pop());
    }

}
