package puzzle.core;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.Objects;

public final class Rational extends Number implements Comparable<Rational> {

    public static final Rational NaN = new Rational(0, 0);
    public static final Rational POSITIVE_INFINITY = new Rational(1, 0);
    public static final Rational NEGATIVE_INFINITY = new Rational(-1, 0);
    public static final Rational ZERO = new Rational(0, 1);
    public static final Rational ONE = new Rational(1, 1);
    public static final Rational MINUS_ONE = new Rational(-1, 1);
    private static final long serialVersionUID = 1L;

    public final long numerator;
    public final long denominator;

    private Rational(long numerator, long denominator) {
        if (denominator < 0) {
            numerator = -numerator;
            denominator = -denominator;
        }
        // Convert to reduced form
        if (denominator == 0 && numerator > 0) {
            this.numerator = 1; // +Inf
            this.denominator = 0;
        } else if (denominator == 0 && numerator < 0) {
            this.numerator = -1; // -Inf
            this.denominator = 0;
        } else if (denominator == 0 && numerator == 0) {
            this.numerator = 0; // NaN
            this.denominator = 0;
        } else if (numerator == 0) {
            this.numerator = 0;
            this.denominator = 1;
        } else {
            long gcd = gcd(numerator, denominator);
            this.numerator = numerator / gcd;
            this.denominator = denominator / gcd;
        }
    }

    private Rational(long numerator) {
        this(numerator, 1);
    }

    public static Rational of(long numerator, long denominator) {
        return new Rational(numerator, denominator);
    }

    public static Rational of(long numerator) {
        return new Rational(numerator);
    }

    public Rational multiply(Rational r) {
        return new Rational(numerator * r.numerator, denominator * r.denominator);
    }

    public Rational divide(Rational r) {
        return new Rational(numerator * r.denominator, denominator * r.numerator);
    }

    public Rational add(Rational r) {
        return new Rational(numerator * r.denominator + r.numerator * denominator, denominator * r.denominator);
    }

    public Rational subtract(Rational r) {
        return new Rational(numerator * r.denominator - r.numerator * denominator, denominator * r.denominator);
    }

