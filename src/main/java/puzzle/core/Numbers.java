package puzzle.core;

public class Numbers {
    
    private Numbers() {}
    
    public static int gcd(int m, int n) {
        while (n != 0) {
            int temp = n;
            n = m % n;
            m = temp;
        }
        return Math.abs(m);
    }

    public static int lcm(int m, int n) {
        int gcd = gcd(m, n);
        return gcd == 0 ? 0 : Math.abs(m / gcd * n);
    }

    public static int lcm1(int m, int n) {
        if (m == 0 || n == 0)
            return 0;
        if (m < 0)
            return lcm(-m, n);
        if (n < 0)
            return lcm(m, -n);
        if (m < n)
            return lcm(n, m);
        int r = m;
        while (r % n != 0)
            r += m;
        return r;
    }

}
