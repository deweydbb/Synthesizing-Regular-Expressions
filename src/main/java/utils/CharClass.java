package utils;

import java.util.HashSet;
import java.util.Set;

public class CharClass {

    private final String representation;
    private final Set<Character> charSet;
    private final Quantifier quantifier;

    public CharClass(String characters) {
        this.representation = characters;
        this.quantifier = null;

        charSet = new HashSet<>();

        for (int i = 0; i < characters.length(); i++) {
            char c = characters.charAt(i);

            charSet.add(c);
        }
    }

    // shallow copy okay sense class is immutable
    private CharClass(CharClass c, Quantifier quantifier) {
        this.representation = c.representation;
        this.charSet = c.charSet;
        this.quantifier = quantifier;
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
