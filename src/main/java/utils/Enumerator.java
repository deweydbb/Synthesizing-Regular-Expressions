package utils;

import java.util.*;


/**
 * The Enumerator class performs bottom up enumeration of character classes
 */
public class Enumerator implements Iterator<CharClass> {

    private final List<CharClass> charClasses;

    private int index;
    private int modifierIndex;

    public Enumerator(String[] matches, String[] negative) {
        Set<Character> allCharacters = new HashSet<>();
        for (String s : matches) {
            for (char c : s.toCharArray()) {
                allCharacters.add(c);
            }
        }

        for (String s : negative) {
            for (char c : s.toCharArray()) {
                allCharacters.add(c);
            }
        }

        charClasses = new ArrayList<>();

        for (Character c : allCharacters) {
            CharClass cClass = new CharClass(c + "");
            charClasses.add(cClass);
//            CharClass negation = cClass.withNegation();
//            charClasses.add(negation);
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
}
