package test.tree;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.AbstractSet;
import java.util.Iterator;

import org.junit.jupiter.api.Test;

class TestTreeSet0 {

    public class TreeSet<E extends Comparable<E>> extends AbstractSet<E> {

        @Override
        public Iterator<E> iterator() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public int size() {
            // TODO Auto-generated method stub
            return 0;
        }

    }

    @Test
    void test() {
        fail("Not yet implemented");
    }

}
