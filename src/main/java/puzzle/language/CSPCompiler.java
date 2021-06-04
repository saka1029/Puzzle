package puzzle.language;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
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
                    writer.printf("public static void solve(Consumer<Map<String, Integer>> callback) {%n", className);
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
                    writer.printf("callback.accept(Map.of(%s));%n",
                        variables.stream()
                            .map(v -> "\"" + v.name + "\", " + v.name)
                            .collect(Collectors.joining(", ")));
                    epilog();
                }

            }.gen();
        }

        public void solve(File destination, Consumer<Map<String, Integer>> callback)
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

        public Set<Map<String, Integer>> solve(File destination)
            throws MalformedURLException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, SecurityException,
            ClassNotFoundException, SimplerJavaCompileError, IOException {
            Set<Map<String, Integer>> answers = new LinkedHashSet<>();
            solve(destination, answers::add);
            return answers;
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

            void semicolon() {
                if (!match(";"))
                    throw error("';' expected");
            }

            void problem() {
                if (match("problem"))
                    if (match(CLASS_NAME)) {
                        packageClassName = token;
                        int p = packageClassName.lastIndexOf('.');
                        packageName = p < 0 ? null : packageClassName.substring(0, index);
                        className = p < 0 ? packageClassName : packageClassName.substring(index + 1);
                        semicolon();
                    } else
                        throw error("FQCN expected");
                else
                    throw error("'problem' expected");
            }

            void imports() {
                while (match("import"))
                    if (match(IMPORT_NAME)) {
                        imports.add(token);
                        semicolon();
                    }
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
                    semicolon();
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
                        semicolon();
                    } else if (match("different")) {
                        List<String> vars = new ArrayList<>();
                        while (match(VARIABLE_NAME))
                            vars.add(token);
                        problem.allDifferent(vars.toArray(String[]::new));
                        semicolon();
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

    static class StringSequence implements CharSequence {
        final String s;
        public int index;

        public StringSequence(String s, int index) {
            this.s = s;
            this.index = index;
        }

        @Override
        public int length() {
            return s.length() - index;
        }

        @Override
        public char charAt(int index) {
            return s.charAt(this.index + index);
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            return s.substring(index + start, index + end);
        }

        public boolean startsWith(String pat) {
            return s.startsWith(pat, index);
        }
    }
}
