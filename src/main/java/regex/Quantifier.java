package regex;

import lombok.AllArgsConstructor;
import lombok.Getter;
import utils.QuantifierType;

@AllArgsConstructor
public class Quantifier implements Operator {

    @Getter
    private final QuantifierType type;
    @Getter
    private final Operator op;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Quantifier other) {
            return type == other.type && op.equals(other.op);
        }
        return false;
    }

    @Override
    public StringBuilder getRepresentation() {
        StringBuilder res = op.getRepresentation();
        String quantifier = QuantifierType.representation(type);
        if (res.length() == 1) {
            return res.append(quantifier);
        }

        return new StringBuilder("(").append(res).append(")").append(quantifier);
    }

    @Override
    public long rank() {
        return op.rank();
    }

    @Override
    public String toString() {
        return getRepresentation().toString();
    }
}
