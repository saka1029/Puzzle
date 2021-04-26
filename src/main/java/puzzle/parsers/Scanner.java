package puzzle.parsers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scanner {

    final String source;
    public int index = 0;
    public String token;

    public Scanner(String source) {
        this.source = source;
    }

    public int top() {
        return index < source.length() ? source.charAt(index) : -1;
    }

    public boolean isSpace(int ch) {
        return Character.isWhitespace(ch);
    }

    public void spaces() {
        while (index < source.length() && isSpace(top()))
            ++index;
    }

    public boolean match(String... expects) {
        spaces();
        for (String expect : expects)
            if (source.startsWith(expect, index)) {
                token = expect;
                index += expect.length();
                return true;
            }
        return false;
    }

    public boolean match(Pattern expect) {
        spaces();
        Matcher matcher = expect.matcher(source.substring(index));
        if (!matcher.find())
            return false;
        token = matcher.group();
        index += token.length();
        return true;
    }

    public boolean isIdentifierStart(int ch) {
        return Character.isLetter(ch);
    }

    public boolean isIdentifierRest(int ch) {
        return Character.isLetterOrDigit(ch) || ch == '_';
    }

    public boolean identifier() {
        spaces();
        if (!isIdentifierStart(top()))
            return false;
        int start = index++;
        while (isIdentifierRest(top()))
            ++index;
        token = source.substring(start, index);
        return true;
    }

    public static final Pattern REAL_NUMBER = Pattern.compile("^[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?");

    public boolean realNumber() {
        return match(REAL_NUMBER);
    }

    RuntimeException error(String format, Object... args) {
        return new RuntimeException(String.format(format, args));
    }
}
