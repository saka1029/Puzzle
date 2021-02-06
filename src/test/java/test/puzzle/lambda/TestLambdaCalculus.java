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
        assertEquals("a", parse("a").toString());
        assertEquals("λx.λx.λx.x", parse("λx.λx.λx.x").toString());
        assertEquals("λx.λx.λx.x", parse("λx x x.x").toString());
        assertEquals("(λx.(λx.(λx.x) x) x) x", parse("(λx.(λx.(λx.x) x) x) x").toString());
        assertEquals("a (λx.x)", parse("a λx.x").toString());
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

    static void assertNormalizeEquals(String expected, String actual) {
        assertEquals(parse(expected).normalize(), parse(actual).normalize());
    }

    static void assertNormalizeNotEquals(String expected, String actual) {
        assertNotEquals(parse(expected).normalize(), parse(actual).normalize());
    }

    @Test
    void testEquivalant() {
        assertNormalizeEquals("λx.x", "λa.a");
        assertNormalizeEquals("λx.x", "λa.(a)");
        assertNormalizeEquals("λx.x", "(λa.a)");
        assertNormalizeEquals("λx.x", "(λa.(a))");
        assertNormalizeEquals("λx.x b", "λa.a b");
        assertNormalizeNotEquals("λx.x B", "λa.a b");
        assertNormalizeEquals("λx.x b", "λa.a (b)");
        assertNormalizeEquals("λx.x b", "λa.(a b)");
        assertNormalizeEquals("λx.(λx.(λx.x) x) x", "λa.(λb.(λc.c) b) a");
        assertNormalizeEquals("λx.(λa.a x) (λb.b x)", "λx.(λy.y x) λy.y x");
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
        testReduce("λx.x", "(λx.λy.y) a");
        testReduce("λx.x", "(λx y.y) a");
        testReduce("λx.x", "λx.((λx.x) x)");
        testReduce("λx.x", "λx.(λx.x) x");
        testReduce("λx.x", "(λx.(λx.x)) x");
        testReduce("λx.x", "(λx.λx.x) x");
        testReduce("λx.x", "λ☺.☺");
        testReduce("λx.x", "λ𠮷野家.𠮷野家");
    }

    @Test
    void testExceptions() {
        try {
            parse("λ.");
            fail();
        } catch (RuntimeException e) {
        }
        try {
            parse("(a");
            fail();
        } catch (RuntimeException e) {
        }
        try {
            parse("λx.");
            fail();
        } catch (RuntimeException e) {
        }
        try {
            parse(")");
            fail();
        } catch (RuntimeException e) {
        }
    }

    /**
     * https://ja.wikipedia.org/wiki/%E3%83%A9%E3%83%A0%E3%83%80%E8%A8%88%E7%AE%97#%E5%81%9C%E6%AD%A2%E6%80%A7
     */
    @Test
    void testHaltingProblem() {
        try {
            parse("(λx.x x) (λx.x x)").reduce();
            fail();
        } catch (StackOverflowError e) {
        }
    }

    Map<String, Term> globals = new HashMap<>();

    void define(String name, String body) {
        Term reduced = parse(body).expand(globals).reduce();
        logger.info("define: " + name + " = " + reduced);
        globals.put(name, reduced);
    }

    void assertEquivalent(String expected, String actual) {
        assertEquals(parse(expected).expand(globals).normalize(),
            parse(actual).expand(globals).reduce().normalize());
    }

    /**
     * https://ja.wikipedia.org/wiki/%E3%83%A9%E3%83%A0%E3%83%80%E8%A8%88%E7%AE%97#%E8%87%AA%E7%84%B6%E6%95%B0%E3%81%A8%E7%AE%97%E8%A1%93
     */
    @Test
    void testChurchNumerals() {
        define("0", "λf x.x");
        define("1", "λf x.f x");
        define("2", "λf x.f(f x)");
        define("3", "λf x.f(f(f x))");
        define("succ", "λn f x.f(n f x))");
        assertEquivalent("1", "succ 0");
        assertEquivalent("2", "succ 1");
        assertEquivalent("3", "succ 2");
        define("+", "λm n f x.m f(n f x)");
        assertEquivalent("1", "+ 0 1");
        assertEquivalent("1", "+ 1 0");
        assertEquivalent("2", "+ 1 1");
        assertEquivalent("3", "+ 1 2");
        assertEquivalent("3", "+ 2 1");
        define("*", "λm n f.m(n f)");
        assertEquivalent("0", "* 0 1");
        assertEquivalent("0", "* 1 0");
        assertEquivalent("2", "* 1 2");
        assertEquivalent("2", "* 2 1");
        assertEquivalent("3", "* 1 3");
        assertEquivalent("3", "* 3 1");
        define("pred", "λn f x.n(λg h.h(g f)) (λu.x) (λu.u)");
        assertEquivalent("0", "pred 1");
        assertEquivalent("1", "pred 2");
        assertEquivalent("2", "pred (succ 2)");
        define("pred2", "λn.n(λg k.(g 1)(λu.+(g k)1)k)(λv.0)0)");
        assertEquivalent("0", "pred2 1");
        assertEquivalent("2", "pred2 3");
    }

    /**
     * https://ja.wikipedia.org/wiki/%E3%83%A9%E3%83%A0%E3%83%80%E8%A8%88%E7%AE%97#%E8%AB%96%E7%90%86%E8%A8%98%E5%8F%B7%E3%81%A8%E8%BF%B0%E8%AA%9E
     */
    @Test
    void testCharchBooleans() {
        define("true", "λt f.t");
        define("false", "λt f.f");
        define("test", "λp t f.p t f");
        define("and", "λp q.p q false");
        define("or", "λp q.p true q");
        define("not", "λp.p false true");
        assertEquivalent("true", "and true true");
        assertEquivalent("false", "and true false");
        assertEquivalent("false", "and false true");
        assertEquivalent("false", "and false false");
        assertEquivalent("true", "or true true");
        assertEquivalent("true", "or true false");
        assertEquivalent("true", "or false true");
        assertEquivalent("false", "or false false");
        assertEquivalent("false", "not true");
        assertEquivalent("true", "not false");
        assertEquivalent("v", "test (and true true) v w");
        assertEquivalent("w", "test (and true false) v w");
        assertEquivalent("w", "test (and false true) v w");
        assertEquivalent("w", "test (and false false) v w");
        assertEquivalent("v", "test (or true true) v w");
        assertEquivalent("v", "test (or true false) v w");
        assertEquivalent("v", "test (or false true) v w");
        assertEquivalent("w", "test (or false false) v w");
        assertEquivalent("w", "test (not true) v w");
        assertEquivalent("v", "test (not false) v w");
    }

    /**
     * https://ja.wikipedia.org/wiki/%E3%83%A9%E3%83%A0%E3%83%80%E8%A8%88%E7%AE%97#%E5%AF%BE
     */
    @Test
    void testChurchPairs() {
        define("true", "λt f.t");
        define("false", "λt f.f");
        define("cons", "λs b f.f s b");
        define("car", "λp.p true");
        define("cdr", "λp.p false");
        define("[]", "false");
        define("[A]", "cons A []");
        define("[AB]", "cons A (cons B [])");
        assertEquivalent("λx.x A false", "[A]");
        assertEquivalent("λx.x A (λx.x B false)", "[AB]");
        assertEquivalent("A", "car [A]");
        assertEquivalent("[]", "cdr [A]");
        assertEquivalent("A", "car [AB]");
        assertEquivalent("B", "car(cdr [AB])");
        assertEquivalent("[]", "cdr(cdr [AB])");
        assertEquivalent("λx.x", "car(cdr(cdr [AB]))"); // 空リストのcarはidになる。
        assertEquivalent("λx y.x", "car(cdr(cdr(cdr [AB])))"); // ???
    }

    /**
     * https://en.wikipedia.org/wiki/Church_encoding#List_encodings
     */
    @Test
    void testListEncodings() {
        define("true", "λt f.t");
        define("false", "λt f.f");
        define("nil", "false");
        define("isnil", "λl.l (λh.λt.false) true");
        define("cons", "λh.λt.λc.λn.c h (t c n)");
        define("head", "λl.l (λh.λt.h) false");
        define("tail", "λl.λc.λn.l (λh.λt.λg.g h (t c)) (λt.n) (λh.λt.t)");
        assertEquivalent("λh.λt.h A t", "cons A nil");
        assertEquivalent("λh.λt.h A (h B t)", "cons A (cons B nil)");
        assertEquivalent("λh.λt.h A (h B (h C t))", "cons A (cons B (cons C nil))");
        define("[ABC]", "cons A (cons B (cons C nil))");
        assertEquivalent("A", "head [ABC]");
        assertEquivalent("B", "head(tail [ABC])");
        assertEquivalent("C", "head(tail(tail [ABC]))");
        assertEquivalent("nil", "tail(tail(tail [ABC]))");
        assertEquivalent("true", "isnil nil");
        assertEquivalent("false", "isnil [ABC]");
        assertEquivalent("true", "isnil(tail(tail(tail [ABC])))");
        assertEquivalent("nil", "head(tail(tail(tail [ABC])))"); // nilのheadはnilになる。
        assertEquivalent("nil", "head(tail(tail(tail(tail [ABC]))))");
    }

    /**
     * https://ja.wikipedia.org/wiki/SKI%E3%82%B3%E3%83%B3%E3%83%93%E3%83%8D%E3%83%BC%E3%82%BF%E8%A8%88%E7%AE%97
     */
    @Test
    void testSKICombinator() {
        define("S", "λx y z.x z (y z)");
        define("K", "λx y.x");
        define("I", "λx.x");
        assertEquivalent("I", "S K K"); // IはS K Kで表現できる。
        assertEquivalent("λx.x", "S K K");
        assertEquivalent("a a", "(S I I) a"); // 自己適用
        assertEquivalent("b a", "(S (K (S I)) K) a b"); // 式の逆転
        define("T", "K");
        define("F", "S K");
        define("NOT", "S (S I (K F)) (K T)");
        assertEquivalent("F", "NOT T");
        assertEquivalent("T", "NOT F");
        define("OR", "S I (K T)");
        assertEquivalent("T", "OR T T");
        assertEquivalent("T", "OR T F");
        assertEquivalent("T", "OR F T");
        assertEquivalent("F", "OR F F");
        define("AND", "S S (K (K F))");
        assertEquivalent("T", "AND T T");
        assertEquivalent("F", "AND T F");
        assertEquivalent("F", "AND F T");
        assertEquivalent("F", "AND F F");
    }
}
