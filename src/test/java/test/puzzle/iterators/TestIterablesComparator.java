package test.puzzle.iterators;

import static java.util.Comparator.*;
import static java.util.stream.Collectors.*;
import static org.junit.jupiter.api.Assertions.*;
import static puzzle.Iterables.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

class TestIterablesComparator {

    @SuppressWarnings("preview")
    static record Foo(int i, String s) {}

    static List<Foo> foos = List.of(
        new Foo(2, "c"),
        new Foo(1, "b"),
        new Foo(3, "c"),
        new Foo(2, "b"),
        new Foo(3, "b"),
        new Foo(2, "a"),
        new Foo(1, "c"),
        new Foo(3, "a"),
        new Foo(1, "a"));

    @Test
    void testAscDesc() {
        List<Foo> expected0 = List.of(
            new Foo(1, "a"),
            new Foo(1, "b"),
            new Foo(1, "c"),
            new Foo(2, "a"),
            new Foo(2, "b"),
            new Foo(2, "c"),
            new Foo(3, "a"),
            new Foo(3, "b"),
            new Foo(3, "c"));
        assertEquals(expected0,
            list(sorted(and(asc(Foo::i), asc(Foo::s)), foos)));
        assertEquals(expected0,
            foos.stream()
                .sorted(comparing(Foo::i).thenComparing(Foo::s))
                .collect(toList()));
        List<Foo> expected1 = List.of(
            new Foo(1, "c"),
            new Foo(1, "b"),
            new Foo(1, "a"),
            new Foo(2, "c"),
            new Foo(2, "b"),
            new Foo(2, "a"),
            new Foo(3, "c"),
            new Foo(3, "b"),
            new Foo(3, "a"));
        assertEquals(expected1,
            list(sorted(and(asc(Foo::i), desc(Foo::s)), foos)));
        assertEquals(expected1,
            foos.stream()
                .sorted(comparing(Foo::i).thenComparing(comparing(Foo::s).reversed()))
                .collect(toList()));
    }

    @Test
    void testReverse() {
        List<Foo> expected1 = List.of(
            new Foo(1, "c"),
            new Foo(1, "b"),
            new Foo(1, "a"),
            new Foo(2, "c"),
            new Foo(2, "b"),
            new Foo(2, "a"),
            new Foo(3, "c"),
            new Foo(3, "b"),
            new Foo(3, "a"));
        assertEquals(expected1,
            list(sorted(and(asc(Foo::i), reverse(asc(Foo::s))), foos)));
    }

    @Test
    void testSortNumberString() {
        List<String> numbers = list(map(i -> "No." + i, range(1, 100)));
        List<String> random = list(numbers);
        Collections.shuffle(random);
        assertEquals(numbers,
            list(sorted(and(asc(String::length), asc(identity())), random)));
        assertEquals(numbers,
            random.stream()
                .sorted(Comparator.comparing(String::length).thenComparing(Function.identity()))
                .collect(Collectors.toList()));
    }

}
