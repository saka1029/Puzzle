package puzzle.csp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class CspInt {

    public static class Problem {
        final List<Variable> pVariables = new ArrayList<>();
        final List<Constraint> pConstraints = new ArrayList<>();
        public final List<Variable> variables = Collections.unmodifiableList(pVariables);
        public final List<Constraint> constraints = Collections.unmodifiableList(pConstraints);

        public Variable variable(String name, Domain domain) {
            if (pVariables.stream().anyMatch(v -> v.name.equals(name)))
                throw new IllegalArgumentException("variable '" + name + "' duplicate");
            Variable v =  new Variable(name, domain);
            pVariables.add(v);
            return v;
        }

        public Constraint constraint(IntsPredicate predicate, Variable... variables) {
            Constraint c = new Constraint(predicate, variables);
            for (Variable v : variables)
                v.pConstraints.add(c);
            return c;
        }

        public void allDifferent(Variable... variables) {
            for (int i = 0, max = variables.length; i < max; ++i)
                for (int j = i + 1; j < max; ++j)
                    constraint(a -> a[0] != a[1], variables[i], variables[j]);
        }
    }

    public static class Domain {
        private final int[] values;

        private Domain(int... values) {
            if (values.length == 0)
                throw new IllegalArgumentException("empty elements");
            this.values = values;
        }

        public Domain of(int... values) {
            return new Domain(values.clone());
        }

        public Domain range(int start, int end) {
            return new Domain(IntStream.range(start, end).toArray());
        }

        public Domain rangeClosed(int start, int end) {
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

    interface IntsPredicate {
        boolean test(int... a);
    }

    public static class Constraint {
        public final IntsPredicate predicate;
        public final List<Variable> variables;

        Constraint(IntsPredicate predicate, Variable... variables) {
            Objects.requireNonNull(predicate, "predicate");
            if (variables.length == 0)
                throw new IllegalArgumentException("empty variables");
            this.predicate = predicate;
            this.variables = List.of(variables);
        }

        @Override
        public String toString() {
            return "constraint" + variables;
        }
    }

}
