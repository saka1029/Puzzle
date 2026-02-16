package puzzle.core;

import java.util.HashMap;
import java.util.Map;

public class Triex<K, V> {
    /**
     * NodeKeyは<code>Node node</code>と<code>K key</code>の複合キー
     * です。<code>hashCode()</code>と<code>equals()<code>
     * は<code>node</code>と<code>key</code>に基づいて正しく実装されている
     * 必要があります。
     * @param <K>
     * @param <V>
     */
    record NodeKey<K, V>(Node<K, V> node, K key) {}

    /**
     * <code>nodes</code>は親ノードから子ノードへのマップです。
     * 親ノードごとにマップを保持するとメモリ効率が悪いので、
     * これを集約したマップです。
     */
    final Map<NodeKey<K, V>, Node<K, V>> nodes = new HashMap<>();
    final Node<K, V> root = new Node<>();

    Node<K, V> add(Node<K, V> node, K key) {
        return nodes.computeIfAbsent(new NodeKey<>(node, key), k -> new Node<>());
    }

    @SuppressWarnings("unchecked")
    public void put(V data, K... keys) {
        Node<K, V> node = root;
        for (K key : keys)
            node = add(node, key);
        node.data = data;
    }
    
    Node<K, V> get(Node<K, V> node, K key) {
        return nodes.get(new NodeKey<>(node, key));
    }
    
    @SuppressWarnings("unchecked")
    public V get(K... keys) {
        Node<K, V> node = root;
        for (K key : keys)
//            if ((node = node.get(key)) == null)
            if ((node = get(node, key)) == null)
                return null;
        return node.data;
    }

    @Override
    public String toString() {
        return nodes.toString();
    }

    /**
     * Nodeは単に<code>V data</code>のラッパーです。
     * <code>Object</code>の<code>hashCode()</code>と<code>equals()</code>
     * の実装をそのまま使いたかったのであえてラップします。
     * そのため<code>hashCode()</code>と<code>equals()</code>
     * をオーバーライドしてはいけません。
     * @param <K>
     * @param <V>
     */
    static class Node<K, V> {
        public V data;

        @Override
        public String toString() {
            return data == null ? "null" : data.toString();
        }
    }

}
