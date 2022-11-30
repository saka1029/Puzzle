package test.puzzle.language;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static puzzle.language.Stack.*;

import java.io.StringReader;
import java.util.Locale;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Test;

import puzzle.language.Stack;
import puzzle.language.Stack.Context;
import puzzle.language.Stack.Value;

public class TestStack {

    static String LOG_FORMAT_KEY = "java.util.logging.SimpleFormatter.format";
    static String LOG_FORMAT = "%1$tFT%1$tT.%1$tL %4$s %3$s %5$s %6$s%n";
    static {
        Locale.setDefault(Locale.ENGLISH);
        System.setProperty(LOG_FORMAT_KEY, LOG_FORMAT);
    }
    static final Logger logger = Logger.getLogger(TestStack.class.getSimpleName());

    static void methodName() {
//        logger.info("*** " + Thread.currentThread().getStackTrace()[2].getMethodName());
    }

    Context context = context(20);//.traceTo(logger::info);

    static Cons l(Value... elements) {
        return Cons.of(elements);
    }
    
    static Int i(int value) {
        return Int.of(value);
    }
    
    static Value w(String word) {
        return Context.code(word);
    }
    
    static Value eval(Context c, Value v) {
        v.run(c);
        return c.pop();
    }
    
    @Test
    public void testPlus() {
        methodName();
        assertEquals(i(3), eval(context, l(i(1), i(2), w("+"))));
    }

    static Value read(String input) {
        Reader reader = new Reader(new StringReader(input));
        return reader.read();
    }

    @Test
    public void testReaderIntegerDecimal() {
        methodName();
        assertEquals(Int.of(12345), read("12345"));
        assertEquals(Int.of(12345), read("+12345"));
        assertEquals(Int.of(-12345), read("-12345"));
        assertEquals(Int.of(12345), read("  12345"));
    }

    @Test
    public void testReaderIntegerHexadecimal() {
        methodName();
        assertEquals(Int.of(0x23ab), read("0x23ab"));
        assertEquals(Int.of(0x23ab), read("+0x23ab"));
        assertEquals(Int.of(-0x23ab), read("-0x23ab"));
    }

    @Test
    public void testReaderIntegerOctal() {
        methodName();
        assertEquals(Int.of(01234567), read("01234567"));
        assertEquals(Int.of(01234567), read("+01234567"));
        assertEquals(Int.of(-01234567), read("-01234567"));
    }

    @Test
    public void testReaderIntegerBinary() {
        methodName();
        assertEquals(Int.of(0b11100110), read("0b11100110"));
        assertEquals(Int.of(0b11100110), read("+0b11100110"));
        assertEquals(Int.of(-0b11100110), read("-0b11100110"));
    }

    @Test
    public void testReaderChar() {
        methodName();
        assertEquals(Int.of('a'), read("'a'"));
        assertEquals(Int.of('a'), read("\n'a'"));
        assertEquals(Int.of('あ'), read("'あ'"));
        assertEquals(Int.of("𩸽".codePointAt(0)), read("'𩸽'"));
    }

    @Test
    public void testReaderWord() {
        methodName();
        assertEquals("+", read("+").toString());
    }

    @Test
    public void testReaderSymbol() {
        methodName();
        assertEquals(Str.of("abc"), read("/abc"));
    }

    @Test
    public void testReaderCons() {
        methodName();
        assertEquals(Cons.NIL, read("[]"));
        assertEquals(Cons.of(Int.ONE), read("[1]"));
        assertEquals(Cons.of(Int.ONE, Int.of(2), Int.of(3)), read("[1 2 3]"));
        assertEquals(Cons.of(Str.of("𩸽")), read("[\"𩸽\"]"));
    }

    @Test
    public void testReaderStr() {
        methodName();
        assertEquals(Str.of("a"), read("\"a\""));
        assertEquals(Str.of("a\nb"), read("\"a\nb\""));
        assertEquals(Str.of("a\rb"), read("\"a\rb\""));
        assertEquals(Str.of("a\b"), read("\"a\b\""));
        assertEquals(Str.of("\tb"), read("\"\tb\""));
        assertEquals(Str.of("𩸽"), read("\"\\u{29e3d}\""));
        assertEquals(Str.of("\r\n"), read("\"\\u{d}\\u{a}\""));
        assertEquals(Str.of("𩸽は"), read("\"\\u{d867}\\u{de3d}は\""));
        assertEquals(Str.of("\ud867\ude3dは"), read("\"\\u{d867}\\u{de3d}は\""));
    }

    @Test
    public void testReaderEnd() {
        methodName();
        assertEquals(Reader.END, read("  "));
    }

