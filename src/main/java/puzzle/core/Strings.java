package puzzle.core;

public class Strings {

    static final String[] SAME_HASH_CODES = {"at", "bU", "c6"};
    public static String sameHashCodeString(int n) {
        int mod = SAME_HASH_CODES.length;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; ++i, n /= mod)
            sb.append(SAME_HASH_CODES[n % mod]);
        return sb.toString();
    }

    public static void main(String[] args) {
        for (int i = 1; i < 1000; ++i) {
            String s = sameHashCodeString(i);
            System.out.println(s + " " + s.hashCode());
        }
    }

}
