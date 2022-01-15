package test.puzzle.parsers;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

class Test施設基準parser {

    static final String S空白 = "[ \t　]*";
    static final String S表題 = "(?<T>\\S+)";
    static final String S漢数字 = "[一二三四五六七八九]";
    static final String S漢番号 = "(" + S漢数字 + "?十)?" + S漢数字;
    static final String Sの番号 = S漢番号 + "(の" + S漢番号 + ")*";
    static final Pattern 漢番号 = Pattern.compile("^" + S空白 + S漢番号 + S空白 + S表題);
    static final Pattern の番号 = Pattern.compile("^" + S空白 + Sの番号 + S空白 + S表題);
    static final Pattern 章番号 = Pattern.compile("^" + S空白 + Sの番号 + "章" + S空白 + S表題);

    @Test
    void test章番号() {
        System.out.println(章番号);
        assertTrue(章番号.matcher("  十一の二章　タイトル").matches());
    }

}
