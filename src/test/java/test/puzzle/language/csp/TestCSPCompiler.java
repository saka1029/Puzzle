package test.puzzle.language.csp;

import static org.junit.Assert.*;
import static puzzle.language.SimplerJavaCompiler.*;
import static puzzle.language.csp.CSPCompiler.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import puzzle.Common;
import puzzle.language.SimplerJavaCompiler.SimplerJavaCompileError;
import puzzle.language.SimplerJavaCompiler.Source;
import puzzle.language.csp.CSPCompiler.Problem;

public class TestCSPCompiler {

    static final Logger logger = Common.getLogger(TestCSPCompiler.class);

    File dest = new File("temp");

    @Test
    void testIdentPattern() {
        logger.info(Common.methodName());
        Pattern ID = Pattern.compile("\\p{L}[\\p{L}\\p{IsDigit}]*");
        assertTrue(ID.matcher("変数").matches());
        assertTrue(ID.matcher("変数３").matches());
        assertFalse(ID.matcher("１２３").matches());
    }

    @Test
    void testVARIABLE_PATTERN() {
        logger.info(Common.methodName());
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
        logger.info(Common.methodName());
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
        List<int[]> answers = new ArrayList<>();
        Consumer<int[]> callback = a -> answers.add(a);
        loader.loadClass(packageName + "." + className)
            .getMethod("solve", Consumer.class).invoke(null, callback);
        int[][] expects = {
            {1, 2, 3},
            {1, 3, 2},
            {2, 1, 3},
            {2, 3, 1},
            {3, 1, 2},
            {3, 2, 1}};
        assertArrayEquals(expects, answers.toArray(int[][]::new));
    }

    public static int number(int... digits) {
        return IntStream.of(digits).reduce(0, (n, d) -> n * 10 + d);
    }

    @Test
    void testSendMoreMoney() throws MalformedURLException,
        IllegalAccessException, IllegalArgumentException,
        InvocationTargetException, NoSuchMethodException, SecurityException,
        ClassNotFoundException, SimplerJavaCompileError, IOException {
        logger.info(Common.methodName());
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
        List<int[]> answers = new ArrayList<>();
        Consumer<int[]> callback = answers::add;
        p.solve(dest, callback);
        // System.out.println(p);
        // System.out.println(p.generate());
        assertArrayEquals(new int[][] {{9, 5, 6, 7, 1, 0, 8, 2}},
            answers.toArray(int[][]::new));
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
        logger.info(Common.methodName());
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
        assertArrayEquals(
            new int[][] {{5, 3, 4, 7, 6, 8, 9, 1, 2}},
            parse(source).solve(dest).toArray(int[][]::new));
    }

    @Test
    public void testParse() throws IOException, IllegalAccessException, IllegalArgumentException,
        InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException,
        SimplerJavaCompileError {
        logger.info(Common.methodName());
        String source = "problem TestParse;\n"
            + "variable [1..3] a b c;\n"
            + "different a b c;\n";
        int[][] expects = {
            {1, 2, 3},
            {1, 3, 2},
            {2, 1, 3},
            {2, 3, 1},
            {3, 1, 2},
            {3, 2, 1}};
        assertArrayEquals(expects, parse(source).solve(dest).toArray(int[][]::new));
    }

    @Test
    public void testParseSendMoreMoney()
        throws IOException, IllegalAccessException, IllegalArgumentException,
        InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException,
        SimplerJavaCompileError {
        logger.info(Common.methodName());
        String source = "problem TestParseSendMoreMoney;\n"
            + "# SEND + MORE = MONEY\n"
            + "import java.util.stream.IntStream;\n"
            + "variable [1..9] s;\n"
            + "variable [0..9] e n d;\n"
            + "variable [1..9] m;\n"
            + "variable [0..9] o r y;\n"
            + "different s m e n d o r y;\n"
            + "constraint number(s, e, n, d) + number(m, o, r, e) == number(m, o, n, e, y);\n"
            + "static int number(int... digits) {\n"
            + "    return IntStream.of(digits).reduce(0, (n, d) -> n * 10 + d);\n"
            + "}";
        int[][] expects = {{9, 5, 6, 7, 1, 0, 8, 2}};
        assertArrayEquals(expects, parse(source).solve(dest).toArray(int[][]::new));
    }

    /**
     * https://youtu.be/0tvF39-zpSg a, b はすべて自然数 a² + b² = 2020
     */
    @Test
    public void test整数問題2020() throws MalformedURLException, IllegalAccessException,
        IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
        SecurityException, ClassNotFoundException, SimplerJavaCompileError, IOException {
        logger.info(Common.methodName());
        String source = "problem 整数問題2020;\n"
            + "# https://youtu.be/0tvF39-zpSg\n"
            + "# a, b はすべて自然数 a² + b² = 2020\n"
            + "# sqrt(2020) ≒ 44.944\n"
            + "variable [1..45] a b;\n"
            + "constraint a < b;\n"
            + "constraint a * a + b * b == 2020;\n";
        assertArrayEquals(new int[][] {{16, 42}, {24, 38}}, parse(source).solve(dest).toArray(int[][]::new));
    }

    /**
     * <pre>
     *    4x
     * ------- が整数となるxを求めよ。
     * x²+2x+2
     * </pre>
     */
    @Test
    public void test分数型の整数問題() throws MalformedURLException, IllegalAccessException,
        IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
        SecurityException, ClassNotFoundException, SimplerJavaCompileError, IOException {
        logger.info(Common.methodName());
        String source = "problem 分数型の整数問題;\n"
            + "import puzzle.Rational;\n"
            + "variable [-100..100] x p;\n"
            // + "constraint 4 * x == p * ((x + 2) * x + 2);\n";
            + "constraint Rational.of(4 * x, (x + 2) * x + 2).equals(Rational.of(p));\n";
        Problem p = parse(source);
        List<int[]> r = p.solve(dest);
        p.print(r, logger::info);
    }

