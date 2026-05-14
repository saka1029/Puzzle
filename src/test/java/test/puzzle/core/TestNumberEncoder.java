package test.puzzle.core;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.junit.Test;

import puzzle.core.NumberEncoder;

public class TestNumberEncoder {

    @Test
    public void testNumberEncoder() {
        NumberEncoder ne = NumberEncoder.of("0123456789abcdef");
        assertEquals("NumberEncoder(base=16,decode=0123456789abcdef)", ne.toString());
        assertEquals(BigInteger.valueOf(65535), ne.encode("ffff"));
        assertEquals("ffff", ne.decode(BigInteger.valueOf(65535)));
        NumberEncoder b64 = NumberEncoder.of(
            "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ.-");
        assertEquals("19Bwbi", b64.decode(BigInteger.valueOf(1234567890)));
        assertEquals(BigInteger.valueOf(1234567890), b64.encode("19Bwbi"));
    }

}
