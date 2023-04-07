package regex;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CharMatch implements Operator {

    private final String representation;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CharMatch other) {
            return representation.equals(other.representation);
        }

        return false;
    }

    @Override
    public StringBuilder getRepresentation() {
        StringBuilder res = new StringBuilder();

        if (representation.length() == 1 || (representation.length() == 2 && representation.charAt(0) == '\\')) {
            return res.append(representation);
        }

        return res.append('[').append(representation).append(']');
    }

    @Override
    public long rank() {
        if (representation.charAt(0) == '^') {
            return 128 - representation.length() + 1;
        }

        return representation.length();
    }

    @Override
    public String toString() {
        return getRepresentation().toString();
    }
}
