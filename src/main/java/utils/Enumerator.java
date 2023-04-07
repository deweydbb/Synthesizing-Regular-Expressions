package utils;

import java.util.*;


/**
 * The Enumerator class performs bottom up enumeration of character classes
 */
public class Enumerator implements Iterator<CharClass> {

    private final List<CharClass> charClasses;

    private int index;
    private int modifierIndex;

    public Enumerator(Specification spec) {
        Set<Character> allCharacters = new HashSet<>();
        for (String s : spec.getMatching()) {
            for (char c : s.toCharArray()) {
                allCharacters.add(c);
            }
        }

        for (String s : spec.getNegative()) {
            for (char c : s.toCharArray()) {
                allCharacters.add(c);
            }
        }

        charClasses = new ArrayList<>();

        for (Character c : allCharacters) {
            String special = isSpecial(c) ? "\\" : "";
            charClasses.add(new CharClass(special + c));
        }

        List<String> base = new ArrayList<>(List.of("a-z", "A-Z", "a-zA-Z", "0-9", "a-zA-Z0-9", "\\s"));
        base.addAll(spec.getExtraCharClasses());

        for (String representation : base) {
            charClasses.add(new CharClass(representation));
        }
    }

    public void reset() {
        index = 0;
    }

    @Override
    public boolean hasNext() {
        return index < charClasses.size();
    }

    /**
     * returns the next character class
     */
    public CharClass next() {
        if (modifierIndex == 0) {
            modifierIndex++;
            return charClasses.get(index);
        } else {
            modifierIndex = 0;
            return charClasses.get(index++).withQuantifier(QuantifierType.PLUS);
        }
    }

    private static boolean isSpecial(char c) {
        final Set<Character> special = Set.of('.', ',', '*', '?', '^', '$', '(', ')', '[', ']', '{', '}', '|', '\\');

        return special.contains(c);
    }
}
