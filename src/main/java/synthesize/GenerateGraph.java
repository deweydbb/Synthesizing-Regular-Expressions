package synthesize;

import utils.CharClass;
import utils.Enumerator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenerateGraph {

    // Given an example and an enumerator, generates a version space for the example
    public static Graph generateGraph(Enumerator enumerator, Example ex) {
        int start = ex.getStart();
        int end = ex.getEnd();

        Graph g = new Graph(end - start);

        String matchStr = ex.getMatchString();
        Matcher matcher = Pattern.compile("").matcher(matchStr);

        while (enumerator.hasNext()) {
            CharClass c = enumerator.next();

            Pattern p = Pattern.compile(c.toString());

            matcher.usePattern(p);
            matcher.reset();

            while (matcher.find()) {
                int matchStart = matcher.start();
                int matchEnd = matcher.end();
                if (matchStart == matchEnd) continue;

                g.insert(matchStart, matchEnd, c);
            }
        }

        return g;
    }

    public static List<Graph> createGraphs(List<Example> examples, Enumerator enumerator) {
        List<Graph> graphs = new ArrayList<>();

        for (Example ex : examples) {
            Graph g = GenerateGraph.generateGraph(enumerator, ex);

            assert g.numEdges() > 0;

            graphs.add(g);

            enumerator.reset();
        }

        return graphs;
    }
}
