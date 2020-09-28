package test.puzzle;

import static puzzle.Iterables.*;

import java.math.BigInteger;
import java.util.List;

import org.junit.jupiter.api.Test;

class TestAAA {

    @Test
    void test() {
        List<Integer> digits = List.of(1, 2, 3, 4, 5, 6,  7, 8, 9, 0, 1, 2);
        BigInteger value = reduce(BigInteger.ZERO, (a, b) -> a.multiply(BigInteger.TEN).add(BigInteger.valueOf(b)), digits);
        System.out.println(value);
        BigInteger stream = digits.stream()
            .reduce(BigInteger.ZERO, (a, b) -> a.multiply(BigInteger.TEN).add(BigInteger.valueOf(b)), (a, b) -> a.add(b));
        System.out.println(stream);
    }

}
