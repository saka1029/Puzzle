package test.puzzle;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

class TestCollatz {

    static final Logger logger = Logger.getLogger(TestCollatz.class.getName());

    /**
     * 再帰呼び出しで初期値nのコラッツ数列の長さを求めます。
     */
    static long collatz(long n) {
        if (n == 1)
            return 0;
        else if ((n & 1) == 0)
            return collatz(n / 2) + 1;
        else
            return collatz(n * 3 + 1) + 1;
    }

    @Test
    public void testCollatz() {
        assertEquals(111L, collatz(27));
        assertEquals(524L, collatz(837799L));
    }

    /**
     * 再帰をせずに繰り返しで初期値nのコラッツ数列の長さを求めます。
     */
    static long collatzRepeat(long n) {
        long count = 0;
        while (n > 1) {
            if ((n & 1) == 0)
                n = n / 2;
            else
                n = n * 3 + 1;
            ++count;
        }
        return count;
    }

    @Test
    public void testCollatzRepeat() {
        assertEquals(111L, collatzRepeat(27));
        assertEquals(524L, collatzRepeat(837799L));
    }

    /**
     * nを初項とするコラッツ数列を求めます。
     */
    static List<Long> コラッツ数列(long n) {
        List<Long> result = new ArrayList<>((int) n);
        while (n > 1) {
            result.add(n);
            if ((n & 1) == 0)
                n = n / 2;
            else
                n = n * 3 + 1;
        }
        return result;
    }

    @Test
    public void testコラッツ数列() {
        List<Long> result = コラッツ数列(27);
        assertEquals(111, result.size());
        List<Long> expected = List.of(
            27L, 82L, 41L, 124L, 62L, 31L, 94L, 47L, 142L, 71L, 214L, 107L, 322L, 161L, 484L, 242L, 121L, 364L, 182L,
            91L, 274L, 137L, 412L, 206L, 103L, 310L, 155L, 466L, 233L, 700L, 350L, 175L, 526L, 263L, 790L, 395L, 1186L,
            593L, 1780L, 890L, 445L, 1336L, 668L, 334L, 167L, 502L, 251L, 754L, 377L, 1132L, 566L, 283L, 850L, 425L,
            1276L, 638L, 319L, 958L, 479L, 1438L, 719L, 2158L, 1079L, 3238L, 1619L, 4858L, 2429L, 7288L, 3644L, 1822L,
            911L, 2734L, 1367L, 4102L, 2051L, 6154L, 3077L, 9232L, 4616L, 2308L, 1154L, 577L, 1732L, 866L, 433L, 1300L,
            650L, 325L, 976L, 488L, 244L, 122L, 61L, 184L, 92L, 46L, 23L, 70L, 35L, 106L, 53L, 160L, 80L, 40L, 20L, 10L,
            5L, 16L, 8L, 4L, 2L);
        assertEquals(expected, result);
    }

    /**
     * max未満の数を初期値とするコラッツ数列の長さをすべて求める。
     *
     * [コラッツの問題 - Wikipedia]
     * (https://ja.wikipedia.org/wiki/%E3%82%B3%E3%83%A9%E3%83%83%E3%83%84%E3%81%AE%E5%95%8F%E9%A1%8C)
     *
     * [四則演算だけの未解決問題【コラッツ予想】 - YouTube]
     * (https://www.youtube.com/watch?v=-j5ZWffcnQ0)
     *
     * コメント欄にこんな記述があった。
     *
     * glayzone este 15 時間前（編集済み） 自分も同じ実験してコメントしようとしたらこのコメントがあって嬉しいw
     * ちなみにn≦1,000,000,000の範囲を調べると最大で176つ子コラッツ数がありました。
     * 「2以上の自然数n対してnつ子コラッツが存在する」なんて命題も成立するのかも？
     *
     * 注)「176つ子コラッツ数」：176の連続する自然数を初期値とするコラッツ数列の長さがすべて等しい。
     *
     * 1,000,000,000はかなり大きいので1,000,000まで求めようとしたが、
     * メモ化した再帰呼び出しだとスタックがあふれてしまう。
     * -Xss:1024mを指定してもあふれる。 そこで繰り返しで求めるようにした。
     * Stackoverflowにアルゴリズムがあった。
     *
     * [Collatz Conjecture Method - Java - Stack Overflow]
     * (https://stackoverflow.com/questions/38928443/collatz-conjecture-method-java)
     *
     * さらに繰り返し途中で求めた値をすべてキャッシュするようにした。
     * そうすると繰り返し途中の値を保持するstackの確保でOutOfMemoryとなった。
     * デバッグ出力で確認するとiが負の値になることが分かった。
     * intではオーバーフローしてしまう。
     * そこでLongに変えることにより、1秒程度で求めることができるようになった。
     */
    Map<Long, Long> collatzAll(int max) {
//        Map<Long, Long> cache = new TreeMap<>();
        Map<Long, Long> cache = new HashMap<>(2 * (int) max);
        cache.put(1L, 0L);
        Deque<Long> stack = new ArrayDeque<>((int) max);
        new Object() {

            long collatz(long n) {
                stack.clear();
                long i = n;
                while (!cache.containsKey(i)) {
                    stack.push(i); // キャッシュになかった値をスタックする。
                    if ((i & 1) == 0)
                        i /= 2;
                    else
                        i = i * 3 + 1;
                }
                long cached = cache.get(i);
                // String log = "n=" + n + " i=" + i + " cached=" + cached + "
                // stack=" + stack;
                while (!stack.isEmpty())
                    cache.put(stack.pop(), ++cached); // スタックされた値をキャッシュに入れる。
                // logger.info(log + " " + cache);
                return cache.get(n); // nは直前のwhileループの最後にキャッシュされているはず。
            }

            void run() {
                for (long i = 2; i < max; ++i)
                    collatz(i);
            }

        }.run();
        return cache;
    }

    @Test
    void testCollatzAll() {
        int M = 1000000;
        Map<Long, Long> cache = collatzAll(M);
        for (long i = M - 1000; i < M; ++i)
            logger.info("collatz(" + i + ") = " + cache.get(i));
        // 1000以下で最も長いもの
        Entry max1000 = cache.entrySet().stream()
            .filter(e -> e.getKey() <= 100)
            .max(Comparator.comparing(Entry::getValue)).get();
        logger.info(max1000.toString());
        // M以下で最も長いもの
        Entry max = cache.entrySet().stream()
            .max(Comparator.comparing(Entry::getValue)).get();
        logger.info(max.toString());
    }

    /**
     * エラトステネスのふるいのように計算しようとしたが、
     * うまくいかない。
     * nより大きな数字にはみでてから戻ってくる場合があるから。
     */
    static long[] makeCollatzCache(long n) {
        long[] cache = new long[(int)n];
        Arrays.fill(cache, -1);
        Deque<Entry<Long, Long>> stack = new LinkedList<>();
        stack.add(Map.entry(1L, 0L));
        while (!stack.isEmpty()) {
            Entry<Long, Long> start = stack.removeFirst();
            for (long i = start.getKey(), c = start.getValue(); i < n; i *= 2, ++c) {
                cache[(int)i] = c;
                long mod = (i - 1) % 3;
                long div = (i - 1) / 3;
                if (mod == 0 && div > 1 && div < n && cache[(int)div] == -1)
                    stack.addLast(Map.entry(div, c + 1));
            }
        }
        return cache;
    }

    @Test
    public void testMakeCollatzCache() {
        long N = 1000;
        long[] cache = makeCollatzCache(N);
        logger.info(Arrays.toString(cache));
    }

}
