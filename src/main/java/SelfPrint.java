import java.util.Arrays;
public class SelfPrint {
    public static void main(String[] args) {
        char q = 34, c = 44;
        Arrays.stream(T).limit(9).forEach(System.out::println);
        Arrays.stream(T).map(s -> q + s + q + c).forEach(System.out::println);
        Arrays.stream(T).skip(9).forEach(System.out::println);
    }
    private static String[] T = {
"import java.util.Arrays;",
"public class SelfPrint {",
"    public static void main(String[] args) {",
"        char q = 34, c = 44;",
"        Arrays.stream(T).limit(9).forEach(System.out::println);",
"        Arrays.stream(T).map(s -> q + s + q + c).forEach(System.out::println);",
"        Arrays.stream(T).skip(9).forEach(System.out::println);",
"    }",
"    private static String[] T = {",
"    };",
"}",
    };
}
