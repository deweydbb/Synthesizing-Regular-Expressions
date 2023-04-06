package synthesize;

import lombok.Getter;
import regex.Operator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Example {

    @Getter
    private final String ex;

    @Getter
    private final Boolean negative;

    @Getter
    private final int start;

    @Getter
    private final int end;

    public Example(String ex, boolean negative) {
        this.ex = ex;
        this.negative = negative;
        this.start = 0;
        this.end = ex.length();
    }

    public boolean check(Operator regex) {
        Matcher matcher = Pattern.compile(regex.toString()).matcher(ex);

        if (matcher.find()) {
            return matcher.start() == 0 && matcher.end() == ex.length();
        }

        return false;
    }


    public String getMatchString() {
        return ex;
    }

    @Override
    public String toString() {
        return ex;
    }

    public static ArrayList<Example> createExamples(List<String> strings, boolean negative) {
        ArrayList<Example> res = new ArrayList<>();
        for (String s : strings) {
            res.add(new Example(s, negative));
        }

        return res;
    }

}
