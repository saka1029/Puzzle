package test.puzzle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

class TestEmojiReverse {

    static String reverse0(String s) {
        return new StringBuilder(s).reverse().toString();
    }

    @Test
    void testReverse0() {
        System.out.println(reverse0("Hello world👩‍🦰👩‍👩‍👦‍👦 🏴󠁧󠁢󠁷󠁬󠁳󠁿"));
    }

    static void reverse(int[] ints) {
        for (int i = 0, j = ints.length - 1; i < j; ++i, --j) {
            int temp = ints[i];
            ints[i] = ints[j];
            ints[j] = temp;
        }
    }

    static String reverse1(String s) {
        int[] codePoints = s.codePoints().toArray();
        reverse(codePoints);
        return new String(codePoints, 0, codePoints.length);
    }

    @Test
    void testReverse1() {
        System.out.println(reverse0("Hello world👩‍🦰👩‍👩‍👦‍👦 🏴󠁧󠁢󠁷󠁬󠁳󠁿"));
    }

    @Test
    void testPrintHex() {
        "Hello world👩‍🦰👩‍👩‍👦‍👦 🏴󠁧󠁢󠁷󠁬󠁳󠁿".codePoints()
            .forEach(c -> System.out.print(Integer.toHexString(c) + " "));
        System.out.println();
    }

    @Test
    void testJoiner() {
        System.out.println("👨\u200d👩\u200d👧\u200d👦");
        System.out.println("👦\u200d👧\u200d👩\u200d👨");
    }

    static final Pattern JOINED = Pattern.compile(".(\u200D.)+|.");

    static String reverse2(String s) {
        List<String> list = new ArrayList<>();
        Matcher m = JOINED.matcher(s);
        while (m.find())
            list.add(m.group());
        Collections.reverse(list);
        return String.join("", list);
    }

    @Test
    void testReverse2() {
        System.out.println(reverse2("Hello world👩‍🦰👩‍👩‍👦‍👦 🏴󠁧󠁢󠁷󠁬󠁳󠁿"));
    }
}
