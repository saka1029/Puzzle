package puzzle.csp;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.IntStream;

public class CspInt {
    
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

    public static class Problem {
        
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

}
