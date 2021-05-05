package test.puzzle.parsers;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;

import org.junit.jupiter.api.Test;

class Base64 {

    static final byte[] ENCODE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
        .getBytes(StandardCharsets.ISO_8859_1);
    static final byte[] DECODE = new byte[128];
    static {
        Arrays.fill(DECODE, (byte)-1);
        for (int i = 0, max = ENCODE.length; i < max; ++i)
            DECODE[ENCODE[i]] = (byte)i;
        DECODE['='] = 0;    // パディングは0にデコードします。
    }

    /**
     * バイト列をintにパックします。
     *
     * @param array パックする対象となるbyte配列です。
     * @param start 対象データを取り出すbyte配列の開始位置です。
     * @param size パックする対象データのbyte数です。
     * @param bits byte値から取り出す右端のビット数です。
     * @return
     */
    static int packBits(byte[] array, int start, int size, int bits) {
        int len = array.length;
        int packed = 0;
        int mask = (1 << bits) - 1;
        for (int i = start, end = start + size; i < end; ++i) {
            byte b = i < len ? array[i] : 0;
            packed = packed << bits | b & mask;
        }
        return packed;
    }

    /**
     * パックされたint値をbyte配列に展開します。
     *
     * @param array 展開先のbyte配列です。
     * @param start 展開先の開始位置です。
     * @param size 展開するbyte数です。
     * @param bits パックされたデータから単一の値を取り出す右端のビット数です。
     * @param packed パックされたint値です。
     */
    static void unpackBits(byte[] array, int start, int size, int bits, int packed) {
        int len = array.length;
        int mask = (1 << bits) - 1;
        for (int i = start + size - 1; i >= start; --i, packed >>>= bits) {
            byte b = (byte) (packed & mask);
            if (i < len)
                array[i] = b;
        }
    }

    /**
     * <pre>
     * inSize     : 入力のバイト長 = in.length
     * blockCount : チャンクの数 (チャンクサイズは入力が3バイト、出力が4バイト)
     * outSize    : blockCount * 4 (パディングも含むサイズ）
     * </pre>
     */
    public static byte[] encode(byte[] in) {
        int inSize = in.length;
        int blockCount = (inSize + 2) / 3;
        int outSize = blockCount * 4;
        byte out[] = new byte[outSize];
        for (int i = 0, j = 0, max = blockCount * 3; i < max; i += 3, j += 4) {
            unpackBits(out, j, 4, 6, packBits(in, i, 3, 8));
            for (int k = j, kmax = j + 4; k < kmax; ++k)
                out[k] = ENCODE[out[k]];

//            int b0 = in[i];
//            int b1 = i + 1 < inSize ? in[i + 1] : 0;
//            int b2 = i + 2 < inSize ? in[i + 2] : 0;
//            // in : 000000001111111122222222
//            // out: aaaaaabbbbbbccccccdddddd
//            out[j] = ENCODE[b0 >>> 2 & 0b111111];
//            out[j + 1] = ENCODE[b0 << 4 & 0b110000 | b1 >>> 4 & 0b001111];
//            out[j + 2] = ENCODE[b1 << 2 & 0b111100 | b2 >>> 6 & 0b000011];
//            out[j + 3] = ENCODE[b2 & 0b111111];
        }
        // パディング部分が'A'にエンコードされているので'='に置換します。
        for (int j = inSize + blockCount; j < outSize; ++j)
            out[j] = '=';
        return out;
    }

    static byte decode(byte b) {
        if (b < 0)
            throw new IllegalArgumentException("unknown input character '" + (char)b + "'");
        byte decode = DECODE[b];
        if (decode < 0)
            throw new IllegalArgumentException("unknown input character '" + (char)b + "'");
        return decode;
    }

