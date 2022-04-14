package test.puzzle.core;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import org.junit.Test;

import puzzle.functions.Memoizer;

public class TestCollatz {

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
    static List<Long> collatzSequence(long n) {
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

    static final List<Long> sequenceOf27 = List.of(
            27L, 82L, 41L, 124L, 62L, 31L, 94L, 47L, 142L, 71L, 214L, 107L, 322L, 161L, 484L, 242L, 121L, 364L, 182L,
            91L, 274L, 137L, 412L, 206L, 103L, 310L, 155L, 466L, 233L, 700L, 350L, 175L, 526L, 263L, 790L, 395L, 1186L,
            593L, 1780L, 890L, 445L, 1336L, 668L, 334L, 167L, 502L, 251L, 754L, 377L, 1132L, 566L, 283L, 850L, 425L,
            1276L, 638L, 319L, 958L, 479L, 1438L, 719L, 2158L, 1079L, 3238L, 1619L, 4858L, 2429L, 7288L, 3644L, 1822L,
            911L, 2734L, 1367L, 4102L, 2051L, 6154L, 3077L, 9232L, 4616L, 2308L, 1154L, 577L, 1732L, 866L, 433L, 1300L,
            650L, 325L, 976L, 488L, 244L, 122L, 61L, 184L, 92L, 46L, 23L, 70L, 35L, 106L, 53L, 160L, 80L, 40L, 20L, 10L,
            5L, 16L, 8L, 4L, 2L);

    @Test
    public void testCollatzSequence() {
        List<Long> result = collatzSequence(27);
        assertEquals(111, result.size());
        assertEquals(sequenceOf27, result);
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
    public void testCollatzAll() {
        int M = 1000000;
        Map<Long, Long> cache = collatzAll(M);
        for (long i = M - 1000; i < M; ++i)
            logger.info("collatz(" + i + ") = " + cache.get(i));
        // 1000以下で最も長いもの
        Entry<Long, Long> max1000 = cache.entrySet().stream()
            .filter(e -> e.getKey() <= 100)
            .max(Comparator.comparing(Entry::getValue)).get();
        logger.info(max1000.toString());
        // M以下で最も長いもの
        Entry<Long, Long> max = cache.entrySet().stream()
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

    /**
     *
     * [コラッツの問題 - Wikipedia]
     * (https://ja.wikipedia.org/wiki/%E3%82%B3%E3%83%A9%E3%83%83%E3%83%84%E3%81%AE%E5%95%8F%E9%A1%8C#%E3%83%91%E3%83%AA%E3%83%86%E3%82%A3%E3%82%B7%E3%83%BC%E3%82%B1%E3%83%B3%E3%82%B9)
     *
     * パリティシーケンス
     * 本節では、コラッツ関数を少し変形したものを考える：
     *
     * f(n) = n / 2             (nが偶数の時)
     *        (n * 3 + 1) / 2   (nが奇数の時)
     * nが奇数の場合には3n + 1が必ず偶数になるので上記のようにできる。
     *
     * P(…)をパリティ数とする。P(2n) = 0 で、P(2n + 1) = 1 である。
     * 整数nのパリティシーケンス（もしくは、パリティベクトル）を、pi = P(ai),
     * ただしa0 = n, and ai+1 = f(ai) と定義する。
     * (3n + 1)/2 または n/2、どちらの操作が適用されるかは、パリティに依存する。
     * パリティシーケンスは操作のシーケンスに等しい。
     * f(n)に対してこの形式を適用すると、２つの整数m とnのパリティシーケンスは、
     * m とnが2kを法として合同の場合のみ、最初のk項で一致するこが示される。
     * これは、すべての整数がパリティシーケンスにより一意に識別されること意味し、
     * さらに複数のコラッツ数列がある場合、対応するパリティシーケンスが
     * 異なる必要があることを意味します[7][8]。
     * n=a·2k + b に関数 f を k 回適用すると、a·3c + dとなる。
     * ここでdはbに関数fをk回適用した結果で、cはその過程で3倍の演算を行った（増加した）回数である。
     * （例えば、a·25 + 1 では、1が2,1,2,1と変化し最後に2になるので、3回の増加がある。
     * よって結果はa·33+2 である。a·22 + 1 では、1が2に増加しその後1になるので、
     * 結果はa·3 + 1 となる。） bが2k - 1の場合には、 k回の増加があり、結果は2·a·3k - 1となる。
     * aに掛かる係数は、aには無関係で、bにのみ依存する。これにより、特定の形式の数値が特定の反復回数の後、
     * 常により小さい数値になることを予測できます。例えば、4a + 1 は、2回のf操作により3a + 1となり、
     * 16a + 3は4回のf操作により9·a + 2となる。これらの小さくなった数が1へとつながるかどうかは、aの値に依存する。
     *
     *
     * 時間と空間のトレードオフ
     * 上記の パリティシーケンス により、コラッツ数列の計算の高速化が可能である。
     *
     * （パリティシーケンス節のf関数を使ったとして）kステップ先にジャンプする方法ために、
     * まず現在の数値を2つに分割します：b（2進数表記で下位kビット）と、
     * a（残りの上位ビット）。kステップをジャンプした結果は以下になる：
     *
     * f^k(a 2^k + b) = a * 3^(c(b)) + d(b).
     * c(もしくは3^c)とd配列は、kビットの数すべてについて事前計算しておく。
     * d(b)はbにf関数をk回行った数で、c(b)はその間に登場した奇数の数である。
     * 例えばk=5なら5ステップのジャンプが可能で、そのために下位5ビットを分割して、
     * 下記配列を使う：
     *
     * c(0..31) = {0,3,2,2,2,2,2,4,1,4,1,3,2,2,3,4,1,2,3,3,1,1,3,3,2,3,2,4,3,3,4,5}
     * d(0..31) = {0,2,1,1,2,2,2,20,1,26,1,10,4,4,13,40,2,5,17,17,2,2,20,20,8,22,8,71,26,26,80,242}
     *
     * これは、2^k個の事前計算とストレージが要求される。
     * これにより計算速度がk倍高速化出来るが、時間と空間のトレードオフである。
     *
     */

    // Wikipediaに記載の定数
    static final int[] C = {0, 3, 2, 2, 2, 2, 2, 4, 1, 4, 1, 3, 2, 2, 3, 4, 1, 2, 3, 3, 1, 1, 3, 3, 2, 3, 2, 4, 3, 3, 4, 5};
    static final int[] D = {0, 2, 1, 1, 2, 2, 2, 20, 1, 26, 1, 10, 4, 4, 13, 40, 2, 5, 17, 17, 2, 2, 20, 20, 8, 22, 8, 71, 26, 26, 80, 242};
    static final int[] C3 = IntStream.of(C).map(n -> (int)Math.pow(3, n)).toArray();

    /**
     * パリティシーケンスにおけるコラッツ数列を求める。
     */
    static int pcollatz(int n) {
        if (n % 2 == 0)
            return n / 2;
        else
            return (n * 3 + 1) / 2;
    }

    /**
     * パリティシーケンスにおけるk番目のコラッツ数列を求める。
     */
    static int pcollatz(int n, int k) {
        for (int i = 0; i < k; ++i)
            n = pcollatz(n);
        return n;
    }

    @Test
    public void testPcollatz() {
        assertEquals(2, pcollatz(1));
        assertEquals(1, pcollatz(2));
        assertEquals(1, pcollatz(1, 2));
        assertEquals(1, pcollatz(1, 4));
        assertEquals(1, pcollatz(1, 6));
    }

    /**
     * k番目のpcollatzの計算で使用する配列c3(3^c)およびdを求める。
     * @param k
     * @param length
     * @param c3
     * @param d
     */
    static void pcollatzConstants(int k, int length, int[] c3, int[]d) {
        for (int i = 0; i < length; ++i) {
            int di = i;
            int ci = 1;
            for (int j = 0; j < k; ++j) {
                if (di % 2 != 0) ci *= 3;   // 次のpcollatzを求める前に計算するところがポイント。
                di = pcollatz(di);
            }
            d[i] = di;
            c3[i] = ci;
        }
    }

    /**
     * k番目のpcollatzの計算で使用する配列c3(3^c)およびdを求める。
     *
     * @param k
     * @return new int[][] {c3, d}を返す。
     */
    static int[][] pcollatzConstants(int k) {
        int length = 1 << k;
        int[] c3 = new int[length];
        int[] d = new int[length];
        pcollatzConstants(k, length, c3, d);
        return new int[][] {c3, d};
    }

    @Test
    public void testPcollatzConstants() {
        int k = 5;
        int[][] constants = pcollatzConstants(k);
        int[] c3 = constants[0];
        int[] d = constants[1];
        logger.info("c3 = " + Arrays.toString(c3));
        logger.info("d  = " + Arrays.toString(d));
        assertArrayEquals(C3, c3);
        assertArrayEquals(D, d);
    }

    static int pcollatz(int n, int k, int[] c3, int[] d) {
        int a = n >> k;
        int b = n & ((1 << k) - 1);
        return a * c3[b] + d[b];
    }

    /**
     * 5, 10, 15, 20, ... 番目のpcollatzをパリティシーケンスによる方法で求め、
     * 通常の計算と一致することを確認する。
     */
    @Test
    public void testPcollatzSequence() {
        int k = 10;
        int[][] constants = pcollatzConstants(k);
        int[] c3 = constants[0];
        int[] d = constants[1];
        int n = 27;     // 初項
        int e = n;      // 期待値(標準の関数で求める)
        // 最終的に1と2の循環に収束するので2以下になったら終了する。
        for (int i = 0; e > 2; i += k, n = pcollatz(n, k, c3, d), e = pcollatz(e, k)) {
            logger.info(i + " : e=" + e + " n=" + n);
            assertEquals(e, n);
        }
    }

    static long collatzNext(long n) {
        if (n % 2 == 0)
            return n / 2;
        else
            return n * 3 + 1;
    }

    /**
     * 0 <= n < maxの整数についてn -> f(n)のMapを作成します。
     */
    static Map<Long, Long> collatzMap(int max) {
        Map<Long, Long> map = new TreeMap<>();
        map.put(1L, -1L);
        for (long i = 2; i < max; ++i)
            for (long s = i, n = collatzNext(s); !map.containsKey(s); s = n, n = collatzNext(s))
                map.put(s, n);
        return map;
    }

    @Test
    public void testCollatzMap() throws IOException {
        int N = 1000;
        Map<Long, Long> map = collatzMap(N);
        Path file = Paths.get("data", "collatz.gml");
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(file))) {
            out.println("graph [");
            for (long i : map.keySet()) {
                out.println("  node [");
                out.println("    id " + i);
                out.println("    label \"" + i + "\"");
                out.println("  ]");
            }
            for (Entry<Long, Long> e : map.entrySet())
                if (e.getValue() != -1) {
                    out.println("  edge [");
                    out.println("    source " + e.getKey());
                    out.println("    target " + e.getValue());
                    out.println("  ]");
                }
            out.println("]");
        }
    }

    static <T> Comparator<T> reversed(Comparator<T> comparator) {
        return (a, b) -> comparator.compare(b, a);
    }

    @Test
    public void testCacheFunctionMemoize() {
        Memoizer<Long, Long> collatzSize = Memoizer.memoize(self -> n -> {
            if (n == 1L)
                return 0L;
            else if ((n & 1L) == 0)
                return self.apply(n / 2L) + 1L;
            else
                return self.apply(n * 3L + 1L) + 1L;
        });
        long max = 1000000;
        for (long i = 2; i < max; ++i)
            collatzSize.apply(i);
        System.out.println("cache size=" + collatzSize.cache().size());
        collatzSize.cache().entrySet().stream()
            .filter(e -> e.getKey() <= max)
            .sorted(reversed(Comparator.comparing(Entry::getValue)))
            .limit(100)
            .forEach(System.out::println);
    }

}