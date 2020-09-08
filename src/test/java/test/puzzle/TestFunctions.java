package test.puzzle;

import static org.junit.jupiter.api.Assertions.*;
import static puzzle.Functions.*;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

class TestFunctions {

    @Test
    void testAnd() {
        List<String> sorted = List.of("3", "2", "1", "11", "20", "13").stream()
            .sorted(and(comparing​(String::length), comparing​(s -> s)))
            .collect(Collectors.toList());
        assertEquals(List.of("1", "2", "3", "11", "13", "20"), sorted);
    }

    @Test
    void testCompose() {
        Function<Integer, Integer> square = x -> x * x;
        Function<Integer, Integer> doubles = x -> x + x;
        assertEquals(50, compose(square, doubles).apply(5));
        assertEquals(100, compose(doubles, square).apply(5));
        Function<String, Integer> length = x -> x.length();
        assertEquals(25, compose(length, square).apply("abcde"));
    }

}
