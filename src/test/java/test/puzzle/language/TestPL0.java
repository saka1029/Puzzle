package test.puzzle.language;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

/**
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
 *               | expression ( '==' | '!=' | '<' | '<=' | '>' | '>=' ) expression
 * expression = [ '+' | '-' ] term { ( '+' | '-' ) term }
 * term       = factor { ( '*' | '/' ) factor }
 * factor     = ident | number | '(' expression ')'
 * </pre>
 */
class TestPL0 {

    enum InstType {
        lit, opr, lod, sto, cal, inc, jmp, jpc
    }

    enum SymbolType {
        constant, variable, proc
    }

    static class Instruction {
        final InstType fct;
        final int l;
        int a;

        Instruction(InstType fct, int l, int a) {
            this.fct = fct;
            this.l = l;
            this.a = a;
        }

        static final String[] OPR = {
            "return", "negate", "+", "-", "*", "/",
            "odd", "?",
            "=", "!=", "<", ">=", ">", "<=",
        };

        @Override
        public String toString() {
            return String.format("%s%s l=%d a=%d",
                fct, fct == InstType.opr ? " " + OPR[a] : "", l, a);
        }
    }

    static class Symbol {
        final String name;
        final SymbolType type;
        int level, adr;

        Symbol(String name, SymbolType type, int level, int adr) {
            this.name = name;
            this.type = type;
            this.level = level;
            this.adr = adr;
        }

        @Override
        public String toString() {
            return String.format("Symbol(%s:%s, level=%d, adr=%d)",
                name, type, level, adr);
        }
    }

