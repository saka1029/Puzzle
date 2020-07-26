package test.puzzle;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TestExcelColumnName {

    public static int excelColumnNumber(String excelColumn) {
        int result = 0;
        for (int i = 0, size = excelColumn.length(); i < size; ++i)
            result = result * 26 + excelColumn.charAt(i) - 'A' + 1;
        return result;
    }

    public static int excelColumnNumberByReduce(String excelColumn) {
        return excelColumn.chars().reduce(0, (a, b) -> a * 26 + b - 'A' + 1);
    }

    public static String excelColumnName(int d) {
        StringBuilder sb = new StringBuilder();
        for (; d > 0; d = (d - 1) / 26)
            sb.append((char) ((d - 1) % 26 + 'A'));
        return sb.reverse().toString();
    }

    @Test
    void test() {
        assertEquals("A", excelColumnName(1));
        assertEquals("AA", excelColumnName(26 + 1));
        assertEquals("AAA", excelColumnName(26 * 26 + 26 + 1));
        assertEquals("AAAA", excelColumnName(26 * 26 * 26 + 26 * 26 + 26 + 1));
        for (int i = 1; i <= 1000; ++i)
            assertEquals(i, excelColumnNumber(excelColumnName(i)));
        for (int a = 'A', i = 1; a <= 'Z'; ++a, ++i)
            assertEquals(i, excelColumnNumber("" + (char)a));
        for (int a = 'A', i = 27; a <= 'Z'; ++a)
            for (int b = 'A'; b <= 'Z'; ++b, ++i)
                assertEquals(i, excelColumnNumber("" + (char)a + (char)b));
    }

}
