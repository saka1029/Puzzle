package test.puzzle.language;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestTokenizer {
    
    public static class TokenizerException extends Exception {
        private static final long serialVersionUID = 1L;
        public TokenizerException(String format, Object... args) {
            super(format.formatted(args));
        }
    }
    
    public static class Tokenizer {
        
        record Token(int type, int start, int end) {}

        static class FixedSizeQue<E> {
            
            private final Object[] elements;
            private int last = 0, size = 0;
            
            public FixedSizeQue(int capacity) {
                this.elements = new Object[capacity];
            }
            
            public int size() {
                return size;
            }
            
            public void add(E element) {
                elements[last] = element;
                if (++last >= elements.length)
                    last = 0;
                if (size < elements.length)
                    ++size;
            }
            
            @SuppressWarnings("unchecked")
            public E get(int index) {
                if (index < 0 || index >= size)
                    throw new IndexOutOfBoundsException("index");
                int pos = (last - index - 1 + elements.length) % elements.length;
                return (E)elements[pos];
            }
        }

        public static final int TOKEN_BASE = 1 << 21;
        public static final int TOKEN_END = TOKEN_BASE + 1;
        public static final int TOKEN_NUMBER = TOKEN_BASE + 2;
        public static final int TOKEN_ID = TOKEN_BASE + 3;

        public final String source;

        int index = 0, prevIndex = 0;
        public int ch;
        final FixedSizeQue<Token> tokens;
        
        public Tokenizer(String source, int lookAhead) throws TokenizerException {
            this.source = source;
            this.tokens = new FixedSizeQue<>(lookAhead);
            getCh();  // 1文字先読み
            get();
        }
        
        int getCh() {
            if (index < source.length()) {
                ch = source.codePointAt(index);
                prevIndex = index;
                index += Character.isSupplementaryCodePoint(ch) ? 2 : 1;
            } else
                ch = -1;
            return ch;
        }
        
        public int type(int i) {
            return tokens.get(i).type;
        }
        
        public int type() {
            return type(0);
        }

        public String token(int i) {
            Token token = tokens.get(i);
            return source.substring(token.start, token.end);
        }
        
        public String token() {
            return token(0);
        }
        
        public void spaces() {
            while (Character.isWhitespace(ch))
                getCh();
        }
        
        boolean eat(int expected) {
            spaces();
            if (ch == expected) {
                getCh();
                return true;
            }
            return false;
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
        
        public boolean isIdFirst(int ch) {
            return Character.isJavaIdentifierStart(ch);
        }
        
        public boolean isIdRest(int ch) {
            return Character.isJavaIdentifierPart(ch);
        }
        
        void integer() {
            while (isDigit(ch))
                getCh();
        }
        
        public int get() throws TokenizerException {
            spaces();
            int type, start = prevIndex;
            if (ch == -1) {
                type = TOKEN_END;
            } else if (isOneCharToken(ch)) {
                type = ch;
                getCh();
            } else if (isIdFirst(ch)) {
                type = TOKEN_ID;
                do {
                    getCh();
                } while (isIdRest(ch));
            } else if (isDigit(ch)) {
                integer();
                if (eat('.'))
                    integer();
                if (eat('e') || eat('E')) {
                    eat('-');
                    integer();
                }
                type = TOKEN_NUMBER;
            } else
                throw new TokenizerException("unknown char '%c'", ch);
            tokens.add(new Token(type, start, index));
            return type;
        }
    }
    
    @Test
    public void testFixedSizeQue() {
        Tokenizer.FixedSizeQue<Integer> que = new Tokenizer.FixedSizeQue<>(3);
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
    public void testGet() throws TokenizerException {
        Tokenizer t = new Tokenizer("a𩸽b", 1);
        assertEquals(Tokenizer.TOKEN_ID, t.type());
        assertEquals("a𩸽b", t.token());
        Tokenizer u = new Tokenizer("   a𩸽b", 1);
        assertEquals(Tokenizer.TOKEN_ID, u.type());
        assertEquals("a𩸽b", u.token());
    }
    
    @Test
    public void testTokenizerConstant() {
        assertTrue(Tokenizer.TOKEN_NUMBER > (1 << 21));
    }

}
