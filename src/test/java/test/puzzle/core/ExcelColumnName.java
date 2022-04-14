package test.puzzle.core;

import static org.junit.Assert.*;

import org.junit.Test;

public class ExcelColumnName {

    static String excelColumnName(int columnNumber) {
        StringBuilder sb = new StringBuilder();
        for ( ; columnNumber > 0; columnNumber /= 26)
            sb.append((char)('A' + --columnNumber % 26));
        return sb.reverse().toString();
    }

    @Test
    public void testExcelColumnName() {
        String[] columnNames = {
            "",
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
            "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
            "AA", "AB", "AC", "AD", "AE", "AF", "AG", "AH", "AI", "AJ", "AK", "AL", "AM",
            "AN", "AO", "AP", "AQ", "AR", "AS", "AT", "AU", "AV", "AW", "AX", "AY", "AZ",
        };
        for (int i = 1; i <= 52; ++i)
            assertEquals(columnNames[i], excelColumnName(i));
    }

}
