package puzzle.encoder;

public class EncoderEntry<V> {
    public final String key;
    public final V value;

    EncoderEntry(String key, V value) {
        this.key = key;
        this.value = value;
    }

    public static <V> EncoderEntry<V> of(String key, V value) {
        return new EncoderEntry<V>(key, value);
    }

    @Override
    public String toString() {
        return "EncoderEntry(%s, %s)".formatted(key, value);
    }
}
 