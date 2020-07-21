package puzzle;

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
     * フィボナッチ数を返す関数の例
     * <code>
     * Memoizer<BigInteger, BigInteger> fibonacci =
     *     memoize(self -> n ->
     *         n.equals(ZERO) ? ZERO
     *             : n.equals(ONE) ? ONE
     *             : self.apply(n.subtract(ONE)).add(self.apply(n.subtract(TWO))));

     * </code>
     *
     * 複数の引数を持つ関数はカリー化することによってメモ化することができます。
     *
     * @param <T>
     * @param <U>
     * @param f
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
