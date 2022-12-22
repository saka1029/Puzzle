package test.puzzle.language;

import static org.junit.Assert.assertEquals;
import static puzzle.language.IntlispCompiler.*;
import static org.junit.Assert.fail;

import org.junit.Test;

public class TestIntlispCompiler {

    @Test
    public void testConsToString() {
        assertEquals("(a . 2)", cons(sym("a"), i(2)).toString());
        assertEquals("(a 2)", list(sym("a"), i(2)).toString());
        assertEquals(list(sym("a"), i(2)), cons(sym("a"), cons(i(2), NIL)));
        assertEquals(NIL, list());
    }

    @Test
    public void testParse() {
        assertEquals(NIL, parse("nil"));
        assertEquals(sym("abc"), parse("abc"));
        assertEquals(NIL, parse("()"));
        assertEquals(list(sym("a"), sym("b"), sym("c")), parse("(a b c)"));
        assertEquals(list(sym("a"), i(2), sym("c")), parse("(a 2 c)"));
        assertEquals(list(sym("+"), list(sym("-"), i(1), i(2)), i(3)), parse("(+ (- 1 2) 3)"));
//        assertEquals(cons(sym("a"), cons(i(0), i(1))), parse("(a 0 . 1)"));
    }

    @Test
    public void testCompileInt() {
        CompilerContext cc = CompilerContext.create();
        cc.compile(i(3));
        RuntimeContext context = RuntimeContext.create(20);
        for (Code c : cc.codes)
            c.execute(context);
        assertEquals(3, context.pop());
    }

    @Test
    public void testCompileBinary() {
        CompilerContext cc = CompilerContext.create();
        RuntimeContext rc = RuntimeContext.create(20);
        assertEquals(0, cc.compileGo(rc, "(= 1 2)"));
        assertEquals(1, cc.compileGo(rc, "(/= 1 2)"));
        assertEquals(1, cc.compileGo(rc, "(< 1 2)"));
        assertEquals(1, cc.compileGo(rc, "(<= 1 2)"));
        assertEquals(0, cc.compileGo(rc, "(> 1 2)"));
        assertEquals(0, cc.compileGo(rc, "(>= 1 2)"));
        assertEquals(0, cc.compileGo(rc, "(+)"));
        assertEquals(1, cc.compileGo(rc, "(+ 1)"));
        assertEquals(3, cc.compileGo(rc, "(+ 1 2)"));
        assertEquals(6, cc.compileGo(rc, "(+ 1 2 3)"));
        try {
            assertEquals(0, cc.compileGo(rc, "(-)"));
            fail();
        } catch (RuntimeException e) {
            assertEquals("insufficient arguments", e.getMessage());
        }
        assertEquals(-1, cc.compileGo(rc, "(- 1)"));
        assertEquals(-1, cc.compileGo(rc, "(- 1 2)"));
        assertEquals(-4, cc.compileGo(rc, "(- 1 2 3)"));
        assertEquals(1, cc.compileGo(rc, "(*)"));
        assertEquals(1, cc.compileGo(rc, "(* 1)"));
        assertEquals(2, cc.compileGo(rc, "(* 1 2)"));
        assertEquals(6, cc.compileGo(rc, "(* 1 2 3)"));
        assertEquals(24, cc.compileGo(rc, "(* 1 2 3 4)"));
        try {
            assertEquals(1, cc.compileGo(rc, "(/)"));
            fail();
        } catch (RuntimeException e) {
            assertEquals("insufficient arguments", e.getMessage());
        }
        assertEquals(1, cc.compileGo(rc, "(/ 1)"));
        assertEquals(0, cc.compileGo(rc, "(/ 2)"));
        assertEquals(2, cc.compileGo(rc, "(/ 4 2)"));
        assertEquals(1, cc.compileGo(rc, "(/ 8 4 2)"));
        assertEquals(8, cc.compileGo(rc, "(+ (* 2 3) (/ 8 4))"));
        assertEquals("[2, 3, *, 8, 4, /, +]", cc.codes.toString());
        assertEquals(1, cc.compileGo(rc, "(< 2 3)"));
        assertEquals("[2, 3, <]", cc.codes.toString());
        try {
            assertEquals(1, cc.compileGo(rc, "(%)"));
            fail();
        } catch (RuntimeException e) {
            assertEquals("insufficient arguments", e.getMessage());
        }
        assertEquals(0, cc.compileGo(rc, "(% 1)"));
        assertEquals(1, cc.compileGo(rc, "(% 2)"));
        assertEquals(1, cc.compileGo(rc, "(% 4 3)"));
        assertEquals(1, cc.compileGo(rc, "(% 11 4 2)"));
        assertEquals(8, cc.compileGo(rc, "(+ (* 2 3) (% 8 3))"));
        assertEquals("[2, 3, *, 8, 3, %, +]", cc.codes.toString());
    }

