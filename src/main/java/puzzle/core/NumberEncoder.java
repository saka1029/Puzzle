package puzzle.core;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NumberEncoder {

    public final String enc;
    public final int[] encode;
    public final BigInteger base;
    public final Map<Integer, Integer> decode;

    NumberEncoder(String encode) {
        this.enc = encode;
        this.encode = encode.codePoints().toArray();
        this.base = BigInteger.valueOf(this.encode.length);
        this.decode = IntStream.range(0, this.encode.length)
            .mapToObj(i -> i)
            .collect(Collectors.toMap(i -> this.encode[i], i -> i));
    }

    public static NumberEncoder of(String encode) {
        return new NumberEncoder(encode);
    }

    public BigInteger decode(String s) {
        return s.codePoints()
            .map(decode::get)
            .mapToObj(BigInteger::valueOf)
            .reduce(BigInteger.ZERO, (a, b) -> a.multiply(base).add(b));
    }

    public String encode(BigInteger n) {
        List<Integer> list = new LinkedList<>();
        while (n.compareTo(BigInteger.ZERO) > 0) {
            BigInteger[] d = n.divideAndRemainder(base);
            n = d[0];
            list.addFirst(d[1].intValue());
        }
        StringBuilder result = new StringBuilder();
        for (int i : list)
            result.appendCodePoint(encode[i]);
        return result.toString();
    }

    @Override
    public String toString() {
        return "NumberEncoder(base=%d,encode=%s)".formatted(base, enc);
    }

}
