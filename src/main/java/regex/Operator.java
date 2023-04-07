package regex;

public interface Operator extends Comparable<Operator> {

    StringBuilder getRepresentation();

    long rank();

    @Override
    default int compareTo(Operator o) {
        long diff = rank() - o.rank();
        if (diff < 0) {
            return -1;
        } else if (diff > 0) {
            return 1;
        }

        return 0;
    }
}
