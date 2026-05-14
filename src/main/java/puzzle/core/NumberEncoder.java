package puzzle.core;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NumberEncoder {

    public final String dec;
    public final int[] decode;
    public final BigInteger base;
    public final Map<Integer, Integer> encode;

    NumberEncoder(String decode) {
        this.dec = decode;
        this.decode = decode.codePoints().toArray();
        this.base = BigInteger.valueOf(this.decode.length);
        this.encode = IntStream.range(0, this.decode.length)
            .mapToObj(i -> i)
            .collect(Collectors.toMap(i -> this.decode[i], i -> i));
    }

    public static NumberEncoder of(String decode) {
        return new NumberEncoder(decode);
    }

    public BigInteger encode(String s) {
        return s.codePoints()
            .map(encode::get)
            .mapToObj(BigInteger::valueOf)
            .reduce(BigInteger.ZERO, (a, b) -> a.multiply(base).add(b));
    }

    public String decode(BigInteger n) {
        List<Integer> list = new LinkedList<>();
        while (n.compareTo(BigInteger.ZERO) > 0) {
            BigInteger[] d = n.divideAndRemainder(base);
            n = d[0];
            list.addFirst(d[1].intValue());
        }
        StringBuilder result = new StringBuilder();
        for (int i : list)
            result.appendCodePoint(decode[i]);
        return result.toString();
    }

    @Override
    public String toString() {
        return "NumberEncoder(base=%d,decode=%s)".formatted(base, dec);
    }

}
