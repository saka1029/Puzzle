package test.puzzle;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Objects;

import org.junit.jupiter.api.Test;

/**
 * java - Longest Increasing Subsequence using top down approach - Stack Overflow
 *
 * <pre>
 * https://stackoverflow.com/questions/63097680/longest-increasing-subsequence-using-top-down-approach
 *
 * Given an unsorted array of integers, find the length of the longest increasing subsequence.
 *
 * Example:
 * Input: [10,9,2,5,3,7,101,18]
 * Output: 4
 * Explanation: The longest increasing subsequence is [2,3,7,101], therefore the length is 4.
 * </pre>
 *
 */
public class TestLongestIncreasingSubsequence {

    /**
     * Longest Increasing Subsequenceの長さを計算します。
     *
     * Sequenceの先頭を何と比較するかを考えていて、
     * 最初はprevをInteger.MIN_VALUEにしていた。
     * しかし配列a自身がInteger.MIN_VALUEを含む場合は
     * うまくいかないことに気づいた。
     * 先頭であることを表す情報を引数に追加すればよいと考え、
     * boolean firstを追加した。
     *
     * @param a Subsequenceを見つけるべき配列です。
     * @param i 配列aの中の次に見つけるべき位置です。
     * @param first Subsequenceの先頭であるかどうかを示します。
     * @param prev Subsequenceの直前の値を示します。
     *        ただしfirstが真のときは直前の値が存在しません。
     *        その場合はprevで与えられた値は無意味な値であることに注意する必要があります。
     * @param count Subsequenceのこれまでの長さです。
     * @return 最大のSubsequenceの長さを返します。
     */
    static int lengthOfLIS0(int[] a, int i, boolean first, int prev, int count) {
        if (i >= a.length)
            return count;
        else if (first || a[i] > prev)
            return Math.max(
                lengthOfLIS0(a, i + 1, false, a[i], count + 1),
                lengthOfLIS0(a, i + 1, first, prev, count));
        else
            return lengthOfLIS0(a, i + 1, first, prev, count);
    }

    public static int lengthOfLIS0(int[] a) {
        Objects.requireNonNull(a, "a must not be null");
        return lengthOfLIS0(a, 0, true, 0, 0);
    }

    public static int lengthOfLIS(int[] a) {
        Objects.requireNonNull(a, "a must not be null");
        return new Object() {
            int lengthOfLIS(int[] a, int i, boolean first, int prev) {
                if (i >= a.length)
                    return 0;
                int result = lengthOfLIS(a, i + 1, first, prev);
                if (first || a[i] > prev)
                    result = Math.max(result, 1 + lengthOfLIS(a, i + 1, false, a[i]));
                return result;
            }
        }.lengthOfLIS(a, 0, true, 0);
    }

    @Test
    public void testLengthOfLIS() {
        assertEquals(4, lengthOfLIS(new int[] {10, 9, 2, 5, 3, 7, 101, 18}));
        try {
            assertEquals(0, lengthOfLIS(null));
            fail();
        } catch (NullPointerException e) {}
        assertEquals(0, lengthOfLIS(new int[] {}));
        assertEquals(1, lengthOfLIS(new int[] {3}));
        assertEquals(1, lengthOfLIS(new int[] {3, 2}));
        assertEquals(1, lengthOfLIS(new int[] {3, 3}));
        assertEquals(2, lengthOfLIS(new int[] {3, 4}));
        assertEquals(4, lengthOfLIS(new int[] {1, 2, 3, 4}));
        assertEquals(4, lengthOfLIS(new int[] {4, 3, 2, 1, 2, 3, 4}));
        assertEquals(5, lengthOfLIS(new int[] {1, 10, 20, 2, 3, 4, 5}));
        int M = Integer.MIN_VALUE;
        assertEquals(1, lengthOfLIS(new int[] {M, M}));
        assertEquals(2, lengthOfLIS(new int[] {M, M + 1}));
    }

