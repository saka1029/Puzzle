package test.puzzle.language.pl0;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import puzzle.language.pl0.Pl0Try;

/**
 * PL/0 – Pascal for small machines
 * http://pascal.hansotten.com/niklaus-wirth/pl0/
 *
 * 文法上は文として以下が定義されているが、コード上は実装されていない。
 * <ul>
 * <li>'?' ident</li>
 * <li>'!' expression</li>
 * </ul>
 *
 * <pre>
 * program   = block '.'
 * block     = [ 'const' ident '=' number {, ident = number} ';' ]
 *             [ 'var' ident { ',' ident } ';' ]
 *             { 'procedure' ident ';' block ';' } statement
 * statement = [ ident ':=' expression
 *               | 'call' ident
 *               | '?' ident
 *               | '!' expression
 *               | 'begin' statement { ';' statement } 'end'
 *               | 'if' condition 'then' statement
 *               | 'while' condition 'do' statement ]
 * condition  = 'odd' expression
 *               | expression ( '=' | '#' | '<' | '<=' | '>' | '>=' ) expression
 * expression = [ '+' | '-' ] term { ( '+' | '-' ) term }
 * term       = factor { ( '*' | '/' ) factor }
 * factor     = ident | number | '(' expression ')'
 * </pre>
 */
public class TestPL0Try {

    @Test
    public void testCompile() {
        // x:l=0,a=3 y:l=0,a=4 z:l=0,a=5 ok:l=0,a=6
        String source = """
            const ZERO = 0, ONE = 1, TWO = 2, THREE = 3;
            var x, y, z, ok;
            procedure addXandY;
            begin
                z := x + y
            end;
            begin
                ok := 55;
                x := ONE;
                y := TWO;
                call addXandY;
                if z = THREE then
                    ok := -1;
                ! z;
                ! ok 
            end.
            """;
        List<Pl0Try.Instruction> codes = Pl0Try.compile(source);
        assertEquals(List.of(3, 0), Pl0Try.interpret(codes));
    }

    @Test
    public void testCompile2() {
        String source = """
            const ZERO = 0, ONE = 1, TWO = 2, THREE = 3;
            var x, y, z, ok;
            procedure addXandY;
                var x, y;
            begin
                z := x + y
            end;
            begin
                ok := 0;
                x := ONE;
                y := TWO;
                call addXandY;
                if z = THREE then
                    ok := -1;
                ! z;
                ! ok 
            end.
            """;
        List<Pl0Try.Instruction> codes = Pl0Try.compile(source);
        assertEquals(List.of(0, 0), Pl0Try.interpret(codes));
    }

    @Test
    public void testWhile() {
        String source = """
            const
                N = 10;
            var
                i,
                s;
            begin
                s := 0;
                i := 1;
                while i < N do begin
                    s := s + i;
                    i := i + 1
                end
            end.
            """;
        List<Pl0Try.Instruction> codes = Pl0Try.compile(source);
        /* variable name     s  i  s  i  s  i  s  i   s  i   s  i   s  i   s  i   s  i   s   i */
        assertEquals(List.of(0, 1, 1, 2, 3, 3, 6, 4, 10, 5, 15, 6, 21, 7, 28, 8, 36, 9, 45, 10), Pl0Try.interpret(codes));
    }

    @Test
    public void testStack() {
        int[] stack = new int[3];
        int sp = 0;
        stack[sp++] = 2;
        stack[sp++] = 3;
        stack[--sp - 1] = stack[sp - 1] + stack[sp];
        System.out.println("sp=" + sp + " stack=" + Arrays.toString(stack));
    }
}
