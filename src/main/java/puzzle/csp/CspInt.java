package puzzle.csp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.IntStream;

public class CspInt {

    public static class Problem {
        private final List<Variable> pVariables = new ArrayList<>();
        private final List<Constraint> pConstraints = new ArrayList<>();
        public final List<Variable> variables = Collections.unmodifiableList(pVariables);
        public final List<Constraint> constraints = Collections.unmodifiableList(pConstraints);

        public Variable variable(String name, Domain domain) {
            if (pVariables.stream().anyMatch(v -> v.name.equals(name)))
                throw new IllegalArgumentException("variable '" + name + "' duplicate");
            Variable v = new Variable(name, domain);
            pVariables.add(v);
            return v;
        }

        public Constraint constraint0(IntsPredicate predicate, Variable... variables) {
            Constraint c = new Constraint(predicate, variables);
            pConstraints.add(c);
            for (Variable v : variables)
                v.pConstraints.add(c);
            return c;
        }

        public Constraint constraint(IntsPredicate1 predicate, Variable a) {
            return constraint0(predicate, a);
        }

        public Constraint constraint(IntsPredicate2 predicate, Variable a, Variable b) {
            return constraint0(predicate, a, b);
        }

        public Constraint constraint(IntsPredicate3 predicate, Variable a, Variable b, Variable c) {
            return constraint0(predicate, a, b, c);
        }

        public Constraint constraint(IntsPredicate4 predicate, Variable a, Variable b, Variable c,
            Variable d) {
            return constraint0(predicate, a, b, c, d);
        }

        public Constraint constraint(IntsPredicate5 predicate, Variable a, Variable b, Variable c,
            Variable d, Variable e) {
            return constraint0(predicate, a, b, c, d, e);
        }

        public Constraint constraint(IntsPredicate6 predicate, Variable a, Variable b, Variable c,
            Variable d, Variable e, Variable f) {
            return constraint0(predicate, a, b, c, d, e, f);
        }

        public Constraint constraint(IntsPredicate7 predicate, Variable a, Variable b, Variable c,
            Variable d, Variable e, Variable f, Variable g) {
            return constraint0(predicate, a, b, c, d, e, f, g);
        }

        public Constraint constraint(IntsPredicate8 predicate, Variable a, Variable b, Variable c,
            Variable d, Variable e, Variable f, Variable g, Variable h) {
            return constraint0(predicate, a, b, c, d, e, f, g, h);
        }

        public Constraint constraint(IntsPredicate9 predicate, Variable a, Variable b, Variable c,
            Variable d, Variable e, Variable f, Variable g, Variable h, Variable i) {
            return constraint0(predicate, a, b, c, d, e, f, g, h, i);
        }

        public Constraint constraint(IntsPredicate10 predicate, Variable a, Variable b, Variable c,
            Variable d, Variable e, Variable f, Variable g, Variable h, Variable i, Variable j) {
            return constraint0(predicate, a, b, c, d, e, f, g, h, i, j);
        }

        public void allDifferent(Variable... variables) {
            for (int i = 0, max = variables.length; i < max; ++i)
                for (int j = i + 1; j < max; ++j)
                    constraint((a, b) -> a != b, variables[i], variables[j]);
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

    public interface IntsPredicate {
        boolean test(int... a);
    }

    public interface IntsPredicate1 extends IntsPredicate {
        default boolean test(int... a) {
            return test0(a[0]);
        }

        boolean test0(int a);
    }

    public interface IntsPredicate2 extends IntsPredicate {
        default boolean test(int... a) {
            return test0(a[0], a[1]);
        }

        boolean test0(int a, int b);
    }

    public interface IntsPredicate3 extends IntsPredicate {
        default boolean test(int... a) {
            return test0(a[0], a[1], a[2]);
        }

        boolean test0(int a, int b, int c);
    }

    public interface IntsPredicate4 extends IntsPredicate {
        default boolean test(int... a) {
            return test0(a[0], a[1], a[2], a[3]);
        }

        boolean test0(int a, int b, int c, int d);
    }

    public interface IntsPredicate5 extends IntsPredicate {
        default boolean test(int... a) {
            return test0(a[0], a[1], a[2], a[3], a[4]);
        }

        boolean test0(int a, int b, int c, int d, int e);
    }

    public interface IntsPredicate6 extends IntsPredicate {
        default boolean test(int... a) {
            return test0(a[0], a[1], a[2], a[3], a[4], a[5]);
        }

        boolean test0(int a, int b, int c, int d, int e, int f);
    }

    public interface IntsPredicate7 extends IntsPredicate {
        default boolean test(int... a) {
            return test0(a[0], a[1], a[2], a[3], a[4], a[5], a[6]);
        }

        boolean test0(int a, int b, int c, int d, int e, int f, int g);
    }

    public interface IntsPredicate8 extends IntsPredicate {
        default boolean test(int... a) {
            return test0(a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7]);
        }

        boolean test0(int a, int b, int c, int d, int e, int f, int g, int h);
    }

    public interface IntsPredicate9 extends IntsPredicate {
        default boolean test(int... a) {
            return test0(a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8]);
        }

        boolean test0(int a, int b, int c, int d, int e, int f, int g, int h, int i);
    }

    public interface IntsPredicate10 extends IntsPredicate {
        default boolean test(int... a) {
            return test0(a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9]);
        }

        boolean test0(int a, int b, int c, int d, int e, int f, int g, int h, int i, int j);
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
