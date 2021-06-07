package puzzle.language.csp;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import puzzle.language.SimplerJavaCompiler;
import puzzle.language.SimplerJavaCompiler.SimplerJavaCompileError;

public class CSPCompiler {

    public static Pattern VARIABLE_PATTERN = Pattern
        .compile("\\p{L}[\\p{L}\\p{IsDigit}]*");

    public static int[] range(int start, int end) {
        return IntStream.rangeClosed(start, end).toArray();
    }

    public static class Problem {
        public final String packageName, className;
        public final String[] imports;
        final Map<String, Variable> _variables = new LinkedHashMap<>();
        public final Collection<Variable> variables = Collections
            .unmodifiableCollection(_variables.values());
        final Set<Constraint> _constraints = new LinkedHashSet<>();
        public final Set<Constraint> constraints = Collections
            .unmodifiableSet(_constraints);
        public String directText = null;

        public Problem(String packageName, String className, String... imports) {
            this.packageName = packageName;
            this.className = className;
            this.imports = imports.clone();
        }

        public String fullClassName() {
            return packageName == null || packageName.isBlank() ? className
                : packageName + "." + className;
        }

        public Variable variable(String name, int[] domain) {
            if (_constraints.size() > 0)
                throw new IllegalStateException("Constraint already added");
            if (_variables.containsKey(name))
                throw new IllegalArgumentException(
                    "Variable '" + name + "' already exists");
            Variable v = new Variable(name, domain);
            _variables.put(name, v);
            return v;
        }

        public Constraint constraint(String expression) {
            Constraint c = new Constraint(expression);
            _constraints.add(c);
            Matcher m = VARIABLE_PATTERN.matcher(expression);
            while (m.find()) {
                String var = m.group();
                if (!_variables.containsKey(var))
                    continue;
                Variable v = _variables.get(var);
                v._constraints.add(c);
                c._variables.add(v);
            }
            return c;
        }

        public void allDifferent(String... vars) {
            for (int i = 0, max = vars.length; i < max; ++i)
                for (int j = i + 1; j < max; ++j)
                    constraint(vars[i] + " != " + vars[j]);
        }

        @Override
        public String toString() {
            return "問題[" + variables + ", " + constraints + "]";
        }

        public String generate() throws IOException {
            try (StringWriter sw = new StringWriter()) {
                try (PrintWriter writer = new PrintWriter(sw)) {
                    generate(writer);
                }
                return sw.toString();
            }
        }

        void generate(PrintWriter writer) throws IOException {
            List<Variable> variables = new ArrayList<>(this.variables);
            Set<Variable> generated = new HashSet<>();
            Set<Constraint> constraints = new LinkedHashSet<>(this.constraints);
            new Object() {
                void constraint(Collection<Constraint> cset) {
                    if (cset.isEmpty())
                        return;
                    writer.printf("if (%s)%n", cset.stream().map(c -> c.expression)
                        .collect(Collectors.joining(" && ")));
                }

                void variable(Variable v) {
                    writer.printf("for (int %s : _%s_domain)%n", v.name, v.name);
                    generated.add(v);
                    List<Constraint> cset = constraints.stream()
                        .filter(c -> generated.containsAll(c.variables))
                        .toList();
                    constraint(cset);
                    constraints.removeAll(cset);
                }

                void prolog() {
                    String packageName = Problem.this.packageName;
                    if (packageName != null && !packageName.isBlank())
                        writer.printf("package %s;%n", packageName);
                    writer.printf("import java.util.Map;%n");
                    writer.printf("import java.util.function.Consumer;%n");
                    for (String s : imports)
                        writer.printf("import %s;%n", s);
                    writer.printf("public class %s {%n", className);
                    writer.printf("public static void solve(Consumer<int[]> callback) {%n", className);
                }

                void epilog() {
                    writer.printf("}%n");   // end of function solve
                    if (directText != null)
                        writer.print(directText);
                    writer.printf("}%n");   // end of class
                }

                void gen() {
                    prolog();
                    for (Variable v : variables)
                        writer.printf("int[] _%s_domain = {%s};%n", v.name,
                            IntStream.of(v.domain).mapToObj(i -> "" + i)
                                .collect(Collectors.joining(", ")));
                    for (Variable v : variables)
                        variable(v);
                    writer.printf("callback.accept(new int[] {%s});%n",
                        variables.stream()
                            .map(v -> v.name)
                            .collect(Collectors.joining(", ")));
                    epilog();
                }

            }.gen();
        }

