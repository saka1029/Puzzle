package puzzle.functions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * メモ化した関数を作成するためのインタフェースです。
 *
 * @param <T> メモ化した関数の引数の型です。
 * @param <U> メモ化した関数の戻り値の型です。
 */
public interface Memoizer<T, U> extends Function<T, U> {

    /**
     * メモ化の結果を格納したキャッシュを返します。
     * キーは引数、値は関数の戻り値です。
     * @return
     */
    Map<T, U> cache();

    /**
     * メモ化した関数を返します。
     *
     * フィボナッチ数を返す関数の例を以下に示します。
     * <pre>
     * <code>
     * Memoizer<Long, Long> fibonacci =
     *     memoize(self -> n ->
     *         n == 0 ? 0 :
     *         n == 1 ? 1 :
     *         self.apply(n - 1) + self.apply(n - 2));
     *
     * for (long i = 0; i < 10; ++i)
     *     System.out.println(fibonacci.apply(i));
     *
     * // 関数オブジェクトを文字列化するとキャッシュを参照できます。
     * System.out.println(fibonacci);
     * // -> {0=0, 1=1, 2=1, 3=2, 4=3, 5=5, 6=8, 7=13, 8=21, 9=34}
     * </code>
     * </pre>
     *
     * 複数の引数を持つ関数はカリー化することによってメモ化することができます。
     * 以下に<a href="https://ja.wikipedia.org/wiki/%E7%AB%B9%E5%86%85%E9%96%A2%E6%95%B0">竹内関数(tarai関数)</a>の例を示します。
     * <pre><code>
     *  Memoizer<Integer, Memoizer<Integer, Memoizer<Integer, Integer>>> tarai =
     *      memoize(self -> x ->
     *          memoize(selfY -> y ->
     *              memoize(selfZ -> z -> x <= y ? y
     *                  : self.apply(self.apply(x - 1).apply(y).apply(z))
     *                        .apply(self.apply(y - 1).apply(z).apply(x))
     *                        .apply(self.apply(z - 1).apply(x).apply(y)))));
     *
     *  System.out.println(tarai.apply(3).apply(2).apply(1));
     * </code></pre>
     *
     * あるいはrecordを使って引数をひとつにまとめる方法もあります。
     *
     * <pre><code>
     *  record Args(int x, int y, int z) { }
     *
     *  Memoizer<Args, Integer> tarai =
     *      memoize(self -> args -> args.x <= args.y ? args.y
     *          : self.apply(new Args(
     *              self.apply(new Args(args.x - 1, args.y, args.z)),
     *              self.apply(new Args(args.y - 1, args.z, args.x)),
     *              self.apply(new Args(args.z - 1, args.x, args.y)))));
     *
     *  System.out.println(tarai.apply(new Args(3, 2, 1)));
     * </code></pre>
     *
     * @param <T> メモ化した関数の引数の型です。
     * @param <U> メモ化した関数の戻り値の型です。
     * @param f   メモ化する関数を指定します。
     *            Function<T, U>を受け取ってFunction<T, U>を返す関数として定義します。
     *            上記のfibonacci関数の例を参考にしてください。
     *            再帰呼び出しは受け取ったFunction<T, U>のapply(x)を呼び出すことによって
     *            行うことができます。
     * @return
     */
    public static <T, U> Memoizer<T, U> memoize(Function<Function<T, U>, Function<T, U>> f) {

        return new Memoizer<T, U>() {

            private final Map<T, U> cache = new HashMap<>();
            private final Function<T, U> body = f.apply(this);

            @Override
            public U apply(T t) {
                // return cache.computeIfAbsent(t, body);
                U u = cache.get(t);
                if (u == null)
                    cache.put(t, u = body.apply(t));
                return u;
            }

            /**
             * キャッシュを不変のマップとして返します。
             */
            @Override
            public Map<T, U> cache() {
                return Collections.unmodifiableMap(cache);
            }

            /**
             * キャッシュの文字列表現を返します。
             */
            @Override
            public String toString() {
                return cache.toString();
            }
        };
    }

}
