package puzzle;

public class CodePoints {

    private CodePoints() {}

    public static int length(String s) {
        return s.codePointCount(0, s.length());
    }

    public static String substring(String s, int start, int end) {
        int from = s.offsetByCodePoints(0, start);
        int to = s.offsetByCodePoints(from, end - start);
        return s.substring(from, to);
    }

    public static String substring(String s, int start) {
        return substring(s, start, length(s));
    }

    public static int[] codePoints(String s) {
        return s.codePoints().toArray();
    }

    public static String[] strings(String s) {
        return s.split("(?<=.)");
    }

    public static Iterable<Integer> iterable(String s) {
        return () -> s.codePoints().iterator();
    }

}
