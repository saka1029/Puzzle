package test.puzzle.lambda;

import static org.junit.jupiter.api.Assertions.*;
import static puzzle.lambda.LambdaCalculus2.*;

import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

import puzzle.Common;
import puzzle.lambda.LambdaCalculus2.Term;

class TestLambdaCalculus2 {

    static final Logger logger = Common.getLogger(TestLambdaCalculus2.class);

    static void testNormalize(String expectedNormalized, String source) {
        Term term = parse(source);
        String actual = term.normalize();
        assertEquals(expectedNormalized, actual);
    }

//    @Test
    void testToString() {
        logger.info(parse("λx.λx.λx.x").toString());
        logger.info(parse("λx.(λx.(λx.x) x) x").toString());
    }

    @Test
    void testNormalize() {
        testNormalize("λ%0.%0", "λx.x");
        testNormalize("λ%0.λ%1.%1", "λx.λx.x");
        testNormalize("λ%0.λ%1.λ%2.λ%3.λ%4.λ%5.%5", "λx.λx.λx.λx.λx.λx.x");
        testNormalize("λ%0.λ%1.λ%2.λ%3.λ%4.λ%5.%5", "λa.λb.λc.λd.λe.λf.f");
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
        testReduce("λx.x a", "λx.(λy.x y) a");
        testReduce("λx.a", "λx.(λy.y) a");
    }

    @Test
    void testChurchNumerals() {
        String ZERO = "(λf.λx.x)";
        String ONE = "(λf.λx.f x)";
        String TWO = "(λf.λx.f(f x))";
        String THREE = "(λf.λx.f(f(f x)))";
        String SUCC = "(λn.λf.λx.f(n f x))";
        testReduce(ONE, SUCC + ZERO);
        testReduce(TWO, SUCC + ONE);
        testReduce(THREE, SUCC + TWO);
        String PLUS = "(λm.λn.λf.λx.m f(n f x))";
        testReduce(ONE, PLUS + ZERO + ONE);
        testReduce(ONE, PLUS + ONE + ZERO);
        testReduce(TWO, PLUS + ONE + ONE);
        testReduce(THREE, PLUS + ONE + TWO);
        testReduce(THREE, PLUS + TWO + ONE);
        String MULT = "(λm.λn.λf.m(n f))";
        testReduce(ZERO, MULT + ZERO + ONE);
        testReduce(ZERO, MULT + ONE + ZERO);
        testReduce(TWO, MULT + ONE + TWO);
        testReduce(TWO, MULT + TWO + ONE);
        testReduce(THREE, MULT + ONE + THREE);
        testReduce(THREE, MULT + THREE + ONE);
    }

    @Test
    void testChurchBooleans() {
        String TRUE = "(λt.λf.t)";
        String FALSE = "(λt.λf.f)";
        String TEST = "(λp.λt.λf.p t f)";
        String AND = "(λp.λq.p q" + FALSE + ")";
        String OR = "(λp.λq.p" + TRUE + "q)";
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

}
