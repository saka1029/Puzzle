package test.puzzle.parsers;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

class Test施設基準parser {

    static final String S空白 = "[ \t　]*";
    static final String S表題 = "(?<T>.*)";
    static final String S数字 = "(?<N>[0-9０-９]+)";
    static final String Sイロハ = "(?<N>イロハニホヘトチリヌルヲワカヨタレソツネナラムウヰノオクヤマケフコエテアサキユメミシヱヒモセス)";
    static final String S漢数字 = "一二三四五六七八九";
    static final String S漢数字OR = "[" + S漢数字 + "]";
    static final String S漢番号 = "(" + S漢数字OR + "?十)?" + S漢数字OR;
    static final String S番号 = "(?<N>" + S漢番号 + "(の" + S漢番号 + ")*)";
    static final Pattern 漢番号 = Pattern.compile(S漢番号);
    static final Pattern 番号 = Pattern.compile("^" + S空白 + S番号 + S空白 + S表題);
    static final Pattern 章番号 = Pattern.compile("^" + S空白 + S番号 + "章" + S空白 + S表題);

    interface 項番 {
        Pattern pattern();
    }
    static String[] header(Pattern pat, String input) {
        Matcher m = pat.matcher(input);
        return m.find() ? new String[] {m.group("N"), m.group("T")} : new String[2];
    }

    static String 漢番号(String input) {
        int number = 0;
        for (char c : input.toCharArray())
            if (c == '十')
                number = number == 0 ? 1 : number;
            else {
                int index = S漢数字.indexOf(c);
                if (index >= 0)
                    number = number * 10 + index + 1;
                else
                    throw new IllegalArgumentException("input");
            }
        return "" + number;
    }

    static String number(String input) {
        StringBuilder sb = new StringBuilder();
        Matcher m = 漢番号.matcher(input);
        while (m.find()) {
            sb.append(".");
            sb.append(漢番号(m.group()));
        }
        return sb.substring(1);
    }

    @Test
    void testHeader() {
        assertArrayEquals(new String[] {"一の二の三", "タイトル"}, header(番号, "  一の二の三     タイトル"));
        assertArrayEquals(new String[] {"二十三", "タイトル たいとる"}, header(番号, "  二十三     タイトル たいとる"));
        assertArrayEquals(new String[] {"二", "タイトル"}, header(章番号, "  二章　タイトル"));
        assertArrayEquals(new String[] {"十一の二", "タイトル"}, header(章番号, "  十一の二章　タイトル"));
    }

    @Test
    void testNumber() {
        assertEquals("1.2.3", number("一の二の三"));
        assertEquals("23", number("二十三"));
        assertEquals("2", number("二"));
        assertEquals("11.2", number("十一の二"));
        assertEquals("99.99.99", number("九十九の九十九の九十九"));
    }

}