    public boolean isNaN() { return denominator == 0 && numerator == 0; }
    public boolean isInfinite() { return numerator != 0 && denominator == 0; }
    public boolean isFinite() { return denominator != 0; }
    public boolean isZero() { return isFinite() && numerator == 0; }
    private boolean isPosInf() { return denominator == 0 && numerator > 0; }
    private boolean isNegInf() { return denominator == 0 && numerator < 0; }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Rational && equals((Rational) obj);
    }
    private boolean equals(Rational other) {
        return (numerator == other.numerator && denominator == other.denominator);
    }

    @Override
    public String toString() {
        if (isNaN())
            return "NaN";
        else if (isPosInf())
            return "Infinity";
        else if (isNegInf())
            return "-Infinity";
        else if (denominator == 1)
            return numerator + "";
        else
            return numerator + "/" + denominator;
    }

    public float toFloat() {
        // TODO: remove this duplicate function (used in CTS and the shim)
        return floatValue();
    }

    @Override
    public int hashCode() {
        // Bias the hash code for the first (2^16) values for both numerator and denominator
        long numeratorFlipped = numerator << 16 | numerator >>> 16;
        return (int)(denominator ^ numeratorFlipped);
    }

    public static long gcd(long numerator, long denominator) {
        long a = numerator;
        long b = denominator;
        while (b != 0) {
            long oldB = b;
            b = a % b;
            a = oldB;
        }
        return Math.abs(a);
    }

    @Override
    public double doubleValue() {
        return (double)numerator / (double)denominator;
    }
    /**
     * Returns the value of the specified number as a {@code float}.
     *
     * <p>The {@code float} is calculated by converting both the numerator and denominator
     * to a {@code float}; then returning the result of dividing the numerator by the
     * denominator.</p>
     *
     * @return the divided value of the numerator and denominator as a {@code float}.
     */
    @Override
    public float floatValue() {
        return (float)numerator / (float)denominator;
    }

    @Override
    public int intValue() {
        if (isPosInf())
            return Integer.MAX_VALUE;
        else if (isNegInf())
            return Integer.MIN_VALUE;
        else if (isNaN())
            return 0;
        else // finite
            return (int)(numerator / denominator);
    }
    /**
     * Returns the value of the specified number as a {@code long}.
     *
     * <p>{@link #isInfinite Finite} rationals are converted to an {@code long} value
     * by dividing the numerator by the denominator; conversion for non-finite values happens
     * identically to casting a floating point value to a {@code long}, in particular:
     *
     * <p>
     * <ul>
     * <li>Positive infinity saturates to the largest maximum long
     * {@link Long#MAX_VALUE}</li>
     * <li>Negative infinity saturates to the smallest maximum long
     * {@link Long#MIN_VALUE}</li>
     * <li><em>Not-A-Number (NaN)</em> returns {@code 0}.</li>
     * </ul>
     * </p>
     *
     * @return the divided value of the numerator and denominator as a {@code long}.
     */
    @Override
    public long longValue() {
        if (isPosInf())
            return Long.MAX_VALUE;
        else if (isNegInf())
            return Long.MIN_VALUE;
        else if (isNaN())
            return 0;
        else // finite
            return numerator / denominator;
    }

    @Override
    public short shortValue() {
        return (short) intValue();
    }

    @Override
    public int compareTo(Rational another) {
        Objects.requireNonNull(another, "another must not be null");
        if (equals(another))
            return 0;
        else if (isNaN()) // NaN is greater than the other non-NaN value
            return 1;
        else if (another.isNaN()) // the other NaN is greater than this non-NaN value
            return -1;
        else if (isPosInf() || another.isNegInf())
            return 1; // positive infinity is greater than any non-NaN/non-posInf value
        else if (isNegInf() || another.isPosInf())
            return -1; // negative infinity is less than any non-NaN/non-negInf value
        // else both this and another are finite numbers
        // make the denominators the same, then compare numerators
        long thisNumerator = ((long)numerator) * another.denominator; // long to avoid overflow
        long otherNumerator = ((long)another.numerator) * denominator; // long to avoid overflow
        // avoid underflow from subtraction by doing comparisons
        if (thisNumerator < otherNumerator)
            return -1;
        else if (thisNumerator > otherNumerator)
            return 1;
        else
            return 0;
    }
    /*
     * Serializable implementation.
     *
     * The following methods are omitted:
     * >> writeObject - the default is sufficient (field by field serialization)
     * >> readObjectNoData - the default is sufficient (0s for both fields is a NaN)
     */
    /**
     * writeObject with default serialized form - guards against
     * deserializing non-reduced forms of the rational.
     *
     * @throws InvalidObjectException if the invariants were violated
     */
    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        /*
         * Guard against trying to deserialize illegal values (in this case, ones
         * that don't have a standard reduced form).
         *
         * - Non-finite values must be one of [0, 1], [0, 0], [0, 1], [0, -1]
         * - Finite values must always have their greatest common divisor as 1
         */
        if (numerator == 0) { // either zero or NaN
            if (denominator == 1 || denominator == 0)
                return;
            throw new InvalidObjectException(
                    "Rational must be deserialized from a reduced form for zero values");
        } else if (denominator == 0) { // either positive or negative infinity
            if (numerator == 1 || numerator == -1)
                return;
            throw new InvalidObjectException(
                    "Rational must be deserialized from a reduced form for infinity values");
        } else // finite value
            if (gcd(numerator, denominator) > 1)
                throw new InvalidObjectException(
                        "Rational must be deserialized from a reduced form for finite values");
    }
    private static NumberFormatException invalidRational(String s) {
        throw new NumberFormatException("Invalid Rational: \"" + s + "\"");
    }

    public static Rational parseRational(String string) throws NumberFormatException {
        Objects.requireNonNull(string, "string must not be null");
        if (string.equals("NaN"))
            return NaN;
        else if (string.equals("Infinity"))
            return POSITIVE_INFINITY;
        else if (string.equals("-Infinity"))
            return NEGATIVE_INFINITY;
        int sep_ix = string.indexOf(':');
        if (sep_ix < 0)
            sep_ix = string.indexOf('/');
        try {
            if (sep_ix < 0)
                return new Rational(Long.parseLong(string));
            else
                return new Rational(Long.parseLong(string.substring(0, sep_ix)),
                    Long.parseLong(string.substring(sep_ix + 1)));
        } catch (NumberFormatException e) {
            throw invalidRational(string);
        }
    }
}