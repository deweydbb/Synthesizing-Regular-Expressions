package synthesize;

import lombok.Getter;

public class Example {

    @Getter
    private final String ex;

    @Getter
    private final int start;

    @Getter
    private final int end;

    public Example(String ex) {
        this.ex = ex;
        this.start = 0;
        this.end = ex.length();
    }

    public String getMatchString() {
        return ex;
    }

    @Override
    public String toString() {
        return ex;
    }
}
