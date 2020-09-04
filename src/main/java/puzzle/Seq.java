package puzzle;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * 変更不可なリストのインタフェースです。
 * Listインタフェースを実装しているのでListと同様に扱うことができます。
 * ただし自分自身を変更するメソッド(add()など)は
 * UnsupportedOperationExceptionをスローします。
 * このインタフェースだけが公開されていて、
 * 関連する他のクラスはすべてパッケージプライベートなクラスとなっています。
 *
 * @param <T> 要素の型を指定します。
 */
public interface Seq<T> extends List<T> {

    default boolean add(T e) {
        throw new UnsupportedOperationException();
    }

    default boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    default boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    default boolean addAll(int index, Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    default boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    default boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    default void clear() {
        throw new UnsupportedOperationException();
    }

    default T set(int index, T element) {
        throw new UnsupportedOperationException();
    }

    default void add(int index, T element) {
        throw new UnsupportedOperationException();
    }

    default T remove(int index) {
        throw new UnsupportedOperationException();
    }

    default List<T> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    // Listにデフォルト実装された更新メソッド

    default void sort(Comparator<? super T> comparator) {
        throw new UnsupportedOperationException();
    }

    default void replaceAll​(UnaryOperator<T> operator) {
        throw new UnsupportedOperationException();
    }

    // Seq独自のメソッド

    default Seq<T> subst(int index, T element) {
        return new SubstSeq<>(this, index, element);
    }

    default Seq<T> drop(int index) {
        int size = size();
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("index");
        if (index == 0)
            return subSeq(1);
        else if (index == size - 1)
            return subSeq(0, size - 1);
        else
            return concat(subSeq(0, index), subSeq(index + 1));
    }

    default Seq<T> subSeq(int fromIndex, int toIndex) {
        return new SubSeq<>(this, fromIndex, toIndex);
    }

    default Seq<T> subSeq(int fromIndex) {
        return subSeq(fromIndex, size());
    }

    /**
     * List.sort()は自分自身を変更するので"sort"で、
     * Stream.sorted()はソートした結果を返すので"sorted"だと思われます。
     * Seqの場合はソートした結果を返すので"sorted"としています。
     */
    default Seq<T> sorted(Comparator<T> comparator) {
        return new ListSeq<>(stream().sorted(comparator).collect(Collectors.toList()));
    }

    default Seq<T> sorted() {
        return new ListSeq<>(stream().sorted().collect(Collectors.toList()));
    }

    // staticメソッド

    @SafeVarargs
    public static <T> Seq<T> of(T... elements) {
        return new ListSeq<>(elements);
    }

    public static <T> Seq<T> of(Collection<T> collection) {
        return new ListSeq<>(collection);
    }

    static final Seq<?> EMPTY = new ArraySeq<>();

    @SuppressWarnings("unchecked")
    public static <T> Seq<T> empty() {
        return (Seq<T>) EMPTY;
    }

    public static Seq<Integer> of(int... elements) {
        return new ListSeq<>(Arrays.stream(elements).boxed().collect(Collectors.toList()));
    }

    public static Seq<Long> of(long... elements) {
        return new ListSeq<>(Arrays.stream(elements).boxed().collect(Collectors.toList()));
    }

    public static Seq<Double> of(double... elements) {
        return new ListSeq<>(Arrays.stream(elements).boxed().collect(Collectors.toList()));
    }

    public static Seq<Byte> of(byte... elements) {
        int length = elements.length;
        Byte[] array = new Byte[length];
        for (int i = 0; i < length; ++i)
            array[i] = elements[i];
        return new ListSeq<>(array);
    }

    public static Seq<Short> of(short... elements) {
        int length = elements.length;
        Short[] array = new Short[length];
        for (int i = 0; i < length; ++i)
            array[i] = elements[i];
        return new ListSeq<>(array);
    }

    public static Seq<Character> of(char... elements) {
        int length = elements.length;
        Character[] array = new Character[length];
        for (int i = 0; i < length; ++i)
            array[i] = elements[i];
        return new ListSeq<>(array);
    }

    public static Seq<Float> of(float... elements) {
        int length = elements.length;
        Float[] array = new Float[length];
        for (int i = 0; i < length; ++i)
            array[i] = elements[i];
        return new ListSeq<>(array);
    }

    public static Seq<Character> chars(String s) {
        return of(s.toCharArray());
    }

    public static Seq<Integer> codePoints(String s) {
        return new ListSeq<>(s.codePoints().boxed().collect(Collectors.toList()));
    }

    @SafeVarargs
    public static <T> Seq<T> concat(Seq<T>... seqs) {
        return new ArraySeq<>(seqs);
    }

    public static <T> Seq<T> concat(T e, Seq<T> seq) {
        return new ArraySeq<>(of(e), seq);
    }

    public static <T> Seq<T> concat(Seq<T> seq, T e) {
        return new ArraySeq<>(seq, of(e));
    }
}

/**
 * 配列またはListのラッパーとしてのSeqです。
 * コンストラクタでシャローコピーするので、
 * 元の配列やリストを変更しても、このオブジェクトは変更されません。
 * ただしシャローコピーなので、要素それ自身を変更した場合は
 * このオブジェクトの要素も変更される点に注意する必要があります。
 *
 * @param <T> 要素の型を指定します。
 */
class ListSeq<T> extends AbstractList<T> implements Seq<T> {

