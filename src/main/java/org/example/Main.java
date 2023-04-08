package org.example;

import regex.Operator;
import regex.RegexMerge;
import regex.Union;
import synthesize.Example;
import synthesize.GenerateGraph;
import synthesize.Graph;
import utils.Enumerator;
import utils.Specification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * TODO:
 *      Have a union of single characters (or all with question qualifier) be combined into a character class
 */

public class Main {
    public static void main(String[] args) {

        long start = System.currentTimeMillis();

        List<Specification> specs = Specification.readInSpec("./examples/test.txt");
        if (specs == null) {
            System.err.println("Failed to read in spec correctly");
            System.exit(1);
        }

        List<List<Operator>> regexs = new ArrayList<>();

        for (Specification spec : specs) {
            regexs.add(getTopRegexForSpec(spec, true));
        }

        List<Operator> results = RegexMerge.regexConcat(regexs);

        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;

        System.out.println("\n\nFinal Results:");
        printRegularExpressions(results, 30);

        System.out.println("Time elapsed: " + timeElapsed + " milliseconds");
    }


    public static List<Operator> getTopRegexForSpec(Specification spec, boolean debug) {
        List<Example> examples = Example.createExamples(spec.getMatching(), false);
        List<Example> negExamples = Example.createExamples(spec.getNegative(), true);

        Enumerator enumerator = new Enumerator(spec);

        List<Graph> graphs = GenerateGraph.createGraphs(examples, enumerator);
        List<Graph> negGraphs = GenerateGraph.createGraphs(negExamples, enumerator);

        // clean up graphs for each example with the negative examples
        for (Graph graph : graphs) {
            for (Graph negGraph : negGraphs) {
                graph.subtract(negGraph);
            }
        }

        graphs = combineGraphs(graphs);

        if (debug) {
            System.out.println("number of uncombineable graphs: " + graphs.size());
            for (Graph graph : graphs) {
                List<Operator> res = graph.listPossibleRegExpr();
                printRegularExpressions(res);
            }
        }

        List<Operator> res = RegexMerge.forceMerge(graphs);
        Collections.sort(res); // sorts regular expressions with best being first
        res = pruneRegexs(res, examples, negExamples);

        if (debug) {
            printRegularExpressions(res);
        }

        return res.subList(0, Math.min(30, res.size()));
    }

    // greedily combines graphs until no more graphs can be combined
    public static List<Graph> combineGraphs(List<Graph> current) {
        List<Graph> next = current;
        int numCombined;

        boolean[] combinedGraphs;

        do {
            // reset variables
            numCombined = 0;
            current = next;
            next = new ArrayList<>();
            combinedGraphs = new boolean[current.size()];

            // loop over all combinations of i and j. Greedily combine first graphs that contain at least one path
            for (int i = 0; i < current.size(); i++) {
                Graph g1 = current.get(i);

                if (!combinedGraphs[i]) {
                    for (int j = i + 1; j < current.size(); j++) {
                        if (!combinedGraphs[j]) {
                            Graph g2 = current.get(j);

                            Graph combined = g1.intersection(g2);
                            if (combined.numEdges() > 0) {
                                next.add(combined);
                                numCombined++;

                                combinedGraphs[i] = true;
                                combinedGraphs[j] = true;
                                break; // break out of inner loop
                            }
                        }
                    }
                }

                // add any graph that was not able to be combined
                if (!combinedGraphs[i]) {
                    next.add(g1);
                    combinedGraphs[i] = true;
                }
            }

        } while (numCombined > 0);

        return current;
    }

    // checks each regex against the examples and negative examples and removes any that do not match all examples
    // and do not match all negative examples
    private static List<Operator> pruneRegexs(List<Operator> regexs, List<Example> examples, List<Example> negExamples) {
        List<Operator> res = new ArrayList<>();

        for (Operator regEx : regexs) {
            boolean passed = true;

            for (Example ex : examples) {
                if (!ex.check(regEx)) {
                    if (regEx instanceof Union unionRegex) {
                        // often the ordering matters and can lead to failure so try reversing the union and seeing
                        // if it that fixed it.
                        Collections.reverse(unionRegex.getOperators());
                        if (!ex.check(regEx)) {
                            passed = false;
                            break;
                        }
                    }
                }
            }

            if (passed) {
                for (Example ex : negExamples) {
                    if (ex.check(regEx)) {
                        passed = false;
                    }
                }
            }

            if (passed) {
                res.add(regEx);
            } else {
                System.out.println("failed: " + regEx);
            }
        }

        return res;
    }

    private static void printRegularExpressions(List<Operator> res) {
        printRegularExpressions(res, res.size());
    }

    private static void printRegularExpressions(List<Operator> res, int limit) {
        limit = Math.max(0, res.size() - limit);

        for (int i = res.size() - 1; i >= limit; i--) {
            System.out.println(res.get(i));
        }

        System.out.println(res.size() + " regular expressions found");
        System.out.println("-------------------------");
    }
}