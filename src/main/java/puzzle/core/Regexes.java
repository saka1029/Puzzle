package puzzle.core;

import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class Regexes {

    private Regexes() {
    }

    public static String replaceAll(String source, String pattern, Function<MatchResult, String> mapper) {
        return replaceAll(source, Pattern.compile(pattern), mapper);
    }

    public static String replaceAll(String source, Pattern pattern, Function<MatchResult, String> mapper) {
        return pattern.matcher(source).replaceAll(mapper);
//        StringBuilder sb = new StringBuilder();
//        Matcher matcher = pattern.matcher(source);
//        while (matcher.find())
//            matcher.appendReplacement(sb, mapper.apply(matcher));
//        matcher.appendTail(sb);
//        return sb.toString();
    }
    
    static String NormalYearString(String separator, boolean isLeap) {
        String p = Pattern.quote(separator);
        return 
            "(0[1-9]|1[0-2])" + p + "(0[1-9]|1[0-9]|2[0-8])"  // すべての月は01から28日まである。
            + "|(0[13-9]|1[0-2])" + p + "(29|30)"             // 2月以外は29と30日がある。
            + "|(0[13578]|1[02])" + p + "30"                  // 大の月は31日がある。
            + (isLeap ? "|02" + p + "29" : "");
    }

    public static Pattern MMDD(String separator, boolean isLeap) {
        return Pattern.compile(NormalYearString(separator, isLeap));
    }

}