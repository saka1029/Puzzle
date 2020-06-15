package experiment;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.junit.Test;

public class TestPermutationCallbackRepeatIterator {

static final List<String> input3 = List.of("a", "b", "c");
static final Set<List<String>> expected30 = Set.of(
    List.of());
static final Set<List<String>> expected31 = Set.of(
    List.of("a"),
    List.of("b"),
    List.of("c"));
static final Set<List<String>> expected32 = Set.of(
    List.of("a", "b"),
    List.of("a", "c"),
    List.of("b", "a"),
    List.of("b", "c"),
    List.of("c", "a"),
    List.of("c", "b"));
static final Set<List<String>> expected33 = Set.of(
    List.of("a", "b", "c"),
    List.of("a", "c", "b"),
    List.of("b", "a", "c"),
    List.of("b", "c", "a"),
    List.of("c", "a", "b"),
    List.of("c", "b", "a"));

/**
 * 再起呼び出しによるコールバック版
 */
static <T> void recursive0Generate(List<T> list, int n, Consumer<List<T>> callback, int index, boolean[] used, T[] selected) {
    if (index >= n)
        callback.accept(List.of(selected));
    else
        for (int i = 0; i < list.size(); ++i) {
            if (!used[i]) {
                selected[index] = list.get(i);
                used[i] = true;
                recursive0Generate(list, n, callback, index + 1, used, selected);
                used[i] = false;
            }
        }
}

static <T> void recursive0(List<T> list, int n, Consumer<List<T>> callback) {
    final int m = list.size();
    final boolean[] used = new boolean[m];
    @SuppressWarnings("unchecked")
    final T[] selected = (T[]) new Object[n];
    recursive0Generate(list, n, callback, 0, used, selected);
}

@Test
public void testRecursive00() {
    recursive0(List.of("a", "b", "c"), 2, System.out::println);
    /*
        [a, b]
        [a, c]
        [b, a]
        [b, c]
        [c, a]
        [c, b]
     */
}

@Test
public void testRecursive0() {
    Set<List<String>> actual = new HashSet<>();
    actual.clear(); recursive0(input3, 0, e -> actual.add(e)); assertEquals(expected30, actual);
    actual.clear(); recursive0(input3, 1, e -> actual.add(e)); assertEquals(expected31, actual);
    actual.clear(); recursive0(input3, 2, e -> actual.add(e)); assertEquals(expected32, actual);
    actual.clear(); recursive0(input3, 3, e -> actual.add(e)); assertEquals(expected33, actual);
}

/**
 * Iterator実装のための疑似コード
 */
/*****************************
static class PseudoIterator<T> implements Iterator<List<T>> {

    @Override
    public boolean hasNext() {
        return ????;
    }

@Override
public List<T> next() {
    if (index >= n)
        return List.of(selected);   // 結果を返します。
    else
        for (int i = 0; i < list.size(); ++i) {
            if (!used[i]) {
                selected[index] = list.get(i);
                used[i] = true;
                recursive0Generate(list, n, callback, index + 1, used, selected);
                // 次の呼び出しの時はここから再開したい。
                used[i] = false;
            }
        }
}
}
*****************************/

/**
 * 引数を減らす
 */
static <T> void recursive1(List<T> list, int n, Consumer<List<T>> callback) {
    new Object() {

        final int m = list.size();
        final boolean[] used = new boolean[m];
        @SuppressWarnings("unchecked")
        final T[] selected = (T[]) new Object[n];

        void generate(int index) {
            if (index >= n)
                callback.accept(List.of(selected));
            else
                for (int i = 0; i < m; ++i) {
                    if (!used[i]) {
                        selected[index] = list.get(i);
                        used[i] = true;
                        generate(index + 1);
                        used[i] = false;
                    }
                }
        }
    }.generate(0);
}

@Test
public void testRecursive1() {
    Set<List<String>> actual = new HashSet<>();
    actual.clear(); recursive1(input3, 0, e -> actual.add(e)); assertEquals(expected30, actual);
    actual.clear(); recursive1(input3, 1, e -> actual.add(e)); assertEquals(expected31, actual);
    actual.clear(); recursive1(input3, 2, e -> actual.add(e)); assertEquals(expected32, actual);
    actual.clear(); recursive1(input3, 3, e -> actual.add(e)); assertEquals(expected33, actual);
}

/**
 * 引数をなくす
 */
static <T> void recursive2(List<T> list, int n, Consumer<List<T>> callback) {
    new Object() {

        final int m = list.size();
        final boolean[] used = new boolean[m];
        @SuppressWarnings("unchecked")
        final T[] selected = (T[]) new Object[n];
        int index = 0;

        void generate() {
            if (index >= n) {
                callback.accept(List.of(selected));
            } else {
                int i = 0;
                while (i < m) {
                    if (!used[i]) {
                        selected[index] = list.get(i);
                        used[i] = true;
                        ++index;
                        generate();
                        --index;
                        used[i] = false;
                    }
                    ++i;
                }
            }
        }
    }.generate();
}

@Test
public void testRecursive2() {
    Set<List<String>> actual = new HashSet<>();
    actual.clear(); recursive2(input3, 0, e -> actual.add(e)); assertEquals(expected30, actual);
    actual.clear(); recursive2(input3, 1, e -> actual.add(e)); assertEquals(expected31, actual);
    actual.clear(); recursive2(input3, 2, e -> actual.add(e)); assertEquals(expected32, actual);
    actual.clear(); recursive2(input3, 3, e -> actual.add(e)); assertEquals(expected33, actual);
}

/**
 * 自前のスタックに引数とローカル変数を退避する
 */
static <T> void recursive3(List<T> list, int n, Consumer<List<T>> callback) {
    new Object() {

        final int m = list.size();
        final boolean[] used = new boolean[m];
        @SuppressWarnings("unchecked")
        final T[] selected = (T[]) new Object[n];
        final Deque<Integer> stack = new LinkedList<>();

        int index = 0;
        int i = 0;

        void generate() {
            if (index >= n) {
                callback.accept(List.of(selected));
            } else
                for (i = 0; i < m; ++i)
                    if (!used[i]) {
                        selected[index] = list.get(i);
                        used[i] = true;
                        stack.push(index);
                        stack.push(i);
                        ++index;
                        generate();
                        i = stack.pop();
                        index = stack.pop();
                        used[i] = false;
                    }
        }
    }.generate();
}

@Test
public void testRecursive3() {
    Set<List<String>> actual = new HashSet<>();
    actual.clear(); recursive3(input3, 0, e -> actual.add(e)); assertEquals(expected30, actual);
    actual.clear(); recursive3(input3, 1, e -> actual.add(e)); assertEquals(expected31, actual);
    actual.clear(); recursive3(input3, 2, e -> actual.add(e)); assertEquals(expected32, actual);
    actual.clear(); recursive3(input3, 3, e -> actual.add(e)); assertEquals(expected33, actual);
}

/**
 * ループをなくす
 */
static <T> void recursive4(List<T> list, int n, Consumer<List<T>> callback) {
    new Object() {

        final int m = list.size();
        final boolean[] used = new boolean[m];
        @SuppressWarnings("unchecked")
        final T[] selected = (T[]) new Object[n];
        final Deque<Integer> stack = new LinkedList<>();
        int index = 0;
        int i = 0;
        State state;

        void generate() {
            state = State.L0;
            while (true)
                switch (state) {
                case L0:
                    if (index >= n) {                               // if (index >= n)
                        callback.accept(List.of(selected));         //     callback.accept(List.of(selected));
                        return;
                    }                                               // else {
                    i = 0;                                          //     i = 0;
                    /* fall through */
                case L1:
                    if (i >= m) {                                   //     while (i < m) {
                        state = State.L2;
                        break;
                    }
                    if (used[i]) {                                  //         if (!used[i]) {
                        state = State.L3;
                        break;
                    }
                    selected[index] = list.get(i);                  //             selected[index] = list.get(i);
                    used[i] = true;                                 //             used[i] = true;
                    stack.push(index);                              //             stack.push(index);
                    stack.push(i);                                  //             stack.push(i);
                    ++index;                                        //             ++index;
                    generate();                                     //             generate();
                    /* fall through */
                case L2:
                    if (stack.isEmpty())
                        return;
                    i = stack.pop();                                //             i = stack.pop();
                    index = stack.pop();                            //             index = stack.pop();
                    used[i] = false;                                //             used[i] = false;
                    /* fall through */                              //          }
                case L3:
                    ++i;                                            //          ++i;
                    state = State.L1;                               //      }
                    break;
                }
        }
    }.generate();
}

@Test
public void testRecursive4() {
    Set<List<String>> actual = new HashSet<>();
    actual.clear(); recursive4(input3, 0, e -> actual.add(e)); assertEquals(expected30, actual);
    actual.clear(); recursive4(input3, 1, e -> actual.add(e)); assertEquals(expected31, actual);
    actual.clear(); recursive4(input3, 2, e -> actual.add(e)); assertEquals(expected32, actual);
    actual.clear(); recursive4(input3, 3, e -> actual.add(e)); assertEquals(expected33, actual);
}

/**
 * Iteratorのテンプレート
 */
/************************
class IteratorTemplate<E> implements Iterator<E> {

    boolean hasNext = true;

    public IteratorTemplate() {
        forward();  // 最初の要素を求めます。
    }

    void forward() {
        // 次の要素を求めます。
        // 次の要素がない場合はhasNextをfalseにしてreturnします。
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public E next() {
        if (hasNext) throw new NoSuchElementException();
        E result = ???  // 直前のforward()で求めた要素をコピーしておきます。
        forward();      // 次の要素を先に求めておきます。
        return result;  // 退避した要素を返します。
    }
}

************************/

/**
 * 再起呼び出しをやめる
 */
static enum State {L0, L1, L2, L3}
static <T> void repeat2(List<T> list, int n, Consumer<List<T>> callback) {
    new Object() {

        final int m = list.size();
        final boolean[] used = new boolean[m];
        @SuppressWarnings("unchecked")
        final T[] selected = (T[]) new Object[n];
        final Deque<Integer> stack = new LinkedList<>();
        int index = 0;
        int i = 0;
        State state = State.L0;

        void generate() {
            while (true)
                switch (state) {
                case L0:
                    if (index >= n) {                               // if (index >= n)
                        callback.accept(List.of(selected));         //     callback.accept(List.of(selected));
                        state = State.L2;
                        break;
                    }                                               // else {
                    i = 0;                                          //     i = 0;
                    /* fall through */
                case L1:
                    if (i >= m) {                                   //     while (i < m) {
                        state = State.L2;
                        break;
                    }
                    if (used[i]) {                                  //         if (!used[i]) {
                        state = State.L3;
                        break;
                    }
                    selected[index] = list.get(i);                  //             selected[index] = list.get(i);
                    used[i] = true;                                 //             used[i] = true;
                    stack.push(index);
                    stack.push(i);                                  //             stack.push(i);
                    ++index;                                        //             ++index;
                    state = State.L0;                               //             generate();
                    break;
                case L2:
                    if (stack.isEmpty())
                        return;
                    i = stack.pop();                                //             i = stack.pop();
                    index = stack.pop();                            //             index = stack.pop();
                    used[i] = false;                                //             used[i] = false;
                    /* fall through */                              //          }
                case L3:
                    ++i;                                            //          ++i;
                    state = State.L1;                               //      }
                    break;
                }
        }
    }.generate();
}

@Test
public void testRepeat2() {
    Set<List<String>> actual = new HashSet<>();
    actual.clear(); repeat2(input3, 0, e -> actual.add(e)); assertEquals(expected30, actual);
    actual.clear(); repeat2(input3, 1, e -> actual.add(e)); assertEquals(expected31, actual);
    actual.clear(); repeat2(input3, 2, e -> actual.add(e)); assertEquals(expected32, actual);
    actual.clear(); repeat2(input3, 3, e -> actual.add(e)); assertEquals(expected33, actual);
}

/**
 * Iteratorとして実装する
 */
static class PermutationIterator0<T> implements Iterator<List<T>> {

