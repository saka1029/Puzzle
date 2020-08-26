package puzzle.functions;

import java.util.Arrays;

/**
 * equals()で比較可能なObject[]のラッパークラス。
 * 基本的には不変なクラスですが、厳密にはそうではありません。
 * ただし極力、不変となるように努力しています。
 * 自己参照があるようなObject配列は正しく処理できません。
 * ex) <code>Object[] a = {null}; a[0] = a;</code>
 */
public class ObjectArray {

    private final Object[] array;

    /**
     * 自己参照があるようなObject配列は正しく処理できません。
     * ex) Object[] a = {null}; a[0] = a;
     */
    private ObjectArray(Object... objects) {
        // シャローコピーです。
        // どんなにがんばってもディープコピーはむずかしい。
        // シリアライズ・デシリアライズする方法もありますが、
        // 各要素がSerializableを実装していることが前提となってしまいます。
        this.array = objects.clone();
    }

    public static ObjectArray of(Object... objects) {
        return new ObjectArray(objects);
    }

    public int size() {
        return array.length;
    }

    /*
     * Immutable性を阻害する要因になるので削除
     */
//    public Object get(int index) {
//        return array[index];
//    }

    /**
     * Arrays.deepHashCodeは自己参照する配列を与えるとStackOverflowとなります。
     */
    @Override
    public int hashCode() {
        return Arrays.deepHashCode(array);
    }

    /**
     * Arrays.deepEqualsは自己参照する配列を与えるとStackOverflowとなります。
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        return Arrays.deepEquals(array, ((ObjectArray) obj).array);
    }

    /**
     * Arrays.deepToStringは自己参照する配列を与えても"..."大丈夫です。
     */
    @Override
    public String toString() {
        return Arrays.deepToString(array);
    }
}
