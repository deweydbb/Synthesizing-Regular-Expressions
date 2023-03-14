package utils;

/**
 * Represents a set of characters to match. Can include a quantifier
 */
public class CharClass {

    private final String representation;
    private final CharSet charSet;
    private final Quantifier quantifier;

    public CharClass(String characters) {
        this.representation = characters;
        this.quantifier = null;

        charSet = new CharSet();

        for (int i = 0; i < characters.length(); i++) {
            char c = characters.charAt(i);

            charSet.addChar(c);
        }
    }

    // shallow copy okay sense class is immutable
    private CharClass(CharClass c, Quantifier quantifier) {
        this.representation = c.representation;
        this.charSet = c.charSet.copy();
        this.quantifier = quantifier;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CharClass other) {
            return charSet.equals(other.charSet) && quantifier == other.quantifier;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return charSet.hashCode();
    }

    public CharClass withQuantifier(Quantifier quantifier) {
        return new CharClass(this, quantifier);
    }

    public boolean hasQuantifier(Quantifier quantifier) {
        return quantifier != null;
    }

    @Override
    public String toString() {
        return "[" + representation + "]" + Quantifier.representation(quantifier);
    }
}
