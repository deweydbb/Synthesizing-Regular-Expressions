package utils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Enumerator implements Iterator<CharClass> {

    private static final List<String> baseCharClasses = List.of("a", "b", "c", "d");

    private final List<CharClass> currentCharClasses;
    private final List<Quantifier> quantifiers;
    private CharClass currentClass;
    private int quantifierIndex;

    public Enumerator(List<Quantifier> quantifiers) {
        this.currentCharClasses = new LinkedList<>();
        this.quantifiers = quantifiers;

        reset();
    }

    public void reset() {
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

    @Override
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
