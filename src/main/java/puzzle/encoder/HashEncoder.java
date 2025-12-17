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

    // function encode(text) {
    //     const length = text.length;
    //     const result = [];
    //     const sequence = [];

    //     function search(start) {
    //         if (start >= length) {
    //             if (sequence.filter(e => e.data.length == 7).length == 1)
    //                 result.push(sequence.slice());
    //         } else
    //             for (let i = start + 1; i <= length; ++i) {
    //                 const key = text.substring(start, i);
    //                 const data = BYOMEI[key];
    //                 // console.log(`start=${start}, i=${i}, key=${key}, data=${data}`)
    //                 if (data === undefined)
    //                     continue;
    //                 sequence.push({key: key, data: data});
    //                 search(i);
    //                 sequence.pop();
    //             }
    //     }
    //     search(0);
    //     return result;
    // }

    @Override
    public List<List<Entry<V>>> encode(String text, Predicate<List<Entry<V>>> filter) {
        int length = text.length();
        List<List<Entry<V>>> result = new ArrayList<>();
        List<Entry<V>> sequence = new ArrayList<>();
        new Object() {
            void search(int start) {
                if (start >= length) {
                    if (filter.test(sequence))
                        result.add(new ArrayList<>(sequence));
                } else {
                    for (int i = start + 1; i <= length; ++i) {
                        String key = text.substring(start, i);
                        V value = map.get(key);
                        if (value == null)
                            continue;
                        sequence.addLast(Entry.of(key, value));
                        search(i);
                        sequence.removeLast();
                    }
                }
            }
        }.search(0);
        return result;
    }

}
