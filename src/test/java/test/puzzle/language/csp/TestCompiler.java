package test.puzzle.language.csp;

import java.io.IOException;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

import puzzle.Common;
import puzzle.language.csp.Compiler.Problem;
import puzzle.language.csp.Compiler.Variable;

class TestCompiler {

    static final Logger logger = Common.getLogger(TestCompiler.class);

    @Test
    void testGenerate() throws IOException {
        Problem p = new Problem("test.puzzle.language.csp.TestGenerate");
        int[] domain = {1, 2, 3};
        Variable a = p.variable("a", domain);
        Variable b = p.variable("b", domain);
        Variable c = p.variable("c", new int[] {1, 2});
        p.constraint("a < b", a, b);
        p.constraint("b < c", b, c);
        p.constraint("a*a + b*b == c*c", a, b, c);
        System.out.println(p.generate());
    }


}
