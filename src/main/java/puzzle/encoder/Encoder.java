package puzzle.encoder;

import java.util.List;
import java.util.function.Predicate;

public interface Encoder<V> { 

    List<List<EncoderEntry<V>>> encode(String s, Predicate<List<EncoderEntry<V>>> filter);

    default List<List<EncoderEntry<V>>> encode(String s) {
        return encode(s, e -> true);
    }
}