    /**
     * 出題サイトの解答
     *
     * Longest Increasing Subsequence - LeetCode
     * https://leetcode.com/problems/longest-increasing-subsequence/solution/
     *
     * <pre>
     * 感想:
     * (1) 配列にInteger.MIN_VALUEを含む場合が考慮されていない。
     * (2) count引数は不要だったことに気づく。
     * </pre>
     */
    public int lengthofLIS(int[] nums) {
        return lengthofLIS(nums, Integer.MIN_VALUE, 0);
    }

    public int lengthofLIS(int[] nums, int prev, int curpos) {
        if (curpos == nums.length) {
            return 0;
        }
        int taken = 0;
        if (nums[curpos] > prev) {
            taken = 1 + lengthofLIS(nums, nums[curpos], curpos + 1);
        }
        int nottaken = lengthofLIS(nums, prev, curpos + 1);
        return Math.max(taken, nottaken);
    }

    @Test
    public void testLengthofLIS() {
        assertEquals(4, lengthofLIS(new int[] {10, 9, 2, 5, 3, 7, 101, 18}));
        assertEquals(0, lengthofLIS(new int[] {}));
        assertEquals(1, lengthofLIS(new int[] {3}));
        assertEquals(1, lengthofLIS(new int[] {3, 2}));
        assertEquals(1, lengthofLIS(new int[] {3, 3}));
        assertEquals(2, lengthofLIS(new int[] {3, 4}));
        assertEquals(4, lengthofLIS(new int[] {1, 2, 3, 4}));
        assertEquals(4, lengthofLIS(new int[] {4, 3, 2, 1, 2, 3, 4}));
        assertEquals(5, lengthofLIS(new int[] {1, 10, 20, 2, 3, 4, 5}));
//        assertEquals(1, lengthofLIS(new int[] {Integer.MIN_VALUE, Integer.MIN_VALUE}));
//        assertEquals(2, lengthofLIS(new int[] {Integer.MIN_VALUE, Integer.MIN_VALUE + 1}));
    }

    /**
     * Stack overflow質問者のコード
     */
    public int lengthOfLIS_PO(int[] a) {
        if(a==null||a.length==0)
            return 0;
        int n=a.length;
        int dp[]=new int[n];
        Arrays.fill(dp,-1);
        helper(dp,a,Integer.MIN_VALUE,0);
        int ans=0;
        for(int i=0;i<dp.length;i++)    // 配列dpの最大値を求める。
            ans=Math.max(ans, dp[i]);
        return ans;

    }
    int helper(int[] dp,int[] a,int prev,int currIndex){ // 戻り値は使わない。
        if(currIndex==dp.length)
            return 0;
//        if(dp[currIndex]!=-1)                         // この2行を削除するとうまくいく。
//            return dp[currIndex];
        int tmp1=0,tmp2=0;
        if(prev<a[currIndex])
            tmp1=1+helper(dp,a,a[currIndex],currIndex+1);
        tmp2=helper(dp,a,prev,currIndex+1);
        dp[currIndex]=Math.max(tmp1,tmp2);
        return dp[currIndex];
    }

    @Test
    public void lengthOfLIS_PO() {
        assertEquals(4, lengthOfLIS_PO(new int[] {10, 9, 2, 5, 3, 7, 101, 18}));
        assertEquals(0, lengthOfLIS_PO(null));
        assertEquals(0, lengthOfLIS_PO(new int[] {}));
        assertEquals(1, lengthOfLIS_PO(new int[] {3}));
        assertEquals(1, lengthOfLIS_PO(new int[] {3, 2}));
        assertEquals(1, lengthOfLIS_PO(new int[] {3, 3}));
        assertEquals(2, lengthOfLIS_PO(new int[] {3, 4}));
        assertEquals(4, lengthOfLIS_PO(new int[] {1, 2, 3, 4}));
        assertEquals(4, lengthOfLIS_PO(new int[] {4, 3, 2, 1, 2, 3, 4}));
        assertEquals(5, lengthOfLIS_PO(new int[] {1, 10, 20, 2, 3, 4, 5}));
//        assertEquals(2, lengthOfLIS_PO(new int[] {Integer.MIN_VALUE, Integer.MIN_VALUE + 1}));
//        assertEquals(1, lengthOfLIS_PO(new int[] {Integer.MIN_VALUE, Integer.MIN_VALUE}));
    }

}
