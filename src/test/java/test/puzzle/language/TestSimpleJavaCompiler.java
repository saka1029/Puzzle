package test.puzzle.language;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

import puzzle.language.SimpleJavaCompiler;
import puzzle.language.SimpleJavaCompiler.SimpleJavaCompileError;

public class TestSimpleJavaCompiler {

    @Test
    void testSimpleJavaCompiler()
        throws ClassNotFoundException, IOException, SimpleJavaCompileError, URISyntaxException,
        IllegalAccessException, IllegalArgumentException, InvocationTargetException,
        NoSuchMethodException, SecurityException {
        String className = "test.puzzle.language.Main";
        String source = "package test.puzzle.language;\r\n"
            + "public class Main {\r\n"
            + "  public static void main(String[] args) {\r\n"
            + "      System.out.println(\"Hello \" + args[0]);\r\n"
            + "  }\r\n"
            + "}\r\n";
        Class<?> clazz = SimpleJavaCompiler.compile(className, source);
        System.out.println(clazz);
        clazz.getMethod("main", String[].class).invoke(null, new Object[] {new String[] {"John"}});
    }

    public static class SendMoreMoney {
        static int num(int... digits) {
            int r = 0;
            for (int d : digits)
                r = r * 10 + d;
            return r;
        }

        static boolean anyMatch(int i, int... comps) {
            for (int c : comps)
                if (i == c)
                    return true;
            return false;
        }

