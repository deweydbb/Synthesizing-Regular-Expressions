package utils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/**
 * The Enumerator class performs bottom up enumeration of character classes
 */
public class Enumerator implements Iterator<CharClass> {

    private static final List<String> baseCharClasses = List.of("a", "b", "c", "d");

    private final List<CharClass> currentCharClasses;
    private final List<QuantifierType> quantifiers;
    private CharClass currentClass;
    private int quantifierIndex;

    public Enumerator(List<QuantifierType> quantifiers) {
        this.currentCharClasses = new LinkedList<>();
        this.quantifiers = quantifiers;

        reset();
    }

    public void reset() {
        currentCharClasses.clear();
        for (String chars : baseCharClasses) {
            currentCharClasses.add(new CharClass(chars));
        }

        currentClass = currentCharClasses.remove(0);
        quantifierIndex = -1;
    }

    @Override
    public boolean hasNext() {
        return currentCharClasses.size() > 0 || currentClass != null;
    }

    /**
     * returns the next character class
     */
    public CharClass next() {
        assert currentClass != null;

        if (quantifierIndex == -1) {
            quantifierIndex++;
            return currentClass;
        }

        CharClass returnVal = currentClass.withQuantifier(quantifiers.get(quantifierIndex++));

        if (quantifierIndex == quantifiers.size()) {
            currentClass = currentCharClasses.size() > 0 ? currentCharClasses.remove(0) : null;
            quantifierIndex = -1;
        }

        return returnVal;
    }
}
