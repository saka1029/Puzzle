package test.puzzle.language;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static puzzle.language.CSPCompiler.VARIABLE_PATTERN;
import static puzzle.language.SimplerJavaCompiler.compile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;

import org.junit.jupiter.api.Test;

import puzzle.language.CSPCompiler.Problem;
import puzzle.language.SimplerJavaCompiler.SimplerJavaCompileError;
import puzzle.language.SimplerJavaCompiler.Source;

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
        System.out.println(generated);
        File dest = new File("temp");
        ClassLoader loader = compile(dest, null,
            new Source(className, generated));
        Consumer<Map<String, Integer>> callback = a -> System.out.println(a);
        loader.loadClass(packageName + "." + className)
            .getMethod("solve", Consumer.class).invoke(null, callback);
    }

    /**
     * <pre><code>
     * package test.puzzle.language;
     * 
     * import java.util.Map;
     * import java.util.function.Consumer;
     * 
     * public class SendMoreMoney {
     *     public static void solve(Consumer<Map<String, Integer>> callback) {
     *         int[] _s_domain = {1, 2, 3, 4, 5, 6, 7, 8, 9};
     *         int[] _e_domain = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
     *         int[] _n_domain = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
     *         int[] _d_domain = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
     *         int[] _m_domain = {1, 2, 3, 4, 5, 6, 7, 8, 9};
     *         int[] _o_domain = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
     *         int[] _r_domain = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
     *         int[] _y_domain = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
     *         for (int s : _s_domain)
     *             for (int e : _e_domain)
     *                 if (s != e)
     *                     for (int n : _n_domain)
     *                         if (s != n && e != n)
     *                             for (int d : _d_domain)
     *                                 if (s != d && e != d && n != d)
     *                                     for (int m : _m_domain)
     *                                         if (e != m && n != m && s != m && d != m)
     *                                             for (int o : _o_domain)
     *                                                 if (n != o && e != o && d != o && s != o && m != o)
     *                                                     for (int r : _r_domain)
     *                                                         if (n != r && m != r && s != r && o != r && e != r && d != r)
     *                                                             for (int y : _y_domain)
     *                                                                 if (e != y && ((s * 10 + e) * 10 + n) * 10 + d
     *                                                                         + ((m * 10 + o) * 10 + r) * 10 + e
     *                                                                         == (((m * 10 + o) * 10 + n) * 10 + e) * 10 + y
     *                                                                     && d != y && r != y && n != y && o != y && m != y && s != y)
     *                                                                     callback.accept( Map.of( "s", s, "e", e, "n", n, "d", d, "m", m, "o", o, "r", r, "y", y));
     *     }
     * }
     * </code></pre>
     * @throws MalformedURLException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws ClassNotFoundException
     * @throws SimplerJavaCompileError
     * @throws IOException
     */
    @Test
    void testSendMoreMoney() throws MalformedURLException,
        IllegalAccessException, IllegalArgumentException,
        InvocationTargetException, NoSuchMethodException, SecurityException,
        ClassNotFoundException, SimplerJavaCompileError, IOException {
        Problem p = new Problem("test.puzzle.language", "SendMoreMoney");
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
        p.constraint("((s * 10 + e) * 10 + n) * 10 + d"
            + " + ((m * 10 + o) * 10 + r) * 10 + e"
            + " == (((m * 10 + o) * 10 + n) * 10 + e) * 10 + y");
        Consumer<Map<String, Integer>> callback = a -> System.out.println(a);
        p.solve(new File("temp"), callback);
        System.out.println(p.generate());
    }
}
