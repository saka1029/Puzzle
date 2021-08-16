package puzzle.csp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CspInt {

    public static class Problem {
        private final List<Variable> pVariables = new ArrayList<>();
        private final List<Constraint> pConstraints = new ArrayList<>();
        private final Map<String, Variable> pNameVariables = new HashMap<>();
        public final List<Variable> variables = Collections.unmodifiableList(pVariables);
        public final List<Constraint> constraints = Collections.unmodifiableList(pConstraints);
        Pattern variableNames = null;

        public Variable variable(String name, Domain domain) {
            if (pNameVariables.containsKey(name))
                throw new IllegalArgumentException("variable '" + name + "' duplicate");
            if (variableNames != null)
                throw new IllegalStateException("cannot define variable");
            Variable v = new Variable(name, domain);
            pVariables.add(v);
            pNameVariables.put(name, v);
            return v;
        }
        
        public Variable variable(String name) {
            return pNameVariables.get(name);
        }

        public Pattern variableNames() {
            if (variableNames != null)
                return variableNames;
            return variableNames = Pattern.compile(
                "\\b("
                + pVariables.stream().map(v -> v.name).collect(Collectors.joining("|"))
                + ")\\b");
        }

        public List<Variable> variables(String predicate) {
            List<Variable> variables = new ArrayList<>();
            Matcher m = variableNames().matcher(predicate);
            while (m.find())
                variables.add(variable(m.group()));
            return variables;
        }

        public Constraint constraint(String predicate) {
            List<Variable> variables = variables(predicate);
            Constraint c = new Constraint(predicate, variables);
            pConstraints.add(c);
            for (Variable v : variables)
                v.pConstraints.add(c);
            return c;
        }
    }

    public static class Domain {
        private final int[] values;

        private Domain(int... values) {
            if (values.length == 0)
                throw new IllegalArgumentException("empty elements");
            this.values = values;
        }

        public static Domain of(int... values) {
            return new Domain(values.clone());
        }

        public static Domain range(int start, int end) {
            return new Domain(IntStream.range(start, end).toArray());
        }

        public static Domain rangeClosed(int start, int end) {
            return new Domain(IntStream.rangeClosed(start, end).toArray());
        }

        public int[] values() {
            return values.clone();
        }

        @Override
        public String toString() {
            return Arrays.toString(values);
        }

    }

    public static class Variable {
        public final String name;
        public final Domain domain;
        final List<Constraint> pConstraints = new ArrayList<>();
        public final List<Constraint> constraints = Collections.unmodifiableList(pConstraints);

        Variable(String name, Domain domain) {
            Objects.requireNonNull(name, "name");
            Objects.requireNonNull(domain, "domain");
            this.name = name;
            this.domain = domain;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static class Constraint {
        public final String predicate;
        public final List<Variable> variables;

        Constraint(String predicate, List<Variable> variables) {
            Objects.requireNonNull(predicate, "predicate");
            if (variables.size() == 0)
                throw new IllegalArgumentException("require at least one variable");
            this.predicate = predicate;
            this.variables = Collections.unmodifiableList(variables);
        }

        @Override
        public String toString() {
            return predicate;
//            return "constraint:" + predicate + variables;
        }
    }

    public static class Solver {
        public static List<List<Constraint>> constraintOrder(Problem problem,
            List<Variable> bindingOrder) {
            if (!(new HashSet<>(bindingOrder).equals(new HashSet<>(problem.variables))))
                throw new IllegalArgumentException(
                    "bindingOrder must exactry contain all variables");
            Set<Constraint> remainConstraints = new HashSet<>(problem.constraints);
            List<List<Constraint>> constraintOrder = new ArrayList<>();
            Set<Variable> boundVariables = new HashSet<>();
            for (Variable v : bindingOrder) {
                boundVariables.add(v);
                List<Constraint> constraints = new ArrayList<>();
                constraintOrder.add(constraints);
                for (Constraint c : remainConstraints) {
                    if (boundVariables.containsAll(c.variables))
                        constraints.add(c);
                }
                remainConstraints.removeAll(constraints);
            }
            if (remainConstraints.size() != 0)
                throw new IllegalStateException("Illegal constraints: " + remainConstraints);
            return constraintOrder;
        }
    }

}
