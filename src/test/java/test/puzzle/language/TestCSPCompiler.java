package test.puzzle.language;

import static org.junit.Assert.*;
import static puzzle.language.CSPCompiler.*;
import static puzzle.language.SimplerJavaCompiler.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import puzzle.language.CSPCompiler.Problem;
import puzzle.language.SimplerJavaCompiler.SimplerJavaCompileError;
import puzzle.language.SimplerJavaCompiler.Source;

public class TestCSPCompiler {

    File dest = new File("temp");

    @Test
    void testIdentPattern() {
        Pattern ID = Pattern.compile("\\p{L}[\\p{L}\\p{IsDigit}]*");
        assertTrue(ID.matcher("変数").matches());
        assertTrue(ID.matcher("変数３").matches());
        assertFalse(ID.matcher("１２３").matches());
    }

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

    /**
     * 生成されたコード(フォーマット済み)
     *
     * <pre>
     * <code>
     * package test.puzzle.language;
     *
     * import java.util.Map;
     * import java.util.function.Consumer;
     *
     * public class Combination {
     *     public static void solve(Consumer<Map<String, Integer>> callback) {
     *         int[] _a_domain = {1, 2, 3};
     *         int[] _b_domain = {1, 2, 3};
     *         int[] _c_domain = {1, 2, 3};
     *         for (int a : _a_domain)
     *             for (int b : _b_domain)
     *                 if (a != b)
     *                     for (int c : _c_domain)
     *                         if (b != c && a != c)
     *                             callback.accept(Map.of("a", a, "b", b, "c", c));
     *     }
     * }
     * </code>
     * </pre>
     */
    @Test
    void testGenerate()
        throws IOException, SimplerJavaCompileError, IllegalAccessException,
        IllegalArgumentException, InvocationTargetException,
        NoSuchMethodException, SecurityException, ClassNotFoundException {
        String packageName = "test.puzzle.language";
        String className = "Combination";
        Problem p = new Problem(packageName, className);
        int[] domain = new int[] {1, 2, 3};
        p.variable("a", domain);
        p.variable("b", domain);
        p.variable("c", domain);
        p.allDifferent("a", "b", "c");
        String generated = p.generate();
        // System.out.println(generated);
        ClassLoader loader = compile(dest, null,
            new Source(className, generated));
        Set<Map<String, Integer>> answers = new HashSet<>();
        Consumer<Map<String, Integer>> callback = a -> answers.add(a);
        loader.loadClass(packageName + "." + className)
            .getMethod("solve", Consumer.class).invoke(null, callback);
        Set<Map<String, Integer>> expects = Set.of(
            Map.of("a", 1, "b", 2, "c", 3),
            Map.of("a", 1, "b", 3, "c", 2),
            Map.of("a", 2, "b", 1, "c", 3),
            Map.of("a", 2, "b", 3, "c", 1),
            Map.of("a", 3, "b", 1, "c", 2),
            Map.of("a", 3, "b", 2, "c", 1));
        assertEquals(expects, answers);
    }

    public static int number(int... digits) {
        return IntStream.of(digits).reduce(0, (n, d) -> n * 10 + d);
    }

    @Test
    void testSendMoreMoney() throws MalformedURLException,
        IllegalAccessException, IllegalArgumentException,
        InvocationTargetException, NoSuchMethodException, SecurityException,
        ClassNotFoundException, SimplerJavaCompileError, IOException {
        Problem p = new Problem(null, "SendMoreMoney",
            "java.util.stream.IntStream");
        // "static " + getClass().getName() + ".*"); // このテストクラス内の公開静的メソッドを参照する。
        int[] first = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[] rest = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        p.variable("s", first);
        p.variable("e", rest);
        p.variable("n", rest);
        p.variable("d", rest);
        p.variable("m", first);
        p.variable("o", rest);
        p.variable("r", rest);
        p.variable("y", rest);
        p.allDifferent("s", "e", "n", "d", "m", "o", "r", "y");
        p.constraint("number(s, e, n, d) + number(m, o, r, e) == number(m, o, n, e, y)");
        p.directText = "static int number(int... digits) {\n"
            + "    return IntStream.of(digits).reduce(0, (n, d) -> n * 10 + d);\n"
            + "}\n";
        // p.constraint("((s * 10 + e) * 10 + n) * 10 + d"
        // + " + ((m * 10 + o) * 10 + r) * 10 + e"
        // + " == (((m * 10 + o) * 10 + n) * 10 + e) * 10 + y");
        Set<Map<String, Integer>> answers = new HashSet<>();
        Consumer<Map<String, Integer>> callback = a -> answers.add(a);
        p.solve(dest, callback);
        // System.out.println(p);
        // System.out.println(p.generate());
        assertEquals(Set.of(Map.of("s", 9, "e", 5, "n", 6, "d", 7, "m", 1, "o", 0, "r", 8, "y", 2)),
            answers);
    }