    private final List<T> base;

    ListSeq(Collection<T> list) {
        this.base = new ArrayList<T>(list); // シャローコピーします。
    }

    ListSeq(T[] elements) {
        this.base = new ArrayList<>(elements.length);
        for (T e : elements)
            this.base.add(e);
    }

    @Override
    public T get(int index) {
        return base.get(index);
    }

    @Override
    public int size() {
        return base.size();
    }
}

/**
 * 複数のSeqを連結したSeqの実装です。
 *
 * @param <T> 要素の型を指定します。
 */
class ArraySeq<T> extends AbstractList<T> implements Seq<T> {

    private final int size;
    private final Seq<T>[] bases;

    @SafeVarargs
    ArraySeq(Seq<T>... bases) {
        this.size = Arrays.stream(bases).mapToInt(List::size).sum();
        this.bases = bases.clone();
    }

    @Override
    public T get(int index) {
        for (Seq<T> seq : bases) {
            int size = seq.size();
            if (index < size)
                return seq.get(index);
            else
                index -= size;
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    public int size() {
        return this.size;
    }
}

/**
 * Seqの部分範囲を要素とするSeqの実装です。
 *
 * @param <T> 要素の型を指定します。
 */
class SubSeq<T> extends AbstractList<T> implements Seq<T> {

    private final Seq<T> base;
    private final int from, to;

    SubSeq(Seq<T> base, int from, int to) {
        this.base = base;
        this.from = from;
        this.to = to;
    }

    @Override
    public T get(int index) {
        index += from;
        if (index >= to)
            throw new IndexOutOfBoundsException();
        return base.get(index);
    }

    @Override
    public int size() {
        return to - from;
    }
}

/**
 * Seqの1要素を変更した結果のSeqの実装です。
 * Seq.subs(int index, T value)の戻り値を実現します。
 * これはList.set(int index, T value)のSeq版の実装です。
 * Seqは自分自身を変更できないのでSeq.subst()は
 * index番目の要素がvalueに変更された新たなSeqを返します。
 * 以下に使用例を示します。
 * <pre>
 * <code>
 * Seq<Integer> seq = Seq.of(0, 1, 2, 3, 4);
 * Seq<Integer> substituted = seq.subst(2, 20);
 * assertEquals(List.of(0, 1, 20, 3, 4), substituted);
 * </code>
 * </pre>
 *
 * @param <T> 要素の型を指定します。
 */
class SubstSeq<T> extends AbstractList<T> implements Seq<T> {

    private final Seq<T> base;
    private final int index;
    private final T value;

    SubstSeq(Seq<T> base, int index, T value) {
        if (index < 0 || index >= base.size())
            throw new IndexOutOfBoundsException("index");
        this.base = base;
        this.index = index;
        this.value = value;
    }

    @Override
    public T get(int index) {
        if (index == this.index)
            return value;
        return base.get(index);
    }

    @Override
    public int size() {
        return base.size();
    }
}
