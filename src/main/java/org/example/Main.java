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
 *      Have multiple sets of examples combined into one regular expression
 *      Rank Results
 */

public class Main {
    public static void main(String[] args) {

        long start = System.currentTimeMillis();

        List<Specification> specs = Specification.readInSpec("./examples/test.txt");

        Specification spec = specs.get(0);

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

        for (Graph graph : graphs) {
            List<Operator> res = graph.listPossibleRegExpr();
            System.out.println(res.size() + " regular expressions found");

            for (Operator regEx : res) {

                System.out.println(regEx.toString());
            }
            System.out.println("-------------------------");
        }

        List<Operator> res = RegexMerge.forceMerge(graphs);
        Collections.sort(res); // sorts regular expressions with best being first

        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;

        printRegularExpressions(res, examples, negExamples);

        System.out.println("Time elapsed: " + timeElapsed + " milliseconds");
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



    private static void printRegularExpressions(List<Operator> res, List<Example> examples, List<Example> negExamples) {
        System.out.println(res.size() + " regular expressions found");

        for (int i = res.size() - 1; i >= 0; i--) {
            Operator regEx = res.get(i);
            for (Example ex : examples) {
                if (!ex.check(regEx)) {
                    if (regEx instanceof Union unionRegex) {
                        // often the ordering matters and can lead to failure so try reversing the union and seeing
                        // if it that fixed it.
                        Collections.reverse(unionRegex.getOperators());
                        if (!ex.check(regEx)) {
                            System.out.println("\t" + regEx + " failed on example: " + ex);
                        }
                    }
                }
            }

            for (Example ex : negExamples) {
                if (ex.check(regEx)) {
                    System.out.println("\t" + regEx + " failed on negative example: " + ex);
                }
            }

            System.out.println(regEx);
        }

        System.out.println(res.size() + " regular expressions found");
        System.out.println("-------------------------");
    }


}