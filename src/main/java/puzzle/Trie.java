package puzzle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * メモリを節約するためにノードごとにマップを持たないトライ木の実装です。
 * マップ(Map<Long, Node>)のキーは上位32ビットがノードの一連番号で、
 * 下位32ビットが子ノードの先頭文字(Character)です。
 *
 * @param <V>
 */
public class Trie<V> {

    static final long INC_NODE_NO = 1L << Integer.SIZE;
    static final long CHAR_MASK = INC_NODE_NO - 1L;
    static final long NODE_NO_MASK = ~CHAR_MASK;

    private long nextNodeNo = 0;
    private Node root = new Node();
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

    /**
     * 文字列sの先頭に一致する単語をすべて見つけます。
     * @param s
     * @return
     */
    public List<V> search(String s) {
        List<V> result = new ArrayList<>();
        Node node = root;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if ((node = node.get(s.charAt(i))) == null)
                break;
            V v = node.data;
            if (v != null)
                result.add(v);
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
            nextNodeNo += INC_NODE_NO;
        }

        Node get(int key) {
            return nodes.get(no | key);
        }

        Node put(int key) {
            return nodes.computeIfAbsent(no | key, k -> new Node());
        }

        /**
         * このメソッドはtoString()で利用するためだけに実装されています。
         * 子の先頭文字をキーとして子を値とするようなマップを返します。
         */
        Map<String, Node> children() {
            return nodes.entrySet().stream()
                .filter(e -> (e.getKey() & NODE_NO_MASK) == no)
                .map(e -> Map.entry(Character.toString((int) (e.getKey() & CHAR_MASK)), e.getValue()))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
        }

        @Override
        public String toString() {
            return "" + (data == null ? "" : data) + children();
        }
    }

}