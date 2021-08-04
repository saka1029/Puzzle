package puzzle.csp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class CspInt {

    public static class Problem {
        private final Map<String, Variable> variables = new LinkedHashMap<>();
        private final List<Constraint> constraints = new ArrayList<>();

    }

    public static class Domain implements Iterable<Integer> {
        private final int[] elements;

        private Domain(int[] elements) {
            this.elements = elements;
        }

        public Domain of(int... elements) {
            return new Domain(elements.clone());
        }

        public Domain rangeClosed(int start, int end) {
            return new Domain(IntStream.rangeClosed(start, end).toArray());
        }

        @Override
        public Iterator<Integer> iterator() {
            return new Iterator<Integer>() {
                int index = 0;

                @Override
                public boolean hasNext() {
                    return index < elements.length;
                }

                @Override
                public Integer next() {
                    return elements[index++];
                }
            };
        }

        @Override
        public String toString() {
            return Arrays.toString(elements);
        }

    }

    public static class Variable {
        public final String name;
        public final Domain domain;

        Variable(String name, Domain domain) {
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
            this.predicate = predicate;
            this.variables = List.of(variables);
        }
    }

}