    /**
     * <pre>
     * A, B, C, D, E, F, G, H, I:{1..9}
     * A < D < G
     * (A / BC) + (D / EF) + (G / HI) = 1
     * </pre>
     */
    @Test
    public void testParse分数の和() throws MalformedURLException, IllegalAccessException,
        IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
        SecurityException, ClassNotFoundException, SimplerJavaCompileError, IOException {
        String source = "problem 分数の和;\n"
            + "import java.util.stream.IntStream;\n"
            + "import puzzle.Rational;\n"
            + "variable [1..9] A B C D E F G H I;\n"
            + "different A B C D E F G H I;\n"
            + "constraint A < D && D < G;\n"
            + "constraint Rational.of(A, number(B, C))\n"
            + "    .add(Rational.of(D, number(E, F)))\n"
            + "    .add(Rational.of(G, number(H, I)))\n"
            + "    .equals(Rational.ONE);\n"
            + "static int number(int... digits) {\n"
            + "    return IntStream.of(digits).reduce(0, (n, d) -> n * 10 + d);\n"
            + "}\n";
        assertEquals(
            Set.of(Map.of("I", 2, "H", 1, "G", 9, "F", 8, "E", 6, "D", 7, "C", 4, "B", 3, "A", 5)),
            parse(source).solve(dest));
    }

    @Test
    public void testParse() throws IOException, IllegalAccessException, IllegalArgumentException,
        InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException,
        SimplerJavaCompileError {
        String source = "problem TestParse;\n"
            + "variable [1..3] a b c;\n"
            + "different a b c;\n";
        Set<Map<String, Integer>> expects = Set.of(
            Map.of("a", 1, "b", 2, "c", 3),
            Map.of("a", 1, "b", 3, "c", 2),
            Map.of("a", 2, "b", 1, "c", 3),
            Map.of("a", 2, "b", 3, "c", 1),
            Map.of("a", 3, "b", 1, "c", 2),
            Map.of("a", 3, "b", 2, "c", 1));
        assertEquals(expects, parse(source).solve(dest));
    }

    @Test
    public void testParseSendMoreMoney()
        throws IOException, IllegalAccessException, IllegalArgumentException,
        InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException,
        SimplerJavaCompileError {
        String source = "problem TestParseSendMoreMoney;\n"
            + "# SEND + MORE = MONEY\n"
            + "import java.util.stream.IntStream;\n"
            + "variable [1..9] s m;\n"
            + "variable [0..9] e n d o r y;\n"
            + "different s m e n d o r y;\n"
            + "constraint number(s, e, n, d) + number(m, o, r, e) == number(m, o, n, e, y);\n"
            + "static int number(int... digits) {\n"
            + "    return IntStream.of(digits).reduce(0, (n, d) -> n * 10 + d);\n"
            + "}";
        Set<Map<String, Integer>> expects = Set.of(
            Map.of("s", 9, "e", 5, "n", 6, "d", 7, "m", 1, "o", 0, "r", 8, "y", 2));
        assertEquals(expects, parse(source).solve(dest));
    }

    /**
     * https://youtu.be/0tvF39-zpSg
     * a, b はすべて自然数 a² + b² = 2020
     */
    @Test
    public void test整数問題2020() throws MalformedURLException, IllegalAccessException,
        IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
        SecurityException, ClassNotFoundException, SimplerJavaCompileError, IOException {
        String source =
            "problem 整数問題2020;\n"
            + "# https://youtu.be/0tvF39-zpSg\n"
            + "# a, b はすべて自然数 a² + b² = 2020\n"
            + "# sqrt(2020) ≒ 44.944"
            + "variable [1..45] a b;\n"
            + "constraint a < b;\n"
            + "constraint a * a + b * b == 2020;\n";
        assertEquals(Set.of(
            Map.of("a", 16, "b", 42),
            Map.of("a", 24, "b", 38)),
            parse(source).solve(dest));
    }

}