    void testEval(String expected, String source) {
        methodName();
        assertEquals(parse(context, expected), Stack.eval(context, source));
        if (!context.isEmpty()) {
            System.out.println("stack is not empty: stack = " + context);
            fail();
        }
    }

    @Test
    public void testDupDrop() {
        methodName();
        testEval("1", "1 dup drop");
    }

    @Test
    public void testSwap() {
        methodName();
        testEval("2", "1 2 swap drop");
    }

    @Test
    public void testOver() {
        methodName();
        testEval("1", "1 2 over drop drop");
    }

    @Test
    public void testInt() {
        methodName();
        testEval("3", "1 2 +");
        testEval("-1", "1 2 -");
        testEval("6", "2 3 *");
        testEval("3", "7 2 /");
        testEval("1", "7 2 %");
        testEval("3", "9 sqrt");
        testEval("3", "11 sqrt");
        testEval("255", "0xff");
        testEval("15", "0b1111");
        testEval("97", "'a'");
        testEval("171581", "'𩸽'");
        testEval("-3", "-3");
    }

    @Test
    public void testCar() {
        methodName();
        testEval("1", "[1 2] car");
    }

    @Test
    public void testCdr() {
        methodName();
        testEval("[2]", "[1 2] cdr");
    }

    @Test
    public void testCons() {
        methodName();
        testEval("[1 2]", "1 2 [] cons cons");
        testEval("3", "1 2 [+] cons cons exec");
        testEval("3", "1 2 true [+] [-] if");
        testEval("-1", "1 2 false [+] [-] if");
        testEval("3", "1 2 true [+] [-] [if] cons cons cons exec");
        testEval("-1", "1 2 false [+] [-] [if] cons cons cons exec");
    }
    
    @Test
    public void testCarCdrCons() {
        methodName();
        testEval("[4 6]", "[1 2] [3 4] over car over car + over2 cdr car over2 cdr car + [] cons cons swap drop swap drop");
    }

    @Test
    public void testAppend() {
        methodName();
        testEval("[1 2 3 4]", "[1 2] [3 4] +");
        testEval("3", "[1 2] [+] + exec");
    }

    @Test
    public void testExec() {
        methodName();
        testEval("3", "[1 2 +] exec");
    }

    @Test
    public void testIf() {
        methodName();
        testEval("1", "true 1 2 if");
        testEval("2", "false 1 2 if");
        testEval("3", "1 2 true [+] [-] if");
        testEval("-1", "1 2 false [+] [-] if");
    }

    @Test
    public void testMap() {
        methodName();
        testEval("[10 20 30]", "[1 2 3] [10 *] map list");
        testEval("[11 21 31]", "[1 2 3] [10 *] map [1 +] map list");
        // 累積的リスト(cumulative list)
        testEval("[1 3 6 10 15]", "0 [1 2 3 4 5] [+ dup] map list swap drop");
        testEval("[1 2 6 24 120]", "1 1 5 range [* dup] map list swap drop");
    }

    @Test
    public void testFilter() {
        methodName();
        testEval("[0 2 4]", "[0 1 2 3 4 5] [2 % 0 ==] filter list");
        testEval("[0 3 6 9]", "/multipliers [[over % 0 ==] filter list swap drop] define"
            + " 3 0 10 range multipliers list");
    }

    @Test
    public void testDefine() {
        methodName();
        testEval("2", "/inc [1 +] define 1 inc");
    }

    @Test
    public void testFactRecursive() {
        methodName();
        testEval("120", "/fact [dup 1 <= [drop 1] [dup 1 - fact *] if] define 5 fact");
    }

    @Test
    public void testFactLoop() {
        methodName();
        testEval("[1 2 3]", "/iota [1 swap range] define 3 iota list");
        testEval("120", "/fact [1 swap iota [*] for] define 5 fact");
        testEval("120", "5 1 swap 1 swap range [*] for");
    }

    @Test
    public void testSum() {
        methodName();
        testEval("55", "/sum [0 swap [+] for] define 1 10 range sum");
    }

    @Test
    public void testReverse() {
        methodName();
        // 繰り返し版
        testEval("[5 4 3 2 1]", "/reverse [[] swap [swap cons] for] define"
            + " [1 2 3 4 5] reverse");
        // 再帰版
        testEval("[5 4 3 2 1]", "/reverse [dup null [] [dup cdr reverse swap car [] cons +] if] define"
            + " [1 2 3 4 5] reverse");
    }

