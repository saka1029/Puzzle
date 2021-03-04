package test.puzzle;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import puzzle.Rational;

class TestGcd {

    @Test
    void test() {
        assertEquals(1, Rational.gcd(2, 3));
        assertEquals(1, Rational.gcd(3, 2));
        assertEquals(4, Rational.gcd(72, 52));
        assertEquals(4, Rational.gcd(52, 72));
        assertEquals(52, Rational.gcd(0, -52));
    }

}
