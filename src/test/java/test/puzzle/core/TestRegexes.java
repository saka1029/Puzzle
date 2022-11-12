package test.puzzle.core;

import static org.junit.Assert.*;
import static puzzle.core.Regexes.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class TestRegexes {

    @Test
    public void testMatcherResults() {
        Pattern pat = Pattern.compile("\\d+");
        String source = "abc123r4g582s";
        Matcher matcher = pat.matcher(source);
        matcher.results()
            .forEach(m -> System.out.println(m.start() + ", " + m.end() + ", " + m.group() + ", "));
    }

    @Test
    public void testReplaceString() {
        String hex = replaceAll("abc123r4g511s", "\\d+",
            m -> "(" + Integer.toHexString(Integer.parseInt(m.group())) + ")");
        assertEquals("abc(7b)r(4)g(1ff)s", hex);
    }

    @Test
    public void testReplacePattern() {
        String source = "abc123r4g511s";
        Pattern pat = Pattern.compile("\\d+");
        String hex = replaceAll(source, pat,
            m -> "(" + Integer.toHexString(Integer.parseInt(m.group())) + ")");
        assertEquals("abc(7b)r(4)g(1ff)s", hex);
    }

    /**
     * java - Append using Regex Replace with repeating pattern - Stack Overflow
     * https://stackoverflow.com/questions/67224816/append-using-regex-replace-with-repeating-pattern/67226870#67226870
     */
    @Test
    public void testStackoverflow() {
        String content = "{ \"values\" : [\"AnyValue1\", \"TestValue\", \"Dummy\", \"SomeValue\"], \"key\" : \"value\" }";
        Pattern bracket = Pattern.compile("\\[.*?\\]");
        Pattern string = Pattern.compile("\"(.*?)\"");
        String result = replaceAll(content, bracket,
            m -> replaceAll(m.group(), string,
                n -> "\"" + n.group(1) + "_val\""));
        String expected = "{ \"values\" : [\"AnyValue1_val\", \"TestValue_val\", \"Dummy_val\", \"SomeValue_val\"], \"key\" : \"value\" }";
        assertEquals(expected, result);
    }
    
    static int dayInMonth(int month, boolean isLeap) {
        return switch (month) {
        case 2 -> isLeap ? 29 : 28;
        case 1, 3, 5, 7, 8, 10, 12 -> 31;
        case 4, 6, 9, 11 -> 30;
        default -> -1;
        };
    }

    @Test
    public void testMONTH_DAY() {
        String sep = "/";
        Pattern PAT = Pattern.compile(MONTH_DAY.formatted(sep));
        for (int m = 1; m <= 12; ++m)
            for (int d = 1, max = dayInMonth(m, false); d < max; ++d) {
                String md = "%02d%s%02d".formatted(m, sep, d);
                assertTrue(md + " fail", PAT.matcher(md).matches());
            }
        assertFalse(PAT.matcher("00/01").matches());
        assertFalse(PAT.matcher("01/32").matches());
        assertFalse(PAT.matcher("02/29").matches());
        assertFalse(PAT.matcher("02/30").matches());
        assertFalse(PAT.matcher("02/31").matches());
        assertFalse(PAT.matcher("10/00").matches());
        assertFalse(PAT.matcher("13/01").matches());
    }

    @Test
    public void testDAY_MONTH() {
        String sep = "/";
        Pattern PAT = Pattern.compile(DAY_MONTH.formatted(sep));
        for (int m = 1; m <= 12; ++m)
            for (int d = 1, max = dayInMonth(m, false); d < max; ++d) {
                String md = "%02d%s%02d".formatted(d, sep, m);
                assertTrue(md + " fail", PAT.matcher(md).matches());
            }
        assertFalse(PAT.matcher("01/00").matches());
        assertFalse(PAT.matcher("32/01").matches());
        assertFalse(PAT.matcher("29/02").matches());
        assertFalse(PAT.matcher("30/02").matches());
        assertFalse(PAT.matcher("31/02").matches());
        assertFalse(PAT.matcher("00/10").matches());
        assertFalse(PAT.matcher("01/13").matches());
    }

    @Test
    public void testMONTH_DAY_Leap() {
        String sep = "/";
        Pattern PAT = Pattern.compile(MONTH_DAY.formatted(sep) + "|02" + Pattern.quote(sep) + "29");
        for (int m = 1; m <= 12; ++m)
            for (int d = 1, max = dayInMonth(m, true); d < max; ++d) {
                String md = "%02d%s%02d".formatted(m, sep, d);
                assertTrue(md + " fail", PAT.matcher(md).matches());
            }
        assertFalse(PAT.matcher("00/01").matches());
        assertFalse(PAT.matcher("01/32").matches());
        assertTrue(PAT.matcher("02/29").matches());
        assertFalse(PAT.matcher("02/30").matches());
        assertFalse(PAT.matcher("02/31").matches());
        assertFalse(PAT.matcher("10/00").matches());
        assertFalse(PAT.matcher("13/01").matches());
    }
    
    @Test
    public void testLeapYears() {
        Pattern pat = Pattern.compile(LEAP_YEARS);
        assertTrue(pat.matcher("2000").matches());
        assertTrue(pat.matcher("1904").matches());
        for (int i = 0; i <= 9999; ++i)
            assertEquals(isLeapYear(i), pat.matcher("%04d".formatted(i)).matches());
    }
    
    @Test
    public void testYearMonthDay() {
        String sep = "/";
        Pattern pat = Pattern.compile(YEAR_MONTH_DAY.formatted(sep));
        assertFalse(pat.matcher("1900/02/29").matches());
        assertTrue(pat.matcher("2000/02/29").matches());
        assertTrue(pat.matcher("2004/02/29").matches());
        assertFalse(pat.matcher("2100/02/29").matches());
    }
    
    @Test
    public void testDayMonthYear() {
        String sep = "/";
        Pattern pat = Pattern.compile(DAY_MONTH_YEAR.formatted(sep));
        assertFalse(pat.matcher("29/02/1900").matches());
        assertTrue(pat.matcher("29/02/2000").matches());
        assertTrue(pat.matcher("29/02/2004").matches());
        assertFalse(pat.matcher("29/02/2100").matches());
    }
    
    static final DateTimeFormatter formatter = DateTimeFormatter
        .ofPattern("dd.MM.uuuu", Locale.ENGLISH)
        .withResolverStyle(ResolverStyle.STRICT);

    static boolean isValidDate(String str) {
        try {
            LocalDate.parse(str, formatter);
            return true;
        }
        catch (DateTimeParseException e) {
            return false;
        }
    }

    static final SimpleDateFormat SDF = new SimpleDateFormat("dd.MM.yyyy");

    /**
     * こいつは00.00.1900をOKとする馬鹿者。
     */
    static boolean isValidDate0(String str) {
        try {
            SDF.parse(str);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * 遅い！！！
     * 正規表現がよくないせいもあるが、DateTimeFormatterの例外処理の方が問題である。
     */
    @Test
    public void testDayMonthYearStackoferflow() {
        String sep = ".";
        Pattern pat = Pattern.compile(DAY_MONTH_YEAR.formatted(sep));
        for (int y = 1900; y <= 3000; ++y)
            for (int m = 0; m <= 13; ++m)
                for (int d = 0; d <= 31; ++d) {
                    String dt = "%02d%s%02d%s%04d".formatted(d, sep, m, sep, y);
                    pat.matcher(dt).matches();    // 1.4秒
//                    isValidDate(dt);                // 6.7秒
//                    assertEquals("fail " + dt, isValidDate(dt), pat.matcher(dt).matches()); // 8.1秒
                }
    }
}
