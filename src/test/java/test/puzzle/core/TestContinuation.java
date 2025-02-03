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
    interface ContinDouble {
        void apply(double value);
    }

    static double pyth(double x, double y) {
        return Math.sqrt(x * x + y * y);
    }

    static void add(double x, double y, ContinDouble c) { c.apply(x + y); }
    static void multiply(double x, double y, ContinDouble c) { c.apply(x * y); }
    static void sqrt(double x, ContinDouble c) { c.apply(Math.sqrt(x)); }
    
    static void pyth(double x, double y, ContinDouble c) {
        multiply(x, x, x2 ->
            multiply(y, y, y2 ->
                add(x2, y2, x2py2 ->
                    sqrt(x2py2, c))));
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
}
