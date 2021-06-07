package puzzle.language;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;

/**
 * -d オプションを使って、コンパイル結果をクラスファイルとして保存する方法。 クラスローダを定義する必要がない。FileManagerも必要ない。
 */
public class JavaCompiler {

    public static class SimplerJavaCompileError extends Exception {
        private static final long serialVersionUID = 1L;

        SimplerJavaCompileError(DiagnosticCollector<JavaFileObject> diagnostics) {
            super(diagnostics.getDiagnostics().stream()
                .map(d -> d + System.lineSeparator()).collect(Collectors.joining()));
        }
    }

    public static class Source extends SimpleJavaFileObject {
        final String source;
        final String name;

        public Source(String name, String source) {
            super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension),
                Kind.SOURCE);
            this.name = name;
            this.source = source;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return source;
        }
    }

    /**
     * 文字列のJavaソースをコンパイルして指定したディレクトリ(destination)にクラスファイルとして出力します。
     * 指定したディレクトリ(destination)からクラスオブジェクトをロードするためのClassLoaderを返します。 <br>
     * [呼び出し例]
     *
     * <pre>
     * <code>
     * ClassLoader loader = SimplerJavaCompiler.compile(new File("temp"), null,
     *     new SimplerJavaCompiler.Source("Hello",
     *         "public class Hello {\n"
     *         + "    public static void main(String[] args) {\n"
     *         + "        System.out.println("Hello, World!");\n"
     *         + "    }\n";
     *         + "}\n";
     * loader.loadClass("Hello").getMethod("main", new Class<?>[] {String[].class})
     *       .invoke(null, new Object[] {new String[] {}});
     * </code>
     * </pre>
     *
     * @param destination
     *            クラスファイルの出力先ディレクトリを指定します。
     * @param options
     *            コンパイルオプションを指定します。省略する場合はnullを指定できます。
     * @param sources
     *            コンパイルするクラス名とソーステキストを指定します。
     * @return コンパイル結果のクラスオブジェクトをロードするためのクラスローダを返します。
     *         <code>ClassLoader loader = compile(...); Class<?> clazz = loader.loadClass(クラス名);</code>
     * @throws SimplerJavaCompileError
     *             コンパイルエラーが発生したときにスローします。
     * @throws MalformedURLException
     *             destinationで指定したディレクトリをURLに変換できないときにスローします。
     */
    public static ClassLoader compile(File destination, List<String> options, List<Source> sources) {
        try {
            javax.tools.JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
            List<String> compilationOptions = new ArrayList<>();
            compilationOptions.addAll(List.of("-d", destination.getAbsolutePath()));
            if (options != null)
                compilationOptions.addAll(options);
            CompilationTask task = compiler.getTask(
                null, null, diagnostics, compilationOptions, null, sources);
            if (!task.call())
                throw new SimplerJavaCompileError(diagnostics);
            return new URLClassLoader(new URL[] {destination.toURI().toURL()});
        } catch (SimplerJavaCompileError | MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void compileGo(File destination, List<String> options, Source source, String[] args) {
        try {
            ClassLoader loader = compile(destination, options, List.of(source));
            Class<?> clazz = loader.loadClass(source.name);
            clazz.getMethod("main", String[].class).invoke(null, new Object[] {args});
        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException
            | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }
}
