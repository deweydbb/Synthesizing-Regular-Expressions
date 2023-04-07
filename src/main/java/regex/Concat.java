package regex;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
public class Concat implements Operator {

    @Getter
    private final List<Operator> operators;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Concat other) {
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
        StringBuilder res = new StringBuilder();

        for (Operator op : operators) {
            res.append(op.getRepresentation());
        }

        return res;
    }

    @Override
    public long rank() {
        long res = 0;

        for (Operator op : operators) {
            res += op.rank();
        }

        return res;
    }

    @Override
    public String toString() {
        return getRepresentation().toString();
    }
}
