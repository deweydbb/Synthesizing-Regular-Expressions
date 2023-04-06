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
    private final QuantifierType quantifier;

    public CharClass(String characters) {
        this.representation = characters;
        this.quantifier = null;
    }

    // shallow copy okay sense class is immutable
    private CharClass(CharClass c, QuantifierType quantifier) {
        this.representation = c.representation;
        this.quantifier = quantifier;
    }

    private CharClass(CharClass c) {
        this.representation = "^" + c.representation;
        this.quantifier = null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CharClass other) {
            return representation.equals(other.representation) && quantifier == other.quantifier;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return representation.hashCode();
    }

    public CharClass withQuantifier(QuantifierType quantifier) {
        return new CharClass(this, quantifier);
    }

    public CharClass withNegation() {
        return new CharClass(this);
    }

    @Override
    public String toString() {
        return "[" + representation + "]" + QuantifierType.representation(quantifier);
    }


    public static Set<CharClass> getOptionalClasses(Set<CharClass> s) {
        return s.stream().filter(c -> QuantifierType.optional(c.getQuantifier())).collect(Collectors.toSet());
    }
}
