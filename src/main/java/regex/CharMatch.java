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
        if (representation.length() > 1) {
            return res.append('[').append(representation).append(']');
        }

        return res.append(representation);
    }

    @Override
    public String toString() {
        return getRepresentation().toString();
    }
}
