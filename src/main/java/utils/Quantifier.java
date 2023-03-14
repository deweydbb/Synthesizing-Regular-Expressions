package utils;

/**
 * Lists the types of quantifiers for character classes
 */
public enum Quantifier {
    STAR, // 0 or more elements
    PLUS, // 1 or more elements
    QUESTION; // 0 or 1 element

    public static String representation(Quantifier q) {
        if (q == null) return "";

        switch (q) {
            case STAR -> {
                return "*";
            }
            case PLUS -> {
                return "+";
            }
            case QUESTION -> {
                return "?";
            }
        };

        return "";
    }

}