        public void solve(File destination, Consumer<int[]> callback)
            throws MalformedURLException, SimplerJavaCompileError, IOException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, SecurityException,
            ClassNotFoundException {
            String fqcn = this.fullClassName();
            ClassLoader loader = SimplerJavaCompiler.compile(destination, null,
                new SimplerJavaCompiler.Source(fqcn, this.generate()));
            loader.loadClass(fqcn).getMethod("solve", Consumer.class)
                .invoke(null, callback);
        }

        public List<int[]> solve(File destination)
            throws MalformedURLException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, SecurityException,
            ClassNotFoundException, SimplerJavaCompileError, IOException {
            List<int[]> answers = new ArrayList<>();
            solve(destination, answers::add);
            return answers;
        }

        public void print(List<int[]> answers, Consumer<String> output) {
            int columns = variables.size();
            int[] width = new int[columns];
            int i = 0;
            for (Variable v : variables) {
                int c = i;
                width[i] = Math.max(v.name.length(),
                    answers.stream()
                        .mapToInt(row -> ("" + row[c]).length())
                        .max().orElse(0));
                ++i;
            }
            StringBuilder line = new StringBuilder();
            i = 0;
            for (Variable v : variables)
                line.append(String.format("%" + (width[i++] + 1) + "s", v.name));
            output.accept(line.toString());
            line.setLength(0);
            for (int c = 0; c < columns; ++c)
                line.append(" " + "-".repeat(width[c]));
            output.accept(line.toString());
            for (int[] row : answers) {
                line.setLength(0);
                for (int c = 0, max = row.length; c < max; ++c)
                    line.append(String.format("%" + (width[c] + 1) + "s", row[c]));
                output.accept(line.toString());
            }
        }
    }

    public static class Variable {
        public final String name;
        public final int[] domain;
        final Set<Constraint> _constraints = new LinkedHashSet<>();
        public final Set<Constraint> constrains = Collections.unmodifiableSet(_constraints);

        Variable(String name, int[] domain) {
            this.name = name;
            this.domain = domain.clone();
        }

        @Override
        public String toString() {
            return name + Arrays.toString(domain);
        }
    }

    public static class Constraint {
        public final String expression;
        final Set<Variable> _variables = new LinkedHashSet<>();
        public final Set<Variable> variables = Collections
            .unmodifiableSet(_variables);

        Constraint(String expression) {
            this.expression = expression;
        }

        @Override
        public String toString() {
            return "制約[" + expression + "]";
        }
    }

    static final String NAME = "\\p{L}[\\p{L}\\p{IsDigit}]*";
    static final Pattern CLASS_NAME = Pattern.compile("^" + NAME + "(\\." + NAME + ")*");
    static final Pattern IMPORT_NAME = Pattern.compile("^(static\\s+)?" + NAME + "(\\." + NAME + ")*(\\.\\*)?");
    static final Pattern VARIABLE_NAME = Pattern.compile("^" + NAME);
    static final Pattern NUMBER = Pattern.compile("^-?\\d+");

