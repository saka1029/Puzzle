package test.puzzle.language.csp;

import static puzzle.language.csp.Compiler.parse;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import org.junit.Test;

import puzzle.core.Common;
import puzzle.language.JavaCompilerInMemory.CompileError;
import puzzle.language.csp.Compiler.Problem;
import puzzle.language.csp.Compiler.Variable;

public class TestCompiler {

    static final Logger logger = Common.getLogger(TestCompiler.class);

//    @Test
    public void testGenerate() {
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
    public void testParse() {
        String source =
            "problem test.puzzle.language.csp.TestGenerate;\n"
            + "variable [1..20] a b c;\n"
            + "constraint a < b;\n"
            + "constraint b < c;\n"
            + "constraint a*a + b*b == c*c;\n";
        Problem problem = parse(source);
        System.out.println(problem.generate());
    }

    @Test
    public void testCompileGo() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, CompileError {
        String source = "problem SendMoreMoney;\r\n"
            + "\r\n"
            + "# SEND + MORE = MONEY\r\n"
            + "\r\n"
            + "variable [1..9] s;\r\n"
            + "variable [0..9] e n d;\r\n"
            + "variable [1..9] m;\r\n"
            + "variable [0..9] o r y;\r\n"
            + "\r\n"
            + "different s e n d m o r y;\r\n"
            + "constraint\r\n"
            + "            number(s, e, n, d)\r\n"
            + "          + number(m, o, r, e)\r\n"
            + "         == number(m, o, n, e, y);\r\n"
            + "\r\n"
            + "static int number(int... digits) {\r\n"
            + "    return IntStream.of(digits).reduce(0, (n, d) -> n * 10 + d);\r\n"
            + "};\r\n";
        parse(source).compileGo();
//        System.out.println(parse(source).generate());
    }

    @Test
    public void testNQueens() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, CompileError {
        Problem p = new Problem("NQueens",
            "static java.lang.Math.abs");
        int n = 10;
        int[] domain = IntStream.range(0, n).toArray();
        Variable[] v = IntStream.range(0, n)
            .mapToObj(i -> p.variable("v" + i, domain))
            .toArray(Variable[]::new);
        p.allDifferent(v);
        for (int i = 0; i < n; ++i)
            for (int j = i + 1; j < n; ++j) {
                int d = Math.abs(i - j);
                p.constraint("abs(v" + i + " - v" + j + ") != " + d, v[i], v[j]);
            }
        p.compileGo();
        System.out.println(p.generate());
    }

    @Test
    public void test8QueensFile() throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, CompileError {
        String source = Files.readString(Path.of("src/test/csp/Queen8.csp"));
        Problem problem = parse(source);
        System.out.println(problem.generate());
        problem.compileGo();
    }

}
