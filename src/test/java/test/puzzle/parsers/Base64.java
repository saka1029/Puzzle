package test.puzzle.parsers;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

class Base64 {

    static final byte[] BASE64_TABLE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
        .getBytes(StandardCharsets.ISO_8859_1);

    /**
     * m : 入力の長さ n : 出力の長さ n = (m + 2) / 3 * 4;
     *
     */
    static byte[] encode(byte[] in) {
        int inSize = in.length;
        int chunkSize = (inSize + 2) / 3;
        int outSize = chunkSize * 4;
        byte out[] = new byte[outSize];
        for (int i = 0, j = 0, max = chunkSize * 3; i < max; i += 3, j += 4) {
            int b0 = in[i];
            int b1 = i + 1 < inSize ? in[i + 1] : 0;
            int b2 = i + 2 < inSize ? in[i + 2] : 0;
            out[j] = BASE64_TABLE[b0 >>> 2 & 0b111111];
            out[j + 1] = BASE64_TABLE[b0 << 4 & 0b110000 | b1 >>> 4 & 0b001111];
            out[j + 2] = BASE64_TABLE[b1 << 2 & 0b111100 | b2 >>> 6 & 0b000011];
            out[j + 3] = BASE64_TABLE[b2 & 0b111111];
        }
        // パディング部分が'A'にエンコードされているので'='に置換します。
        for (int j = inSize + chunkSize; j < outSize; ++j)
            out[j] = '=';
        return out;
    }

    static byte[] bytes(String s) {
        return s.getBytes(StandardCharsets.ISO_8859_1);
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

    static java.util.Base64.Encoder javaEncoder = java.util.Base64.getEncoder();

    static void testStandard(String s) {
        byte[] input = s.getBytes(StandardCharsets.UTF_8);
        assertArrayEquals(javaEncoder.encode(input), encode(input));
    }

    @Test
    public void testStandard() {
        testStandard("ABCDEFG");
        testStandard("ABCDEF");
        testStandard("ABCDE");
        testStandard("ABCD");
        testStandard("ABC");
        testStandard("AB");
        testStandard("A");
        testStandard("");
        testStandard("日本語をエンコード");
    }

    @Test
    void testLength() {
        assertEquals(4, encode(new byte[1]).length);
        assertEquals(4, encode(new byte[2]).length);
        assertEquals(4, encode(new byte[3]).length);
        assertEquals(8, encode(new byte[4]).length);
        assertEquals(8, encode(new byte[5]).length);
        assertEquals(8, encode(new byte[6]).length);
        assertEquals(12, encode(new byte[7]).length);
        assertEquals(12, encode(new byte[8]).length);
        assertEquals(12, encode(new byte[9]).length);
        assertEquals(16, encode(new byte[10]).length);
    }

}
