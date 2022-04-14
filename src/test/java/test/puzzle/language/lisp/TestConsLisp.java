package test.puzzle.language.lisp;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.junit.Test;

public class TestConsLisp {

    public static final Object NIL = "nil";
    public static final Object T = "T";

    public static class Cons {
        final Object car, cdr;
        Cons(Object car, Object cdr) {
            this.car = car;
            this.cdr = cdr;
        }
        @Override
        public int hashCode() {
            return Objects.hash(car, cdr);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Cons other = (Cons) obj;
            return other.car.equals(car) && other.cdr.equals(cdr);
        }

    }

    public static Object cons(Object car, Object cdr) {
        return new Cons(car, cdr);
    }

    public static Object list(Object... es) {
        Object r = NIL;
        for (int i = es.length - 1; i >= 0; --i)
            r = cons(es[i], r);
        return r;
    }

    public static Object list(List<Object> list) {
        Object r = NIL;
        for (int i = list.size() - 1; i >= 0; --i)
            r = cons(list.get(i), r);
        return r;
    }

    public static Object car(Object e) {
        if (e instanceof Cons cons)
            return cons.car;
        throw new RuntimeException("cant car : " + print(e));
    }

    public static Object cdr(Object e) {
        if (e instanceof Cons cons)
            return cons.cdr;
        throw new RuntimeException("cant cdr : " + print(e));
    }

    public static boolean eq(Object a, Object b) {
        return a.equals(b);
    }

    public static boolean equal(Object a, Object b) {
        return a.equals(b);
    }

    public static boolean nul(Object a) {
        return a.equals(NIL);
    }

    public static boolean atom(Object a) {
        return !(a instanceof Cons);
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

    public static Object cddr(Object e) {
        return cdr(cdr(e));
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
     * <pre>
     */
    static Object pairlis(Object x, Object y, Object a) {
        return nul(x) ? a : cons(cons(car(x), car(y)), pairlis(cdr(x), cdr(y), a));
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
            else if (eq(fn, "null"))
                return nul(car(x)) ? T : NIL;  // booleanをオブジェクトに変換
            else
                return apply(eval(fn, a), x, a);
        else if (eq(car(fn), "lambda"))
            return eval(caddr(fn), pairlis(cadr(fn), x, a));
        else if (eq(car(fn), "label"))
            return apply(caddr(fn), x, cons(cons(cadr(fn), caddr(fn)), a));
        else
            throw new RuntimeException("cannot apply "
                + print(fn) + " to " + print(x) + " in " + print(a));
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
            if (e.equals("nil"))
                return NIL;
            else if (e.equals("T"))
                return T;
            else
                return cdr(assoc(e, a));
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

        Object paren() {
            List<Object> list = new ArrayList<>();
            while (ch != -1 && ch != ')') {
                list.add(read());
                spaces();
            }
            if (ch != ')')
                throw new RuntimeException("')' expected");
            get();
            return list(list);
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
        if (nul(obj))
            return "()";
        if (obj instanceof Cons cons) {
            if (!atom(cdr(cons)) && eq(cddr(cons), NIL) && eq(cons.car, "quote"))
                return "'" + print(cadr(obj));
            StringBuilder sb = new StringBuilder("(");
            String sep = "";
            Object c = cons;
            for ( ; c instanceof Cons; c = cdr(c), sep = " ")
                sb.append(sep).append(print(car(c)));
            if (!nul(c))
                sb.append(" . ").append(print(c));
            sb.append(")");
            return sb.toString();
        } else
            return Objects.toString(obj);
    }

    @Test
    public void testCar() {
        assertEquals("a", car(cons("a", "b")));
        assertEquals("a", car(list("a", "b")));
    }

    @Test
    public void testCdr() {
        assertEquals("b", cdr(cons("a", "b")));
        assertEquals(list("b"), cdr(list("a", "b")));
    }

    @Test
    public void testAtom() {
        assertTrue(atom("a"));
        assertTrue(atom(123));
        assertTrue(atom(NIL));
        assertFalse(atom(cons("a", NIL)));
        assertFalse(atom(list("a", "b", "c")));
    }

    @Test
    public void testPrint() {
        assertEquals("a", print("a"));
        assertEquals("(a b c)", print(list("a", "b", "c")));
        assertEquals("(a b c)", print(cons("a", cons("b", cons("c", NIL)))));
        assertEquals("(a b c . d)", print(cons("a", cons("b", cons("c", "d")))));
        assertEquals("'(b c)", print(list("quote", list("b", "c"))));
        assertEquals("(quote)", print(list("quote")));
        assertEquals("(quote a . c)", print(cons("quote", cons("a", "c"))));
        assertEquals("'b", print(list("quote", "b")));
    }

    static Object env(String... x) {
        Object r = NIL;
        for (int i = 0, max = x.length; i < max; i += 2)
            r = cons(cons(read(x[i]), read(x[i + 1])), r);
        return r;
    }

    @Test
    public void testEval() {
        assertEquals("A", eval(read("a"), env("a", "A")));
        assertEquals("a", eval(read("'a"), env("a", "A")));
        assertEquals("a", eval(read("(car '(a b))"), NIL));
        assertEquals(read("a"), eval(read("(car '(a b))"), NIL));
        assertEquals(read("(b)"), eval(read("(cdr '(a b))"), NIL));
        assertEquals(read("T"), eval(read("(atom 'a)"), NIL));
        assertEquals(read("nil"), eval(read("(atom '(a b))"), NIL));
        assertEquals(read("T"), eval(read("(eq 'a 'a)"), NIL));
        assertEquals(read("nil"), eval(read("(eq 'a 'b)"), NIL));
        assertEquals(read("T"), eval(read("(eq '(a b) '(a b))"), NIL));
        assertEquals(read("nil"), eval(read("(eq '(a b) '(a x))"), NIL));
        assertEquals(read("T"), eval(read("(null nil)"), NIL));
        assertEquals(read("nil"), eval(read("(null 'a)"), NIL));
        assertEquals(read("nil"), eval(read("(null T)"), NIL));
        assertEquals(read("a"), eval(read("((lambda (x) (car x)) '(a b))"), NIL));
        assertEquals(read("a"), eval(read("((label foo (lambda (x) (car x))) '(a b))"), NIL));
        assertEquals(read("(a b c d)"),
            eval(read(
                "((label append"
                + "  (lambda (a b)"
                + "    (cond"
                + "      ((null a) b)"
                + "      (T (cons (car a) (append (cdr a) b)))  )))"
                + " '(a b) '(c d))"),
                NIL));
    }

}
