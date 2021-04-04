package test.puzzle;

import static puzzle.Iterables.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

/**
 *
 * 小町算 - Wikipedia
 * https://ja.wikipedia.org/wiki/%E5%B0%8F%E7%94%BA%E7%AE%97#%E3%81%9D%E3%81%AE%E4%BB%96
 *
 * 欧米ではセンチュリーパズルと呼ばれる問題がある。1～9の数を1つずつ使用し、帯分数の形で100を表すものである。
 * <pre>
 *       1428
 * 96 + ------ = 100
 *        357
 *
 *      69258
 * 3 + ------- = 100
 *       714
 * </pre>
 * などがある
 * イギリスのヘンリー・E・デュードニーによって、11解が発表されている。
 *
 * Dudeney's Century Puzzle
 *
 * Dudeney-s-Century-Puzzle.pdf
 * https://www.pepsplace.org.uk/Trivia/JRM/Dudeney-s-Century-Puzzle.pdf
 *
 * This is my solution to problem 2876 in Journal of Recreational Mathematics,
 * vol 38, #1, set by * Andy Pepperdine.
 *
 * For (a) and (b) all representations are included in the following table:
 *
 *<pre>
 * Nine digits Ten digits result = 10
 *
 * (9 digits)
 * 6 + 5892 / 1473
 * 7 + 5469 / 1823
 * 7 + 5496 / 1832
 * 7 + 6549 / 2183
 * 7 + 6954 / 2318
 * 7 + 9546 / 3182
 * 7 + 9654 / 3218
 *
 * (10 digits)
 * 1 + 85203 / 9467
 * 6 + 19032 / 4758
 * 6 + 30192 / 7548
 * 6 + 37140 / 9285
 *
 * result = 100
 *
 * (9 digits)
 * 3 + 69258 / 714
 * 81 + 5643 / 297
 * 81 + 7524 / 396
 * 82 + 3546 / 197
 * 91 + 5742 / 638
 * 91 + 5823 / 647
 * 91 + 7524 / 836
 * 94 + 1578 / 263
 * 96 + 1428 / 357
 * 96 + 1752 / 438
 * 96 + 2148 / 537
 *
 * (10 digits)
 * 27 + 65043 / 891
 * 36 + 57024 / 891
 * 43 + 51072 / 896
 * 45 + 21780 / 396
 * 51 + 34692 / 708
 * 72 + 13860 / 495
 * 73 + 24516 / 908
 * 82 + 10674 / 593
 *
 * result = 1000
 *
 * (9 digits)
 * 534 + 9786 / 21
 * 597 + 4836 / 12
 * 597 + 8463 / 21
 * 751 + 9462 / 38
 * 756 + 4392 / 18
 * 913 + 4872 / 56
 * 924 + 3876 / 51
 * 951 + 4263 / 87
 * 954 + 3726 / 81
 * 957 + 3612 / 84
 * 967 + 1254 / 38
 *
 * (10 digits)
 * 153 + 60984 / 72
 * 208 + 41976 / 53
 * 396 + 27180 / 45
 * 561 + 40827 / 93
 * 745 + 21930 / 86
 * 843 + 15072 / 96
 * 957 + 4386 / 102
 * 957 + 8643 / 201
 * 964 + 3852 / 107
 * 987 + 4056 / 312
 *
 * result = 10000
 *
 * (9 digits)
 * 348 + 57912 / 6
 * 451 + 76392 / 8
 * 631 + 74952 / 8
 * 948 + 27156 / 3
 * 978 + 54132 / 6
 * 7914 + 6258 / 3
 * 9316 + 5472 / 8
 * 9541 + 3672 / 8
 * 9753 + 1482 / 6
 *
 * (10 digits)
 * 1047 + 26859 / 3
 * 3691 + 50472 / 8
 * 4785 + 31290 / 6
 * 4908 + 15276 / 3
 * 5041 + 39672 / 8
 * 5401 + 36792 / 8
 * 5491 + 36072 / 8
 * 7503 + 14982 / 6
 * 7845 + 12930 / 6
 * 9345 + 7860 / 12
 * 9435 + 6780 / 12
 * 9637 + 5082 / 14
 * 9702 + 5364 / 18
 * 9745 + 8160 / 32
 * 9765 + 4230 / 18
 * 9853 + 6027 / 41
 *
 * result = 100000
 *
 * (9 digits)
 * None
 *
 * (10 digits)
 * 321 + 598074 / 6
 * 376 + 498120 / 5
 * 483 + 597102 / 6
 * 651 + 298047 / 3
 *
 * For (c), there are 29 ways of representing 26 using the nine non-zero digits:
 *
 * 3 + 21758 / 946
 * 18 + 4736 / 592
 * 21 + 3485 / 697
 * 4 + 16258 / 739
 * 18 + 5392 / 674
 * 21 + 3845 / 769
 * 8 + 17352 / 964
 * 18 + 5432 / 679
 * 21 + 4685 / 937
 * 9 + 12546 / 738
 * 18 + 5936 / 742
 * 21 + 4835 / 967
 * 12 + 6398 / 457
 * 18 + 6352 / 794
 * 21 + 4865 / 973
 * 14 + 3576 / 298
 * 18 + 7456 / 932
 * 23 + 1974 / 658
 * 18 + 3672 / 459
 * 18 + 7536 / 942
 * 24 + 1358 / 679
 * 18 + 3752 / 469
 * 18 + 7624 / 953
 * 24 + 1538 / 769
 * 18 + 4296 / 537
 * 18 + 7632 / 954
 * 24 + 1586 / 793
 * 18 + 4632 / 579
 * 19 + 5236 / 748
 *
 * And 30 ways if we include a zero:
 *
 * 3 + 24587 / 1069
 * 8 + 57042 / 3169
 * 9 + 54706 / 3218
 * 6 + 34580 / 1729
 * 9 + 26078 / 1534
 * 9 + 58072 / 3416
 * 6 + 35840 / 1792
 * 9 + 34867 / 2051
 * 9 + 68357 / 4021
 * 6 + 38540 / 1927
 * 9 + 35768 / 2104
 * 9 + 68527 / 4031
 * 6 + 43580 / 2179
 * 9 + 46801 / 2753
 * 9 + 71536 / 4208
 * 6 + 47180 / 2359
 * 9 + 47651 / 2803
 * 9 + 78251 / 4603
 * 6 + 54380 / 2719
 * 9 + 51476 / 3028
 * 9 + 80512 / 4736
 * 6 + 58340 / 2917
 * 9 + 51782 / 3046
 * 9 + 86241 / 5073
 * 6 + 71840 / 3592
 * 9 + 52768 / 3104
 * 9 + 86734 / 5102
 * 7 + 30286 / 1594
 * 9 + 54026 / 3178
 * 24 + 6158 / 3079
 * </pre>
 * Andy Pepperdine
 *
 */
