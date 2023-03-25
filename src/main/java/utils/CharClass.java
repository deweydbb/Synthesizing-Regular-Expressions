package utils;

import lombok.Getter;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a set of characters to match. Can include a quantifier
 */
public class CharClass {

    @Getter
    private final String representation;
    @Getter
    private final CharSet charSet;
    @Getter
    private final QuantifierType quantifier;

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
    private CharClass(CharClass c, QuantifierType quantifier) {
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

    public CharClass withQuantifier(QuantifierType quantifier) {
        return new CharClass(this, quantifier);
    }

    @Override
    public String toString() {
        return "[" + representation + "]" + QuantifierType.representation(quantifier);
    }


    public static Set<CharClass> getOptionalClasses(Set<CharClass> s) {
        return s.stream().filter(c -> QuantifierType.optional(c.getQuantifier())).collect(Collectors.toSet());
    }
}
