package test.puzzle.language;

import java.util.Map;
import java.util.function.Consumer;

public class SendMoreMoney {
    public static void solve(Consumer<Map<String, Integer>> callback) {
        int[] _s_domain = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[] _e_domain = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[] _n_domain = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[] _d_domain = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[] _m_domain = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[] _o_domain = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[] _r_domain = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[] _y_domain = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        for (int s : _s_domain)
            for (int e : _e_domain)
                if (s != e)
                    for (int n : _n_domain)
                        if (s != n && e != n)
                            for (int d : _d_domain)
                                if (s != d && e != d && n != d)
                                    for (int m : _m_domain)
                                        if (e != m && n != m && s != m
                                            && d != m)
                                            for (int o : _o_domain)
                                                if (n != o && e != o && d != o
                                                    && s != o && m != o)
                                                    for (int r : _r_domain)
                                                        if (n != r && m != r
                                                            && s != r && o != r
                                                            && e != r && d != r)
                                                            for (int y : _y_domain)
                                                                if (e != y
                                                                    && ((s * 10
                                                                        + e)
                                                                        * 10
                                                                        + n)
                                                                        * 10
                                                                        + d
                                                                        + ((m
                                                                            * 10
                                                                            + o)
                                                                            * 10
                                                                            + r)
                                                                            * 10
                                                                        + e == (((m
                                                                            * 10
                                                                            + o)
                                                                            * 10
                                                                            + n)
                                                                            * 10
                                                                            + e)
                                                                            * 10
                                                                            + y
                                                                    && d != y
                                                                    && r != y
                                                                    && n != y
                                                                    && o != y
                                                                    && m != y
                                                                    && s != y)
                                                                    callback
                                                                        .accept(
                                                                            Map.of(
                                                                                "s",
                                                                                s,
                                                                                "e",
                                                                                e,
                                                                                "n",
                                                                                n,
                                                                                "d",
                                                                                d,
                                                                                "m",
                                                                                m,
                                                                                "o",
                                                                                o,
                                                                                "r",
                                                                                r,
                                                                                "y",
                                                                                y));
    }
}