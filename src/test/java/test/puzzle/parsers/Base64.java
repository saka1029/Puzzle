package test.puzzle.parsers;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    public static class Base64OutputStream extends FilterOutputStream {

        static class LineSeparateOutputStream extends FilterOutputStream {

            final int lineSize;
            final byte[] lineSeparator;
            int outSize = 0;

            public LineSeparateOutputStream(OutputStream out, int lineSize, byte[] lineSeparator) {
                super(out);
                this.lineSize = lineSize;
                this.lineSeparator = lineSeparator;
            }

            void writeLineSeparator() throws IOException {
                out.write(lineSeparator);
                outSize = 0;
            }

            @Override
            public void write(int b) throws IOException {
                out.write(b);
                if (lineSize > 0 && ++outSize >= lineSize)
                    writeLineSeparator();
            }

            @Override
            public void close() throws IOException {
                if (lineSize > 0 && outSize > 0)
                    writeLineSeparator();
                super.close();
            }
        }

        static final int MAX_IN = 3;
        static final int DEFAULT_LINE_SIZE = 76;
        static final byte[] DEFAULT_LINE_SEPARATOR = {'\r', '\n'};
        static final byte[] ENCODE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
            .getBytes(StandardCharsets.ISO_8859_1);

        final byte[] outBuffer = new byte[4];
        int inBuffer = 0, inSize = 0, outSize = 0;

        public Base64OutputStream(OutputStream out, int lineSize, byte[] lineSeparator) {
            super(lineSize > 0 ? new LineSeparateOutputStream(out, lineSize, lineSeparator) : out);
        }

        public Base64OutputStream(OutputStream out) {
            this(out, DEFAULT_LINE_SIZE, DEFAULT_LINE_SEPARATOR);
        }

        public Base64OutputStream(OutputStream out, int lineSize) {
            this(out, lineSize, DEFAULT_LINE_SEPARATOR);
        }

        void add(int b) {
            inBuffer |= (b & 0xFF) << ((MAX_IN - 1 - inSize++) * 8);
        }

        void writeBuffer() throws IOException {
            for (int i = outBuffer.length - 1; i >= 0; --i, inBuffer >>>= 6)
                outBuffer[i] = ENCODE[inBuffer & 0b111111];
            out.write(outBuffer, 0, inSize + 1);
            for (int i = inSize; i < MAX_IN; ++i)
                out.write('=');
            inSize = 0;
        }

        @Override
        public void write(int b) throws IOException {
            if (inSize >= MAX_IN)
                writeBuffer();
            add(b);
        }

        @Override
        public void close() throws IOException {
            if (inSize > 0)
                writeBuffer();
            super.close();
        }

    }

    static byte[] encode(byte[] in, int lineSize, byte[] lineSeparator) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (Base64OutputStream os = new Base64OutputStream(bos, lineSize, lineSeparator)) {
            os.write(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bos.toByteArray();
    }

    static byte[] encode(byte[] in, int lineSize) {
        return encode(in, lineSize, Base64OutputStream.DEFAULT_LINE_SEPARATOR);
    }

    String text = "Base64は、データを64種類の印字可能な英数字のみを用いて、\r\n"
        + "それ以外の文字を扱うことの出来ない通信環境にてマルチバイト文字や\r\n"
        + "バイナリデータを扱うためのエンコード方式である。MIMEによって規定\r\n"
        + "されていて、7ビットのデータしか扱うことの出来ない電子メールにて\r\n"
        + "広く利用されている。具体的には、A–Z, a–z, 0–9 までの62文字と、\r\n"
        + "記号2つ (+, /)、さらにパディング（余った部分を詰める）のための\r\n"
        + "記号として = が用いられる。この変換によって、データ量は4/3（約133%）\r\n"
        + "になる。また、MIMEの基準では76文字ごとに改行コードが入るため、\r\n"
        + "この分の2バイトを計算に入れるとデータ量は約137%となる。\r\n";

    @Test
    public void testBase64OutputStream() throws IOException {
//        try (OutputStream os = new Base64OutputStream(System.out)) {
//            os.write(text.getBytes(StandardCharsets.UTF_8));
//        }
        System.out.println(new String(encode(text.getBytes(StandardCharsets.UTF_8), 72), StandardCharsets.UTF_8));
    }
    
    public static class Base64InputStream extends FilterInputStream {
        
        static final int MAX_IN_SIZE = 4, MAX_OUT_SIZE = 3;
        static final byte[] DECODE = new byte[128];
        static {
            Arrays.fill(DECODE, (byte)-1);
            for (int i = 0, max = ENCODE.length; i < max; ++i)
                DECODE[ENCODE[i]] = (byte)i;
//            DECODE['='] = 0;    // パディングは0にデコードします。
        }

        boolean isReadable = true;
        int ch = 0, inBuffer = 0;
        int inSize = 0, outSize = 0;

        public Base64InputStream(InputStream in) {
            super(in);
        }
        
        int get() throws IOException {
            if (ch == -1) return -1;
            while ((ch = in.read()) != -1)
                if (ch < 128) {
                    ch = DECODE[ch];
                    if (ch != -1)
                        return ch;
                }
            return ch;
        }

        void fill() throws IOException {
            inBuffer = 0;
            if (ch == -1) return;
            for (int i = 0; i < MAX_IN_SIZE; ++i) {
                int b = get();
                if (b == -1)
                    b = 0;
                else
                    ++inSize;
                inBuffer = inBuffer << 6 | b & 0b111111;
            }
        }
        
        @Override
        public int read() throws IOException {
            if (ch == -1) return -1;
            if (outSize < inSize - 1)

            
        }
        
    }
}
