package test.puzzle.language.framestack;

import static org.junit.Assert.*;

import org.junit.Test;

import puzzle.language.framestack.Bool;
import puzzle.language.framestack.Context;
import puzzle.language.framestack.Int;
import puzzle.language.framestack.List;
import puzzle.language.framestack.Symbol;

public class TestContext {

    @Test
    public void testPlus() {
        Context c = Context.of(10, 10);
        Int.of(1).execute(c);
        Int.of(2).execute(c);
        Symbol.of("+").execute(c);
        assertEquals(1, c.sp);
        assertEquals(Int.of(3), c.pop());
    }

    @Test
    public void testIfThen() {
        Context c = Context.of(10, 10);
        List.of(Bool.TRUE, Int.ONE, Int.TWO, Symbol.of("stack"), Symbol.of("if")).execute(c);
        assertEquals(1, c.sp);
        assertEquals(Int.ONE, c.pop());
    }

    @Test
    public void testIfElse() {
        Context c = Context.of(10, 10);
        List.of(Bool.FALSE, Int.ONE, Int.TWO, Symbol.of("stack"), Symbol.of("if")).execute(c);
        assertEquals(1, c.sp);
        assertEquals(Int.TWO, c.pop());
    }

}
