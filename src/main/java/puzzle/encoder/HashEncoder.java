package puzzle.encoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class HashEncoder<V> implements Encoder<V>{

    final Map<String, V> map = new HashMap<>();

    @Override
    public void put(String key, V value) {
        map.put(key, value);
    }

    @Override
    public List<List<Entry<V>>> encode(String text, Predicate<List<Entry<V>>> filter) {
        int[] textcp = text.codePoints().toArray();
        int length = textcp.length;
        List<List<Entry<V>>> result = new ArrayList<>();
        List<Entry<V>> sequence = new ArrayList<>();
        new Object() {
            void search(int start) {
                if (start >= length) {
                    if (filter.test(sequence))
                        result.add(new ArrayList<>(sequence));
                } else {
                    for (int i = start + 1; i <= length; ++i) {
                        String key = new String(textcp, start, i - start);
                        V value = map.get(key);
                        if (value == null)
                            continue;
                        sequence.addLast(new Entry<>(key, value));
                        search(i);
                        sequence.removeLast();
                    }
                }
            }
        }.search(0);
        return result;
    }

}
