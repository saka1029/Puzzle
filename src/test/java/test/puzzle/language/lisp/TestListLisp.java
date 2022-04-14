package test.puzzle.language.lisp;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.junit.Test;

public class TestListLisp {

    public static final Object NIL = Collections.emptyList();
    public static final Object T = "T";

    public static List<?> list(Object... elements) {
        return List.of(elements);
    }

    public static Object car(Object obj) {
        return ((List<?>) obj).get(0);
    }

    public static List<?> cdr(Object obj) {
        List<?> list = (List<?>) obj;
        return list.subList(1, list.size());
    }

    public static List<?> cons(Object car, Object cdr) {
        List<?> list = (List<?>) cdr;
        return new AbstractList<Object>() {
            @Override
            public Object get(int index) {
                return index == 0 ? car : list.get(index - 1);
            }

            @Override
            public int size() {
                return list.size() + 1;
            }
        };
    }

    public static boolean eq(Object x, Object y) {
        return x.equals(y);
    }

    public static boolean equal(Object x, Object y) {
        return x.equals(y);
    }

    public static boolean atom(Object obj) {
        return !(obj instanceof List);
    }

    public static boolean nul(Object obj) {
        return obj instanceof List<?> list && list.isEmpty();
    }

    public static Object caar(Object e) {
        return car(car(e));
    }

    public static Object cadr(Object e) {
        return car(cdr(e));
    }

    public static Object cdar(Object e) {
        return cdr(car(e));
    }

    public static Object cadar(Object e) {
        return car(cdar(e));
    }

    public static Object caddr(Object e) {
        return cadr(cdr(e));
    }

    /**
     * <pre>
     * pairlis[x;y;a]=[null[x]->a;T->cons[cons[car[x];car[y]];
     * pairlis[cdr[x];cdr[y];a]]]
     *
     * <pre>
     * ただし点対は表現できないので、cons[car[x];car[y]]はlist[car[x],car[y]]とする。
     */
    static Object pairlis(Object x, Object y, Object a) {
        return nul(x) ? a : cons(list(car(x), car(y)), pairlis(cdr(x), cdr(y), a));
    }

    /**
     * <pre>
     * assoc[x:a]=[equal[caar[a];x]->car[a];T->assoc[x;cdr[a]]]
     * </pre>
     */
    static Object assoc(Object x, Object a) {
//        System.out.println("assoc(" + x + ", " + a + ")");
        return equal(caar(a), x) ? car(a) : assoc(x, cdr(a));
    }

    /**
     * <pre>
     * evcon[c;a]=[eval[caar[c];a]->eval[cadar[c];a];
     *             T->evcon[cdr[c];a]];
     * </pre>
     */
    static Object evcon(Object c, Object a) {
//        System.out.println("evcon(" + c + ", " + a + ")");
        return !nul(eval(caar(c), a)) ? eval(cadar(c), a) : evcon(cdr(c), a);
    }

    /**
     * <pre>
     * evlis[m;a]=[null[m]->NIL; T->cons[eval[car[m];a];evlis[cdr[m];a]]]
     */
    static Object evlis(Object m, Object a) {
        // System.out.println("evlis(" + m + ", " + a + ")");
        return nul(m) ? NIL : cons(eval(car(m), a), evlis(cdr(m), a));
    }

    /**
     * <pre>
     * evalquote[fn;x] = apply[fn;x:NIL]
     * </pre>
     */
    public static Object evalquote(Object fn, Object x) {
        return apply(fn, x, NIL);
    }

