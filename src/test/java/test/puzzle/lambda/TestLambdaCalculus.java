package test.puzzle.lambda;

import static org.junit.jupiter.api.Assertions.*;
import static puzzle.lambda.LambdaCalculus.*;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

import puzzle.Common;
import puzzle.lambda.LambdaCalculus.Term;

class TestLambdaCalculus {

    static final Logger logger = Common.getLogger(TestLambdaCalculus.class);

    static void testNormalize(String expectedNormalized, String source) {
        Term term = parse(source);
        String actual = term.normalize();
        assertEquals(expectedNormalized, actual);
    }

    @Test
    void testToString() {
        assertEquals("a[free]", parse("a").toString());
        assertEquals("λx[r=0].λx[r=0].λx[r=1].x", parse("λx.λx.λx.x").toString());
        assertEquals("λx[r=0].λx[r=0].λx[r=1].x", parse("λx x x.x").toString());
        assertEquals("(λx[r=1].(λx[r=1].(λx[r=1].x) x) x) x[free]", parse("(λx.(λx.(λx.x) x) x) x").toString());
        assertEquals("a[free] (λx[r=1].x)", parse("a λx.x").toString());
    }

    @Test
    void testNormalize() {
        testNormalize("λ%0.%0", "λx.x");
        testNormalize("λ%0.λ%1.%1", "λx.λx.x");
        testNormalize("λ%0.λ%1.λ%2.λ%3.λ%4.λ%5.%5", "λx.λx.λx.λx.λx.λx.x");
        testNormalize("λ%0.λ%1.λ%2.λ%3.λ%4.λ%5.%5", "λa.λb.λc.λd.λe.λf.f");
        testNormalize("λ%0.λ%1.λ%2.λ%3.λ%4.λ%5.%5", "λa b c d e f.f");
        testNormalize("λ%0.λ%1.λ%2.λ%3.λ%4.λ%5.%5", "λx x x x x x.x");
        testNormalize("λ%0.λ%1.λ%2.λ%3.λ%4.λ%5.%2", "λa b c d e f.c");
        testNormalize("λ%0.(λ%1.%1) %0", "λx.(λx.x) x");
        testNormalize("a b c", "a b c");
        testNormalize("a b c", "(a b) c");
        testNormalize("a (b c)", "a (b c)");
        testNormalize("a (λ%0.%0)", "a (λx.x)");
        testNormalize("a (λ%0.%0)", "a λx.x");
        testNormalize("a (λ%0.%0) b", "a (λx.x) b");
        testNormalize("a (λ%0.%0 b)", "a λx.x b");
    }

    static void assertEquivalant(String expected, String actual) {
        assertEquals(parse(expected).normalize(), parse(actual).normalize());
    }

    static void assertNotEquivalant(String expected, String actual) {
        assertNotEquals(parse(expected).normalize(), parse(actual).normalize());
    }

    @Test
    void testEquivalant() {
        assertEquivalant("λx.x", "λa.a");
        assertEquivalant("λx.x", "λa.(a)");
        assertEquivalant("λx.x", "(λa.a)");
        assertEquivalant("λx.x", "(λa.(a))");
        assertEquivalant("λx.x b", "λa.a b");
        assertNotEquivalant("λx.x B", "λa.a b");
        assertEquivalant("λx.x b", "λa.a (b)");
        assertEquivalant("λx.x b", "λa.(a b)");
        assertEquivalant("λx.(λx.(λx.x) x) x", "λa.(λb.(λc.c) b) a");
        assertEquivalant("λx.(λa.a x) (λb.b x)", "λx.(λy.y x) λy.y x");
    }

    static void testReduce(String expected, String actual) {
        assertEquals(parse(expected).normalize(), parse(actual).reduce().normalize());
    }

    @Test
    void testReduce() {
        testReduce("a", "(λx.x) a");
        testReduce("λx.x", "(λx.λy.y) a");
        testReduce("λx.x", "(λx y.y) a");
        testReduce("λx.x a", "λx.(λy.x y) a");
        testReduce("λx.a", "λx.(λy.y) a");
        testReduce("λy.y", "(λx.λy.y) a");
        testReduce("λy.y", "(λx y.y) a");
    }

