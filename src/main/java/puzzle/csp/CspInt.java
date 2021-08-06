package puzzle.csp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

public class CspInt {

    public static class Problem {
        private final Map<String, Variable> variables_private = new LinkedHashMap<>();
        private final List<Constraint> constraints = new ArrayList<>();

        public Variable variable(String name, Domain domain) {
            if (variables_private.containsKey(name))
                throw new IllegalArgumentException("variable '" + name + "' duplicate");
            Variable v =  new Variable(name, domain);
            variables_private.put(name, v);
            return v;
        }

        public Constraint constraint(IntsPredicate predicate, Variable... variables) {
            return new Constraint(predicate, variables);
        }
    }

    public static class Domain {
        private final int[] values;

        private Domain(int... elements) {
            if (elements.length == 0)
                throw new IllegalArgumentException("empty elements");
            this.values = elements;
        }

        public Domain of(int... elements) {
            return new Domain(elements.clone());
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
    }

}