    /**
     * Incredible Factorial Problem! - YouTube
     * https://www.youtube.com/watch?v=9dyK_op-Ocw
     */
    @Test
    public void testIncredibleFactorialProblem()
        throws MalformedURLException, IllegalAccessException, IllegalArgumentException,
        InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException,
        SimplerJavaCompileError, IOException {
        logger.info(Common.methodName());
        String source =
            "problem IncredibleFactorialProblem;\n"
            + "import java.util.stream.IntStream;\n"
            + "variable [0..20] a b c;\n"
            + "constraint fact(a) * fact(b) == fact(a) + fact(b) + fact(c);\n"
            + "static int fact(int n) {\n"
            + "    return IntStream.rangeClosed(2, n).reduce(1, (p, d) -> p * d);"
            + "}\n";
        assertArrayEquals(new int[][] {{3, 3, 4}}, parse(source).solve(dest).toArray(int[][]::new));
    }

    @Test
    public void testParsePeachLemonApple()
        throws IOException, IllegalAccessException, IllegalArgumentException,
        InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException,
        SimplerJavaCompileError {
        logger.info(Common.methodName());
        String source = "problem TestParsePeachLemonApple;\n"
            + "# PEACH + LEMON = APPLE\n"
            + "import java.util.stream.IntStream;\n"
            + "variable [1..9] p l a;\n"
            + "variable [0..9] e c h m o n;\n"
            + "different p l a e c h m o n;\n"
            + "constraint number(p, e, a, c, h) + number(l, e, m, o, n) == number(a, p, p, l, e);\n"
            + "static int number(int... digits) {\n"
            + "    return IntStream.of(digits).reduce(0, (n, d) -> n * 10 + d);\n"
            + "}";
        Problem p = parse(source);
        List<int[]> result = p.solve(dest);
        p.print(result, logger::info);
        /*
        2.3秒くらい
        CSPIntによる単一制約の結果では6秒弱かかっている
        CSPIntでは桁ごとに分解して束縛順序を調整することで0.1秒で解けている。
        2021-06-05T16:58:49.494 情報 TestPeachLemonApple *****test単一式による制約
        2021-06-05T16:58:49.622 情報 TestPeachLemonApple answer: {P=1, E=0, A=4, C=5, H=2, L=3, M=6, O=7, N=8}
        2021-06-05T16:58:49.626 情報 TestPeachLemonApple answer: {P=1, E=0, A=4, C=5, H=8, L=3, M=6, O=7, N=2}
        2021-06-05T16:58:49.631 情報 TestPeachLemonApple answer: {P=1, E=0, A=4, C=7, H=2, L=3, M=6, O=5, N=8}
        2021-06-05T16:58:49.634 情報 TestPeachLemonApple answer: {P=1, E=0, A=4, C=7, H=8, L=3, M=6, O=5, N=2}
        2021-06-05T16:58:51.394 情報 TestPeachLemonApple answer: {P=3, E=6, A=8, C=1, H=7, L=4, M=5, O=2, N=9}
        2021-06-05T16:58:51.396 情報 TestPeachLemonApple answer: {P=3, E=6, A=8, C=1, H=9, L=4, M=5, O=2, N=7}
        2021-06-05T16:58:51.398 情報 TestPeachLemonApple answer: {P=3, E=6, A=8, C=2, H=7, L=4, M=5, O=1, N=9}
        2021-06-05T16:58:51.398 情報 TestPeachLemonApple answer: {P=3, E=6, A=8, C=2, H=9, L=4, M=5, O=1, N=7}
        2021-06-05T16:58:55.367 情報 TestPeachLemonApple 束縛回数: [9, 81, 576, 4032, 24192, 105840, 423360, 1270080, 8]
        */
    }

    @Test
    public void testParsePeachLemonApple2()
        throws IOException, IllegalAccessException, IllegalArgumentException,
        InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException,
        SimplerJavaCompileError {
        logger.info(Common.methodName());
        String source = "problem TestParsePeachLemonApple2;\n"
            + "#   C4 C3 C2 C1  Z\n"
            + "#    P  E  A  C  H\n"
            + "#    L  E  M  O  N\n"
            + "#    A  P  P  L  E\n"
            + "#CSPIntで最速だった束縛順序\n"
            + "#bind order: [C4, Z, P, L, A, C3, E, C2, M, C1, C, O, H, N]\n"
            + "import java.util.stream.IntStream;\n"
            + "variable [0 1] c4;\n"
            + "variable [0] z;\n"
            + "variable [1..9] p;\n"
            + "variable [1..9] l;\n"
            + "variable [1..9] a;\n"
            + "variable [0 1] c3;\n"
            + "variable [0..9] e;\n"
            + "variable [0 1] c2;\n"
            + "variable [0..9] m;\n"
            + "variable [0 1] c1;\n"
            + "variable [0..9] c;\n"
            + "variable [0..9] o;\n"
            + "variable [0..9] h;\n"
            + "variable [0..9] n;\n"
            + "constraint add(z,  h, n, e, c1);\n"
            + "constraint add(c1, c, o, l, c2);\n"
            + "constraint add(c2, a, m, p, c3);\n"
            + "constraint add(c3, e, e, p, c4);\n"
            + "constraint add(c4, p, l, a, z);\n"
            + "different p l a e c h m o n;\n"
            + "static boolean add(int ca, int x, int y, int z, int cb) {\n"
            + "    return ca + x + y == z + cb * 10;\n"
            + "}";
        Problem p = parse(source);
        List<int[]> result = p.solve(dest);
        p.print(result, logger::info);
    }

}
