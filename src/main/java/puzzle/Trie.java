package puzzle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * メモリを節約するためにトライ木ごとに一つのマップしか持たない
 * (ノードごとにマップを持たない)トライ木の実装です。
 *
 * @param <V>
 */
public class Trie<V> {

    static final long INC = 1L << 32;
    static final long MASK = INC - 1L;

    long nextNodeNo = 0;
    Node root = new Node();
    private final Map<Long, Node> nodes = new HashMap<>();

    public int size() {
        return nodes.size();
    }

    public void put(String s, V data) {
        Node node = root;
        for (int i = 0, len = s.length(); i < len; ++i)
            node = node.put(s.charAt(i));
        node.data = data;
    }

    public V get(String s) {
        Node node = root;
        for (int i = 0, len = s.length(); i < len; ++i)
            if ((node = node.get(s.charAt(i))) == null)
                return null;
        return node.data;
    }

    public List<V> search(String s) {
        List<V> result = new ArrayList<>();
        Node node = root;
        for (int i = 0, len = s.length(); i < len; ++i) {
            V v = node.data;
            if (v != null)
                result.add(v);
            if ((node = node.get(s.charAt(i))) == null)
                break;
        }
        return result;
    }

    @Override
    public String toString() {
        return root.toString();
    }

    class Node {
        final long no;
        V data;

        Node() {
            this.no = nextNodeNo;
            nextNodeNo += INC;
        }

        long makeKey(int key) {
            return no | key;
        }

        Node get(int key) {
            return nodes.get(no | key);
        }

        Node put(int key) {
            return nodes.computeIfAbsent(no | key, k -> new Node());
        }

        /**
         * このメソッドはtoString()で利用するためだけに実装されています。
         */
        Map<String, Node> children() {
            return nodes.entrySet().stream()
                .filter(e -> (e.getKey() & ~MASK) == no)
                .map(e -> Map.entry(Character.toString((int) (e.getKey() & MASK)), e.getValue()))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
        }

        @Override
        public String toString() {
            return "" + data + children();
        }
    }

}