package puzzle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * 型Kの列を格納するTrie木の実装です。
 * 登録、検索する列はIterable<K>で指定します。
 * 文字列を格納する場合はTrie.characters(string)または
 * Trie.codePoints(string)を指定します。
 * 型Kは前者の場合Characterで、後者の場合はInteger(コードポイント)となります。
 * 
 * @param <K> 列の要素の型を指定します。
 * @param <V> 列に対応して格納する値の型を指定します。
 */
public class Trie<K, V> {

    private final Node<K, V> root = new Node<>();

    private int size = 0;
    public int size() { return size; }

    private static class Node<K, V> {
        V data;
        final Map<K, Node<K, V>> children = new HashMap<>();

        @Override
        public String toString() {
            String c = children.toString();
            return "(" + data + " : " + c.substring(1, c.length() - 1) + ")";
        }
    }

    public V get(Iterable<K> key) {
        Node<K, V> node = root;
        for (K k : key)
            if ((node = node.children.get(k)) == null)
                return null;
        return node.data;
    }
    
    public List<V> search(Iterable<K> sequence) {
        List<V> result = new ArrayList<>();
        Node<K, V> node = root;
        for (K k : sequence) {
            V v = node.data;
            if (v != null)
                result.add(v);
            if ((node = node.children.get(k)) == null)
                break;
        }
        return result;
    }

    public void put(Iterable<K> key, V data) {
        Node<K, V> node = root;
        for (K k : key)
            node = node.children.computeIfAbsent(k, dummy -> new Node<>());
        node.data = data;
        ++size;
    }
 
    @Override
    public String toString() {
        return "Trie" + root;
    }
    
    // support methods
    public static Iterable<Integer> iterable(int[] array) {
        return () -> Arrays.stream(array).iterator();
    }
  
    public static Iterable<Long> iterable(long[] array) {
        return () -> Arrays.stream(array).iterator();
    }
  
    public static Iterable<Character> characters(String s) {
        return () -> s.chars().mapToObj(i -> (char)i).iterator();
    }

    public static Iterable<Character> characters(String s, int start, int end) {
        return () -> IntStream.range(start, end).mapToObj(i -> s.charAt(i)).iterator();
    }

    public static Iterable<Character> characters(String s, int start) {
        return characters(s, start, s.length());
    }
    
    public static Iterable<Integer> iterable(String s) {
        return () -> s.codePoints().iterator();
    }
    
    public static int[] ints(String s) {
        return s.codePoints().toArray();
    }
}
