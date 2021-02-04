package test.puzzle.lambda;

import static org.junit.jupiter.api.Assertions.*;
import static puzzle.lambda.LambdaCalculus.*;

import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

import puzzle.Common;
import puzzle.lambda.LambdaCalculus.Term;

class TestLambdaCalculus {
    static Logger logger = Common.getLogger(TestLambdaCalculus.class);

    static void testParse(String source) {
        logger.info(source + " -> " + parse(source).toString());
    }

    @Test
    void testParse() {
        testParse("λx.λy.x y x");
        testParse("λ x . λ y . x (y a) b c");
        testParse("λt.λf.t");
        testParse("λt.λf.f");
        testParse("λx.(λx.x)x");
        testParse("(λx.(λx.x)x)x");
    }

    static void testReduce(String source) {
        Term t = parse(source);
        Term r = t.reduce();
        Term n = t.normalize();
        logger.info(source + " -> " + t + " -> " + r + " -> " + n);
    }

    @Test
    void testReduce() {
        testReduce("(λx.x) b");
        String TRUE = "(λt.λf.t)";
        String FALSE = "(λt.λf.f)";
        testReduce("(λl.λm.λn.l m n) " + TRUE + " v w");
        testReduce("(λl.λm.λn.l m n) " + FALSE + " v w");

    }

    static void assertEquivalent(Term expected, Term actual) {
        Term expectedNormalized = expected.normalize();
        Term actualNormalized = expected.normalize();
        assertEquals(expectedNormalized, actualNormalized);
    }

    static void assertNotEquivalent(Term expected, Term actual) {
        Term expectedNormalized = expected.normalize();
        Term actualNormalized = expected.normalize();
        assertNotEquals(expectedNormalized, actualNormalized);
    }

    @Test
    void testSame() {
        assertEquals(parse("λx.x y"), parse("λx.x y"));
        assertNotEquals(parse("λx.x y"), parse("λv.v y"));
        assertEquivalent(parse("λx.x y"), parse("λv.v y"));
        assertEquivalent(parse("λx.λy.x y x"), parse("λa.λb.a b a"));
        assertEquivalent(parse("v"), parse("(λl.λm.λn.l m n) (λt.λf.t) v w").reduce());
    }

    @Test
    public void testNormalizeBoudVariableNames() {
        assertEquals("(λA.(((λB.B) A) p))", parse("λx.(λx.x)x p").normalize().toString());
        assertEquals("(λA.((λB.((λC.C) B)) A))", parse("λx.(λx.(λx.x) x) x").normalize().toString());
        assertEquals("(λA.(λB.(λC.(λD.(λE.(λF.(λG.(λH.(λI.(λJ.(λK.(λL.(λM.(λN.(λO.(λP.(λQ.(λR.(λS.(λT.(λU.(λV.(λW.(λX.(λY.(λZ.(λAA.(λAB.(λAC.(λAD.(λAE.(λAF.(λAG.(λAH.(λAI.(λAJ.(λAK.(λAL.(λAM.(λAN.(λAO.(λAP.(λAQ.(λAR.(λAS.(λAT.(λAU.(λAV.(λAW.(λAX.(λAY.(λAZ.(λBA.(λBB.BB))))))))))))))))))))))))))))))))))))))))))))))))))))))",
            parse("λx.λx.λx.λx.λx.λx.λx.λx.λx.λx.λx.λx.λx.λx.λx.λx.λx.λx.λx.λx.λx.λx.λx.λx.λx.λx.λx.λx.λx.λx.λx.λx.λx.λx.λx.λx.λx.λx.λx.λx.λx.λx.λx.λx.λx.λx.λx.λx.λx.λx.λx.λx.λx.λx.x").normalize().toString());
    }

