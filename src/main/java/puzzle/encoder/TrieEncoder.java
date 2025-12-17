package puzzle.encoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class TrieEncoder<V> implements Encoder<V> {

    static final long INC_NODE_NO = 1L << Integer.SIZE;

    class Node {
        final long no;
        V value;

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
    }

    private long nextNodeNo = 0;
    private Node root = new Node();
    private final Map<Long, Node> nodes = new HashMap<>();

    @Override
    public void put(String key, V value) {
        Node node = root;
        int[] keycp = key.codePoints().toArray();
        int length = keycp.length;
        for (int i = 0; i < length; ++i)
            node = node.put(keycp[i]);
        node.value = value;
    }

    class Found {
        final int end;
        final V value;
        Found(int end, V value) {
            this.end = end;
            this.value = value;
        }
    }

    @Override
    public List<List<Entry<V>>> encode(String text, Predicate<List<Entry<V>>> filter) {
        int[] textcp = text.codePoints().toArray();
        int length = textcp.length;
        List<List<Entry<V>>> result = new ArrayList<>();
        List<Entry<V>> sequence = new ArrayList<>();
        new Object() {

            List<Found> find(int start) {
                Node node = root;
                List<Found> found = new ArrayList<>();
                for (int i = start; i < length; ++i) {
                    if ((node = node.get(textcp[i])) == null)
                        break;
                    V value = node.value;
                    if (value != null)
                        found.add(new Found(i, value));
                }
                return found;
            }

            void search(int index) {
                if (index >= length) {
                    if (filter.test(sequence))
                        result.add(new ArrayList<>(sequence));
                } else {
                    for (Found f : find(index)) {
                        sequence.addLast(Entry.of(new String(textcp, index, f.end - index), f.value));
                        search(f.end);
                        sequence.removeLast();
                    }
                }
            }
        }.search(0);
        return result;
    }

}