    enum State {L0, L1, L2, L3};
    final List<T> list;
    final int m, n;
    final boolean[] used;
    final T[] selected;
    final Deque<Integer> stack;
    boolean hasNext;
    int index, i;
    State state;

    @SuppressWarnings("unchecked")
    public PermutationIterator0(List<T> list, int n) {
        this.list = list;
        this.m = list.size();
        this.n = n;
        this.used = new boolean[this.m];
        this.selected = (T[]) new Object[n];
        this.stack = new LinkedList<>();
        this.hasNext = true;
        this.index = 0;
        this.state = State.L0;
        forward();
    }

    void forward() {
        if (!hasNext) return;
        while (true)
            switch (state) {
            case L0:
                if (index >= n) {                               // if (index >= n)
                    state = State.L2;                           //     accept(List.of(selected))
                    return;
                }                                               // else {
                i = 0;                                          //     i = 0;
                /* fall through */
            case L1:
                if (i >= m) {                                   //     while (i < m) {
                    state = State.L2;
                    break;
                }
                if (used[i]) {                                  //         if (!used[i]) {
                    state = State.L3;
                    break;
                }
                selected[index] = list.get(i);                  //             selected[index] = list.get(i);
                used[i] = true;                                 //             used[i] = true;
                stack.push(index);                              //             stack.push(index);
                stack.push(i);                                  //             stack.push(i);
                ++index;                                        //             ++index;
                state = State.L0;                               //             generate();
                break;
            case L2:
                if (stack.isEmpty()) {
                    hasNext = false;
                    return;
                }
                i = stack.pop();                                //             i = stack.pop();
                index = stack.pop();                            //             index = stack.pop();
                used[i] = false;                                //             used[i] = false;
                /* fall through */
            case L3:
                ++i;                                            //          ++i;
                state = State.L1;                               //      }
                break;
            }
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public List<T> next() {
        if (!hasNext) throw new NoSuchElementException();
        List<T> result = List.of(selected);
        forward();
        return result;
    }

}

static <T> Iterator<List<T>> iterator0(List<T> list, int n) {
    return new PermutationIterator0<>(list, n);
}

static <T> Set<List<T>> testIterator0(List<T> list, int n) {
    Set<List<T>> actual = new HashSet<>();
    for (Iterator<List<T>> i = iterator0(list, n); i.hasNext();)
        actual.add(i.next());
    return actual;
}

@Test
public void testIterator0() {
    assertEquals(expected30, testIterator0(input3, 0));
    assertEquals(expected31, testIterator0(input3, 1));
    assertEquals(expected32, testIterator0(input3, 2));
    assertEquals(expected33, testIterator0(input3, 3));
}

/**
 * Iterableを実装する
 */
static <T> Iterable<List<T>> iterable0(List<T> list, int n) {
    return () -> iterator0(list, n);
}

static <T> Set<List<T>> testIterable0(List<T> list, int n) {
    Set<List<T>> actual = new HashSet<>();
    for (List<T> x : iterable0(list, n))
        actual.add(x);
    return actual;
}

@Test
public void testIterable0() {
    assertEquals(expected30, testIterable0(input3, 0));
    assertEquals(expected31, testIterable0(input3, 1));
    assertEquals(expected32, testIterable0(input3, 2));
    assertEquals(expected33, testIterable0(input3, 3));
}

/**
 * Streamを実装する
 */
static <T> Stream<List<T>> stream0(List<T> list, int n) {
    return StreamSupport.stream(iterable0(list, n).spliterator(), false);
}

static <T> Set<List<T>> testStream0(List<T> list, int n) {
    return stream0(list, n).collect(Collectors.toSet());
}

@Test
public void testStream0() {
    assertEquals(expected30, testStream0(input3, 0));
    assertEquals(expected31, testStream0(input3, 1));
    assertEquals(expected32, testStream0(input3, 2));
    assertEquals(expected33, testStream0(input3, 3));
}

/**
 * アドホックなやり方で再起呼び出しをやめる
 */
static <T> void repeat5(List<T> list, int n, Consumer<List<T>> callback) {
    final int m = list.size();
    final boolean[] used = new boolean[m];
    @SuppressWarnings("unchecked")
    final T[] selected = (T[]) new Object[n];
    final Deque<Integer> stack = new LinkedList<>();
    new Object() {
        int index = 0;
        int i;
        boolean init = true;
        void generate() {
            L: while (true) {   // 再帰を繰り返しに変える。
                if (index >= n)
                    callback.accept(List.of(selected));
                else {
                    for (i = init ? 0 : i; i < m; ++i) {    // initが真の時だけiを初期化する
                        if (!used[i]) {
                            selected[index] = list.get(i);
                            used[i] = true;
                            stack.push(index);   // 引数indexを退避する。
                            stack.push(i);  // ローカル変数iを退避する。
                            ++index;
                            init = true;    // 内側のwhileループはi=0で初期化する。
                            continue L;     // 次のindex値で繰り返し
                        }
                    }
                }
                // callbackを呼び出した後、またはfor文の実行後にここに来る。
                // いずれの場合もひとつ前のindex値で繰り返す必要がある。
                if (stack.isEmpty()) break; // 戻るべき処理がなければ終了する。
                i = stack.pop();  // ローカル変数iを回復する。
                index = stack.pop(); // 引数indexを回復する。
                used[i] = false;    // 使用していた値を未使用に戻す。
                ++i;
                init = false;   // 内側のwhileループの途中から再開する。
            }
        }
    }.generate();
}

@Test
public void testRepeat5() {
    Set<List<String>> actual = new HashSet<>();
    actual.clear(); repeat5(input3, 0, e -> actual.add(e)); assertEquals(expected30, actual);
    actual.clear(); repeat5(input3, 1, e -> actual.add(e)); assertEquals(expected31, actual);
    actual.clear(); repeat5(input3, 2, e -> actual.add(e)); assertEquals(expected32, actual);
    actual.clear(); repeat5(input3, 3, e -> actual.add(e)); assertEquals(expected33, actual);
}

}