class TestCenturyPuzzle {

    static long number(List<Integer> digits) {
        if (digits.get(0) == 0)
            return -1;
        return reduce(0L, (a, b) -> a * 10 + b, digits);
    }

    static long number(List<Integer> digits, int start, int end) {
        if (digits.size() == 0 || end - start == 0)
            throw new IllegalArgumentException(
                "digits.size=" + digits.size() + " start=" + start + " end=" + end);
        return number(digits.subList(start, end));
    }

    static record Answer(long goal, long a, long b, long c) implements Comparable<Answer> {
        @Override
        public int compareTo(Answer o) {
            int r = Long.compare(goal, o.goal);
            if (r == 0) r = Long.compare(a, o.a);
            if (r == 0) r = Long.compare(b, o.b);
            if (r == 0) r = Long.compare(c, o.c);
            return r;
        }

        @Override
        public String toString() {
            return String.format("%d + %d / %d = %d", a, b, c, goal);
        }
    }

    static void test(List<Integer> digits, Map<Integer, Set<Answer>> answers, int asize, int bsize, int... goals) {
        int pos = 0;
        long a = number(digits, pos, pos += asize);
        long b = number(digits, pos, pos += bsize);
        long c = number(digits, pos, digits.size());
        if (a < 0 || b < 0 || c < 0 || b % c != 0)
            return;
        for (int goal : goals)
            if (a + b / c == goal) {
                answers.computeIfAbsent(goal, k -> new TreeSet<>())
                    .add(new Answer(goal, a, b, c));
                return;
            }
    }

    static void check(List<Integer> digits, Map<Integer, Set<Answer>> answers, int... goals) {
        for (int a = 1, size = digits.size(); a < size; ++a)
            for (int b = 1, bsize = size - a; b < bsize; ++b)
                test(digits, answers, a, b, goals);
    }

    static void centuryPuzzle(Iterable<Integer> digits) {
        Map<Integer, Set<Answer>> answers9 = new TreeMap<>();
        forEach(list -> check(list, answers9, 10, 100, 1000, 10000, 100000), permutation(digits));
        for (Set<Answer> list : answers9.values())
            for (Answer a : list)
                System.out.println(a);
        System.out.println();
    }

    static void centuryPuzzle() {
        centuryPuzzle(rangeClosed(1, 9));
        centuryPuzzle(rangeClosed(0, 9));
    }

    @Test
    void test() {
        centuryPuzzle();
    }

}
