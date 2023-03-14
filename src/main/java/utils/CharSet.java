package utils;

public class CharSet {

    private static final int LONG_NUM_BITS = 64;

    private long s1;
    private long s2;

    public CharSet() {}

    private CharSet(long s1, long s2) {
        this.s1 = s1;
        this.s2 = s2;
    }

    public void addChar(char c) {
        if (c < LONG_NUM_BITS) {
            long mask = 1L << c;
            s1 |= mask;
        } else {
            c -= LONG_NUM_BITS;
            long mask = 1L << c;
            s2 |= mask;
        }
    }

    public void removeChar(char c) {
        if (c < LONG_NUM_BITS) {
            long mask = ~(1L << c);
            s1 &= mask;
        } else {
            c -= LONG_NUM_BITS;
            long mask = ~(1L << c);
            s2 &= mask;
        }
    }

    public boolean contains(char c) {
        if (c < LONG_NUM_BITS) {
            long mask = 1L << c;
            return (s1 & mask) != 0;
        } else {
            c -= LONG_NUM_BITS;
            long mask = 1L << c;
            return (s2 & mask) != 0;
        }
    }

    public CharSet intersection(CharSet other) {
        long newS1 = s1 & other.s1;
        long newS2 = s2 & other.s2;

        return new CharSet(newS1, newS2);
    }


    public CharSet copy() {
        return new CharSet(s1, s2);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CharSet other) {
            return s1 == other.s1 && s2 == other.s2;
        }

        return false;
    }

    @Override
    public int hashCode() {
        int int1 = (int) s1;
        int int2 = (int) (s1 >> LONG_NUM_BITS / 2);
        int int3 = (int) s2;
        int int4 = (int) (s2 >> LONG_NUM_BITS / 2);

        return int1 ^ int2 ^ int3 ^ int4;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");

        for (char c = 0; c < 128; c++) {
            if (contains(c)) {
                sb.append(c);
                sb.append(", ");
            }
        }

        if (sb.length() > 2) {
            sb.delete(sb.length() - 2, sb.length());
        }
        sb.append("]");

        return sb.toString();
    }
}