    public static class CompileError extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public CompileError(String message) {
            super(message);
        }
    }

    static Pattern IDENT = Pattern.compile("^[a-z][a-z0-9]*", Pattern.CASE_INSENSITIVE);

    static boolean isIdentStart(int ch) {
        return ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z';
    }

    static Pattern NUMBER = Pattern.compile("^\\d+", Pattern.CASE_INSENSITIVE);

    static boolean isNumber(int ch) {
        return ch >= '0' && ch <= '9';
    }

    public static List<Instruction> compile(String source) {
        return new Object() {
            int index = 0;
            String token;
            Deque<Symbol> symbols = new ArrayDeque<>();
            List<Instruction> codes = new ArrayList<>();

            String error() {
                int range = 20;
                return source.substring(Math.max(index - range, 0), index)
                    + "◆" + source.substring(index, Math.min(index + range, source.length()));
            }

            CompileError error(String format, Object... args) {
                return new CompileError(String.format(format, args) + " at " + error());
            }

            int ch() {
                return index < source.length() ? source.charAt(index) : -1;
            }

            void spaces() {
                while (index < source.length() && Character.isWhitespace(source.charAt(index)))
                    ++index;
            }

            boolean match(String... expects) {
                spaces();
                for (String e : expects)
                    if (source.startsWith(e, index)) {
                        token = e;
                        index += token.length();
                        return true;
                    }
                return false;
            }

            boolean match(Pattern expect) {
                spaces();
                java.util.regex.Matcher matcher = expect.matcher(source.substring(index));
                if (!matcher.find())
                    return false;
                token = matcher.group();
                index += token.length();
                return true;
            }

            String ident() {
                if (!match(IDENT))
                    throw error("ident expected");
                return token;
            }

            int number() {
                if (!match(NUMBER))
                    throw error("number expected");
                return Integer.parseInt(token);
            }

            void expect(String expect) {
                if (!match(expect))
                    throw error("'%s' expected", expect);
            }

            Symbol find(String name) {
                Iterable<Symbol> it = () -> symbols.descendingIterator();
                for (Symbol s : it)
                    if (s.name.equals(name))
                        return s;
                return null;
            }

            void block(Symbol symbol, int lev) {
                new Object() {
                    int dx = 3;
                    void factor() {
                        spaces();
                        if (isIdentStart(ch())) {
                            String name = ident();
                            Symbol s = find(name);
                            if (s == null)
                                throw error("symbol '%s' is not defined", name);
                            switch (s.type) {
                            case proc:
                                throw error("symbol '%s' is procedure", name);
                            case constant:
                                codes.add(new Instruction(InstType.lit, 0, s.adr));
                                break;
                            case variable:
                                codes.add(new Instruction(InstType.lod, lev - s.level, s.adr));
                                break;
                            }
                        } else if (isNumber(ch())) {
                            int value = number();
                            codes.add(new Instruction(InstType.lit, 0, value));
                        } else if (match("(")) {
                            expression();
                            expect(")");
                        } else
                            throw error("unknown factor");
                    }

                    void term() {
                        factor();
                        while (match("*", "/")) {
                            int adr = token.equals("*") ? 4 : 5;
                            factor();
                            codes.add(new Instruction(InstType.opr, 0, adr));
                        }
                    }

                    void expression() {
                        int sign = 1;
                        if (match("+", "-"))
                            sign = token.equals("+") ? 1 : -1;
                        term();
                        if (sign == -1)
                            codes.add(new Instruction(InstType.opr, 0, 1));
                        while (match("+", "-")) {
                            int adr = token.equals("+") ? 2 : 3;
                            term();
                            codes.add(new Instruction(InstType.opr, 0, adr));
                        }
                    }

                    void condition() {
                        if (match("odd")) {
                            expression();
                            codes.add(new Instruction(InstType.opr, 0, 6));
                        } else {
                            expression();
                            if (match("!=", "<=", ">=", "=", "<", ">")) {
                                int adr = switch (token) {
                                case "=" -> 8;
                                case "!=" -> 9;
                                case "<" -> 10;
                                case ">=" -> 11;
                                case ">" -> 12;
                                case "<=" -> 13;
                                default -> -1;
                                };
                                if (adr == -1)
                                    throw error("unknown compare operator '%s'", adr);
                                expression();
                                codes.add(new Instruction(InstType.opr, 0, adr));
                            }
                        }
                    }

                    void statement() {
                        if (match("call")) {
                            String name = ident();
                            Symbol s = find(name);
                            if (s == null)
                                throw error("symbol '%s' is not defined", name);
                            switch (s.type) {
                            case proc:
                                codes.add(new Instruction(InstType.cal, s.level, s.adr));
                                break;
                            case constant:
                                throw error("symbol '%s' is constant", name);
                            case variable:
                                throw error("symbol '%s' is variable", name);
                            }
                            // } else if (match("!")) {
                            // expression();
                        } else if (match("begin")) {
                            statement();
                            while (match(";"))
                                statement();
                            expect("end");
                        } else if (match("if")) {
                            condition();
                            expect("then");
                            int jpc = codes.size();
                            codes.add(new Instruction(InstType.jpc, 0, 0));
                            statement();
                            codes.get(jpc).a = codes.size();
                        } else if (match("while")) {
                            int cond = codes.size();
                            condition();
                            expect("do");
                            int body = codes.size();
                            codes.add(new Instruction(InstType.jpc, 0, 0));
                            statement();
                            codes.add(new Instruction(InstType.jmp, 0, cond));
                            codes.get(body).a = codes.size();
                        } else if (isIdentStart(ch())) {
                            String name = ident();
                            expect(":=");
                            expression();
                            Symbol s = find(name);
                            if (s == null)
                                throw error("symbol '%s' is not defined", name);
                            switch (s.type) {
                            case proc:
                                throw error("symbol '%s' is procedure", name);
                            case constant:
                                throw error("symbol '%s' is constant", name);
                            case variable:
                                codes.add(new Instruction(InstType.sto, lev - s.level, s.adr));
                                break;
                            }
                        } else
                            throw error("unknown statement");
                    }

                    void constDecl() {
                        String name = ident();
                        expect("=");
                        int value = number();
                        symbols.add(new Symbol(name, SymbolType.constant, 0, value));
                    }

                    void varDecl() {
                        String name = ident();
                        symbols.add(new Symbol(name, SymbolType.variable, lev, dx++));
                    }

                    void blockMain() {
                        if (match("const")) {
                            constDecl();
                            while (match(","))
                                constDecl();
                            expect(";");
                        }
                        if (match("var")) {
                            varDecl();
                            while (match(","))
                                varDecl();
                            expect(";");
                        }
                        while (match("procedure")) {
                            symbols.add(new Symbol(ident(), SymbolType.proc, lev, 0));
                            expect(";");
                            block(symbols.getLast(), lev + 1);
                            expect(";");
                        }
                        symbol.adr = codes.size(); // プロシジャーの先頭アドレス
                        codes.add(new Instruction(InstType.inc, 0, dx)); // int命令の生成
                        statement();
                        codes.add(new Instruction(InstType.opr, 0, 0)); // return命令の生成
                    }
                }.blockMain();
            }

            void program() {
                Symbol main = new Symbol("#main#", SymbolType.proc, 0, 0);
                symbols.add(main);
                // 先頭にメインプログラムへのjmp命令を生成します。
                Instruction jmp = new Instruction(InstType.jmp, 0, 0);
                codes.add(jmp);
                block(symbols.getLast(), 0);
                // メインプログラムの先頭アドレスをjmp命令のオペランドにセットします。
                jmp.a = main.adr;
                expect(".");
            }

            List<Instruction> parse() {
                program();
                for (Symbol e : symbols)
                    System.out.println(e);
                for (int i = 0, max = codes.size(); i < max; ++i)
                    System.out.println(i + ":" + codes.get(i));
                return codes;
            }
        }.parse();
    }

    public static void interpret(List<Instruction> codes) {
        new Object() {
            int[] stack = new int[500];
            int sp, pc, bp;

            int base(int l) {
                int b1 = bp;
                while (l > 0) {
                    b1 = stack[b1];
                    --l;
                }
                return b1;
            }

            String print() {
                StringBuilder sb = new StringBuilder();
                sb.append(pc).append(":");
                sb.append(codes.get(pc));
                sb.append(" pc=").append(pc);
                sb.append(" bp=").append(bp);
                sb.append(" sp=").append(sp);
                sb.append(" [");
                String sep = "";
                for (int i = 0; i < sp; ++i, sep = " ")
                    sb.append(sep).append(stack[i]);
                sb.append("]");
                return sb.toString();
            }

            void run() {
                System.out.println(" start pl/0");
                sp = 0;
                bp = 0;
                pc = 0;
                stack[0] = stack[1] = stack[2] = 0;
                do {
                    System.out.println(print());
                    Instruction i = codes.get(pc++);
                    switch (i.fct) {
                    case lit:
                        stack[sp++] = i.a;
                        break;
                    case opr:
                        switch (i.a) {
                        case 0:
                            sp = bp - 1;
                            pc = stack[sp + 3];
                            bp = stack[sp + 2];
                            break;
                        case 1:
                            stack[sp - 1] = -stack[sp - 1];
                            break;
                        case 2:
                            stack[--sp - 1] = stack[sp - 1] + stack[sp];
                            break;
                        case 3:
                            stack[--sp - 1] = stack[sp - 1] - stack[sp];
                            break;
                        case 4:
                            stack[--sp - 1] = stack[sp - 1] * stack[sp];
                            break;
                        case 5:
                            stack[--sp - 1] = stack[sp - 1] / stack[sp];
                            break;
                        case 6:
                            stack[sp - 1] &= 1;
                            break;
                        case 8:
                            stack[--sp - 1] = stack[sp - 1] == stack[sp] ? 1 : 0;
                            break;
                        case 9:
                            stack[--sp - 1] = stack[sp - 1] != stack[sp] ? 1 : 0;
                            break;
                        case 10:
                            stack[--sp - 1] = stack[sp - 1] < stack[sp] ? 1 : 0;
                            break;
                        case 11:
                            stack[--sp - 1] = stack[sp - 1] >= stack[sp] ? 1 : 0;
                            break;
                        case 12:
                            stack[--sp - 1] = stack[sp - 1] > stack[sp] ? 1 : 0;
                            break;
                        case 13:
                            stack[--sp - 1] = stack[sp - 1] <= stack[sp] ? 1 : 0;
                            break;
                        }
                        break;
                    case lod:
                        stack[sp++] = stack[base(i.l) + i.a];
                        break;
                    case sto:
                        System.out.println(stack[base(i.l) + i.a] = stack[--sp]);
                        break;
                    case cal:
                        stack[sp] = base(i.l);
                        stack[sp + 1] = bp;
                        stack[sp + 2] = pc;
                        bp = sp;
                        pc = i.a;
                        break;
                    case inc:
                        sp += i.a;
                        break;
                    case jmp:
                        pc = i.a;
                        break;
                    case jpc:
                        if (stack[--sp] == 0)
                            pc = i.a;
                        break;
                    }

                } while (pc > 0);
                System.out.println(" end pl/0");
            }
        }.run();
    }

    @Test
    public void testCompile() {
        String source = "const ZERO = 0, ONE = 1, TWO = 2, THREE = 3;\r\n"
            + "var x, y, z, ok;\r\n"
            + "\r\n"
            + "procedure addXandY;\r\n"
            // + " var x, y;\r\n"
            + "begin"
            + "    z := x + y\r\n"
            + "end;\r\n"
            + "begin\r\n"
            + "  ok := 0;\r\n"
            + "  x := ONE;\r\n"
            + "  y := TWO;\r\n"
            + "  call addXandY;\r\n"
            + "  if z = THREE then\r\n"
            + "      ok := -1\r\n"
            + "end.\r\n";
        List<Instruction> codes = compile(source);
        interpret(codes);
    }

    @Test
    public void testWhile() {
        String source = "const N = 10;\r\n"
            + "var i, s;\r\n"
            + "begin\r\n"
            + "  s := 0;\r\n"
            + "  i := 1;\r\n"
            + "  while i < N do begin\r\n"
            + "    s := s + i;"
            + "    i := i + 1\r\n"
            + "  end"
            + "end.";
        List<Instruction> codes = compile(source);
        interpret(codes);
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
