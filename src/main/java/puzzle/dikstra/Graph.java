package puzzle.dikstra;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Graph {

    static class Node {

        final String name;
        int d;
        Node prev = null;
        final Map<Node, Integer> reachableNodes = new HashMap<>();

        Node(String name) {
            this.name = name;
        }

        void length(Node node, int length) {
            reachableNodes.put(node, length);
        }

        @Override
        public String toString() {
            return String.format("Node(name=%s, d=%d, prev=%s)",
                name, d, prev == null ? "null" : prev.name);
        }
    }

    final Map<String, Node> nodes = new HashMap<>();

    public void add(String nodeName1, String nodeName2, int length) {
        Node node1 = nodes.computeIfAbsent(nodeName1, k -> new Node(nodeName1));
        Node node2 = nodes.computeIfAbsent(nodeName2, k -> new Node(nodeName2));
        node1.length(node2, length);
        node2.length(node1, length);
    }

    Set<Node> init(Node start) {
        Set<Node> q = new HashSet<>();
        for (Node node : nodes.values()) {
            node.d = node.equals(start) ? 0 : Integer.MAX_VALUE;
            node.prev = null;
            q.add(node);
        }
        return q;
    }

    public void solve(String startName) {
        Node start = nodes.get(startName);
        Set<Node> q = init(start);
        while (!q.isEmpty()) {
            Node u = q.stream().min(Comparator.comparingInt(n -> n.d)).get();
            q.remove(u);
            for (Entry<Node, Integer> e : u.reachableNodes.entrySet()) {
                Node v = e.getKey();
                int length = e.getValue();
                if (v.d > u.d + length) {
                    v.d = u.d + length;
                    v.prev = u;
                }
            }
        }
    }

    @Override
    public String toString() {
        return nodes.values().toString();
    }
}