    /**
     * <pre>
     * apply[fn;x;a]=
     *  [atom[fn]->[eq[fn;CAR]->caar[x];
     *              eq[fn;CDR]->cdar[x];
     *              eq[fn;CONS]->cons[car[x];cadr[x]];
     *              eq[fn;ATOM]->atom[car[x]];
     *              eq[fn;EQ]->eq[car[x];cadr[x]];
     *              T->apply[eval[fn;a];x;a]];
     *  eq[car[fn];LAMBDA]->eval[caddr[fn];pairlis[cadr[fn];x;a]];
     *  eq[car[fn];LABEL]->apply[caddr[fn];x;cons[cons[cadr[fn];caddr[fn]];a]]]
     * </pre>
     */
    public static Object apply(Object fn, Object x, Object a) {
        if (atom(fn))
            if (eq(fn, "car"))
                return caar(x);
            else if (eq(fn, "cdr"))
                return cdar(x);
            else if (eq(fn, "cons"))
                return cons(car(x), cadr(x));
            else if (eq(fn, "atom"))
                return atom(car(x)) ? T : NIL;  // booleanをオブジェクトに変換
            else if (eq(fn, "eq"))
                return eq(car(x), cadr(x)) ? T : NIL;  // booleanをオブジェクトに変換
            else
                return apply(eval(fn, a), x, a);
        else if (eq(car(fn), "lambda"))
            return eval(caddr(fn), pairlis(cadr(fn), x, a));
        else if (eq(car(fn), "label"))
            // 点対がないのでcons(cadr(fn), caddr(fn))をlist(cadr(fn), caddr(fn))に変更
            return apply(caddr(fn), x, cons(list(cadr(fn), caddr(fn)), a)); // cons->list
        else
            throw new RuntimeException("cannot apply " + fn + " to " + x + " in " + a);
    }

    /**
     * <pre>
     *  eval[e:a]=[atom[e]->cdr[assoc[e;a]];
     *      atom[car[e]]->
     *          [eq[car[e],QUOTE]->cadr[e];
     *           eq[car[e];COND]->evcon[cdr[e];a];
     *           T->apply[car[e];evlis[cdr[e];a];a]];
     *      T->apply[car[e];evlis[cdr[e];a];a]]
     * </pre>
     */
    public static Object eval(Object e, Object a) {
        if (atom(e))
            return cadr(assoc(e, a)); // assocは点対ではないリストを返すので、cdr->cadr
        else if (atom(car(e)))
            if (eq(car(e), "quote"))
                return cadr(e);
            else if (eq(car(e), "cond"))
                return evcon(cdr(e), a);
            else
                return apply(car(e), evlis(cdr(e), a), a);
        else
            return apply(car(e), evlis(cdr(e), a), a);
    }

    public static class LispReader {
        final Reader reader;
        int ch;

        public LispReader(Reader reader) {
            this.reader = reader;
            this.ch = get();
        }

        public LispReader(String source) {
            this(new StringReader(source));
        }

