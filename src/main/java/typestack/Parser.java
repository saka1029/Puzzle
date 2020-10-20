package typestack;

import java.util.regex.Pattern;

public class Parser {

    final TypeStack typeStack;
    final String source;
    int index = 0;
    int ch = ' ';

    public Parser(TypeStack typeStack, String source) {
        this.typeStack = typeStack;
        this.source = source;
    }

    int getCh() {
        return ch = (index < source.length() ? source.charAt(index++) : -1);
    }

    static boolean isSpace(int ch) {
        return ch != -1 && Character.isWhitespace(ch);
    }

    static final Pattern INTEGER_PATTERN = Pattern.compile("[-+]?\\d+");

    void integer(String word, Block parent) {
        int value = Integer.valueOf(word);
        Definition loadInt = new Definition() {

            @Override
            public Executable executable() {
                return rc -> rc.push(value);
            }

            @Override
            public FunctionType type(FunctionType left) {
                return Type.functionType(Type.types(), Type.types(Type.INTEGER));
            }

        };
        parent.add(loadInt);
    }

    void word(String word, Block parent) {
        for (Definition d : typeStack.get(word)) {
            Type type = parent.type.composite(d.type(parent.type));
            if (type == null)
                continue;
            parent.add(d);
            break;
        }
    }

    void string(String string, Block parent) {
        Definition loadString = new Definition() {

            @Override
            public Executable executable() {
                return rc -> rc.push(string);
            }

            @Override
            public FunctionType type(FunctionType left) {
                return Type.functionType(Type.types(), Type.types(Type.STRING));
            }

        };
        parent.add(loadString);

    }

    void parseWord(Block parent) {
        StringBuilder sb = new StringBuilder();
        while (ch != -1 && ch != ']' && !isSpace(ch)) {
            sb.append((char)ch);
            getCh();
        }
        String word = sb.toString();
        if (INTEGER_PATTERN.matcher(word).matches())
            integer(word, parent);
        else
            word(word, parent);
    }

    void parseBlock(Block parent) {
        getCh();    // skip '['
        Block child = parse();
        if (ch != ']')
            throw new RuntimeException("']' expected.");
        getCh();    // skip ']'
        Definition loadBlock = new Definition() {

            @Override
            public Executable executable() {
                return rc -> rc.push(child);
            }

            @Override
            public FunctionType type(FunctionType left) {
                return Type.functionType(Type.types(), Type.types(child));
            }

        };
        parent.add(loadBlock);
    }

    void parseString(Block block) {
        getCh();    // skip '"'
        StringBuilder sb = new StringBuilder();
        while (ch != -1 && ch != '\"') {
            sb.append((char)ch);
            getCh();
        }
        if (ch != '\"')
            throw new RuntimeException("'\"' expected.");
        getCh();    // skip '"'
        string(sb.toString(), block);
    }

    public Block parse() {
        Block block = new Block();
        while (true) {
            while (isSpace(ch))
                getCh();
            switch (ch) {
            case -1: case ']':
                return block;
            case '[':
                parseBlock(block);
                break;
            case '\"':
                parseString(block);
            default:
                parseWord(block);
                break;
            }
        }
    }

}
