package test.puzzle.language;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;

import org.junit.Test;

import puzzle.language.Stack.Int;
import puzzle.language.Stack.Reader;
import puzzle.language.Stack.Value;

public class TestStackReader {

    static Value read(String input) {
        Reader reader = new Reader(new StringReader(input));
        return reader.read();
    }

    @Test
    public void testIntegerDecimal() {
        assertEquals(Int.of(12345), read("12345"));
        assertEquals(Int.of(12345), read("+12345"));
        assertEquals(Int.of(-12345), read("-12345"));
        assertEquals(Int.of(12345), read("  12345"));
    }

    @Test
    public void testIntegerHexadecimal() {
        assertEquals(Int.of(0x23ab), read("0x23ab"));
        assertEquals(Int.of(0x23ab), read("+0x23ab"));
        assertEquals(Int.of(-0x23ab), read("-0x23ab"));
    }

    @Test
    public void testIntegerOctal() {
        assertEquals(Int.of(01234567), read("01234567"));
        assertEquals(Int.of(01234567), read("+01234567"));
        assertEquals(Int.of(-01234567), read("-01234567"));
    }

    @Test
    public void testIntegerBinary() {
        assertEquals(Int.of(0b11100110), read("0b11100110"));
        assertEquals(Int.of(0b11100110), read("+0b11100110"));
        assertEquals(Int.of(-0b11100110), read("-0b11100110"));
    }

    @Test
    public void testChar() {
        assertEquals(Int.of('a'), read("'a'"));
        assertEquals(Int.of('あ'), read("'あ'"));
        assertEquals(Int.of("𩸽".codePointAt(0)), read("'𩸽'"));
    }

}