        int get() {
            try {
                return ch = reader.read();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        void spaces() {
            while (Character.isWhitespace(ch))
                get();
        }

        static boolean isSymbolFirst(int ch) {
            switch (ch) {
            case '!': case '#': case '$': case '%': case '&':
            case '-': case '+': case '*': case '/': case '^':
            case '=': case '<': case '>': case '_': case '@':
            case '?': case ';': case ':':
                return true;
            default:
                return Character.isLetter(ch);
            }
        }

        static boolean isSymbolRest(int ch) {
            return isSymbolFirst(ch) || Character.isDigit(ch);
        }

        List<?> paren() {
            List<Object> list = new ArrayList<>();
            while (ch != -1 && ch != ')') {
                list.add(read());
                spaces();
            }
            if (ch != ')')
                throw new RuntimeException("')' expected");
            get();
            return list;
        }

        String symbol() {
            StringBuilder sb = new StringBuilder();
            do {
                sb.append((char) ch);
                get();
            } while (isSymbolRest(ch));
            return sb.toString();
        }

        public Object read() {
            spaces();
            if (ch == -1) {
                return null;
            } else if (ch == '(') {
                get();
                return paren();
            } else if (ch == '\'') {
                get();
                return list("quote", read());
            } else if (isSymbolFirst(ch))
                return symbol();
            else
                throw new RuntimeException("unknown char: '" + (char) ch + "'");
        }
    }

    static Object read(String source) {
        return new LispReader(source).read();
    }

    static String print(Object obj) {
        if (obj instanceof List<?> list) {
            if (list.isEmpty())
                return "()";
            if (list.size() >= 2 && list.get(0).equals("quote"))
                return "'" + print(cadr(obj));
            StringBuilder sb = new StringBuilder("(");
            String sep = "";
            for (Object e : list) {
                sb.append(sep).append(print(e));
                sep = " ";
            }
            sb.append(")");
            return sb.toString();
        } else
            return Objects.toString(obj);
    }

    @Test
    public void testList() {
        assertEquals(List.of(1, List.of(2, 3), 4), list(1, list(2, 3), 4));
        assertEquals(1, car(list(1, 2, 3, 4)));
        assertEquals(List.of(2, 3, 4), cdr(list(1, 2, 3, 4)));
        assertEquals(List.of(1, 2, 3, 4), cons(1, list(2, 3, 4)));
        assertTrue(atom("abc"));
        assertFalse(atom(list("abc", 1)));
        assertTrue(nul(list()));
        assertFalse(nul(list(1)));
        assertFalse(nul("abc"));
        assertFalse(nul(null));
    }

    @Test
    public void testEvalquote() {
        assertEquals("a", evalquote("car", list(list("a", "b"))));
    }

    @Test
    public void testApplyCar() {
        // (car '(a b)) -> a
        assertEquals("a", eval(list("car", list("quote", list("a", "b"))), NIL));
    }

    @Test
    public void testApplyCdr() {
        // (cdr '(a b)) -> (b)
        assertEquals(list("b"), eval(list("cdr", list("quote", list("a", "b"))), NIL));
    }

    @Test
    public void testApplyCons() {
        // (cons 'a nil) in ((nil ())) -> (a)
        assertEquals(list("a"),
            eval(list("cons", list("quote", "a"), "nil"), list(list("nil", NIL))));
    }

    @Test
    public void testApplyAtom() {
        // (atom '(a b)) -> ()
        assertEquals(NIL, eval(list("atom", list("quote", list("a", "b"))), NIL));
        // (atom 'a) -> T
        assertEquals(T, eval(list("atom", list("quote", "a")), NIL));
    }

    @Test
    public void testApplyEq() {
        // (eq 'a 'a) -> T
        assertEquals(T, eval(list("eq", list("quote", "a"), list("quote", "a")), NIL));
        // (eq 'a 'b) -> ()
        assertEquals(NIL, eval(list("eq", list("quote", "a"), list("quote", "b")), NIL));
        // (eq '(a b) '(a b)) -> T
        assertEquals(T,
            eval(list("eq", list("quote", list("a", "b")), list("quote", list("a", "b"))), NIL));
        // (eq 'a '(a b)) -> ()
        assertEquals(NIL, eval(list("eq", list("quote", "a"), list("quote", list("a", "b"))), NIL));
    }

    @Test
    public void testApplyEvalFn() {
        // (kar '(a b)) in ((kar car)) -> a
        assertEquals("a",
            eval(list("kar", list("quote", list("a", "b"))), list(list("kar", "car"))));
    }

    @Test
    public void testApplyLambda() {
        // ((lambda (x) (car x)) '(a b)) -> a
        assertEquals("a", eval(
            list(list("lambda", list("x"), list("car", "x")), list("quote", list("a", "b"))), NIL));
        // ((lambda (x y) (cons x (cons y '())) 'a 'b) -> (a b)
        assertEquals(list("a", "b"),
            eval(list(
                list("lambda", list("x", "y"),
                    list("cons", "x", list("cons", "y", list("quote", NIL)))),
                list("quote", "a"), list("quote", "b")), NIL));
    }

    @Test
    public void testApplyLabel() {
        // ((label ff (lambda (x) (cond ((atom x) x) (T (ff (car x)))))) '((a b)
        // c)) -> a
        assertEquals("a", eval(list(list("label", "ff",
            list("lambda", list("x"),
                list("cond",
                    list(list("atom", "x"), "x"),
                    list(T, list("ff", list("car", "x")))))),
            list("quote", list(list("a", "b"), "c"))), list(list(T, T))));
    }

    @Test
    public void testApplyError() {
        try {
            eval(list(list("unknown")), NIL);
            fail();
        } catch (RuntimeException e) {
            assertEquals("cannot apply [unknown] to [] in []", e.getMessage());
        }
    }

    @Test
    public void testEvalVariable() {
        // a in ((a 123)) -> 123
        assertEquals(123, eval("a", list(list("a", 123))));
        // T in ((T T)) -> T
        assertEquals(T, eval(T, list(list(T, T))));
    }

    @Test
    public void testEvalQuote() {
        // (quote a) -> a
        assertEquals("a", eval(list("quote", "a"), NIL));
        // (quite (a b)) -> (a b)
        assertEquals(list("a", "b"), eval(list("quote", list("a", "b")), NIL));
    }

    @Test
    public void testEvalCond() {
        // (cond (T a)) in ((T T) (a 123))
        assertEquals(123, eval(list("cond", list(T, "a")), list(list(T, T), list("a", 123))));
        // (cond ((atom '(a)) T) (T b)) in ((T T) (a 321) (b 123)) -> 123
        assertEquals(123,
            eval(list("cond", list(list("atom", list("quote", list("a"))), T), list(T, "b")),
                list(list("nil", list()), list(T, T), list("a", 321), list("b", 123))));
        // assertEquals(NIL, eval(list("cond"), NIL));
    }

    @Test
    public void testReader() {
        LispReader reader = new LispReader("abc (a b (c d) e) 'x001 '(a b) +!#$%&=-@+*;:<>/_?");
        assertEquals("abc", reader.read());
        assertEquals(list("a", "b", list("c", "d"), "e"), reader.read());
        assertEquals(list("quote", "x001"), reader.read());
        assertEquals(list("quote", list("a", "b")), reader.read());
        assertEquals("+!#$%&=-@+*;:<>/_?", reader.read());
        assertEquals(null, reader.read());
    }

    @Test
    public void testReaderError() {
        try {
            LispReader reader = new LispReader("(a");
            reader.read();
            fail();
        } catch (RuntimeException e) {
            assertEquals("')' expected", e.getMessage());
        }
        try {
            LispReader reader = new LispReader("123");
            reader.read();
            fail();
        } catch (RuntimeException e) {
            assertEquals("unknown char: '1'", e.getMessage());
        }
    }

    @Test
    public void testPrint() {
        assertEquals("null", print(null));
        assertEquals("()", print(list()));
        assertEquals("a", print("a"));
        assertEquals("(a b)", print(list("a", "b")));
        assertEquals("'a", print(list("quote", "a")));
        assertEquals("'(a b)", print(list("quote", list("a", "b"))));
        assertEquals("quote", print("quote"));
        assertEquals("(quote)", print(list("quote")));
        assertEquals("(x '(a b) y)", print(list("x", list("quote", list("a", "b")), "y")));
    }

    static Object define(String... values) {
        List<Object> result = new ArrayList<>();
        for (int i = 0, max = values.length; i < max; i += 2)
            result.add(List.of(values[i], read(values[i + 1])));
        return result;
    }

    @Test
    public void testAppend() {
        Object env = define(
            "append", "(lambda (x y) (cond (x (cons (car x) (append (cdr x) y))) (T y)))",
            "T",  "T"
        );
        assertEquals(read("(a b c d e)"), eval(read("(append '() '(a b c d e))"), env));
        assertEquals(read("(a b c d e)"), eval(read("(append '(a) '(b c d e))"), env));
        assertEquals(read("(a b c d e)"), eval(read("(append '(a b) '(c d e))"), env));
        assertEquals(read("(a b c d e)"), eval(read("(append '(a b c) '(d e))"), env));
        assertEquals(read("(a b c d e)"), eval(read("(append '(a b c d) '(e))"), env));
        assertEquals(read("(a b c d e)"), eval(read("(append '(a b c d e) '())"), env));
    }

    @Test
    public void testAppendWithNull() {
        Object env = define(
            "append", "(lambda (x y) (cond ((null x) y) (T (cons (car x) (append (cdr x) y)))))",
            "null", "(lambda (x) (eq x '()))",
            "T", "T"
        );
        assertEquals(read("(a b c d e)"), eval(read("(append '() '(a b c d e))"), env));
        assertEquals(read("(a b c d e)"), eval(read("(append '(a) '(b c d e))"), env));
        assertEquals(read("(a b c d e)"), eval(read("(append '(a b) '(c d e))"), env));
        assertEquals(read("(a b c d e)"), eval(read("(append '(a b c) '(d e))"), env));
        assertEquals(read("(a b c d e)"), eval(read("(append '(a b c d) '(e))"), env));
        assertEquals(read("(a b c d e)"), eval(read("(append '(a b c d e) '())"), env));
    }
}
