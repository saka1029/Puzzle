package test.puzzle.language;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Logger;

import org.junit.Test;

import puzzle.core.Common;
import puzzle.language.JavaCompilerInMemory;
import puzzle.language.JavaCompilerInMemory.CompileError;
import puzzle.language.JavaCompilerInMemory.Source;

public class TestJavaCompilerInMemory {

    static final Logger logger = Common.getLogger(TestJavaCompilerInMemory.class);

    static final List<String> OPTIONS = List.of("-g:none");

    public static String name() {
        return "Invoke TestJavaCompilerInMemory.name()";
    }

    /**
     * <pre>
     * コンパイル環境のクラスはコンパイルされたコードからロードできる。
     * ということはProblemで定義した制約にアクセスすることはできるはず。
     * </pre>
     */
    @Test
    public void testCompileReferCompilingEnvironment()
        throws InstantiationException, IllegalAccessException, ClassNotFoundException,
        IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
        SecurityException, CompileError {
        logger.info(Common.methodName());
        String fullName = "DynaClass";
        String sourceCode = "import test.puzzle.language.TestJavaCompilerInMemory;\n"
            + "public class DynaClass {\n"
            + "    public String toString() {\n"
            + "        return \"Hello, I am\"\n"
            + "            + \" \" + this.getClass().getSimpleName()"
            + "            + \" \" + TestJavaCompilerInMemory.name();\n"
            + "    }\n"
            + "}\n";
        Class<?> clazz = JavaCompilerInMemory.compile(fullName, sourceCode, OPTIONS);
        Object result = clazz.getDeclaredConstructor().newInstance().toString();
        assertEquals("Hello, I am DynaClass Invoke TestJavaCompilerInMemory.name()", result);
    }

    @Test
    public void testCompileMultiFiles()
        throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException,
        InvocationTargetException, NoSuchMethodException, SecurityException, CompileError {
        logger.info(Common.methodName());
        List<Source> sources = List.of(
            new Source("A", "public class A { public static String foo() { return \"I am A\"; }}"),
            new Source("B",
                "public class B { public static String foo() { return \"B calls A.foo() : \" + A.foo(); }}"));
        ClassLoader loader = JavaCompilerInMemory.compile(sources, OPTIONS);
        assertEquals("B calls A.foo() : I am A", loader.loadClass("B").getMethod("foo").invoke(null));
    }

    @Test
    public void testCompileFullQualifiedClassName()
        throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException,
        InvocationTargetException, NoSuchMethodException, SecurityException, CompileError {
        logger.info(Common.methodName());
        List<Source> sources = List.of(
            new Source("A", "package foo.bar; public class A {"
                + " public static String foo() { return \"I am A\"; }}"),
            new Source("B", "package foo.bar; public class B {"
                + " public static String foo() { return \"B calls A.foo() : \" + A.foo(); }}"));
        ClassLoader loader = JavaCompilerInMemory.compile(sources, OPTIONS);
        assertEquals("B calls A.foo() : I am A", loader.loadClass("foo.bar.B").getMethod("foo").invoke(null));
    }

    @Test
    public void testCompileError() throws ClassNotFoundException, InstantiationException,
        IllegalAccessException, IllegalArgumentException, InvocationTargetException,
        NoSuchMethodException, SecurityException {
        logger.info(Common.methodName());
        String fullName = "CompileErrorClass";
        String sourceCode = "public class CompileErrorClass {\n"
            + "    1 + 2;\n"
            + "}\n";
        try {
            Class<?> clazz = JavaCompilerInMemory.compile(fullName, sourceCode, OPTIONS);
            logger.info(clazz.getDeclaredConstructor().newInstance().toString());
            fail();
        } catch (CompileError e) {
//            logger.info(e.getMessage());
            assertTrue(e.getMessage().contains("CompileErrorClass"));
            assertTrue(e.getMessage().contains("1 + 2;"));
        }
    }
}
