package puzzle;

import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regexes {

    private Regexes() {
    }

    public static String replaceAll(String source, String pattern, Function<MatchResult, String> mapper) {
        return replaceAll(source, Pattern.compile(pattern), mapper);
    }

    public static String replaceAll(String source, Pattern pattern, Function<MatchResult, String> mapper) {
        StringBuilder sb = new StringBuilder();
        Matcher matcher = pattern.matcher(source);
        while (matcher.find())
            matcher.appendReplacement(sb, mapper.apply(matcher));
        matcher.appendTail(sb);
        return sb.toString();
    }
}