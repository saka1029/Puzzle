package puzzle.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.Normalizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class 施設基準Parser {

    static final String 数字文字列 = "[0-9０-９]+";
    static final String 漢数字文字列 = "一二三四五六七八九";
    static final String 丸数字文字列 = "①②③④⑤⑥⑦⑧⑨⑩⑪⑫⑬⑭⑮⑯⑰⑱⑲⑳";
    static final String イロハ文字列 = "イロハニホヘトチリヌルヲワカヨタレソツネ"
        + "ナラムウヰノオクヤマケフコエテアサキユメミシヱヒモセス";

    public static String 半角変換(String target) {
        return Normalizer.normalize(target, Normalizer.Form.NFKD);
    }

    interface 項番 {
        Pattern pattern();

        int number(String 項番);

        default boolean isMatch(String 項番) {
            return pattern().matcher(項番).matches();
        }

        default boolean isFirst(String 項番) {
            return number(項番) == 1;
        }

        default boolean isNext(String 項番1, String 項番2) {
            return isMatch(項番1) && isMatch(項番2)
                && number(項番1) + 1 == number(項番2);
        }

        default String id(String 項番) {
            return 半角変換(項番);
        }
    }

    public static class 数字 implements 項番 {
        final Pattern pat;

        数字(String pat) {
            this.pat = Pattern.compile(pat);
        }

        public 数字() {
            this("^" + 数字文字列 + "$");
        }

        public Pattern pattern() {
            return pat;
        }

        public int number(String s) {
            return isMatch(s)
                ? Integer.parseInt(s.replaceAll("[^0-9０-９]", "")) : -1;
        }
    }

    public static class 括弧数字 extends 数字 {
        public 括弧数字() {
            super("^[(（]" + 数字文字列 + "[)）]$");
        }
    }

    static final String 漢数字列 = "([" + 漢数字文字列 + "]?十)?[" + 漢数字文字列 + "]";
    static final String 漢数字列の = 漢数字列 + "(の" + 漢数字列 + ")*";
    static final String 漢数字列範囲 = "(" + 漢数字列の + "(及び|から))?" + 漢数字列の + "(まで)?";

    static final Pattern 漢数字列正規表現 = Pattern.compile("(?<N>" + 漢数字列 + ")");
    static final Pattern 漢数字列の正規表現 = Pattern.compile("(?<N>" + 漢数字列の + ")");

    public static class 漢数字 implements 項番 {
        final Pattern pat;

        漢数字(String pat) {
            this.pat = Pattern.compile(pat);
        }

        public 漢数字() {
            this(漢数字列範囲);
        }

        public Pattern pattern() {
            return pat;
        }

        public int number(String s) {
            return -1;
        }

        static int 半角数字(String s) {
            int number = 0;
            for (char c : s.toCharArray())
                if (c == '十')
                    number = number == 0 ? 1 : number;
                else {
                    int index = 漢数字文字列.indexOf(c);
                    if (index >= 0)
                        number = number * 10 + index + 1;
                    else
                        throw new IllegalArgumentException("s");
                }
            return number;
        }

        public String id(String s) {
            if (!isMatch(s))
                return null;
            Matcher m = 漢数字列の正規表現.matcher(s);
            StringBuilder sb = new StringBuilder();
            for (String sep = ""; m.find(); sep = ":") {
                sb.append(sep);
                String n = m.group("N");
                Matcher x = 漢数字列正規表現.matcher(n);
                for (String sep2 = ""; x.find(); sep2 = "-")
                    sb.append(sep2).append(半角数字(x.group()));
            }
            return sb.toString();
        }

        @Override
        public boolean isFirst(String 項番) {
            return isMatch(項番);
        }

        @Override
        public boolean isNext(String a, String b) {
            return isMatch(a) && isMatch(b);
        }
    }

    public static class 第漢数字 extends 漢数字 {
        第漢数字(String s) {
            super(s);
        }

        public 第漢数字() {
            this("第" + 漢数字列の);
        }

        @Override
        public boolean isFirst(String 項番) {
            return id(項番).equals("1");
        }

        static final Pattern 最後の数字 = Pattern.compile("\\d+$");

        public String nextId(String id) {
            return 最後の数字.matcher(id).replaceAll(m -> "" + (Integer.parseInt(m.group()) + 1));
        }

        public String nextId2(String id) {
            return id + "-2";
        }

        @Override
        public boolean isNext(String 項番1, String 項番2) {
            if (!isMatch(項番1) || !isMatch(項番2))
                return false;
            String id1 = id(項番1), id2 = id(項番2);
            return id2.equals(nextId(id1)) || id2.equals(nextId2(id1));
        }
    }

    public static class 漢数字章 extends 第漢数字 {
        public 漢数字章() {
            super(漢数字列の + "章");
        }
    }

    public static class 単一文字項番 implements 項番 {
        final String all;
        final Pattern pat;

        単一文字項番(String all) {
            this.all = all;
            this.pat = Pattern.compile("^[" + all + "]$");
        }

        public Pattern pattern() {
            return pat;
        }

        public int number(String s) {
            if (!isMatch(s))
                return -1;
            int n = all.indexOf(s);
            return n == -1 ? -1 : n + 1;
        }

        public String id(String s) {
            return "" + number(s);
        }
    }

    public static class 丸数字 extends 単一文字項番 {
        public 丸数字() {
            super(丸数字文字列);
        }
    }

    public static class イロハ extends 単一文字項番 {
        public イロハ() {
            super(イロハ文字列);
        }
    }

    public static class LineReader implements AutoCloseable {
        final BufferedReader reader;
        public String line = null;
        public String file = null;
        public int lineNo = 0;
        public int pageNo = 0;
        public int lineNoInPage = 0;

        public LineReader(Reader reader) {
            this.reader = new BufferedReader(reader);
        }
        public LineReader(File file) throws FileNotFoundException {
            this(new FileReader(file));
        }

        static final Pattern FILE_COMMENT = Pattern.compile(
            "\\s*#\\s*file:\\s*(\\S+)\\s*page:\\s*(\\d+).*");

        void line(String line) {
            if (line == null)
                return;
            this.line = line;
            Matcher m = FILE_COMMENT.matcher(line);
            if (m.matches()) {
                file = m.group(1);
                pageNo = Integer.parseInt(m.group(2));
                lineNoInPage = 0;
            }
            ++lineNo;
            ++lineNoInPage;
        }

        public String readLine() {
            try {
                line(reader.readLine());
                return line;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void close() throws IOException {
            reader.close();
        }
    }
}
