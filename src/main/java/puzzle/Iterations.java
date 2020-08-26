package puzzle;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Iterations {

    private Iterations() {}

    public static Iterable<Integer> iterable(String s) {
        return () -> s.codePoints().iterator();
    }

    public static Iterable<Integer> codePoints(String s) {
        return () -> s.codePoints().iterator();
    }

    public static Stream<Character> streamChar(String s) {
        return s.chars().mapToObj(i -> (char)i);
    }

    public static Iterable<Character> characters(String s) {
        return () -> streamChar(s).iterator();
    }

    public static Iterable<Long> iterable(long[] array) {
        return () -> Arrays.stream(array).iterator();
    }

    public static Iterable<Integer> iterable(int[] array) {
        return () -> Arrays.stream(array).iterator();
    }

    public static List<Integer> list(int[] array) {
        return Arrays.stream(array).boxed().collect(Collectors.toList());
    }

    public static List<Long> list(long[] array) {
        return Arrays.stream(array).boxed().collect(Collectors.toList());
    }

}
