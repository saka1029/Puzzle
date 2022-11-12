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
    
    public static boolean isLeapYear(int year) {
        return year % 4 == 0 && year % 100 != 0 || year % 400 == 0;
    }

    /**
     * うるう年(YYYY)にマッチするパターン
     * 100で割り切れないうるう年    [0-9][0-9](0[48]|[2468][048]|[13579][26])
     * 400で割り切れる年            ([02468][048]|[13579][26])00
     */
    public static final String LEAP_YEARS =
        "([0-9][0-9](0[48]|[2468][048]|[13579][26])"        // year % 4 == 0 && year % 100 != 0
        + "|([02468][048]|[13579][26])00)";                 // year % 400 == 0

    /**
     * 年月(MM-DD)にマッチするパターン
     * ただし2月29日にはマッチしない。
     * 区切り文字を以下のようにして付与する必要がある。
     * MONTH_DAY.formatted(月日の区切り文字)
     */
    public static final String MONTH_DAY =
        "((0[1-9]|1[0-2])\\Q%1$s\\E(0[1-9]|1[0-9]|2[0-8])"  // すべての月は01から28日まである。
        + "|(0[13-9]|1[0-2])\\Q%1$s\\E(29|30)"              // 2月以外は29と30日がある。
        + "|(0[13578]|1[02])\\Q%1$s\\E31)";                 // 大の月は31日がある。
    
    /**
     * 年月(DD-MM)にマッチするパターン
     * ただし2月29日にはマッチしない。
     * 区切り文字を以下のようにして付与する必要がある。
     * DAY_MONTH.formatted(月日の区切り文字)
     */
    public static final String DAY_MONTH =
        "((0[1-9]|1[0-9]|2[0-8])\\Q%1$s\\E(0[1-9]|1[0-2])"  // すべての月は01から28日まである。
        + "|(29|30)\\Q%1$s\\E(0[13-9]|1[0-2])"              // 2月以外は29と30日がある。
        + "|31\\Q%1$s\\E(0[13578]|1[02]))";                 // 大の月は31日がある。
    
    /**
     * 年月日(YYYY-MM-DD)にマッチするパターン
     * 区切り文字を以下のようにして付与する必要がある。
     * YEAR_MONTH_DAY.formatted(月日の区切り文字)
     */
    public static final String YEAR_MONTH_DAY =
        "([0-9]{4}\\Q%1$s\\E" + MONTH_DAY                   // 2月29日以外のすべての日にマッチする。
        + "|" + LEAP_YEARS + "\\Q%1$s\\E02\\Q%1$s\\E29)";   // うるう年の2月29日にマッチする。
    
    /**
     * 年月日(DD-MM-YYYY)にマッチするパターン
     * 区切り文字を以下のようにして付与する必要がある。
     * DAY_MONTH_YEAR.formatted(月日の区切り文字)
     */
    public static final String DAY_MONTH_YEAR =
        "(" + DAY_MONTH + "\\Q%1$s\\E[0-9]{4}"              // 2月29日以外のすべての日にマッチする。
        + "|29\\Q%1$s\\E02\\Q%1$s\\E" + LEAP_YEARS + ")";   // うるう年の2月29日にマッチする。
}