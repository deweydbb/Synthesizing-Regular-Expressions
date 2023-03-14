package synthesize;

import utils.CharClass;
import utils.Enumerator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenerateGraph {

    public static Graph generateGraph(Enumerator enumerator, String example, int start, int end) {
        Graph g = new Graph(end - start);

        String matchStr = example.substring(start, end);
        Matcher matcher = Pattern.compile("").matcher(matchStr);

        while (enumerator.hasNext()) {
            CharClass c = enumerator.next();

            Pattern p = Pattern.compile(c.toString());

            matcher.usePattern(p);
            matcher.reset();

            while (matcher.find()) {
                int matchStart = matcher.start();
                int matchEnd = matcher.end();

                g.insert(matchStart, matchEnd, c);
            }
        }

        return g;
    }

}
