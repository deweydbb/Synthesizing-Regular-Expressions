package regex;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
public class Union implements Operator {

    @Getter
    private final List<Operator> operators;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Union other) {
            if (operators.size() != other.operators.size()) {
                return false;
            }

            for (int i = 0; i < operators.size(); i++) {
                if (!operators.get(i).equals(other.operators.get(i))) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    @Override
    public StringBuilder getRepresentation() {
        if (operators.size() == 1) {
            return operators.get(0).getRepresentation();
        }

        StringBuilder res = new StringBuilder("(");

        res.append(operators.get(0).getRepresentation());

        for (int i = 1; i < operators.size(); i++) {
            res.append("|");
            res.append(operators.get(i).getRepresentation());
        }

        return res.append(")");
    }

    @Override
    public String toString() {
        return getRepresentation().toString();
    }
}