    public static byte[] decode(byte[] in) {
        int inSize = in.length;
        if (inSize % 4 != 0)
            throw new IllegalArgumentException("input size != 4 * n");
        int blockCount = inSize / 4;
        int paddingSize = 0;
        if (inSize >= 1 && in[inSize - 1] == '=') ++paddingSize;
        if (inSize >= 2 && in[inSize - 2] == '=') ++paddingSize;
        int outSize = blockCount * 3 - paddingSize;
        byte[] out = new byte[outSize];
        byte[] block = new byte[4];
        for (int i = 0, j = 0; i < inSize; i += 4, j += 3) {
            for (int k = 0, kk = i; k < 4; ++k, ++kk)
                block[k] = decode(in[kk]);
            unpackBits(out, j, 3, 8, packBits(block, 0, 4, 6));

//            byte b0 = decode(in[i]);
//            byte b1 = decode(in[i + 1]);
//            byte b2 = decode(in[i + 2]);
//            byte b3 = decode(in[i + 3]);
//            // in : 000000111111222222333333
//            // out: AAAAAAAABBBBBBBBCCCCCCCC
//            out[j] = (byte) (b0 << 2 & 0b11111100 | b1 >>> 4 & 0b00000011);
//            if (j + 1 < outSize) out[j + 1] = (byte) (b1 << 4 & 0b11110000 | b2 >>> 2 & 0b00001111);
//            if (j + 2 < outSize) out[j + 2] = (byte) (b2 << 6 & 0b11000000 | b3 & 0b00111111);
        }
        return out;
    }

    static byte[] bytes(String s) {
        return s.getBytes(StandardCharsets.ISO_8859_1);
    }

    static Random random = new Random(1);
    static byte[] randomBytes(int size) {
        byte[] out = new byte[size];
        for (int i = 0; i < size; ++i)
            out[i] = (byte)random.nextInt(256);
        return out;
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

    @Test
    public void testEncodeDecode() {
        byte[] random = randomBytes(4001);
//        System.out.println(Arrays.toString(random));
//        System.out.println(new String(encode(random), StandardCharsets.ISO_8859_1));
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
    void testEncodeLength() {
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

    @Test
    void testDecodeLength() {
        assertEquals(7, decode(bytes("QUJDREVGRw==")).length);
        assertEquals(6, decode(bytes("QUJDREVG")).length);
        assertEquals(5, decode(bytes("QUJDREU=")).length);
        assertEquals(4, decode(bytes("QUJDRA==")).length);
        assertEquals(3, decode(bytes("QUJD")).length);
        assertEquals(2, decode(bytes("QUI=")).length);
        assertEquals(1, decode(bytes("QQ==")).length);
    }

    @Test
    void testDecodeTable() {
        byte[] expected = {
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63,
            52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, +0, -1, -1,
            -1, +0, +1, +2, +3, +4, +5, +6, +7, +8, +9, 10, 11, 12, 13, 14,
            15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1,
            -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
            41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1
        };
        assertArrayEquals(expected, DECODE);
    }

    @Test
    public void testPack() {
        assertEquals(0x010203, packBits(new byte[] {1, 2, 3}, 0, 3, 8));
        assertEquals(0x010200, packBits(new byte[] {1, 2}, 0, 3, 8));
        assertEquals(0xff02ff, packBits(new byte[] {-1, 2, -1}, 0, 3, 8));
        assertEquals(0b000001_000010_000011_000100, packBits(new byte[] {1, 2, 3, 4}, 0, 4, 6));
        assertEquals(0b111111_000010_000011_111111, packBits(new byte[] {-1, 2, 3, -1}, 0, 4, 6));
    }

    @Test
    public void testUnpack() {
        byte[] array = new byte[4];
        unpackBits(array, 0, 4, 6, 0b000001_000010_000011_000100);
        assertArrayEquals(new byte[] {1, 2, 3, 4}, array);
        byte[] array2 = new byte[3];
        unpackBits(array2, 0, 3, 8, 0b000001_000010_000011_000100);
        assertArrayEquals(new byte[] {0b00000100, 0b00100000, (byte)0b11000100}, array2);
    }
}
