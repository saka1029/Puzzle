package test.puzzle.language.csp;

import static puzzle.language.csp.Compiler.parse;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import puzzle.Common;
import puzzle.language.csp.Compiler.Problem;
import puzzle.language.csp.Compiler.Variable;

class TestCompiler {

    static final Logger logger = Common.getLogger(TestCompiler.class);

    @Test
    void testGenerate() throws IOException {
        Problem p = new Problem("test.puzzle.language.csp.TestGenerate");
        int[] domain = IntStream.rangeClosed(1, 20).toArray();
        Variable a = p.variable("a", domain);
        Variable b = p.variable("b", domain);
        Variable c = p.variable("c", domain);
        p.constraint("a < b", a, b);
        p.constraint("b < c", b, c);
        p.constraint("a*a + b*b == c*c", a, b, c);
        System.out.println(p.generate());
    }
    
    @Test
    void testParse() {
        String source =
            "problem test.puzzle.language.csp.TestGenerate;\n"
            + "variable [1..20] a b c;\n"
            + "constraint a < b;\n"
            + "constraint b < c;\n"
            + "constraint a*a + b*b == c*c;\n";
        Problem problem = parse(source);
        System.out.println(problem.generate());
    }


}