    @Test
    public void testFibonacciRecursive() {
        methodName();
        testEval("8", "/fibonacci [dup 1 <= [] [dup 1 - fibonacci swap 2 - fibonacci +] if] define 6 fibonacci");
    }

    @Test
    public void testFibonacciLoop() {
        methodName();
        Function<Integer, Integer> fibonacci = n -> {
            int a = 0, b = 1;
            while (n-- > 0) {
                int c = a + b;
                a = b;
                b = c;
            }
            return a;
        };
        assertEquals((Integer)0, fibonacci.apply(0));
        assertEquals((Integer)1, fibonacci.apply(1));
        assertEquals((Integer)1, fibonacci.apply(2));
        assertEquals((Integer)2, fibonacci.apply(3));
        assertEquals((Integer)3, fibonacci.apply(4));
        assertEquals((Integer)5, fibonacci.apply(5));
        assertEquals((Integer)8, fibonacci.apply(6));
        assertEquals((Integer)13, fibonacci.apply(7));
        testEval("[1 2 3]", "/iota [1 swap range] define 3 iota list");
        testEval("5", "/fibonacci [0 swap 1 swap iota [drop swap over +] for drop] define 5 fibonacci");
        testEval("8", "6 fibonacci");
        testEval("13", "7 fibonacci");
    }

    /**
     * エラトステネスの篩
     * rangeに対して2, 3, ... , sqrt(n)のふるいを適用することでnまでの素数を求めます。
     * <pre>
     * 2 n range 2 sieve 3 sieve ... n sqrt
     * </pre>
     */
    @Test
    public void testPrimeJava() {
        int max = 100;
        int maxSieve = (int)Math.sqrt(max);
        IntStream stream = IntStream.rangeClosed(2, max);
        for (int i = 2; i <= maxSieve; ++i) {
            final int k = i;
            // ラムダ式は外部の変数kを参照しています。
            // これはラムダ式を定義したときに静的に見えている変数です。
            stream = stream.filter(n -> n == k || n % k != 0);
        }
        assertArrayEquals(new int[] {
            2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41,
            43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97},
            stream.toArray());
    }

    @Test
    public void testPrime() {
        methodName();
        testEval("[2 3 5 7 9 11 13 15 17 19]", "/sieve2 [2 swap [over over == [drop true] [over % 0 !=] if] filter] define\n"
            + "2 20 range sieve2 list swap drop");
        testEval("[2 3 5 7 9 11 13 15 17 19]", "/sieve [[over over == [drop true] [over % 0 !=] if] filter] define\n"
            + "2 20 range 2 swap sieve list swap drop");
        /*
         * ラムダ式が参照すべき引数をラムダ式内に含めることによって、funarg問題を解決する。
         */
        testEval("[2 3 5 7 11 13 17 19]",
            "/sieve-of-2 [[2 over over == [drop drop true] [% 0 !=] if] filter] define\n"
            + "/sieve-of-3 [[3 over over == [drop drop true] [% 0 !=] if] filter] define\n"
            + "2 20 range sieve-of-2 sieve-of-3 list");
        testEval("[2 3 5 7 11 13 17 19]",
            "/sieve-2 [2 over over == [drop drop true] [% 0 !=] if] define\n"
            + "/sieve-3 [3 over over == [drop drop true] [% 0 !=] if] define\n"
            + "2 20 range [sieve-2] filter [sieve-3] filter list");
    }

    /**
     * マクロでラムダ式を作り出す。
     * ラムダ式が参照すべき引数をラムダ式内に含めることによって、funarg問題を解決する。
     * <pre><code>
     * "sieve-macro" [[over over == [drop drop true] [% 0 !=] if] cons] define
     *
     * 7 sieve-macro
     *   ->
     * [7 over over == [drop drop true] [% 0 !=] if]
     * </code></pre>
     */
    @Test
    public void testPrimeMacro() {
        testEval("[2 3 5 7 11 13 17 19 23 29 31 37 41 43 47 53 59 61 67 71 73 79 83 89 97]",
            "/sieve-macro [[over over == [drop drop true] [% 0 !=] if] cons] define\n"
            + "/prime [dup 2 swap range swap 2 swap sqrt range [sieve-macro filter] for list] define\n"
            + "100 prime");
    }

    @Test
    public void testWhile() {
        methodName();
        testEval("45", "0 0 [dup 10 <] [swap over + swap 1 +] while drop");
    }

