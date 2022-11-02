package test.puzzle.language;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestTokenizer {
    
    public static class FixedSizeQue<E> {
        
        private final Object[] elements;
        private int p = 0, size = 0;
        
        public FixedSizeQue(int capacity) {
            this.elements = new Object[capacity];
        }
        
        public int size() {
            return size;
        }
        
        public void add(E element) {
            elements[p] = element;
            if (++p >= elements.length)
                p = 0;
            if (size < elements.length)
                ++size;
        }
        
        /**
         * 
         * @param index 0は最後に追加された要素、1はその前に追加された要素を指定します。
         *              index < size()である必要があります。
         * @return indexで指定された要素を返します。
         */
        @SuppressWarnings("unchecked")
        public E get(int index) {
            if (index < 0 || index >= size)
                throw new IndexOutOfBoundsException("index");
            int pos = (p - index - 1 + elements.length) % elements.length;
            return (E)elements[pos];
        }
        
    }
    
    public static class TokenizerException extends Exception {
        private static final long serialVersionUID = 1L;
        public TokenizerException(String format, Object... args) {
            super(format.formatted(args));
        }
    }
    
    public static class Tokenizer {

        public static final int TOKEN_BASE = 1 << 21;
        public static final int TOKEN_NUMBER = TOKEN_BASE + 1;
        public static final int TOKEN_ID = TOKEN_BASE + 2;

        public final String source;

        int nextIndex = 0, index = 0;
        public int ch;
        
        public Tokenizer(String source) {
            this.source = source;
            getCh();  // 1文字先読み
        }
        
        public int getCh() {
            if (nextIndex < source.length()) {
                ch = source.codePointAt(nextIndex);
                index = nextIndex;
                nextIndex += Character.isSupplementaryCodePoint(ch) ? 2 : 1;
            } else
                ch = -1;
            return ch;
        }
        
        public void skipSpaces() {
            while (Character.isWhitespace(ch))
                getCh();
        }
        
        public boolean isOneCharToken(int ch) {
            switch (ch) {
            case '(': case ')':
            case '+': case '-':
            case '*': case '/':
            case '=':
                return true;
            default:
                return false;
            }
        }
        
        public boolean isDigit(int ch) {
            return ch >= '0' && ch <= '9';
        }
        
        public int get() throws TokenizerException {
            skipSpaces();
            if (isOneCharToken(ch)) {
                int type = ch;
                getCh();
                return type;
            } else if (isDigit(ch)) {
                
            }
            throw new TokenizerException("unknown char '%c'", ch);
        }
    }
    
    @Test
    public void testFixedSizeQue() {
        FixedSizeQue<Integer> que = new FixedSizeQue<>(3);
        que.add(1);
        assertEquals(1, que.size());
        que.add(2);
        assertEquals(2, que.size());
        que.add(3);
        assertEquals(3, que.size());
        assertEquals(3, (int)que.get(0));
        assertEquals(2, (int)que.get(1));
        assertEquals(1, (int)que.get(2));
        que.add(4);
        assertEquals(3, que.size());
        assertEquals(4, (int)que.get(0));
        assertEquals(3, (int)que.get(1));
        assertEquals(2, (int)que.get(2));
    }
    
    @Test
    public void testGet() {
        Tokenizer t = new Tokenizer("a𩸽b");
        assertEquals('a', t.ch); t.getCh();
        assertEquals("𩸽".codePointAt(0), t.ch); t.getCh();
        assertEquals('b', t.ch); t.getCh();
        assertEquals(-1, t.ch); t.getCh();
    }
    
    @Test
    public void testToken() {
        System.out.println(Tokenizer.TOKEN_BASE);
    }
    

}
