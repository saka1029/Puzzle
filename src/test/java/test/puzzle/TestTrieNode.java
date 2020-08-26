package test.puzzle;

import static org.junit.Assert.assertEquals;
import static puzzle.Trie.characters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.junit.Test;

import puzzle.Trie;

public class TestTrieNode {

    static final Logger logger = Logger.getLogger(TestTrieNode.class.getName());

    @Test
    public void testPutGet() {
        Trie<Character, Integer> trie = new Trie<>();
        String[] words = {"abc", "abd", "cbd", "abcd", "c"};
        int i = 0;
        for (String word : words)
            trie.put(characters(word), i++);
        logger.info(trie.toString());
        /*
         * (null : a=(null : b=(null : c=(0 : d=(3 : )), d=(1 : ))), c=(4 :
         * b=(null : d=(2 : ))))
         */
        i = 0;
        for (String word : words)
            assertEquals(i++, (int) trie.get(characters(word)));
        assertEquals(null, trie.get(characters("NOT FOUND")));
        List<Integer> found = trie.search(characters("abcdefghji"));
        assertEquals(List.of(0, 3), found);
    }

    static class TrieInt<V> {

        static final long INC = 1L << 32;
        long nextNodeNo = 0;
        Node root = new Node();
        private final Map<Long, Node> nodes = new HashMap<>();

        static Iterable<Integer> codePoints(String s) {
            return () -> s.codePoints().iterator();
        }
        
        void put(String s, V data) {
            Node node = root;
            for (int c : codePoints(s))
                node = node.put(c);
            node.data = data;
        }

        V get(String s) {
            Node node = root;
            for (int c : codePoints(s))
                if ((node = node.get(c)) == null)
                    return null;
            return node.data;
        }

        public List<V> search(String sequence) {
            List<V> result = new ArrayList<>();
            Node node = root;
            for (int k : codePoints(sequence)) {
                V v = node.data;
                if (v != null)
                    result.add(v);
                if ((node = node.get(k)) == null)
                    break;
            }
            return result;
        }

        class Node {
            final long no;
            V data;

            Node() { this.no = nextNodeNo; nextNodeNo += INC; }
            long makeKey(int key) { return no | key; }
            Node get(int key) { return nodes.get(no | key); }
            Node put(int key) { return nodes.computeIfAbsent(no | key, k -> new Node()); }
        }

    }
    
    @Test
    public void testTrieInt() {
        String[] words = {"A", "to", "tea", "ted", "ten", "i", "in", "inn"};
        TrieInt<String> trie = new TrieInt<>();
        for (String s : words)
            trie.put(s, s);
        for (String s : words)
            assertEquals(s, trie.get(s));
        assertEquals(null, trie.get("NO DATA"));
        assertEquals(List.of("i", "in", "inn"), trie.search("innto"));
        assertEquals(10, trie.nodes.size());
    }

}
