package puzzle.language;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class SimpleJavaCompiler {

    public static class SimpleJavaCompileError extends Exception {
        private static final long serialVersionUID = 1L;

        public SimpleJavaCompileError(DiagnosticCollector<JavaFileObject> diagnostics) {
            super(diagnostics.getDiagnostics().stream()
                .map(d -> d + System.lineSeparator()).collect(Collectors.joining()));
        }
    }

    /**
     *
     * @param className
     * @param source
     * @param options
     * @return
     * @throws IOException
     * @throws SimpleJavaCompileError
     * @throws ClassNotFoundException
     */
    public static Class<?> compile(String className, String source, String... options)
        throws IOException, SimpleJavaCompileError, ClassNotFoundException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        JavaByteObject byteObject = new JavaByteObject(className);
        StandardJavaFileManager standardFileManager = compiler.getStandardFileManager(diagnostics,
            null, null);
        try (JavaFileManager fileManager = createFileManager(standardFileManager, byteObject)) {
            Iterable<? extends JavaFileObject> compilationUnits = Arrays
                .asList(new JavaStringObject(className, source));
            JavaCompiler.CompilationTask task = compiler.getTask(
                null, fileManager, diagnostics, Arrays.asList(options), null, compilationUnits);
            if (!task.call())
                throw new SimpleJavaCompileError(diagnostics);
        }
        Class<?> clazz = new ByteObjectClassLoader(byteObject).loadClass(className);
        return clazz;
    }

    private static JavaFileManager createFileManager(StandardJavaFileManager fileManager, JavaByteObject byteObject) {
        return new ForwardingJavaFileManager<StandardJavaFileManager>(fileManager) {
            @Override
            public JavaFileObject getJavaFileForOutput(Location location, String className,
                JavaFileObject.Kind kind, FileObject sibling) throws IOException {
                return byteObject;
            }
        };
    }

    static class JavaByteObject extends SimpleJavaFileObject {
        final ByteArrayOutputStream outputStream;

        JavaByteObject(String name) {
            super(URI.create("bytes:///" + name + name.replaceAll("\\.", "/")), Kind.CLASS);
            outputStream = new ByteArrayOutputStream();
        }

        @Override
        public OutputStream openOutputStream() throws IOException {
            return outputStream;
        }

        public byte[] getBytes() {
            return outputStream.toByteArray();
        }
    }

    static class JavaStringObject extends SimpleJavaFileObject {
        final String source;

        JavaStringObject(String name, String source) {
            super(URI.create("string:///" + name.replaceAll("\\.", "/") + Kind.SOURCE.extension), Kind.SOURCE);
            this.source = source;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors)
            throws IOException {
            return source;
        }
    }

    static class ByteObjectClassLoader extends ClassLoader {
        final JavaByteObject byteObject;

        ByteObjectClassLoader(JavaByteObject byteObject) {
            this.byteObject = byteObject;
        }

        @Override
        public Class<?> findClass(String name) throws ClassNotFoundException {
            byte[] bytes = byteObject.getBytes();
            return defineClass(name, bytes, 0, bytes.length);
        }
    }

}
