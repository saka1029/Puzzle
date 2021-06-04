package puzzle.language;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import puzzle.language.SimplerJavaCompiler.SimplerJavaCompileError;

public class CSPCompiler {

    public static Pattern VARIABLE_PATTERN = Pattern
        .compile("\\p{IsLetter}[\\p{IsLetter}\\p{IsDigit}]*");

    public static int[] range(int start, int end) {
        return IntStream.rangeClosed(start, end).toArray();
    }

    public static class Problem {
        public final String packageName, className;
        final Map<String, Variable> _variables = new LinkedHashMap<>();
        public final Collection<Variable> variables = Collections
            .unmodifiableCollection(_variables.values());
        final Set<Constraint> _constraints = new LinkedHashSet<>();
        public final Set<Constraint> constraints = Collections
            .unmodifiableSet(_constraints);

        public Problem(String packageName, String className) {
            this.packageName = packageName;
            this.className = className;
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

        public String generate() throws IOException {
            try (StringWriter sw = new StringWriter()) {
                try (PrintWriter writer = new PrintWriter(sw)) {

                    new Object() {
                        List<Variable> variables = new ArrayList<>(
                            Problem.this.variables);
                        Set<Variable> generated = new HashSet<>();
                        Set<Constraint> constraints = new HashSet<>(
                            Problem.this.constraints);

                        void constraint(Collection<Constraint> cset) {
                            if (cset.isEmpty())
                                return;
                            writer.printf("if (%s)%n",
                                cset.stream().map(c -> c.expression)
                                    .collect(Collectors.joining(" && ")));
                        }

                        void variable(Variable v) {
                            writer.printf("for (int %s : _%s_domain)%n", v.name,
                                v.name);
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
                                writer.printf("package %s;%n", Problem.this.packageName);
                            writer.printf("import java.util.Map;%n");
                            writer.printf(
                                "import java.util.function.Consumer;%n");
                            writer.printf("public class %s {%n",
                                Problem.this.className);
                            writer.printf(
                                "public static void solve(Consumer<Map<String, Integer>> callback) {%n",
                                Problem.this.className);
                        }

                        void epilog() {
                            writer.printf("}%n");
                            writer.printf("}%n");
                        }

                        void gen() {
                            prolog();
                            for (Variable v : variables)
                                writer.printf("int[] _%s_domain = {%s};%n",
                                    v.name,
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
                return sw.toString();
            }
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
    }

    public static class Variable {
        public final String name;
        public final int[] domain;
        final Set<Constraint> _constraints = new LinkedHashSet<>();
        public final Set<Constraint> constrains = Collections
            .unmodifiableSet(_constraints);

        Variable(String name, int[] domain) {
            this.name = name;
            this.domain = domain.clone();
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
    }
}
