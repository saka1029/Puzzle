package experiment;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

public class TestMultiKeyMap {

public static <A, B, C, D> D get(Map<A, Map<B, Map<C, D>>> map, A a, B b, C c) {
    return Optional.ofNullable(map.get(a))
        .map(m -> m.get(b))
        .map(m -> m.get(c))
        .orElse(null);
}

public static <A, B, C, D> void put(Map<A, Map<B, Map<C, D>>> map, A a, B b, C c, D d) {
    map.computeIfAbsent(a, t -> new HashMap<>())
        .computeIfAbsent(b, t -> new HashMap<>())
        .put(c, d);
}

@Test
public void testMultiKeyMap() {
    Map<Integer, Map<Integer, Map<Integer, Integer>>> map = new HashMap<>();
    for (int a = 0, value = 0; a < 2; ++a)
        for (int b = 0; b < 2; ++b)
            for (int c = 0; c < 2; ++c)
                put(map, a, b, c, value++);
    System.out.println(map);
    Map<Integer, Map<Integer, Map<Integer, Integer>>> expected =
        Map.of(0, Map.of(0, Map.of(0, 0,
                                   1, 1),
                         1, Map.of(0, 2,
                                   1, 3)),
               1, Map.of(0, Map.of(0, 4,
                                   1, 5),
                         1, Map.of(0, 6,
                                   1, 7)));
    assertEquals(expected, map);
    for (int a = 0, value = 0; a < 2; ++a)
        for (int b = 0; b < 2; ++b)
            for (int c = 0; c < 2; ++c)
                assertEquals(value++, get(map, a, b, c));
    assertEquals(null, get(map, 2, 2, 2));
}

}