        public static void solve(Consumer<Map<String, Integer>> answer) {
            for (int s = 1; s <= 9; ++s) {
                for (int e = 0; e <= 9; ++e) {
                    if (anyMatch(e, s))
                        continue;
                    for (int n = 0; n <= 9; ++n) {
                        if (anyMatch(n, s, e))
                            continue;
                        for (int d = 0; d <= 9; ++d) {
                            if (anyMatch(d, s, e, n))
                                continue;
                            for (int m = 1; m <= 9; ++m) {
                                if (anyMatch(m, s, e, n, d))
                                    continue;
                                for (int o = 0; o <= 9; ++o) {
                                    if (anyMatch(o, s, e, n, d, m))
                                        continue;
                                    for (int r = 0; r <= 9; ++r) {
                                        if (anyMatch(r, s, e, n, d, m, o))
                                            continue;
                                        for (int y = 0; y <= 9; ++y) {
                                            if (anyMatch(y, s, e, n, d, m, o, r))
                                                continue;
                                            if (num(s, e, n, d) + num(m, o, r, e) == num(m, o, n, e,
                                                y))
                                                answer.accept(Map.of("s", s, "e", e, "n", n, "d", d,
                                                    "m", m, "o", o, "r", r, "y", y));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    void testSendMoreMoney() {
        SendMoreMoney.solve(map -> System.out.println(map));
    }

    @Test
    void testCompileSendMoreMoney()
        throws ClassNotFoundException, IOException, SimpleJavaCompileError, URISyntaxException,
        IllegalAccessException, IllegalArgumentException, InvocationTargetException,
        NoSuchMethodException, SecurityException {
        String className = "SendMoreMoney";
        String source = "    import java.util.Map;\n"
            + "    import java.util.function.Consumer;\n"
            + "\n"
            + "    public class SendMoreMoney {\n"
            + "        public static int num(int... digits) {\n"
            + "            int r = 0;\n"
            + "            for (int d : digits)\n"
            + "                r = r * 10 + d;\n"
            + "            return r;\n"
            + "        }\n"
            + "\n"
            + "        static boolean anyMatch(int left, int... rights) {\n"
            + "            for (int right : rights)\n"
            + "                if (left == right)\n"
            + "                    return true;\n"
            + "            return false;\n"
            + "        }\n"
            + "\n"
            + "        public static void solve(Consumer<Map<String, Integer>> answer) {\n"
            + "            for (int s = 1; s <= 9; ++s) {\n"
            + "                for (int e = 0; e <= 9; ++e) {\n"
            + "                    if (anyMatch(e, s)) continue;\n"
            + "                    for (int n = 0; n <= 9; ++n) {\n"
            + "                        if (anyMatch(n, s, e)) continue;\n"
            + "                        for (int d = 0; d <= 9; ++d) {\n"
            + "                            if (anyMatch(d, s, e, n)) continue;\n"
            + "                            for (int m = 1; m <= 9; ++m) {\n"
            + "                                if (anyMatch(m, s, e, n, d)) continue;\n"
            + "                                for (int o = 0; o <= 9; ++o) {\n"
            + "                                    if (anyMatch(o, s, e, n, d, m)) continue;\n"
            + "                                    for (int r = 0; r <= 9; ++r) {\n"
            + "                                        if (anyMatch(r, s, e, n, d, m, o)) continue;\n"
            + "                                        for (int y = 0; y <= 9; ++y) {\n"
            + "                                            if (anyMatch(y, s, e, n, d, m, o, r)) continue;\n"
            + "                                            if (num(s, e, n, d) + num(m, o, r, e) == num(m, o, n, e, y))\n"
            + "                                                answer.accept(Map.of(\"s\", s, \"e\", e, \"n\", n, \"d\", d, \"m\", m, \"o\", o, \"r\", r, \"y\", y));\n"
            + "                                        }\n"
            + "                                    }\n"
            + "                                }\n"
            + "                            }\n"
            + "                        }\n"
            + "                    }\n"
            + "                }\n"
            + "            }\n"
            + "        }\n"
            + "    }\n"
            + "";
        Class<?> clazz = SimpleJavaCompiler.compile(className, source);
        Consumer<Map<String, Integer>> answer = a -> System.out.println(a);
        clazz.getMethod("solve", Consumer.class).invoke(null, answer);
    }

    static class TestGson {
        public static Object parse(String json) {
            return new Gson().fromJson(json, Map.class);
        }
    }

    @Test
    public void testLib() {
        System.out.println(TestGson.parse("{\"key\": 123}"));
    }

    /**
     * クラスパスを指定しなくてもコンパイル時のクラスパスが参照される。
     */
    @Test
    public void testLibraryReference()
        throws ClassNotFoundException, IOException, SimpleJavaCompileError, URISyntaxException,
        IllegalAccessException, IllegalArgumentException, InvocationTargetException,
        NoSuchMethodException, SecurityException {
        String source = "import com.google.gson.Gson;"
            + "import java.util.Map;"
            + "    public class TestGson {\n"
            + "        public static Object parse(String json) {\n"
            + "            return new Gson().fromJson(json, Map.class);\n"
            + "        }\n"
            + "    }\n"
            + "";
        String className = "TestGson";
        Class<?> clazz = SimpleJavaCompiler.compile(className, source);
        String json = "{\"key\": 123}";
        Object parsed = clazz.getMethod("parse", String.class).invoke(null, json);
        assertEquals(com.google.gson.internal.LinkedTreeMap.class, parsed.getClass());
        assertEquals(Map.of("key", 123.0), parsed);
    }

    public interface Calc {
        int add(int a, int b);
    }

    @Test
    public void testInterfaceReference() throws InstantiationException, IllegalAccessException,
        IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
        SecurityException, ClassNotFoundException, IOException, SimpleJavaCompileError {
        String className = "CalcImpl";
        String source = "import " + getClass().getName() + ".Calc;\r\n"
            + "public class CalcImpl implements Calc {\r\n"
            + "    @Override\r\n"
            + "    public int add(int a, int b) {\r\n"
            + "        return a + b;\r\n"
            + "    }\r\n"
            + "}\r\n";
        @SuppressWarnings("unchecked")
        Class<Calc> clazz = (Class<Calc>) SimpleJavaCompiler.compile(className, source);
        Calc obj = clazz.getConstructor().newInstance();
        int result = obj.add(2, 3);
        assertEquals(5, result);
    }

}