    @Test
    void testExceptions() {
        try { parse("λ."); fail(); } catch (RuntimeException e) {}
        try { parse("(a"); fail(); } catch (RuntimeException e) {}
        try { parse("λx."); fail(); } catch (RuntimeException e) {}
        try { parse(")"); fail(); } catch (RuntimeException e) {}
    }

    @Test
    void testChurchNumerals() {
        String ZERO = "(λf x.x)";
        String ONE = "(λf x.f x)";
        String TWO = "(λf x.f(f x))";
        String THREE = "(λf x.f(f(f x)))";
        String SUCC = "(λn f x.f(n f x))";
        testReduce(ONE, SUCC + ZERO);
        testReduce(TWO, SUCC + ONE);
        testReduce(THREE, SUCC + TWO);
        String PLUS = "(λm n f x.m f(n f x))";
        testReduce(ONE, PLUS + ZERO + ONE);
        testReduce(ONE, PLUS + ONE + ZERO);
        testReduce(TWO, PLUS + ONE + ONE);
        testReduce(THREE, PLUS + ONE + TWO);
        testReduce(THREE, PLUS + TWO + ONE);
        String MULT = "(λm n f.m(n f))";
        testReduce(ZERO, MULT + ZERO + ONE);
        testReduce(ZERO, MULT + ONE + ZERO);
        testReduce(TWO, MULT + ONE + TWO);
        testReduce(TWO, MULT + TWO + ONE);
        testReduce(THREE, MULT + ONE + THREE);
        testReduce(THREE, MULT + THREE + ONE);
        String PRED = "(λn f x.n(λg h.h(g f)) (λu.x) (λu.u))";
        testReduce(ZERO, PRED + ONE);
        testReduce(ONE, PRED + TWO);
        testReduce(TWO, PRED + "(" + SUCC + TWO + ")");
        String PRED2 = "(λn.n(λg k.(g"+ ONE + ")(λu." + PLUS + "(g k)" + ONE + ")k)(λv." + ZERO + ")" + ZERO + ")";
        testReduce(ZERO, PRED2 + ONE);
        testReduce(TWO, PRED2 + THREE);
    }

    @Test
    void testChurchBooleans() {
        String TRUE = "(λt f.t)";
        String FALSE = "(λt f.f)";
        String TEST = "(λp t f.p t f)";
        String AND = "(λp q.p q" + FALSE + ")";
        String OR = "(λp q.p" + TRUE + "q)";
        String NOT = "(λp.p" + FALSE + TRUE + ")";
        testReduce("v", TEST + TRUE + "v w");
        testReduce("w", TEST + FALSE + "v w");
        testReduce(TRUE, AND + TRUE + TRUE);
        testReduce(FALSE, AND + TRUE + FALSE);
        testReduce(FALSE, AND + FALSE + TRUE);
        testReduce(FALSE, AND + FALSE + FALSE);
        testReduce(TRUE, OR + TRUE + TRUE);
        testReduce(TRUE, OR + TRUE + FALSE);
        testReduce(TRUE, OR + FALSE + TRUE);
        testReduce(FALSE, OR + FALSE + FALSE);
        testReduce(FALSE, NOT + TRUE);
        testReduce(TRUE, NOT + FALSE);
        testReduce(FALSE, NOT + "(" + NOT + FALSE + ")");
        testReduce("v", TEST + "(" + AND + TRUE + TRUE + ")" + "v w");
        testReduce("w", TEST + "(" + AND + TRUE + FALSE + ")" + "v w");
        testReduce("w", TEST + "(" + AND + FALSE + TRUE + ")" + "v w");
        testReduce("w", TEST + "(" + AND + FALSE + FALSE + ")" + "v w");
        testReduce("v", TEST + "(" + OR + TRUE + TRUE + ")" + "v w");
        testReduce("v", TEST + "(" + OR + TRUE + FALSE + ")" + "v w");
        testReduce("v", TEST + "(" + OR + FALSE + TRUE + ")" + "v w");
        testReduce("w", TEST + "(" + OR + FALSE + FALSE + ")" + "v w");
        testReduce("w", TEST + "(" + NOT + TRUE + ")" + "v w");
        testReduce("v", TEST + "(" + NOT + FALSE + ")" + "v w");
    }

