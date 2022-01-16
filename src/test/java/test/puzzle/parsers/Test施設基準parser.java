package test.puzzle.parsers;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.Normalizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

class Test施設基準parser {

    static final String 数字文字列 = "[0-9０-９]+";
    static final String 漢数字文字列 = "一二三四五六七八九";
    static final String 丸数字文字列 = "①②③④⑤⑥⑦⑧⑨⑩⑪⑫⑬⑭⑮⑯⑰⑱⑲⑳";
    static final String イロハ文字列 = "イロハニホヘトチリヌルヲワカヨタレソツネ"
        + "ナラムウヰノオクヤマケフコエテアサキユメミシヱヒモセス";

    static String 半角変換(String target) {
        return Normalizer.normalize(target, Normalizer.Form.NFKD);
    }

    interface 項番 {
        Pattern pattern();

        int number(String 項番);

        default boolean isMatch(String 項番) {
            return pattern().matcher(項番).matches();
        }

        default boolean isFirst(String 項番) {
            return number(項番) == 1;
        }

        default boolean isNext(String 項番1, String 項番2) {
            return isMatch(項番1) && isMatch(項番2)
                && number(項番1) + 1 == number(項番2);
        }

        default String id(String 項番) {
            return 半角変換(項番);
        }
    }

    static class 数字 implements 項番 {
        final Pattern pat;

        数字(String pat) {
            this.pat = Pattern.compile(pat);
        }

        数字() {
            this("^" + 数字文字列 + "$");
        }

        public Pattern pattern() {
            return pat;
        }

        public int number(String s) {
            return isMatch(s)
                ? Integer.parseInt(s.replaceAll("[^0-9０-９]", "")) : -1;
        }
    }

    static class 括弧数字 extends 数字 {
        括弧数字() {
            super("^[(（]" + 数字文字列 + "[)）]$");
        }
    }

    static final String 漢数字列 = "([" + 漢数字文字列 + "]?十)?[" + 漢数字文字列 + "]";
    static final String 漢数字列の = 漢数字列 + "(の" + 漢数字列 + ")*";
    static final String 漢数字列範囲 = "(" + 漢数字列の + "(及び|から))?" + 漢数字列の + "(まで)?";

    static final Pattern 漢数字列正規表現 = Pattern.compile("(?<N>" + 漢数字列 + ")");
    static final Pattern 漢数字列の正規表現 = Pattern.compile("(?<N>" + 漢数字列の + ")");

    static class 漢数字 implements 項番 {
        final Pattern pat;

        漢数字(String pat) {
            this.pat = Pattern.compile(pat);
        }

        漢数字() {
            this(漢数字列範囲);
        }

        public Pattern pattern() {
            return pat;
        }

        public int number(String s) {
            return -1;
        }

        static int 半角数字(String s) {
            int number = 0;
            for (char c : s.toCharArray())
                if (c == '十')
                    number = number == 0 ? 1 : number;
                else {
                    int index = 漢数字文字列.indexOf(c);
                    if (index >= 0)
                        number = number * 10 + index + 1;
                    else
                        throw new IllegalArgumentException("s");
                }
            return number;
        }

        public String id(String s) {
            if (!isMatch(s))
                return null;
            Matcher m = 漢数字列の正規表現.matcher(s);
            StringBuilder sb = new StringBuilder();
            for (String sep = ""; m.find(); sep = ":") {
                sb.append(sep);
                String n = m.group("N");
                Matcher x = 漢数字列正規表現.matcher(n);
                for (String sep2 = ""; x.find(); sep2 = "-")
                    sb.append(sep2).append(半角数字(x.group()));
            }
            return sb.toString();
        }

        @Override
        public boolean isFirst(String 項番) {
            return isMatch(項番);
        }

        @Override
        public boolean isNext(String a, String b) {
            return isMatch(a) && isMatch(b);
        }
    }

    static class 単一文字項番 implements 項番 {
        final String all;
        final Pattern pat;

        単一文字項番(String all) {
            this.all = all;
            this.pat = Pattern.compile("^[" + all + "]$");
        }

        public Pattern pattern() {
            return pat;
        }

        public int number(String s) {
            if (!isMatch(s))
                return -1;
            int n = all.indexOf(s);
            return n == -1 ? -1 : n + 1;
        }

        public String id(String s) {
            return "" + number(s);
        }
    }

    static class 丸数字 extends 単一文字項番 {
        丸数字() {
            super(丸数字文字列);
        }
    }

    static class イロハ extends 単一文字項番 {
        イロハ() {
            super(イロハ文字列);
        }
    }

    @Test
    void test半角変換() {
        assertEquals("abc", 半角変換("ａｂｃ"));
        assertEquals("123", 半角変換("１２３"));
        assertEquals("(12)", 半角変換("（１２）"));
    }

    @Test
    void test数字() {
        assertEquals(12, new 数字().number("12"));
        assertEquals(12, new 数字().number("１２"));
        assertTrue(new 数字().isNext("3", "4"));
    }

    @Test
    void test括弧数字() {
        assertEquals(12, new 括弧数字().number("(12)"));
        assertEquals(12, new 括弧数字().number("（12）"));
        assertEquals(12, new 括弧数字().number("（１２）"));
        assertTrue(new 括弧数字().isNext("（１２）", "(13)"));
    }

    @Test
    void test丸数字() {
        assertEquals(12, new 丸数字().number("⑫"));
        assertTrue(new 丸数字().isNext("⑫", "⑬"));
    }

    @Test
    void testイロハ() {
        assertEquals(12, new イロハ().number("ヲ"));
        assertEquals("12", new イロハ().id("ヲ"));
        assertTrue(new イロハ().isNext("ヲ", "ワ"));
    }

    @Test
    void test漢数字() {
        assertTrue(new 漢数字().pattern().matcher("三").matches());
        assertTrue(new 漢数字().pattern().matcher("十二").matches());
        assertTrue(new 漢数字().pattern().matcher("三十三から三十三の五まで").matches());
        assertTrue(new 漢数字().pattern().matcher("三十四及び三十五").matches());
        assertEquals("35-5-2", new 漢数字().id("三十五の五の二"));
        assertEquals("34:35-5-2", new 漢数字().id("三十四及び三十五の五の二まで"));
    }

}
