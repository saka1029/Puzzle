package test.puzzle.core;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import puzzle.core.Trie;

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
        assertEquals(List.of("i", "in", "inn"), trie.findPrefix("inn"));
        assertEquals(10, trie.size());
    }

    static class Byo {
        final String 型;
        final String コード;
        final String 名称;

        Byo(String 型, String コード, String 名称) {
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

    static List<Byo> read(String path, int 型位置, int コード位置, int 名称位置)
        throws IOException {
        try (Stream<String> stream = Files.lines(Path.of(path), CHARSET)) {
            return stream.map(line -> line.split(","))
                .map(f -> new Byo(unq(f[型位置]), unq(f[コード位置]), unq(f[名称位置])))
                .collect(Collectors.toList());
        }
    }

    static class ByoEnc extends Trie<Byo> {

        List<List<Byo>> encode(String s) {
            int length = s.length();
            Map<Integer, List<Byo>> map = findAll(s);
            Deque<Byo> 選択 = new LinkedList<>();
            List<List<Byo>> 結果 = new ArrayList<>();
            new Object() {
                void find(int index, int 傷病名数) {
                    if (index >= length) {
                        if (傷病名数 == 1)
                            結果.add(new ArrayList<>(選択));
                        return;
                    }
                    List<Byo> list = map.get(index);
                    if (list == null) return;
                    for (Byo b : list) {
                        int 合計傷病名数 = 傷病名数 + (b.型.equals("B") ? 1 : 0);
                        if (合計傷病名数 > 1) continue;
                        選択.addLast(b);
                        find(index + b.名称.length(), 合計傷病名数);
                        選択.removeLast();
                    }
                }
            }.find(0, 0);
            return 結果;
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
        List<Byo> 修飾語マスタ = read("data/レセ電/z_20200601.txt", 1, 2, 6);
        List<Byo> 傷病名マスタ = read("data/レセ電/b_20200601.txt", 1, 2, 5);
        logger.info("修飾語マスタ 件数=" + 修飾語マスタ.size());
        logger.info("傷病名マスタ 件数=" + 傷病名マスタ.size());
        ByoEnc encoder = new ByoEnc();
        for (Byo b : 傷病名マスタ)
            encoder.put(b.名称, b);
        for (Byo b : 修飾語マスタ)
            encoder.put(b.名称, b);
        logger.info("trie size=" + encoder.size());
        for (List<Byo> b : encoder.encode("急性潰瘍性大腸炎"))
            logger.info("\t" + b);
        logger.info(encoder.encode("急性潰瘍性大腸炎").toString());
        List<String> 未コード化傷病名 = read("data/レセ電/micode.txt", 1);
        for (String s : 未コード化傷病名) {
            List<List<Byo>> r = encoder.encode(s);
            logger.info(s + " -> " + r.size() + ":" + r);
        }
    }

}