    static void testExpand(Map<String, Term> globals, String expected, String actual) {
        assertEquals(parse(expected).expand(globals).normalize(),
            parse(actual).expand(globals).reduce().normalize());
    }

    @Test
    void testExpand() {
        Map<String, Term> globals = new HashMap<>();
        testExpand(globals, "λx.λy.x", "λx.λy.x");
    }

    @Test
    void testExpandChurchNumerals() {
        Map<String, Term> globals = new HashMap<>();
        globals.put("0", parse("λf x.x"));
        globals.put("1", parse("λf x.f x"));
        globals.put("2", parse("λf x.f(f x)"));
        globals.put("3", parse("λf x.f(f(f x))"));
        globals.put("succ", parse("λn f x.f(n f x))"));
        testExpand(globals, "1", "succ 0");
        testExpand(globals, "2", "succ 1");
        testExpand(globals, "3", "succ 2");
        globals.put("+", parse("λm n f x.m f(n f x)"));
        testExpand(globals, "1", "+ 0 1");
        testExpand(globals, "1", "+ 1 0");
        testExpand(globals, "2", "+ 1 1");
        testExpand(globals, "3", "+ 1 2");
        testExpand(globals, "3", "+ 2 1");
        globals.put("*", parse("λm n f.m(n f)"));
        testExpand(globals, "0", "* 0 1");
        testExpand(globals, "0", "* 1 0");
        testExpand(globals, "2", "* 1 2");
        testExpand(globals, "2", "* 2 1");
        testExpand(globals, "3", "* 1 3");
        testExpand(globals, "3", "* 3 1");
        globals.put("pred", parse("λn f x.n(λg h.h(g f)) (λu.x) (λu.u)"));
        testExpand(globals, "0", "pred 1");
        testExpand(globals, "1", "pred 2");
        testExpand(globals, "2", "pred (succ 2)");
        globals.put("pred2", parse("λn.n(λg k.(g 1)(λu.+(g k)1)k)(λv.0)0)"));
        testExpand(globals, "0", "pred2 1");
        testExpand(globals, "2", "pred2 3");
    }

    @Test
    void testExpandCharchBooleans() {
        Map<String, Term> globals = new HashMap<>();
        globals.put("true", parse("λt f.t"));
        globals.put("false", parse("λt f.f"));
        globals.put("test", parse("λp t f.p t f"));
        globals.put("and", parse("λp q.p q false"));
        globals.put("or", parse("λp q.p true q"));
        globals.put("not", parse("λp.p false true"));
        testExpand(globals, "true", "and true true");
        testExpand(globals, "false", "and true false");
        testExpand(globals, "false", "and false true");
        testExpand(globals, "false", "and false false");
        testExpand(globals, "true", "or true true");
        testExpand(globals, "true", "or true false");
        testExpand(globals, "true", "or false true");
        testExpand(globals, "false", "or false false");
        testExpand(globals, "false", "not true");
        testExpand(globals, "true", "not false");
        testExpand(globals, "v", "test (and true true) v w");
        testExpand(globals, "w", "test (and true false) v w");
        testExpand(globals, "w", "test (and false true) v w");
        testExpand(globals, "w", "test (and false false) v w");
        testExpand(globals, "v", "test (or true true) v w");
        testExpand(globals, "v", "test (or true false) v w");
        testExpand(globals, "v", "test (or false true) v w");
        testExpand(globals, "w", "test (or false false) v w");
        testExpand(globals, "w", "test (not true) v w");
        testExpand(globals, "v", "test (not false) v w");
    }

}