    @Test
    public void testCompileIf() {
        CompilerContext cc = CompilerContext.create();
        RuntimeContext rc = RuntimeContext.create(20);
        assertEquals(2, cc.compileGo(rc, "(if 1 2 3)"));
        assertEquals(3, cc.compileGo(rc, "(if 0 2 3)"));
        assertEquals(1, cc.compileGo(rc, "(if (< 1 2) 1 2)"));
        assertEquals(2, cc.compileGo(rc, "(if (> 1 2) 1 2)"));
        assertEquals("[1, 2, >, jumpF 6, 1, jump 7, 2]", cc.codes.toString());
    }

    @Test
    public void testCompileNot() {
        CompilerContext cc = CompilerContext.create();
        RuntimeContext rc = RuntimeContext.create(20);
        assertEquals(1, cc.compileGo(rc, "(not 0)"));
        assertEquals(0, cc.compileGo(rc, "(not 1)"));
        assertEquals(0, cc.compileGo(rc, "(not 2)"));
        assertEquals("[2, not]", cc.codes.toString());
    }

    @Test
    public void testCompileAnd() {
        CompilerContext cc = CompilerContext.create();
        RuntimeContext rc = RuntimeContext.create(20);
        assertEquals(1, cc.compileGo(rc, "(and)"));
        assertEquals(0, cc.compileGo(rc, "(and 0)"));
        assertEquals(2, cc.compileGo(rc, "(and 2)"));
        assertEquals(3, cc.compileGo(rc, "(and 2 3)"));
        assertEquals(0, cc.compileGo(rc, "(and 2 0)"));
        assertEquals(0, cc.compileGo(rc, "(and 0 3)"));
        assertEquals(0, cc.compileGo(rc, "(and 0 0)"));
        assertEquals("[0, 0, and]", cc.codes.toString());
    }

    @Test
    public void testCompileOr() {
        CompilerContext cc = CompilerContext.create();
        RuntimeContext rc = RuntimeContext.create(20);
        assertEquals(0, cc.compileGo(rc, "(or)"));
        assertEquals(0, cc.compileGo(rc, "(or 0)"));
        assertEquals(2, cc.compileGo(rc, "(or 2)"));
        assertEquals(2, cc.compileGo(rc, "(or 2 3)"));
        assertEquals(2, cc.compileGo(rc, "(or 2 0)"));
        assertEquals(3, cc.compileGo(rc, "(or 0 3)"));
        assertEquals(0, cc.compileGo(rc, "(or 0 0)"));
        assertEquals("[0, 0, or]", cc.codes.toString());
    }

    @Test
    public void testCompileDefine() {
        CompilerContext cc = CompilerContext.create();
        RuntimeContext rc = RuntimeContext.create(20);
        assertEquals(3, cc.compileGo(rc, "(define (add a b) (+ a b)) (add 1 2)"));
        assertEquals("[jump 6, enter 2, arg 0, arg 1, +, exit, 1, 2, call 1]", cc.codes.toString());
    }

    @Test
    public void testCompileFact() {
        CompilerContext cc = CompilerContext.create();
        RuntimeContext rc = RuntimeContext.create(20);
        assertEquals(24, cc.compileGo(rc, "(define (fact n) (if (<= n 1) 1 (* n (fact (- n 1))))) (fact 4)"));
        assertEquals("[jump 15, enter 1, arg 0, 1, <=, jumpF 8, 1, jump 14, arg 0, arg 0, 1, -, call 1, *, exit, 4, call 1]", cc.codes.toString());
    }

    @Test
    public void testCompileFibonacci() {
        String source =
            "(define (fibonacci n)"
            + " (if (or (= n 0) (= n 1))"
            + "     n"
            + "     (+ (fibonacci (- n 2)) (fibonacci (- n 1)))  ))"
            + "(fibonacci 7)";
        String codes =
            /*   0*/ "[jump 22,"
            + /* 1*/ " enter 1,"
            + /* 2*/ " arg 0, 0, =, arg 0, 1, =, or, jumpF 12,"
            + /*10*/ " arg 0, jump 21,"
            + /*12*/ " arg 0, 2, -, call 1,"
            + /*16*/ " arg 0, 1, -, call 1, +,"
            + /*21*/ " exit,"
            + /*22*/ " 7, call 1]";
        CompilerContext cc = CompilerContext.create();
        RuntimeContext rc = RuntimeContext.create(30);
        assertEquals(13, cc.compileGo(rc, source));
        assertEquals(codes, cc.codes.toString());
    }
}
