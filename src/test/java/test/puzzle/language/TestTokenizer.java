package test.puzzle.language;

import static org.junit.Assert.assertEquals;

import java.util.Objects;

import org.junit.Test;

public class TestTokenizer {
    
    public static class Tokenizer {

        public final String source;

        int index;
        public int ch;
        
        public Tokenizer(String source) {
            this.source = source;
            getCh();  // 1文字先読み
        }
        
        public int getCh() {
            if (index < source.length()) {
                ch = source.codePointAt(index);
                index += Character.isSupplementaryCodePoint(ch) ? 2 : 1;
            } else
                ch = -1;
            return ch;
        }
    }
    
    @Test
    public void testGet() {
        Tokenizer t = new Tokenizer("a𩸽b");
        assertEquals('a', t.ch); t.getCh();
        assertEquals("𩸽".codePointAt(0), t.ch); t.getCh();
        assertEquals('b', t.ch); t.getCh();
        assertEquals(-1, t.ch); t.getCh();
    }
    

}
