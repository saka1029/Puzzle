package test.puzzle.language.framestack;

import static org.junit.Assert.*;

import org.junit.Test;

import puzzle.language.framestack.Bool;
import puzzle.language.framestack.Context;
import puzzle.language.framestack.Int;
import puzzle.language.framestack.List;
import puzzle.language.framestack.Quote;
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
    public void testLE() {
        Context c = Context.of(10, 10);
        Int.ONE.execute(c);
        Int.TWO.execute(c);
        Symbol.of("<=").execute(c);
        assertEquals(1, c.sp);
        assertEquals(Bool.TRUE, c.pop());
        Int.TWO.execute(c);
        Int.ONE.execute(c);
        Symbol.of("<=").execute(c);
        assertEquals(1, c.sp);
        assertEquals(Bool.FALSE, c.pop());
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
    
    @Test
    public void testDefineConstant() {
        Context c = Context.of(10, 10);
        Int.ONE.execute(c);
        Quote.of(Symbol.of("one")).execute(c);
        Symbol.of("define").execute(c);
        assertEquals(0, c.sp);
        Symbol.of("one").execute(c);
        assertEquals(1, c.sp);
        assertEquals(Int.ONE, c.pop());
    }
    
    @Test
    public void testDefineFunction() {
        Context c = Context.of(10, 10);
        Quote.of(List.of(Symbol.of("dup"), Symbol.of("+"))).execute(c);
        Quote.of(Symbol.of("double")).execute(c);
        Symbol.of("define").execute(c);
        assertEquals(0, c.sp);
        Int.ONE.execute(c);
        Symbol.of("double").execute(c);
        assertEquals(1, c.sp);
        assertEquals(Int.TWO, c.pop());
    }
    
    @Test
    public void testFactorial() {
        Context c = Context.of(10, 10);
        Quote.of(List.of(Symbol.of("dup"), Symbol.of("+"))).execute(c);
        Quote.of(Symbol.of("double")).execute(c);
        Symbol.of("define").execute(c);
        
    }

    @Test
    public void testEval() {
        Context c = Context.of(10, 10);
        assertEquals(Int.ONE, c.eval("1"));
        assertEquals(Int.THREE, c.eval(" 1 2 +"));
        assertEquals(Int.THREE, c.eval("(1 2 +)"));
        assertEquals(Int.of(6), c.eval("3 (dup +)"));               // list形式
        assertEquals(Int.of(6), c.eval("3 (1 1 : A1 A1 +)"));       // block形式
        assertEquals(Symbol.of("foo"), c.eval("'foo"));             // Quote
        assertEquals(Int.of(6), c.eval("'(dup +) 'double0 define 3 double0"));       // list形式関数定義
        assertEquals(Int.of(6), c.eval("'(1 1 : A1 A1 +) 'double define 3 double"));       // block形式関数定義
    }
}
