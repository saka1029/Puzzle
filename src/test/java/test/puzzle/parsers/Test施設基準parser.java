package test.puzzle.parsers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.Normalizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

class Test施設基準parser {

    static final String 数字 = "[0-9０-９]+";
    static final String 丸数字 = "①②③④⑤⑥⑦⑧⑨⑩⑪⑫⑬⑭⑮⑯⑰⑱⑲⑳";
    static final String イロハ = "イロハニホヘトチリヌルヲワカヨタレソツネ"
        + "ナラムウヰノオクヤマケフコエテアサキユメミシヱヒモセス";

    static String 半角変換(String target) {
        return Normalizer.normalize(target, Normalizer.Form.NFKD);
    }

    interface 項番 {
        Pattern pattern();
        int number(String 項番);
        default boolean isNext(String a, String b) {
            return number(a) + 1 == number(b);
        }
    }

    static class 数字 implements 項番 {
        final Pattern pat;

        数字(String pat) {
            this.pat = Pattern.compile(pat);
        }

        数字() {
            this("^" + 数字 + "$");
        }

        public Pattern pattern() {
            return pat;
        }

        public int number(String s) {
            Matcher m = pat.matcher(s);
            if (!m.find())
                return -1;
            return Integer.parseInt(m.group().replaceAll("[^0-9０-９]", ""));
        }
    }

    static class 括弧数字 extends 数字 {
        括弧数字() {
            super("^[(（]" + 数字 + "[)）]$");
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
            if (s.length() != 1)
                return -1;
            int n = all.indexOf(s);
            return n == -1 ? -1 : n + 1;
        }
    }

    static class 丸数字 extends 単一文字項番 {
        丸数字() {
            super(丸数字);
        }
    }

    static class イロハ extends 単一文字項番 {
        イロハ() {
            super(イロハ);
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
    }
    
    @Test
    void test括弧数字() {
        assertEquals(12, new 括弧数字().number("(12)"));
        assertEquals(12, new 括弧数字().number("（12）"));
        assertEquals(12, new 括弧数字().number("（１２）"));
    }
    
    @Test
    void test丸数字() {
        assertEquals(12, new 丸数字().number("⑫"));
    }
    
    @Test
    void testイロハ() {
        assertEquals(12, new イロハ().number("ヲ"));
    }

}
