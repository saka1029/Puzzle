package test.puzzle.core;

import static org.junit.Assert.*;
import static puzzle.core.Numbers.*;

import org.junit.Test;

public class TestNumbers {

    @Test
    public void testGcd() {
        assertEquals(6, gcd(12, 18));
        assertEquals(6, gcd(-12, 18));
        assertEquals(6, gcd(12, -18));
        assertEquals(6, gcd(-12, -18));
        assertEquals(7, gcd(0, 7));
        assertEquals(7, gcd(7, 0));
        assertEquals(0, gcd(0, 0));
    }

    @Test
    public void testLcm() {
        assertEquals(36, lcm(12, 18));
        assertEquals(36, lcm(-12, 18));
        assertEquals(36, lcm(12, -18));
        assertEquals(36, lcm(-12, -18));
        assertEquals(0, lcm(0, 7));
        assertEquals(0, lcm(7, 0));
        assertEquals(0, lcm(0, 0));
    }

    @Test
    public void testLcm1() {
        assertEquals(36, lcm1(12, 18));
        assertEquals(36, lcm1(-12, 18));
        assertEquals(36, lcm1(12, -18));
        assertEquals(36, lcm1(-12, -18));
        assertEquals(0, lcm1(0, 7));
        assertEquals(0, lcm1(7, 0));
        assertEquals(0, lcm1(0, 0));
    }

}
