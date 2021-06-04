package test.puzzle.language;

import static org.junit.Assert.*;
import static puzzle.language.CSPCompiler.*;

import java.io.IOException;
import java.util.regex.Matcher;

import org.junit.jupiter.api.Test;

import puzzle.language.CSPCompiler.Problem;

class TestCSPCompiler {

    @Test
    void testVARIABLE_PATTERN() {
        String s = "  変数 == 3 + var3 - 2 + FOO(𩸽) % 2";
        Matcher m = VARIABLE_PATTERN.matcher(s);
        assertTrue(m.find());
        assertEquals("変数", m.group());
        assertTrue(m.find());
        assertEquals("var3", m.group());
        assertTrue(m.find());
        assertEquals("FOO", m.group());
        assertTrue(m.find());
        assertEquals("𩸽", m.group());
        assertFalse(m.find());
    }

    @Test
    void testGenerate() throws IOException {
        Problem p = new Problem("test.puzzle.language", "Combination");
        int[] domain = new int[] {1, 2, 3};
        p.variable("a", domain);
        p.variable("b", domain);
        p.variable("c", domain);
        p.allDifferent("a", "b", "c");
        String generated = generate(p);
        System.out.println(generated);
    }
}
