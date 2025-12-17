package puzzle.encoder;

import java.util.List;
import java.util.function.Predicate;

public interface Encoder<V> { 

    public record Entry<V>(String key, V value) {}
    // public static class Entry<V> {
    //     public final String key;
    //     public final V value;

    //     Entry(String key, V value) {
    //         this.key = key;
    //         this.value = value;
    //     }

    //     public static <V> Entry<V> of(String key, V value) {
    //         return new Entry<V>(key, value);
    //     }

    //     @Override
    //     public String toString() {
    //         return "Entry(%s, %s)".formatted(key, value);
    //     }
    // }

    void put(String key, V value);

    List<List<Entry<V>>> encode(String text, Predicate<List<Entry<V>>> filter);

    default List<List<Entry<V>>> encode(String text) {
        return encode(text, e -> true);
    }
}

