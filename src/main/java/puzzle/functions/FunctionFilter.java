package puzzle.functions;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <img src="{@docRoot}/doc-files/fibonacci-memoize-trace.png"></img>
 */
public class FunctionFilter {

    private FunctionFilter() {}

    /**
     * 関数の出力値をキャッシュして関数の呼び出し回数を削減するためのクラスです。
     *
     * @param <T> 関数の入力の型です。
     * @param <R> 関数の出力の型です。
     */
    private static class MemoizeFunction<T, R> implements Function<Function<T, R>, Function<T, R>> {
        final Function<Function<T, R>, Function<T, R>> f;
        final Map<T, R> cache;

        /**
         * キャッシュを引数で与えるコンストラクタです。
         * キャッシュをクラス外で使用する場合に使います。
         *
         * @param f キャッシュの対象となる自己参照型関数を指定します。
         * @param cache キャッシュを指定します。
         */
        MemoizeFunction(Function<Function<T, R>, Function<T, R>> f, Map<T, R> cache) {
            this.f = f;
            this.cache = cache;
        }

        /**
         * キャッシュを自動生成するコンストラクタです。
         * キャッシュをクラス外で使用しない場合に使います。
         *
         * @param f キャッシュの対象となる自己参照型関数を指定します。
         */
        MemoizeFunction(Function<Function<T, R>, Function<T, R>> f) {
            this(f, new HashMap<>());
        }

        @Override
        public Function<T, R> apply(Function<T, R> self) {
            return n -> {
                R result = cache.get(n);
                if (result == null)
                    cache.put(n, result = f.apply(self).apply(n));
                return result;
            };
        }

        @Override
        public String toString() {
            return cache.toString();
        }
    }

    /**
     *
     * メモ化した関数を返します。
     *
     * <pre><code>
     *     // 関数の定義
     *     static Function<Function<Integer, Integer>, Function<Integer, Integer>> fibonacci =
     *         self -> n ->
     *             n == 0 ? 0 :
     *             n == 1 ? 1 :
     *             self.apply(n - 1) + self.apply(n - 2);
     *
     *      // メモ化した関数の実行
     *      Map<Integer, Integer> cache = new TreeMap<>();
     *      function(memoize(cache, fibonacci)).apply(4);
     * </code><pre>
     *
     * @param <T> 関数の入力型です。
     * @param <R> 関数の出力型です。
     * @param cache キャッシュを格納するマップを指定します。
     * @param f メモ化する自己参照型関数を指定します。
     * @return メモ化した自己参照型関数を返します。
     */
    public static <T, R> Function<Function<T, R>, Function<T, R>> memoize(
            Map<T, R> cache,
            Function<Function<T, R>, Function<T, R>> f) {
        return new MemoizeFunction<>(f, cache);
    }

    /**
     * メモ化した関数を返します。
     * キャッシュは内部で自動的に作成されるので引数で指定する必要はありません。
     *
     * <pre><code>
     *     // 関数の定義
     *     static Function<Function<Integer, Integer>, Function<Integer, Integer>> fibonacci =
     *         self -> n ->
     *             n == 0 ? 0 :
     *             n == 1 ? 1 :
     *             self.apply(n - 1) + self.apply(n - 2);
     *
     *      // メモ化した関数の実行
     *      function(memoize(fibonacci)).apply(4);
     * </code><pre>
     *
     * @param <T> 関数の入力型です。
     * @param <R> 関数の出力型です。
     * @param f メモ化する自己参照型関数を指定します。
     * @return メモ化した自己参照型関数を返します。
     */
    public static <T, R> Function<Function<T, R>, Function<T, R>> memoize(
            Function<Function<T, R>, Function<T, R>> f) {
        return new MemoizeFunction<>(f);
    }

    /**
     * 関数の統計を取ります。
     * 関数の入力値ごとの呼び出し回数のマップを作成します。
     *
     * <pre><code>
     *     static Function<Function<Integer, Integer>, Function<Integer, Integer>> fibonacci =
     *         self -> n ->
     *             n == 0 ? 0 :
     *             n == 1 ? 1 :
     *             self.apply(n - 1) + self.apply(n - 2);
     *
     *     Map<Integer, Integer> stat = new TreeMap<>();
     *     function(statistics(stat, fibonacci)).apply(5);
     * </code></pre>
     * @param <T> 関数の入力型です。
     * @param <R> 関数の出力型です。
     * @param stat 統計情報を蓄積するマップを指定します。
     * @param f 統計を取る対象の自己参照型関数を指定します。
     * @return 統計を取る自己参照型関数を返します。
     */
    public static <T, R> Function<Function<T, R>, Function<T, R>> statistics(
            Map<T, Integer> statistics,
            Function<Function<T, R>, Function<T, R>> f) {
        return new Function<Function<T, R>, Function<T, R>>() {

            @Override
            public Function<T, R> apply(Function<T, R> self) {
                return n -> {
                    R result = f.apply(self).apply(n);
                    statistics.compute(n, (k, v) -> v == null ? 1 : v + 1);
                    return result;
                };
            }

            @Override
            public String toString() {
                return statistics.toString();
            }

        };
    }

    /**
     * 関数の呼び出し状況を表すトレースを出力します。
     *
     * @param <T> 関数の入力型です。
     * @param <R> 関数の出力型です。
     * @param name トレース上に表示する関数の名前です。
     * @param output トレースの出力先を指定します。
     * @param f トレース出力する自己参照型関数を指定します。
     * @return トレース出力する機能を付加した自己参照型関数を返します。
     */
    public static <T, R> Function<Function<T, R>, Function<T, R>> trace(
            String name,
            Consumer<String> output,
            Function<Function<T, R>, Function<T, R>> f) {
        return new Function<Function<T, R>, Function<T, R>>() {

            int nest = 0;

            @Override
            public Function<T, R> apply(Function<T, R> self) {
                return n -> {
                    String indent = "  ".repeat(nest);
                    output.accept(String.format("%s%s(%s)", indent, name, n));
                    ++nest;
                    R result = f.apply(self).apply(n);
                    --nest;
                    output.accept(String.format("%s%s", indent, result));
                    return result;
                };
            }

        };
    }

    /**
     * 自己参照型関数(<code>Function&lt;Function&lt;T, R&gt;, Function&lt;T, R&gt;&gt;</code>)を
     * 通常の関数(<code>Function&lt;T, R&gt;</code>)に変換します。
     * @param <T> 関数の入力型です。
     * @param <R> 関数の出力型です。
     * @param f 自己参照型の関数を指定します。
     * @return 返還後の通常の関数を返します。
     */
    public static <T, R> Function<T, R> function(Function<Function<T, R>, Function<T, R>> f) {
        return new Function<T, R>() {

            @Override
            public R apply(T n) {
                return f.apply(this).apply(n);
            }

            @Override
            public String toString() {
                return f.toString();
            }
        };
    }

}
