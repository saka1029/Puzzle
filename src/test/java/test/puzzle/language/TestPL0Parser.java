package test.puzzle.language;

import static org.junit.Assert.*;

import java.util.List;

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

    static class Memory {
        final byte[] memory;
        int pointer = 0;

        Memory(byte[] memory) {
            this.memory = memory;
        }

        Memory(int size) {
            this.memory = new byte[size];
        }
        
        void set(int address, int value) {
            memory[address] = (byte)(value & 0xff);
        }
        
        void setInt(int address, int value) {
            set(address, value >> 8);
            set(address + 1, value);
        }
        
        void add(int instruction) {
            set(pointer++, instruction);
        }

        void add(int instruction, int level) {
            add(instruction);
            add(level);
        }

        void add(int instruction, int level, int operand) {
            add(instruction, level);
            setInt(pointer, operand);
            pointer += 2;
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
