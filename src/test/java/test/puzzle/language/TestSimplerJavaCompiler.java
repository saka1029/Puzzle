package test.puzzle.language;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import puzzle.language.JavaCompiler;
import puzzle.language.JavaCompiler.Source;

public class TestSimplerJavaCompiler {

    File destination = new File("temp");

    @Test
    void testCompile()
        throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
        NoSuchMethodException, SecurityException, ClassNotFoundException {
        String className = "HelloWorld";
        String source = "public class HelloWorld {\r\n" +
            "    public static void main(String args[]) {\r\n" +
            "        System.out.println(\"Hello \" + args[0]);\r\n" +
            "    }" +
            "}\r\n";
        ClassLoader loader = JavaCompiler.compile(destination, null,
            List.of(new Source(className, source)));
        loader.loadClass(className).getMethod("main", new Class<?>[] {String[].class})
            .invoke(null, new Object[] {new String[] {"John"}});
    }

    public interface Calculate {
        int calc(int a, int b);
    }

    @Test
    void testInterfaceReference() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        ClassLoader loader = JavaCompiler.compile(destination, null,
            List.of(new Source("CalculateImpl",
                "import " + getClass().getName() + ".Calculate;\r\n" +
                    "public class CalculateImpl implements Calculate {\r\n" +
                    "     @Override public int calc(int a, int b) { return a + b; }\r\n" +
                    "}\r\n")));
        @SuppressWarnings("unchecked")
        Class<Calculate> clazz = (Class<Calculate>) loader.loadClass("CalculateImpl");
        Calculate obj = clazz.getDeclaredConstructor().newInstance();
        int result = obj.calc(1, 2);
        assertEquals(3, result);
    }

    static int number(int... digits) {
        return IntStream.of(digits).reduce(0, (n, d) -> n * 10 + d);
    }

    @Test
    void testCompileSendMoreMoney() throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        String className = "SendMoreMoney";
        String source = "import java.util.Map;\n"
            + "import java.util.function.Consumer;\n"
            + "\n"
            + "public class SendMoreMoney {\n"
            + "    public static int num(int... digits) {\n"
            + "        int r = 0;\n"
            + "        for (int d : digits)\n"
            + "            r = r * 10 + d;\n"
            + "        return r;\n"
            + "    }\n"
            + "\n"
            + "    static boolean anyMatch(int left, int... rights) {\n"
            + "        for (int right : rights)\n"
            + "            if (left == right)\n"
            + "                return true;\n"
            + "        return false;\n"
            + "    }\n"
            + "\n"
            + "    public static void solve(Consumer<Map<String, Integer>> callback) {\n"
            + "        for (int s = 1; s <= 9; ++s) {\n"
            + "            for (int e = 0; e <= 9; ++e) {\n"
            + "                if (anyMatch(e, s)) continue;\n"
            + "                for (int n = 0; n <= 9; ++n) {\n"
            + "                    if (anyMatch(n, s, e)) continue;\n"
            + "                    for (int d = 0; d <= 9; ++d) {\n"
            + "                        if (anyMatch(d, s, e, n)) continue;\n"
            + "                        for (int m = 1; m <= 9; ++m) {\n"
            + "                            if (anyMatch(m, s, e, n, d)) continue;\n"
            + "                            for (int o = 0; o <= 9; ++o) {\n"
            + "                                if (anyMatch(o, s, e, n, d, m)) continue;\n"
            + "                                for (int r = 0; r <= 9; ++r) {\n"
            + "                                    if (anyMatch(r, s, e, n, d, m, o)) continue;\n"
            + "                                    for (int y = 0; y <= 9; ++y) {\n"
            + "                                        if (anyMatch(y, s, e, n, d, m, o, r)) continue;\n"
            + "                                        if (num(s, e, n, d) + num(m, o, r, e) == num(m, o, n, e, y))\n"
            + "                                            callback.accept(Map.of(\"s\", s, \"e\", e, \"n\", n,\n"
            + "                                                                   \"d\", d, \"m\", m, \"o\", o,\n"
            + "                                                                   \"r\", r, \"y\", y));\n"
            + "                                    }\n"
            + "                                }\n"
            + "                            }\n"
            + "                        }\n"
            + "                    }\n"
            + "                }\n"
            + "            }\n"
            + "        }\n"
            + "    }\n"
            + "}\n"
            + "";
        ClassLoader loader = JavaCompiler.compile(destination, null,
            List.of(new Source(className, source)));
        Class<?> clazz = loader.loadClass(className);
        Consumer<Map<String, Integer>> found = a -> {
            assertEquals(Map.of("s", 9, "e", 5, "n", 6, "d", 7, "m", 1, "o", 0, "r", 8, "y", 2), a);
            assertEquals(number(a.get("m"), a.get("o"), a.get("n"), a.get("e"), a.get("y")),
                number(a.get("s"), a.get("e"), a.get("n"), a.get("d"))
                    + number(a.get("m"), a.get("o"), a.get("r"), a.get("e")));
        };
        clazz.getMethod("solve", Consumer.class).invoke(null, found);
    }
}
