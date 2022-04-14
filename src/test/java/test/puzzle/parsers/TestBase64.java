package test.puzzle.parsers;

import static org.junit.Assert.*;
import static puzzle.parsers.Base64.*;

import java.nio.charset.StandardCharsets;
import java.util.Random;

import org.junit.Test;

public class TestBase64 {

    static byte[] bytes(String s) {
        return s.getBytes(StandardCharsets.UTF_8);
    }

    static String string(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @Test
    public void testEncode() {
        assertArrayEquals(bytes("QUJDREVGRw=="), encode(bytes("ABCDEFG")));
        assertArrayEquals(bytes("QUJDREVG"), encode(bytes("ABCDEF")));
        assertArrayEquals(bytes("QUJDREU="), encode(bytes("ABCDE")));
        assertArrayEquals(bytes("QUJDRA=="), encode(bytes("ABCD")));
        assertArrayEquals(bytes("QUJD"), encode(bytes("ABC")));
        assertArrayEquals(bytes("QUI="), encode(bytes("AB")));
        assertArrayEquals(bytes("QQ=="), encode(bytes("A")));
    }

    @Test
    public void testDecode() {
        assertArrayEquals(bytes("ABCDEFG"), decode(bytes("QUJDREVGRw==")));
        assertArrayEquals(bytes("ABCDEF"), decode(bytes("QUJDREVG")));
        assertArrayEquals(bytes("ABCDE"), decode(bytes("QUJDREU=")));
        assertArrayEquals(bytes("ABCD"), decode(bytes("QUJDRA==")));
        assertArrayEquals(bytes("ABC"), decode(bytes("QUJD")));
        assertArrayEquals(bytes("AB"), decode(bytes("QUI=")));
        assertArrayEquals(bytes("A"), decode(bytes("QQ==")));
    }

    static Random random = new Random(1);

    static byte[] randomBytes(int size) {
        byte[] out = new byte[size];
        for (int i = 0; i < size; ++i)
            out[i] = (byte) random.nextInt(256);
        return out;
    }

    @Test
    public void testEncodeDecodeRandom() {
        byte[] random = randomBytes(4001);
        assertArrayEquals(random, decode(encode(random)));
    }

    static java.util.Base64.Encoder javaEncoder = java.util.Base64.getEncoder();

    static void testStandardEncode(String s) {
        byte[] input = s.getBytes(StandardCharsets.UTF_8);
        assertArrayEquals(javaEncoder.encode(input), encode(input));
    }

    @Test
    public void testStandardEncode() {
        testStandardEncode("ABCDEFG");
        testStandardEncode("ABCDEF");
        testStandardEncode("ABCDE");
        testStandardEncode("ABCD");
        testStandardEncode("ABC");
        testStandardEncode("AB");
        testStandardEncode("A");
        testStandardEncode("");
        testStandardEncode("日本語をエンコード");
    }

    static java.util.Base64.Decoder javaDecoder = java.util.Base64.getDecoder();

    static void testStandardDecode(String s) {
        byte[] input = s.getBytes(StandardCharsets.UTF_8);
        byte[] encoded = javaEncoder.encode(input);
        assertArrayEquals(javaDecoder.decode(encoded), decode(encoded));
    }

    @Test
    public void testStandardDecode() {
        testStandardDecode("ABCDEFG");
        testStandardDecode("ABCDEF");
        testStandardDecode("ABCDE");
        testStandardDecode("ABCD");
        testStandardDecode("ABC");
        testStandardDecode("AB");
        testStandardDecode("A");
        testStandardDecode("");
        testStandardDecode("日本語をエンコード");
    }

    @Test
    public void testEncodeDecode日本語() {
        String text = "Base64は、データを64種類の印字可能な英数字のみを用いて、\r\n"
            + "それ以外の文字を扱うことの出来ない通信環境にてマルチバイト文字や\r\n"
            + "バイナリデータを扱うためのエンコード方式である。MIMEによって規定\r\n"
            + "されていて、7ビットのデータしか扱うことの出来ない電子メールにて\r\n"
            + "広く利用されている。具体的には、A–Z, a–z, 0–9 までの62文字と、\r\n"
            + "記号2つ (+, /)、さらにパディング（余った部分を詰める）のための\r\n"
            + "記号として = が用いられる。この変換によって、データ量は4/3（約133%）\r\n"
            + "になる。また、MIMEの基準では76文字ごとに改行コードが入るため、\r\n"
            + "この分の2バイトを計算に入れるとデータ量は約137%となる。\r\n";
        byte[] bytes = bytes(text);
        byte[] encoded = encode(bytes, 76);
        byte[] decoded = decode(encoded);
        assertArrayEquals(bytes, decoded);
        String encodedString = string(encoded);
        System.out.println(encodedString);
    }

}
