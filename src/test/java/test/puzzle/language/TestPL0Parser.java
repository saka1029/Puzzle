package test.puzzle.language;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class TestPL0Parser {

    
    /**
     * <pre>
     * 命令語 bit 構成
     * alxxxxxx
     * a bit = 0:アドレスあり 1:アドレスなし
     * l bit = 0:レベルあり 1:レベルなし
     * 4バイト命令 00xxxxxx AAAAAAAA AAAAAAAA LLLLLLLL
     * 3バイト命令 01xxxxxx AAAAAAAA AAAAAAAA
     * 1バイト命令 11xxxxxx
     * </pre>
     */
    static final int LOD = 0x00, STO = 0x01, CAL = 0x02;
    static final int LIT = 0x40, INC = 0x41, JMP = 0x42, JPC = 0x43;
    static final int
        OPRET = 0xC0, OPNEG = 0xC1, OPADD = 0xC2, OPSUB = 0xC3,
        OPMUL = 0xC4, OPDIV = 0xC5, OPODD = 0xC6, OPINP = 0xC7,
        OPOUT = 0xC8, OPCEQ = 0xC9, OPCNE = 0xCA, OPCLT = 0xCB,
        OPCLE = 0xCC, OPCGT = 0xCD, OPCGE = 0xCE;
    static final int ADDR_MASK = 0x80, LEVEL_MASK = 0x40;
    static final Map<Integer, String> OPNAME = Map.ofEntries(
        Map.entry(LOD, "lod"), Map.entry(STO, "sto"), Map.entry(CAL, "cal"),
        Map.entry(LIT, "lit"), Map.entry(INC, "inc"), Map.entry(JMP, "jmp"), Map.entry(JPC, "jpc"),
        Map.entry(OPRET, "ret"), Map.entry(OPNEG, "neg"), Map.entry(OPADD, "add"), Map.entry(OPSUB, "sub"),
        Map.entry(OPMUL, "mul"), Map.entry(OPDIV, "div"), Map.entry(OPODD, "odd"), Map.entry(OPINP, "inp"),
        Map.entry(OPOUT, "out"), Map.entry(OPCEQ, "ceq"), Map.entry(OPCNE, "cne"), Map.entry(OPCLT, "clt"),
        Map.entry(OPCLE, "cle"), Map.entry(OPCGT, "cgt"), Map.entry(OPCGE, "cge"));

    record Instrucion(int opcode, int address, int level) {
    }

    static class Memory {
        final byte[] memory;
        int pointer = 0;

        public Memory(byte[] memory) {
            this.memory = memory;
        }

        public Memory(int size) {
            this.memory = new byte[size];
        }
        
        public void set(int address, int value) {
            memory[address] = (byte)(value & 0xff);
        }
        
        public void setInt(int address, int value) {
            set(address, value >> 8);
            set(address + 1, value);
        }
        
        public void add(int opcode) {
            set(pointer++, opcode);
        }

        public void add(int opcode, int level) {
            add(opcode);
            add(level);
        }

        public void add(int opcode, int level, int operand) {
            add(opcode, level);
            setInt(pointer, operand);
            pointer += 2;
        }
        
        public byte[] toByteArray() {
            return Arrays.copyOfRange(memory, 0, pointer);
        }

    }

    static final List<String> RESERVED = List.of(
        "const", "var", "procedure", "call", "begin", "end", "if", "then", "while", "do", "odd");

    enum Type {
        RESERVED, IDENT, NUMBER, END;
    }

    /**
     * <pre>
     * program    = block "." .
     * block      = [ "const" ident "=" number {"," ident "=" number} ";"]
     *              [ "var" ident {"," ident} ";"]
     *              { "procedure" ident ";" block ";" }
     *              statement .
     * statement  = [ ident ":=" expression
     *              | "call" ident
     *              | "?" ident
     *              | "!" expression
     *              | "begin" statement {";" statement } "end"
     *              | "if" condition "then" statement
     *              | "while" condition "do" statement ].
     * condition  = "odd" expression
     *              | expression ("="|"#"|"<"|"<="|">"|">=") expression .
     * expression = [ "+"|"-"] term { ("+"|"-") term}.
     * term       = factor {("*"|"/") factor}.
     * factor     = ident | number | "(" expression ")".
     * </pre>
     */
    static void parse(String source) {
        new Object() {
            int length = source.length(), index = 0;
            Type type;
            String nextToken, token;

            RuntimeException error(String format, Object... args) {
                return new RuntimeException(format.formatted(args));
            }

            int ch() {
                return index < length ? source.charAt(index) : -1;
            }

            boolean isNumber(int ch) {
                return ch >= '0' && ch <= '9';
            }

            boolean isIdFirst(int ch) {
                return ch >= 'a' && ch <= 'z'
                    || ch >= 'A' && ch <= 'Z';
            }

            boolean isIdRest(int ch) {
                return isIdFirst(ch) || isNumber(ch);
            }

            void get() {
                while (Character.isWhitespace(ch()))
                    ++index;
                int start = index;
                switch (ch()) {
                    case -1:
                        type = Type.END;
                        nextToken = null;
                        break;
                    case '(':
                    case ')':
                    case ';':
                    case ',':
                    case '.':
                    case '?':
                    case '!':
                    case '=':
                    case '#':
                    case '+':
                    case '-':
                    case '*':
                    case '/':
                        type = Type.RESERVED;
                        nextToken = Character.toString(ch());
                        ++index;
                        break;
                    case '<':
                    case '>':
                    case ':':
                        char f = (char) ch();
                        ++index;
                        type = Type.RESERVED;
                        if (ch() == '=') {
                            nextToken = f + "=";
                            ++index;
                        } else
                            nextToken = Character.toString(f);
                        break;
                    default:
                        if (isIdFirst(ch())) {
                            do {
                                ++index;
                            } while (isIdRest(ch()));
                            nextToken = source.substring(start, index);
                            type = RESERVED.contains(nextToken.toLowerCase()) ? Type.RESERVED : Type.IDENT;
                        } else if (isNumber(ch())) {
                            do {
                                ++index;
                            } while (isNumber(ch()));
                            type = Type.NUMBER;
                            nextToken = source.substring(start, index);
                        }
                        break;
                }
            }

            boolean eat(String... expects) {
                for (String expect : expects)
                    if (nextToken.equalsIgnoreCase(expect)) {
                        token = nextToken;
                        get();
                        return true;
                    }
                return false;
            }

            boolean eat(Type expect) {
                if (expect == type) {
                    token = nextToken;
                    get();
                    return true;
                }
                return false;
            }

            void check(String message, String... expects) {
                if (!eat(expects))
                    throw error(message);
            }

            void check(String message, Type type) {
                if (!eat(type))
                    throw error(message);
            }

            void factor() {
                if (eat(Type.IDENT)) {
                } else if (eat(Type.NUMBER)) {
                } else if (eat("(")) {
                    expression();
                    check("')' expected", ")");
                }
            }

            void term() {
                factor();
                while (eat("*", "/"))
                    factor();
            }

            void expression() {
                if (eat("+", "-")) {
                }
                term();
                while (eat("+", "-"))
                    term();
            }

            void condition() {
                if (eat("odd")) {
                    expression();
                } else {
                    expression();
                    check("'=', '#', '<', '<=', '>', '>=' expected",
                        "=", "#", "<", "<=", ">", ">=");
                    expression();
                }
            }

            void statement() {
                if (eat(Type.IDENT)) {
                    check("':=' expected", ":=");
                    expression();
                } else if (eat("call")) {
                    check("ident expected", Type.IDENT);
                } else if (eat("?")) {
                    check("ident expected", Type.IDENT);
                } else if (eat("!")) {
                    expression();
                } else if (eat("begin")) {
                    do {
                        statement();
                    } while (eat(";"));
                    check("'end' expected", "end");
                } else if (eat("if")) {
                    condition();
                    check("'then' expected", "then");
                    statement();
                } else if (eat("while")) {
                    condition();
                    check("'do' expected", "do");
                    statement();
                } else if (eat("?")) {
                    check("ident expected", Type.IDENT);
                } else if (eat("!")) {
                    expression();
                }
            }

            void block() {
                if (eat("const")) {
                    do {
                        check("ident expected", Type.IDENT);
                        check("=", "'=' expected");
                        check("number expected", Type.NUMBER);
                    } while (eat(","));
                    check("';' expected", ";");
                }
                if (eat("var")) {
                    do {
                        check("ident expected", Type.IDENT);
                    } while (eat(","));
                    check("';' expected", ";");
                }
                while (eat("procedure")) {
                    check("ident expected", Type.IDENT);
                    check("';' expected", ";");
                    block();
                    check("';' expected", ";");
                }
                statement();
            }

            void program() {
                block();
                check("'.' expected", ".");
            }

            void parse() {
                get();
                program();
                check("extra tokens", Type.END);
            }
        }.parse();
    }

    @Test
    public void testParse() {
        String source = "VAR x, squ;\r\n"
            + "\r\n"
            + "PROCEDURE square;\r\n"
            + "BEGIN\r\n"
            + "   squ := x * x\r\n"
            + "END;\r\n"
            + "\r\n"
            + "BEGIN\r\n"
            + "   x := 1;\r\n"
            + "   WHILE x <= 10 DO\r\n"
            + "   BEGIN\r\n"
            + "      CALL square;\r\n"
            + "      ! squ;\r\n"
            + "      x := x + 1;\r\n"
            + "   END\r\n"
            + "END.";
        parse(source);
    }

}
