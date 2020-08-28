package test.puzzle;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import puzzle.Trie;

public class TestTrie {

    static final Logger logger = Logger.getLogger(TestTrie.class.getName());

    @Test
    public void testTrie() {
        String[] words = {"A", "to", "tea", "ted", "ten", "i", "in", "inn"};
        Trie<String> trie = new Trie<>();
        for (String s : words)
            trie.put(s, s);
        logger.info(trie.toString());
        /*********************
         * <pre>
         *  {
         *      A=A{},
         *      t={
         *          e={
         *              a=tea{},
         *              d=ted{},
         *              n=ten{}
         *          },
         *          o=to{}
         *      },
         *      i=i{
         *          n=in{
         *              n=inn{}
         *          }
         *      }
         *  }
         * </pre>
         */
        for (String s : words)
            assertEquals(s, trie.get(s));
        assertEquals(null, trie.get("NO DATA"));
        assertEquals(List.of("i", "in", "inn"), trie.search("inn"));
        assertEquals(10, trie.size());
    }

    static class 傷病名 {
        final String 型;
        final String コード;
        final String 名称;

        傷病名(String 型, String コード, String 名称) {
            this.型 = 型;
            this.コード = コード;
            this.名称 = 名称;
        }

        @Override
        public String toString() {
            return "傷病名(" + 型 + ", " + コード + ", " + 名称 + ")";
        }
    }

    static String unq(String s) {
        return s.replace("\"", "");
    }

    static final Charset CHARSET = Charset.forName("Shift_JIS");

    static List<傷病名> read(String path, int 型位置, int コード位置, int 名称位置)
        throws IOException {
        try (Stream<String> stream = Files.lines(Path.of(path), CHARSET)) {
            return stream.map(line -> line.split(","))
                .map(f -> new 傷病名(unq(f[型位置]), unq(f[コード位置]), unq(f[名称位置])))
                .collect(Collectors.toList());
        }
    }

    static class Found {
        final int start;
        final 傷病名 傷病名;

        Found(int start, 傷病名 傷病名) {
            this.start = start;
            this.傷病名 = 傷病名;
        }

        @Override
        public String toString() {
            return "Found(" + start + ", " + 傷病名 + ")";
        }
    }

    static class コード化 {
        final Trie<傷病名> trie;

        コード化(Trie<傷病名> trie) {
            this.trie = trie;
        }
        
        List<Found> encode(String s) {
            int length = s.length();
            List<Found> result = new ArrayList<>();
            for (int i = 0; i < length; ++i)
                for (傷病名 b : trie.search(s.substring(i)))
                    result.add(new Found(i, b));
            return result;
        }
    }

    static List<String> read(String path, int columnNo) throws IOException {
        try (Stream<String> stream = Files.lines(Path.of(path))) {
            return stream.map(line -> line.split(","))
                .map(field -> unq(field[columnNo]))
                .collect(Collectors.toList());
        }

    }
    @Test
    public void test傷病名コード化() throws IOException {
        List<傷病名> 修飾語マスタ = read("data/レセ電/z_20200601.txt", 1, 2, 6);
        List<傷病名> 傷病名マスタ = read("data/レセ電/b_20200601.txt", 1, 2, 5);
        logger.info("修飾語マスタ 件数=" + 修飾語マスタ.size());
        logger.info("傷病名マスタ 件数=" + 傷病名マスタ.size());
        Trie<傷病名> trie = new Trie<>();
        for (傷病名 b : 傷病名マスタ)
            trie.put(b.名称, b);
        for (傷病名 b : 修飾語マスタ)
            trie.put(b.名称, b);
        logger.info("trie size=" + trie.size());
        コード化 encoder = new コード化(trie);
        logger.info(encoder.encode("ニューモシスチス肺炎の発症抑制").toString());
        List<String> 未コード化傷病名 = read("data/レセ電/micode.txt", 1);
        for (String s : 未コード化傷病名)
            logger.info(s + " -> " + encoder.encode(s));

    }

}
