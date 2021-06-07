package puzzle.language.csp;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Compiler {

    private Compiler() {
    }

    public static final String TEMPLATE;
    static {
        try (InputStream is = Compiler.class.getResourceAsStream("Compiler.template")) {
            TEMPLATE = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class Problem {
        final Map<String, Variable> variables = new LinkedHashMap<>();
        final List<Constraint> constraints = new ArrayList<>();
        final String fqcn;
        final List<String> imports;
        String functions = null;

        public Problem(String fqcn, String... imports) {
            this.fqcn = fqcn;
            this.imports = List.of(imports);
        }

        public Variable variable(String name, int[] domain) {
            if (variables.containsKey(name))
                throw new IllegalArgumentException("duplicated variable: " + name);
            if (!constraints.isEmpty())
                throw new IllegalStateException("constraint already added");
            Variable v = new Variable(name, domain);
            variables.put(name, v);
            return v;
        }

        public Constraint constraint(String expression, Variable... variables) {
            Constraint c = new Constraint(expression, variables);
            constraints.add(c);
            return c;
        }

        public void allDifferent(Variable... variables) {
            for (int i = 0, max = variables.length; i < max; ++i)
                for (int j = i + 1; j < max; ++j)
                    constraint(variables[i] + " != " + variables[j], variables[i], variables[j]);
        }

        static final String NAME = "\\p{L}[\\p{L}\\p{IsDigit}]*";
        static final Pattern MACRO_VAR = Pattern.compile("#" + NAME + "#(\r\n)*");

        static void append(StringBuilder sb, String format, Object... arguments) {
            sb.append(String.format(format, arguments));
        }

        void generate(Map<String, String> map) {
            int index = fqcn.lastIndexOf('.');
            map.put("#PACKAGE#", index >= 0 ?
                String.format("package " + fqcn.substring(0, index) + ";%n") : "");
            map.put("#CLASS#", index >= 0 ? fqcn.substring(index + 1) : fqcn);
            StringBuilder sb = new StringBuilder();
            sb.setLength(0);
            for (String s : imports)
                append(sb, "import %s;%n", s);
            map.put("#IMPORTS#", sb.toString());
            sb.setLength(0);
            for (Variable v : variables.values())
                append(sb, "int[] _%s_domain = {%s};%n", v.name,
                    IntStream.of(v.domain).mapToObj(n -> "" + n).collect(Collectors.joining(", ")));
            map.put("#DECLARE#", sb.toString());
            sb.setLength(0);
            Set<Constraint> remains = new HashSet<>(constraints);
            List<Variable> generated = new ArrayList<>();
            for (Variable v : variables.values()) {
                append(sb, "for (int %s : _%s_domain)%n", v.name, v.name);
                generated.add(v);
                List<Constraint> gen = remains.stream()
                    .filter(c -> generated.containsAll(c.variables))
                    .toList();
                if (!gen.isEmpty()) {
                    append(sb, "if (%s)%n",
                        gen.stream().map(c -> c.expression).collect(Collectors.joining(" && ")));
                    remains.removeAll(gen);
                }
            }
            append(sb, "callback.accept(new int[] {%s});%n", variables.values().stream()
                .map(v -> v.name).collect(Collectors.joining(", ")));
            map.put("#FOR#", sb.toString());
            map.put("#FUNCTIONS#", functions != null ? functions : "");
            sb.setLength(0);
            map.put("#VARIABLES#", "\"" + variables.values().stream()
                .map(v -> v.name).collect(Collectors.joining(",")) + "\"");
        }

        public String generate() {
            Map<String, String> map = new HashMap<>();
            generate(map);
            StringBuilder sb = new StringBuilder();
            Matcher m = MACRO_VAR.matcher(TEMPLATE);
            while (m.find()) {
                String repl = map.computeIfAbsent(m.group().trim(), k -> "!UNDEFINED " + k + "!");
                m.appendReplacement(sb, repl);
            }
            m.appendTail(sb);
            return sb.toString();
        }
    }

    public static class Variable {
        final String name;
        final int[] domain;
        final List<Constraint> constraints = new ArrayList<>();

        Variable(String name, int[] domain) {
            this.name = name;
            this.domain = domain.clone();
        }
    }

    public static class Constraint {
        final String expression;
        final List<Variable> variables;

        Constraint(String expression, Variable... variables) {
            this.expression = expression;
            this.variables = List.of(variables);
            for (Variable v : variables)
                v.constraints.add(this);
        }
    }
}