    @Test
    public void testStr() {
        methodName();
        testEval("\"ABC\"", "\"abc\" [0x20 -] map str");
        testEval("'A'", "\"ABC\" car");
        testEval("\"BC\"", "\"ABC\" cdr");
        testEval("\"ABC\"", "'A' \"BC\" cons");
        testEval("\"ABCDE\"", "\"ABC\" \"DE\" +");
        testEval("\"A\nBC\"", "\"A\nBC\"");
        testEval("\"ABC\"", "[65 66 67] str");
        testEval("[65 66 67]", "\"ABC\" list");
    }
    
    static void testContext(String expected, Context context, String source) {
        repl(context, source);
        assertEquals(expected, context.toString());
    }
    
    /*
     * 例えば問題は以下の[drop 1]の部分にある。
     * 
     * /fact [dup 1 <= [drop 1] [dup 1 - fact *] if] define
     * 
     * この「drop」は引数を捨てるためにある。
     * 「dup 1 - fact *]は最後の「*」によって引数が消費されるため、「drop]する必要がない。
     * 引数をすべてdropして戻り値をpush()してから関数を終了する必要がある。
     * コードを書いている間、脳内にスタックを置いてシミュレートする必要がある。
     * 
     * フレームポインタを導入してスタック上の引数やローカル変数に固定したワードでアクセスできるようにすれば、
     * この問題を解決できる。
     * 
     * /fact [ 1 1 $( $0 1 <= [1] [$0 1 - fact $0 *] if )$] define
     * 
     * $( : フレームポインタを退避し、新たなフレームを作成する。
     *      word stack                            operation
     *      $(   [arg0 1 1]                       stack.push(fp)
     *           [arg0 1 1 old_fp]                fp = sp - 1;            
     * )$ : フレームポインタを回復し、戻り値をスタックにpushする。
     *      )$   [arg0 1 1 old_fp a b ... r]      is = stack[fp - 2]; os = stack[fp - 1]; t = fp; fp = stack[fp]; 
     *           [arg0 1 1 old_fp a b ... r]      arraycopy(stack, sp - os, t - is - 2, os); sp = t - is - 2 + os;
     *           [r]
     * $0 : フレームポインタを使って第1引数を取り出す。
     *      $0   [arg0 1 1 old_fp a b ... ]       stack.push(stack[fp - stack[fp - 2] - 2 + 0)
     *      $0   [arg0 arg1 2 1 old_fp a b ... ]  stack.push(stack[fp - stack[fp - 2] - 2 + 0)
     *      $1   [arg0 arg1 2 1 old_fp a b ... ]  stack.push(stack[fp - stack[fp - 2] - 2 + 1)
     */
    @Test
    public void testContext() {
        Context context = context(20); // .traceTo(logger::info);
        testContext("[]", context, "");
        testContext("[1 2 2]", context, "1 2 dup");
        testContext("[1 2]", context, "drop");
        testContext("[2 1]", context, "swap");
        testContext("[3]", context, "+");
        testContext("[]", context, "drop");
        testContext("[1]", context, "[1 2 3] car");
        testContext("[]", context, "drop");
        testContext("[[2 3]]", context, "[1 2 3] cdr");
        testContext("[]", context, "drop");
        testContext("[[1 2 3]]", context, "1 [2 3] cons");
        testContext("[]", context, "drop");
        testContext("[{[1 2 3] [2 *] map}]", context, "[1 2 3] [2 *] map");
        testContext("[[2 4 6]]", context, "list");
        testContext("[]", context, "drop");
        testContext("[[2 4]]", context, "[1 2 3] [2 *] map [4 <=] filter list");
        testContext("[]", context, "drop");
        testContext("[[2 4] [6]]", context, "[1 2 3] [2 *] map dup [4 <=] filter list swap [4 <= !] filter list");
        testContext("[]", context, "drop drop");
        testContext("[[[2 4 6] [8 10 12] [14 16 18]]]", context, "[[1 2 3] [4 5 6] [7 8 9]] [[2 *] map list] map list");
        testContext("[]", context, "drop");
        testContext("[[1 2 3 4 5 6]]", context, "[1 2 3] [4 5 6] +");
        testContext("[]", context, "drop");
        testContext("[[129 130 131 132]]", context, "'a' 'd' range [32 +] map list");
        testContext("[]", context, "drop");
        testContext("[abcd]", context, "'a' 'd' range str");
        testContext("[]", context, "drop");
        testContext("[ABCD]", context, "'a' 'd' range [32 -] map str");
    }
    
    @Test
    public void testJavaStream() {
        int[][] a = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        int[][] b = Stream.of(a)
            .map(x -> IntStream.of(x).map(y -> y * 2).toArray())
            .toArray(int[][]::new);
        assertArrayEquals(new int[][] {{2, 4, 6}, {8, 10, 12}, {14, 16, 18}}, b);
    }
}
