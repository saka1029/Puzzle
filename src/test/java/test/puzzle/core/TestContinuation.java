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

    interface ContinueDouble { void apply(double value); }
    static void add(double x, double y, ContinueDouble k) { k.apply(x + y); }
    static void multiply(double x, double y, ContinueDouble k) { k.apply(x * y); }
    static void sqrt(double x, ContinueDouble k) { k.apply(Math.sqrt(x)); }

    static void pyth(double x, double y, ContinueDouble k) {
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

    interface ContinueInt { void apply(int value); }
    interface ContinueBool { void apply(boolean value); }
    static void equals(int x, int y, ContinueBool k) { k.apply(x == y); }
    static void add(int x, int y, ContinueInt k) { k.apply(x + y); }
    static void subtract(int x, int y, ContinueInt k) { k.apply(x - y); }
    static void multiply(int x, int y, ContinueInt k) { k.apply(x * y); }

    static void factorial(int n, ContinueInt k) {
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

    static void factorial2(int n, ContinueInt k) {
        f_aux(n, 1, k);
    }
    
    static void f_aux(int n, int a, ContinueInt k) {
        equals(n, 0, b -> {
            if (b)
                k.apply(a);
            else
                subtract(n, 1, nm1 ->
                    multiply(n, a, (ContinueInt) nta ->
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
}

