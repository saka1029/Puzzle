package test.puzzle.core;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.junit.Test;

import puzzle.core.NumberEncoder;

public class TestNumberEncoder {

    @Test
    public void testNumberEncoder() {
        NumberEncoder ne = NumberEncoder.of("0123456789abcdef");
        assertEquals("NumberEncoder(base=16,encode=0123456789abcdef)", ne.toString());
        assertEquals(BigInteger.valueOf(65535), ne.decode("ffff"));
        assertEquals("ffff", ne.encode(BigInteger.valueOf(65535)));
    }

    @Test
    public void testBase64() {
        NumberEncoder b64 = NumberEncoder.of(
            "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ.-");
        assertEquals(64, b64.base.intValue());
        assertEquals("19Bwbi", b64.encode(BigInteger.valueOf(1234567890)));
        assertEquals(BigInteger.valueOf(1234567890), b64.decode("19Bwbi"));
        NumberEncoder b11 = NumberEncoder.of("0123456789,");
        String csv = "7,7,,2,,4,,2,,1,,2,,6,,6,3,,,3,,,3,,,,5,,,,3,,,2,,,3,3,,2,,4,,2,,3,,3,,1,";
        String enc = "13plJdoCcgKvq9j48VYeRqG97G-fZTHldYafS-h1EpF";
        assertEquals(enc, b64.encode(b11.decode(csv)));
        assertEquals(csv, b11.encode(b64.decode(enc)));
    }

}
