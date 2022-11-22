package test.puzzle.language;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;

import org.junit.Test;

import puzzle.language.Stack.Cons;
import puzzle.language.Stack.Context;
import puzzle.language.Stack.Int;
import puzzle.language.Stack.Reader;
import puzzle.language.Stack.Str;
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
        assertEquals(Int.of('a'), read("\n'a'"));
        assertEquals(Int.of('あ'), read("'あ'"));
        assertEquals(Int.of("𩸽".codePointAt(0)), read("'𩸽'"));
    }

    @Test
    public void testWord() {
        assertEquals(Context.code("+"), read("+"));
    }

    @Test
    public void testSymbol() {
        assertEquals(Str.of("abc"), read("/abc"));
    }

    @Test
    public void testCons() {
        assertEquals(Cons.END, read("[]"));
        assertEquals(Cons.of(Int.ONE), read("[1]"));
        assertEquals(Cons.of(Int.ONE, Int.of(2), Int.of(3)), read("[1 2 3]"));
        assertEquals(Cons.of(Str.of("𩸽")), read("[\"𩸽\"]"));
    }

    @Test
    public void testStr() {
        assertEquals(Str.of("a"), read("\"a\""));
        assertEquals(Str.of("a\nb"), read("\"a\nb\""));
        assertEquals(Str.of("a\rb"), read("\"a\rb\""));
        assertEquals(Str.of("a\b"), read("\"a\b\""));
        assertEquals(Str.of("\tb"), read("\"\tb\""));
        assertEquals(Str.of("𩸽"), read("\"\\u{29e3d}\""));
        assertEquals(Str.of("\r\n"), read("\"\\u{d}\\u{a}\""));
        assertEquals(Str.of("𩸽は"), read("\"\\u{d867}\\u{de3d}は\""));
        assertEquals(Str.of("\ud867\ude3dは"), read("\"\\u{d867}\\u{de3d}は\""));
    }

    @Test
    public void testEnd() {
        assertEquals(Reader.END, read("  "));
    }

}
