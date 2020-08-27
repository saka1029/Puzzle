package test.puzzle;

import static org.junit.Assert.*;

import java.util.List;
import java.util.logging.Logger;

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
         * null{
         *      A=A{},
         *      t=null{
         *          e=null{
         *              a=tea{},
         *              d=ted{},
         *              n=ten{}},
         *          o=to{}},
         *      i=i{
         *          n=in{
         *              n=inn{}}}}
         * </pre>
         */
        for (String s : words)
            assertEquals(s, trie.get(s));
        assertEquals(null, trie.get("NO DATA"));
        assertEquals(List.of("i", "in", "inn"), trie.search("innto"));
        assertEquals(10, trie.size());
    }

}