    /**
     * https://ja.wikipedia.org/wiki/%E3%83%A9%E3%83%A0%E3%83%80%E8%A8%88%E7%AE%97#%E8%87%AA%E7%84%B6%E6%95%B0%E3%81%A8%E7%AE%97%E8%A1%93
     * 自然数と算術[編集]
     * 自然数をラムダ式で表現する方法はいくつか異なる手法が知られているが、
     * その中でもっとも一般的なのはチャーチ数（英語版）（英: Church numerals）と呼ばれるもので、
     * 以下のように定義されている。
     * 0 := λf x. x
     * 1 := λf x. f x
     * 2 := λf x. f (f x)
     * 3 := λf x. f (f (f x))
     * 以下同様である。直感的には、数 n はラムダ式では f という関数をもらって
     * それを n 回適用したものを返す関数である。つまり、チャーチ数は1引数関数を受け取り、
     * 1引数関数を返す高階関数である。（チャーチの提唱した元々のラムダ計算は、
     * ラムダ式の引数が少なくとも一回は関数の本体に出現していなくてはならないことになっていた。
     * そのため、その体系では上に挙げた 0 の定義は不可能である。）
     * 上のチャーチ数の定義のもとで、後続（後者）を計算する関数、
     * すなわち n を受け取って n + 1 を返す関数を定義することができる。それは以下のようになる。
     * SUCC := λn f x. f (n f x)
     * また、加算は以下のように定義できる。
     * PLUS := λm n f x. m f (n f x)
     * または単にSUCCを用いて、以下のように定義してもよい。
     * PLUS := λm n. m SUCC n
     * PLUS は2つの自然数をとり1つの自然数を返す関数である。
     * この理解のためには例えば、 PLUS 2 3 == 5 であることを確認してみるとよいだろう。また、乗算は以下のように定義される。
     * MULT := λm n. m (PLUS n) 0
     * この定義は、 m と n の乗算は、 0 に n を m回加えることと等しい、ということを利用して作られている。もう少し短く、
     * 以下のように定義することもできる。
     * MULT := λm n f. m (n f)
     * 正の整数 n の先行（前者）を計算する関数 PRED n = n − 1 は簡単ではなく、
     * PRED := λn f x. n (λg h. h (g f)) (λu. x) (λu. u)
     * もしくは
     * PRED := λn. n (λg k. (g 1) (λu. PLUS (g k) 1) k) (λv. 0) 0
     * と定義される。
     * 上の部分式 (g 1) (λu. PLUS (g k) 1) k は、 g(1) がゼロとなるとき k に評価され、
     * そうでないときは g(k) + 1 に評価されることに注意せよ。
     */
    @Test
    public void testNumerals() {
        Term zero = parse("λf.λx.x");
        Term one = parse("λf.λx.f x");
        Term two = parse("λf.λx.f (f x)");
        Term three = parse("λf.λx.f (f (f x))");
        Term four = parse("λf.λx.f (f (f (f x)))");
        Term succ = parse("λn.λf.λx.f (n f x)");
        assertEquivalent(one, succ.apply(zero).reduce());
        assertEquivalent(two, succ.apply(one).reduce());
        Term plus = parse("λm.λn.λf.λx.m f (n f x)");
        assertEquivalent(one, plus.apply(zero).apply(one).reduce());
        assertEquivalent(three, plus.apply(one).apply(two).reduce());
        assertEquivalent(two, plus.apply(succ.apply(zero)).apply(one).reduce());
        Term mult = parse("λm.λn.λf.m (n f)");
        assertEquivalent(zero, mult.apply(zero).apply(one).reduce());
        assertEquivalent(one, mult.apply(one).apply(one).reduce());
        assertEquivalent(two, mult.apply(one).apply(two).reduce());
        assertEquivalent(three, mult.apply(three).apply(one).reduce());
        assertEquivalent(four, mult.apply(two).apply(two).reduce());
        Term pred = parse("λn.λf.λx.n (λg.λh.h (g f)) (λu.x) (λu.u)");
        assertEquivalent(zero, pred.apply(one).reduce());
        // 以下の2つはstack overflow
//        assertEquivalent(one, pred.apply(two).reduce());
//        assertEquivalent(two, pred.apply(three).reduce());
    }

}
