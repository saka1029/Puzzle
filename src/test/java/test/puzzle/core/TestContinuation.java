package test.puzzle.core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestContinuation {
    /*
     * 【直接スタイル】
     * (define (pyth x y)
     *   (sqrt (+ (* x x) (* y y))))
     * 
     * 【継承渡しスタイル】
     * (define (pyth& x y k)
     *   (*& x x (lambda (x2)
     *     (*& y y (lambda (y2)
     *       (+& x2 y2 (lambda (x2py2)
     *         (sqrt& x2py2 k))))))))
     */
    static double pyth(double x, double y) {
        return Math.sqrt(x * x + y * y);
    }

    interface ContinuationDouble { void apply(double value); }
    static void add(double x, double y, ContinuationDouble k) { k.apply(x + y); }
    static void multiply(double x, double y, ContinuationDouble k) { k.apply(x * y); }
    static void sqrt(double x, ContinuationDouble k) { k.apply(Math.sqrt(x)); }

    static void pyth(double x, double y, ContinuationDouble k) {
        multiply(x, x, x2 ->
            multiply(y, y, y2 ->
                add(x2, y2, x2py2 ->
                    sqrt(x2py2, k))));
    }

    @Test
    public void testPyth() {
        double x = 1.0, y = 1.0;
        double direct = pyth(x, y);
        var contin = new Object() { double value; };
        pyth(x, y, c -> contin.value = c);
        System.out.printf("direct=%f%n", direct);
        System.out.printf("contin=%f%n", contin.value);
        assertEquals(direct, contin.value, 1e-5);
    }

    /*
     * 【直接スタイル】
     * (define (factorial n)
     *   (if (= n 0)
     *     1     ; 末尾再帰でない
     *     (* n (factorial (- n 1)))))
     * 
     * 【継承渡しスタイル】
     * (define (factorial& n k)
     *   (=& n 0 (lambda (b)
     *     (if b   ; 再帰呼び出しにおいて
     *       (k 1) ; 継続が成長する
     *       (-& n 1 (lambda (nm1)
     *         (factorial& nm1 (lambda (f)
     *         (*& n f k)))))))))
     */

    static int factorial(int n) {
        return n == 0 ? 1 : n * factorial(n - 1);
    }

    interface ContinuationInt { void apply(int value); }
    interface ContinuationBool { void apply(boolean value); }
    static void equals(int x, int y, ContinuationBool k) { k.apply(x == y); }
    static void add(int x, int y, ContinuationInt k) { k.apply(x + y); }
    static void subtract(int x, int y, ContinuationInt k) { k.apply(x - y); }
    static void multiply(int x, int y, ContinuationInt k) { k.apply(x * y); }

    static void factorial(int n, ContinuationInt k) {
        equals(n, 0, b -> {
            if (b)
                k.apply(1);
            else
                subtract(n, 1, nm1 ->
                    factorial(nm1, f ->
                        multiply(n, f, k)));
        });
    }

    @Test
    public void testFactorial() {
        for (int i = 0; i < 20; ++i) {
            int direct = factorial(i);
            var contin = new Object() { int value; };
            factorial(i, c -> contin.value = c);
            assertEquals(direct, contin.value);
        }
    }

    /*
     * 【直接スタイル】
     * (define (factorial n)
     *   (f-aux n 1))
     * (define (f-aux n a)
     *   (if (= n 0)
     *     a
     *    (f-aux (- n 1) (* n a)))) ; 末尾再帰
     * 
     * 【継承渡しスタイル】
     * (define (factorial& n k) (f-aux& n 1 k))
     * (define (f-aux& n a k)
     *   (=& n 0 (lambda (b)
     *     (if b                  ; 再帰呼び出しにおいて
     *       (k a)                ; 継続が変わらない
     *       (-& n 1 (lambda (nm1) 
     *         (*& n a (lambda (nta)
     *           (f-aux& nm1 nta k)))))))))
     */
    static int factorial2(int n) {
        return f_aux(n, 1);
    }

    static int f_aux(int n, int a) {
        return n == 0 ? a : f_aux(n - 1, n * a);
    }

    static void factorial2(int n, ContinuationInt k) {
        f_aux(n, 1, k);
    }
    
    static void f_aux(int n, int a, ContinuationInt k) {
        equals(n, 0, b -> {
            if (b)
                k.apply(a);
            else
                subtract(n, 1, nm1 ->
                    multiply(n, a, (ContinuationInt) nta ->
                        f_aux(nm1, nta, k)));
        });
    }

    @Test
    public void testFactorial2() {
        for (int i = 0; i < 20; ++i) {
            int direct = factorial2(i);
            var contin = new Object() { int value; };
            factorial2(i, c -> contin.value = c);
            assertEquals(direct, contin.value);
        }
    }

    static void factorial3(int n, ContinuationInt c) {
        if (n == 0)
            c.apply(1);
        else
            factorial3(n - 1, result -> c.apply(n * result));
    }

    @Test
    public void testFactorial3() {
        for (int i = 0; i < 20; ++i) {
            int direct = factorial(i);
            var contin = new Object() { int value; };
            factorial3(i, c -> contin.value = c);
            assertEquals(direct, contin.value);
        }
    }

    static void generator(ContinuationInt c) {
        c.apply(0);
        c.apply(1);
    }

    @Test
    public void testCallback() {
        generator(x -> System.out.println(x));
    }

    /*
     * CPS(継続渡しスタイル)のプログラミングに入門する
     * https://qiita.com/Kai101/items/eae3a00fcd1fc87e25fb
     * 
     * def inc_c(cont, x):
     *     cont(x+1)
     * 
     * def add_c(cont, x, y):
     *     cont(x+y)
     * 
     * def execute():
     *     add_c(
     *         lambda v1: add_c(
     *             lambda v2: inc_c(
     *                 lambda v3: print(v3)
     *             , v2)
     *         , v1, 5), 
     *     1, 3)
     *     
     * execute()
     */

     static void inc_c(ContinuationInt cont, int x) {
        cont.apply(x + 1);
     }

     static void add_c(ContinuationInt cont, int x, int y) {
        cont.apply(x + y);
     }

     @Test
     public void testAddCIncC() {
        var result = new Object() { int value; };
        add_c(
            v1 -> add_c(
                v2 -> inc_c(
                    v3 -> result.value = v3,
                    v2),
                v1, 5),
            1, 3);
        assertEquals(10, result.value);
     }

     static void inc_cr(int x, ContinuationInt cont) {
        cont.apply(x + 1);
     }

     static void add_cr(int x, int y, ContinuationInt cont) {
        cont.apply(x + y);
     }

     @Test
     public void testAddCRIncCR() {
        var result = new Object() { int value; };
        add_cr(1, 3,
            v1 -> add_cr(v1, 5,
                v2 -> inc_cr(v2,
                    v3 -> result.value = v3)));
        assertEquals(10, result.value);
     }

     /*
      * CPSで書かれたプログラムであれば、どのような言語でもcall/ccを簡単に
      * 実装することが出来ます。
      * call/ccのCPSのスタイルでのPythonでの実装は以下になります。
      * 
      * hoge.py
      * def callcc(cont, f):
      *     f(cont, lambda _c, v: cont(v))
      * 
      * callcc関数は、第2引数に呼び出す関数fをとり、
      * その関数fの第2引数に現在の継続を表すlambdaを渡して呼び出すという動作をします。
      * callccに渡って来る関数fもCPSな関数なので、
      * 当然、fの第1引数には継続contが入ります。そのため、
      * 「これだけで現在の継続が渡せているじゃないか？」という気分になりますが、
      * CPSでの第1引数の継続はCPSのコンテクストとしての裏方に徹するべきなので、
      * 第2引数で明示的にcontを渡します。
      * この際、CPSの「第1引数に継続をとる」というルールに沿うために、
      * 「第1引数に継続を取り、それを無視するlambda」にラップされて渡されています。
      * このlambdaをcallccの呼び出し先の関数から呼ぶと、callccの呼び出し元に帰ることが出来ます。
      */

    interface ContinuationContinuationInt {
        void apply(ContinuationInt cont, int value);
    }

    interface ContinuationFunctionInt {
        void apply(ContinuationInt cont, ContinuationContinuationInt x);
    }

    static void callcc(ContinuationInt cont, ContinuationFunctionInt f) {
        f.apply(cont, (c, v) -> cont.apply(v));
    }

}

