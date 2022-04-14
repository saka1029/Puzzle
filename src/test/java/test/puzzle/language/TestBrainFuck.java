package test.puzzle.language;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class TestBrainFuck {

    static Map<Integer, Integer> jumpTable(String code) {
        Deque<Integer> stack = new ArrayDeque<>();
        Map<Integer, Integer> table = new HashMap<>();
        for (int pc = 0, max = code.length(); pc < max; ++pc)
            switch (code.charAt(pc)) {
            case '[':
                stack.push(pc);
                break;
            case ']':
                int prev = stack.pop();
                table.put(prev, pc);
                table.put(pc, prev);
                break;
            }
        if (!stack.isEmpty())
            throw new RuntimeException("']'が足りません。");
        return table;
    }

    interface In {
        int get() throws IOException;
    }

    interface Out {
        void accept(int i) throws IOException;
    }

    static final String INSTRUCTIONS = "><+-.,[]";

    static void run(String code, int memorySize, In input, Out output, boolean debug) throws IOException {
        Map<Integer, Integer> table = jumpTable(code);
        byte[] memory = new byte[memorySize];
        int pointer = 0;
        for (int pc = 0, max = code.length(); pc < max; ++pc) {
            char instruction = code.charAt(pc);
            if (debug && INSTRUCTIONS.indexOf(instruction) >= 0)
                System.out.println("pc=" + pc + " instruction=" + instruction
                    + " pointer=" + pointer + " memory=" + Arrays.toString(memory));
            switch (instruction) {
            case '>':
                ++pointer;
                break;
            case '<':
                --pointer;
                break;
            case '+':
                ++memory[pointer];
                break;
            case '-':
                --memory[pointer];
                break;
            case '.':
                output.accept(memory[pointer]);
                break;
            case ',':
                memory[pointer] = (byte)input.get();
                break;
            case '[':
                if (memory[pointer] == 0)
                    pc = table.get(pc);
                break;
            case ']':
                if (memory[pointer] != 0)
                    pc = table.get(pc);
                break;
            }
        }
    }

    @Test
    public void testJumpTable() {
        assertEquals(Map.of(1, 5, 5, 1, 2, 4, 4, 2), jumpTable("+[[+]]++"));
        assertEquals(Map.of(1, 10, 10, 1, 2, 4, 4, 2, 6, 9, 9, 6), jumpTable("+[[+]+[++]]"));
    }

    @Test
    public void testRun() throws IOException {
        StringBuilder sb = new StringBuilder();
        run(">+++++++++[<++++++++>-]<.>+++++++[<++++>-]<+.+++++++..+++.[-]>++++++++[<++\n"
            + "++>-]<.>+++++++++++[<+++++>-]<.>++++++++[<+++>-]<.+++.------.--------.[-]>\n"
            + "++++++++[<++++>-]<+.[-]++++++++++.",
            10,
            () -> System.in.read(),
            c -> sb.append((char) (int) c), false);
        assertEquals("Hello World!\n", sb.toString());
    }

    /**
     * Brainfuck - Wikipedia
     * https://en.wikipedia.org/wiki/Brainfuck
     */
    @Test
    public void testRot13() throws IOException {
        StringReader in = new StringReader("Hello World!");
        StringBuilder out = new StringBuilder();
        String code = "-,+[                         Read first character and start outer character reading loop\n"
            + "    -[                       Skip forward if character is 0\n"
            + "        >>++++[>++++++++<-]  Set up divisor (32) for division loop\n"
            + "                               (MEMORY LAYOUT: dividend copy remainder divisor quotient zero zero)\n"
            + "        <+<-[                Set up dividend (x minus 1) and enter division loop\n"
            + "            >+>+>-[>>>]      Increase copy and remainder / reduce divisor / Normal case: skip forward\n"
            + "            <[[>+<-]>>+>]    Special case: move remainder back to divisor and increase quotient\n"
            + "            <<<<<-           Decrement dividend\n"
            + "        ]                    End division loop\n"
            + "    ]>>>[-]+                 End skip loop; zero former divisor and reuse space for a flag\n"
            + "    >--[-[<->+++[-]]]<[         Zero that flag unless quotient was 2 or 3; zero quotient; check flag\n"
            + "        ++++++++++++<[       If flag then set up divisor (13) for second division loop\n"
            + "                               (MEMORY LAYOUT: zero copy dividend divisor remainder quotient zero zero)\n"
            + "            >-[>+>>]         Reduce divisor; Normal case: increase remainder\n"
            + "            >[+[<+>-]>+>>]   Special case: increase remainder / move it back to divisor / increase quotient\n"
            + "            <<<<<-           Decrease dividend\n"
            + "        ]                    End division loop\n"
            + "        >>[<+>-]             Add remainder back to divisor to get a useful 13\n"
            + "        >[                   Skip forward if quotient was 0\n"
            + "            -[               Decrement quotient and skip forward if quotient was 1\n"
            + "                -<<[-]>>     Zero quotient and divisor if quotient was 2\n"
            + "            ]<<[<<->>-]>>    Zero divisor and subtract 13 from copy if quotient was 1\n"
            + "        ]<<[<<+>>-]          Zero divisor and add 13 to copy if quotient was 0\n"
            + "    ]                        End outer skip loop (jump to here if ((character minus 1)/32) was not 2 or 3)\n"
            + "    <[-]                     Clear remainder from first division if second division was skipped\n"
            + "    <.[-]                    Output ROT13ed character from copy and clear it\n"
            + "    <-,+                     Read next character\n"
            + "]                            End character reading loop";
        // encode
        run(code, 20, () -> in.read(), c -> out.append((char) (int)c), true);
        assertEquals("Uryyb Jbeyq!", out.toString());
        // decode
        StringReader in2 = new StringReader(out.toString());
        StringBuilder out2 = new StringBuilder();
        run(code, 20, () -> in2.read(), c -> out2.append((char) (int)c), false);
        assertEquals("Hello World!", out2.toString());
    }

}