    /**
     * <pre>
     * PROBLEM          = 'problem' NAME { '.' NAME } ';'
     *                    { 'import' ['static'] NAME { '.' NAME } ['.*'] ';' }
     *                    { 'variable' DOMAIN NAME { NAME } ';' }
     *                    { 'different' NAME { NAME } ';' }
     *                    { 'constraint' CONSTRAINT_STRING ';' }
     *                    DIRECT_TEXT
     * NAME             = \p{L} { \p{L} | \p{IsDigit} }
     * DOMAIN           = '[' RANGE { RANGE } ']'
     * RANGE            = NUMBER [ '..' NUMBER ]
     * NUMBER           = [ '-' ] DIGIT { DIGIT }
     * DIGIT            = '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9'
     * CONSTRINT_STRING = ';'以外の文字列
     * DIRECT_TEXT      =  任意のテキスト(クラス内に展開される)
     * </pre>
     * @param source
     * @return
     */
    public static Problem parse(String source) {
        return new Object() {
            int length = source.length();
            int index = 0;
            String packageClassName, packageName = null, className = null;
            String token;
            List<String> imports = new ArrayList<>();
            Problem problem;

            RuntimeException error(String format, Object... arguments) {
                return new RuntimeException(String.format(format, arguments));
            }

            void spaces() {
                while (index < length) {
                    char ch = source.charAt(index);
                    if (Character.isWhitespace(ch))
                        ++index;
                    else if (ch == '#')
                        while (index < length && ch != '\r' && ch != '\n')
                            ch = source.charAt(++index);
                    else
                        break;
                }
            }

            boolean match(String e) {
                spaces();
                if (!source.startsWith(e, index))
                    return false;
                token = e;
                index += token.length();
                return true;
            }

            boolean match(Pattern e) {
                spaces();
                Matcher m = e.matcher(source.substring(index));
                if (!m.find())
                    return false;
                token = m.group();
                index += token.length();
                return true;
            }

            void semicolon(String word) {
                if (!match(";"))
                    throw error("';' expected after %s", word);
            }

            void problem() {
                if (match("problem"))
                    if (match(CLASS_NAME)) {
                        packageClassName = token;
                        int p = packageClassName.lastIndexOf('.');
                        packageName = p < 0 ? null : packageClassName.substring(0, index);
                        className = p < 0 ? packageClassName : packageClassName.substring(index + 1);
                        semicolon(token);
                    } else
                        throw error("FQCN expected");
                else
                    throw error("'problem' expected");
            }

            void imports() {
                while (match("import"))
                    if (match(IMPORT_NAME)) {
                        imports.add(token);
                        semicolon(token);
                    } else
                        throw error("import class expected");
            }

            int[] domain() {
                Set<Integer> domain = new TreeSet<>();
                if (!match("["))
                    throw error("'[' expected");
                while (match(NUMBER)) {
                    int start = Integer.parseInt(token);
                    if (match(".."))
                        if (match(NUMBER))
                            for (int i = start, end = Integer.parseInt(token); i <= end; ++i)
                                domain.add(i);
                        else
                            throw error("number expected after '..'");
                    else
                        domain.add(start);
                }
                if (!match("]"))
                    throw error("']' expected");
                return domain.stream()
                    .mapToInt(Integer::intValue)
                    .toArray();
            }

            void variables() {
                while (match("variable")) {
                    int[] domain = domain();
                    while (match(VARIABLE_NAME))
                        problem.variable(token, domain);
                    semicolon(token);
                }
            }

            void constraints() {
                while (true) {
                    if (match("constraint")) {
                        spaces();
                        StringBuilder sb = new StringBuilder();
                        while (index < length && source.charAt(index) != ';')
                            sb.append(source.charAt(index++));
                        problem.constraint(sb.toString());
                        semicolon(token);
                    } else if (match("different")) {
                        List<String> vars = new ArrayList<>();
                        while (match(VARIABLE_NAME))
                            vars.add(token);
                        problem.allDifferent(vars.toArray(String[]::new));
                        semicolon(token);
                    } else
                        break;
                }
            }

            Problem parse() {
                problem();
                imports();
                problem = new Problem(packageName, className, imports.toArray(String[]::new));
                variables();
                constraints();
                if (index < length)
                    problem.directText = source.substring(index);
                return problem;
            }
        }.parse();
    }

    static void usage(String opt) {
        System.err.println("java " + CSPCompiler.class.getName() + " [-d DEST] [-s] CSP_FILE");
        System.err.println("-d DEST  クラスファイルの出力ディレクトリ");
        System.err.println("-s       生成されたJavaソースの表示");
        throw new IllegalArgumentException("unknown option: " + opt);
    }

    public static void main(String[] args) throws IOException, IllegalAccessException,
        IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
        SecurityException, ClassNotFoundException, SimplerJavaCompileError {
        File destination = new File(".");
        boolean printSource = false;
        int i = 0;
        L: for (int max = args.length; i < max; ++i) {
            switch (args[i]) {
            case "-d":
                destination = new File(args[++i]);
                break;
            case "-s":
                printSource = true;
                break;
            default:
                if (args[i].startsWith("-"))
                    usage(args[i]);
                break L;
            }
        }
        long start = System.currentTimeMillis();
        Problem problem = parse(Files.readString(Path.of(args[i])));
        int[] width = problem.variables.stream()
            .peek(v -> System.out.print("  " + v.name))
            .mapToInt(v -> v.name.length())
            .toArray();
        System.out.println();
        System.out.println("-".repeat(IntStream.of(width).map(k -> k + 2).sum()));
        problem.solve(destination, row -> {
            for (int c = 0, max = row.length; c < max; ++c)
                System.out.printf("%" + (width[c] + 2) + "d", row[c]);
            System.out.println();
        });
        System.out.println();
        if (printSource)
            System.out.println(problem.generate());
        System.out.printf("%d msec.%n", System.currentTimeMillis() - start);
    }
}
