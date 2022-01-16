package test.puzzle.parsers;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

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
    
    static class 第漢数字 extends 漢数字 {
        第漢数字(String s) {
            super(s);
        }

        第漢数字() {
            this("第" + 漢数字列の);
        }
        
        @Override
        public boolean isFirst(String 項番) {
            return id(項番).equals("1");
        }
        
        static final Pattern 最後の数字 = Pattern.compile("\\d+$");

        String nextId(String id) {
            return 最後の数字.matcher(id).replaceAll(m -> "" + (Integer.parseInt(m.group()) + 1));
        }
        
        String nextId2(String id) {
            return id + "-2";
        }
        
        @Override
        public boolean isNext(String 項番1, String 項番2) {
            if (!isMatch(項番1) || !isMatch(項番2))
                return false;
            String id1 = id(項番1), id2 = id(項番2);
            return id2.equals(nextId(id1)) || id2.equals(nextId2(id1));
        }
    }
    
    static class 漢数字章 extends 第漢数字 {
        漢数字章() {
            super(漢数字列の + "章");
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
        数字 x = new 数字();
        assertEquals(12, x.number("12"));
        assertEquals(12, x.number("１２"));
        assertTrue(x.isNext("3", "4"));
    }

    @Test
    void test括弧数字() {
        括弧数字 x = new 括弧数字();
        assertEquals(12, x.number("(12)"));
        assertEquals(12, x.number("（12）"));
        assertEquals(12, x.number("（１２）"));
        assertTrue(x.isFirst("（１）"));
        assertFalse(x.isFirst("（１2）"));
        assertTrue(x.isNext("（１２）", "(13)"));
        assertEquals("(12)", x.id("（１2）"));
    }

    @Test
    void test丸数字() {
        丸数字 x = new 丸数字();
        assertEquals(12, x.number("⑫"));
        assertTrue(x.isFirst("①"));
        assertFalse(x.isFirst("②"));
        assertTrue(x.isNext("⑫", "⑬"));
    }

    @Test
    void testイロハ() {
        イロハ x = new イロハ();
        assertEquals(12, x.number("ヲ"));
        assertEquals("12", x.id("ヲ"));
        assertTrue(x.isFirst("イ"));
        assertFalse(x.isFirst("ワ"));
        assertTrue(x.isNext("ヲ", "ワ"));
    }

    @Test
    void test漢数字() {
        漢数字 x = new 漢数字();
        assertTrue(x.isMatch("三"));
        assertTrue(x.isMatch("十二"));
        assertTrue(x.isMatch("三十三から三十三の五まで"));
        assertTrue(x.isMatch("三十四及び三十五"));
        assertTrue(x.isFirst("一"));
        assertTrue(x.isFirst("三"));
        assertEquals("35-5-2", x.id("三十五の五の二"));
        assertEquals("34:35-5-2", x.id("三十四及び三十五の五の二まで"));
    }

    @Test
    void test第漢数字() {
        第漢数字 x = new 第漢数字();
        assertTrue(x.isMatch("第三"));
        assertTrue(x.isMatch("第十二"));
        assertTrue(x.isMatch("第三十三の五"));
        assertTrue(x.isMatch("第三十四の二の二"));
        assertTrue(x.isFirst("第一"));
        assertFalse(x.isFirst("第三"));
        assertEquals("4", x.nextId(x.id("第三")));
        assertEquals("3-2", x.nextId2(x.id("第三")));
        assertTrue(x.isNext("第三", "第四"));
        assertTrue(x.isNext("第三", "第三の二"));
        assertEquals("35-5-2", x.id("第三十五の五の二"));
    }

    @Test
    void test漢数字章() {
        漢数字章 x = new 漢数字章();
        assertTrue(x.isMatch("三章"));
        assertTrue(x.isMatch("十二章"));
        assertTrue(x.isMatch("三十三の五章"));
        assertTrue(x.isMatch("三十四の二の二章"));
        assertTrue(x.isFirst("一章"));
        assertFalse(x.isFirst("三章"));
        assertEquals("4", x.nextId(x.id("三章")));
        assertEquals("3-2", x.nextId2(x.id("三章")));
        assertTrue(x.isNext("三章", "四章"));
        assertTrue(x.isNext("三章", "三の二章"));
        assertEquals("35-5-2", x.id("三十五の五の二章"));
    }

}
