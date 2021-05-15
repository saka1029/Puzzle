package puzzle.parsers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Base64 {

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

        void put(int b) {
            inBuffer |= (b & 0xFF) << (MAX_IN - 1 - inSize++) * 8;
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
            put(b);
        }

        @Override
        public void close() throws IOException {
            if (inSize > 0)
                writeBuffer();
            super.close();
        }

    }

    public static byte[] encode(byte[] in, int lineSize, byte[] lineSeparator) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (Base64OutputStream os = new Base64OutputStream(bos, lineSize, lineSeparator)) {
            os.write(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bos.toByteArray();
    }

    public static byte[] encode(byte[] in, int lineSize) {
        return encode(in, lineSize, Base64OutputStream.DEFAULT_LINE_SEPARATOR);
    }

    public static byte[] encode(byte[] in) {
        return encode(in, 0, Base64OutputStream.DEFAULT_LINE_SEPARATOR);
    }

    public static class Base64InputStream extends FilterInputStream {

        static final int MAX_IN_SIZE = 4, MAX_OUT_SIZE = 3;
        static final int DECODE_SIZE = 128;
        static final byte[] DECODE = new byte[DECODE_SIZE];
        static {
            Arrays.fill(DECODE, (byte) -1);
            for (int i = 0, max = Base64OutputStream.ENCODE.length; i < max; ++i)
                DECODE[Base64OutputStream.ENCODE[i]] = (byte) i;
        }

        int ch = 0, buffer = 0;
        int inSize = 0, outSize = 0;

        public Base64InputStream(InputStream in) {
            super(in);
        }

        int get() throws IOException {
            if (ch == -1)
                return -1;
            while ((ch = in.read()) != -1)
                if (ch < DECODE_SIZE) {
                    ch = DECODE[ch];
                    if (ch != -1)
                        return ch;
                }
            return ch;
        }

        void fill() throws IOException {
            buffer = inSize = outSize = 0;
            if (ch == -1)
                return;
            for (int i = 0; i < MAX_IN_SIZE; ++i) {
                int b = get();
                if (b != -1)
                    ++inSize;
                buffer = buffer << 6 | b & 0b111111;
            }
        }

        @Override
        public int read() throws IOException {
            if (outSize >= inSize - 1)
                fill();
            if (outSize >= inSize - 1)
                return -1;
            return buffer >>> (MAX_OUT_SIZE - outSize++ - 1) * 8 & 0xFF;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int size = 0;
            int r;
            for (int i = off, max = off + len; i < max && (r = read()) != -1; ++i, ++size)
                b[i] = (byte) r;
            return size == 0 ? -1 : size;
        }
    }

    public static byte[] decode(byte[] input) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ByteArrayInputStream bis = new ByteArrayInputStream(input);
            Base64InputStream is = new Base64InputStream(bis)) {
            byte[] buffer = new byte[4096];
            while (true) {
                int size = is.read(buffer);
                if (size == -1)
                    break;
                bos.write(buffer, 0, size);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bos.toByteArray();
    }
}
