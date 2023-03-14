package utils;

public enum Quantifier {
    STAR,
    PLUS,
    QUESTION;

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
