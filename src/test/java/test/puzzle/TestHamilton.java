package test.puzzle;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

class TestHamilton {

    static class Node {
        public final String name;

        private int no = -1;
        public int no() { return no; }

        private Set<Node> links = new HashSet<>();
        public void addLink(Node next) {
            links.add(next);
        }

        private Node next;
        public Node next() { return next; }

        public Node(String name) {
            this.name = name;
        }

        void backup(Node next) {
            this.next = next;
            next.links.remove(this);
            for (Node n : next.links)
                n.links.remove(next);
        }

        void restore(Node next) {
            for (Node n : next.links)
                n.links.add(next);
            next.links.add(this);
            this.next = null;
        }

        void search(int no, int size, Consumer<Node> found) {
            if (no >= size && this.no == 0)
                found.accept(this);
            else if (this.next == null) {
                this.no = no;
                for (Node next : links) {
                    backup(next);
                    this.next = next;
                    next.search(no + 1, size, found);
                    this.next = null;
                    restore(next);
                }
                this.no = -1;
            } else
                System.out.println(this);
        }

        public void search(int size, Consumer<Node> found) {
            search(0, size, found);
        }

        @Override
        public String toString() {
            return String.format("(%d:%s next=%s links=%s)",
                no, name, next != null ? next.name : null,
                links.stream().map(n -> n.name).collect(Collectors.joining(", ", "[", "]")));
        }
    }

    @Test
    void testNode2() {
        Node a = new Node("A");
        Node b = new Node("B");
        Node[] nodes = {a, b};
        a.addLink(b);
        b.addLink(a);
        System.out.println(Arrays.toString(nodes));
        a.search(nodes.length, n -> System.out.println(n));
    }

}
