package test.puzzle.parsers;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static puzzle.parsers.施設基準Parser.半角変換;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import puzzle.parsers.施設基準Parser.LineReader;
import puzzle.parsers.施設基準Parser.イロハ;
import puzzle.parsers.施設基準Parser.丸数字;
import puzzle.parsers.施設基準Parser.括弧数字;
import puzzle.parsers.施設基準Parser.数字;
import puzzle.parsers.施設基準Parser.漢数字;
import puzzle.parsers.施設基準Parser.漢数字章;
import puzzle.parsers.施設基準Parser.第漢数字;

class Test施設基準parser {


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

    @Test
    void testLineReader() throws FileNotFoundException, IOException {
        File file = new File("data/施設基準/告示.txt");
        try (LineReader reader = new LineReader(file)) {
            String first = reader.readLine();
            String second = reader.readLine();
            assertEquals("基本診療料の施設基準等の一部を改正する件", reader.line);
            assertEquals(2, reader.lineNo);
            assertEquals("000602943.pdf", reader.file);
            assertEquals(1, reader.pageNo);
            assertEquals(2, reader.lineNoInPage);
        }
    }

}
