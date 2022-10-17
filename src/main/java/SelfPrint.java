public class SelfPrint {
    public static void main(String[] args) {
        System.out.print(getMyText());
    }
    private static String[] programText = {
"public class SelfPrint {",
"    public static void main(String[] args) {",
"        System.out.print(getMyText());",
"    }",
"    private static String[] programText = {",
"    };",
"    private static String getMyText() {",
"        char q = 34, c = 44;",
"        String n = System.lineSeparator();",
"        StringBuilder sb = new StringBuilder();",
"        for (int i = 0; i < 5; i++)",
"            sb.append(programText[i]).append(n);",
"        for (int i = 0; i < programText.length; i++)",
"            sb.append(q + programText[i] + q + c).append(n);",
"        for (int i = 5; i < programText.length; i++)",
"            sb.append(programText[i]).append(n);",
"        return sb.toString();",
"    }",
"}",
    };
    private static String getMyText() {
        char q = 34, c = 44;
        String n = System.lineSeparator();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++)
            sb.append(programText[i]).append(n);
        for (int i = 0; i < programText.length; i++)
            sb.append(q + programText[i] + q + c).append(n);
        for (int i = 5; i < programText.length; i++)
            sb.append(programText[i]).append(n);
        return sb.toString();
    }
}
