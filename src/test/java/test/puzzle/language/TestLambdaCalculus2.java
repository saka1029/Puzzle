package test.puzzle.language;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static puzzle.language.LambdaCalculus2.bindToString;
import static puzzle.language.LambdaCalculus2.get;
import static puzzle.language.LambdaCalculus2.parse;

import java.util.logging.Logger;

import org.junit.Test;

import puzzle.core.Common;
import puzzle.language.LambdaCalculus2.Bind;

public class TestLambdaCalculus2 {

    static final Logger logger = Common.getLogger(TestLambdaCalculus2.class);

    @Test
    public void testBind() {
        Common.methodName();
        assertEquals("{}", bindToString(null));
        Bind<String, String> bind = new Bind<>(null, "a", "A");
        assertEquals("{a=A}", bindToString(bind));
        assertEquals("A", get(bind, "a"));
        assertNull(get(bind, "x"));
        Bind<String, String> bind2 = new Bind<>(bind, "b", "B");
        assertEquals("{b=B, a=A}", bindToString(bind2));
        assertEquals("{b=B, a=A}", bind2.toString());
    }

    @Test
    public void testToString() {
        Common.methodName();
        assertEquals("λx.x", parse("λ x . x").toString());
        assertEquals("λx.x x", parse("λ x . x x").toString());
        assertEquals("λx.λx.x x", parse("λx x.x x").toString());
        assertEquals("(λx.x) x", parse("(λx.x) x").toString());
        assertEquals("x x", parse("x x").toString());
        assertEquals("x (λx.x)", parse("x λx.x").toString());
    }

    static void testToNormalizedString(String expected, String actual) {
        assertEquals(parse(expected).toNormalizedString(), parse(actual).toNormalizedString());
    }

    @Test
    public void testToNormalizedString() {
        Common.methodName();
        assertEquals("λ%0.(λ%1.%1) %0", parse("λx.(λx.x) x").toNormalizedString());
        assertEquals("(λ%0.%0) a", parse("(λx.x) a").toNormalizedString());
        testToNormalizedString("λ a . a a", "λx.x x");
        testToNormalizedString("(λa.a)a", "(λx.x)a");
        testToNormalizedString("(λa.λb.λc.λd.d d d d) x", "(λx.λx.λx.λx.x x x x) x");
        testToNormalizedString("(λa.(λb.(λc.(λd.d) c) b) a) x", "(λx.(λx.(λx.(λx.x) x) x) x) x");
        testToNormalizedString("a (λx.x)", "a λx.x");
    }

    @Test
    public void testParseError() {
        Common.methodName();
        try {
            parse("λx y");
            fail();
        } catch (RuntimeException e) {
            assertEquals("variable expected", e.getMessage());
        }
        try {
            parse("λ.y");
            fail();
        } catch (RuntimeException e) {
            assertEquals("variable expected", e.getMessage());
        }
        try {
            parse("(y");
            fail();
        } catch (RuntimeException e) {
            assertEquals("')' expected", e.getMessage());
        }
        try {
            parse("y)");
            fail();
        } catch (RuntimeException e) {
            assertEquals("extra string ')'", e.getMessage());
        }
        try {
            parse("");
            fail();
        } catch (RuntimeException e) {
            assertEquals("unexpected end of string", e.getMessage());
        }
        try {
            parse(")");
            fail();
        } catch (RuntimeException e) {
            assertEquals("unexpected char ')'", e.getMessage());
        }
    }
}
