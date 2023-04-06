package org.example;

import regex.Operator;
import regex.RegexMerge;
import synthesize.Example;
import synthesize.GenerateGraph;
import synthesize.Graph;
import utils.Enumerator;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO:
 *      Have a union of single characters (or all with question qualifier) be combined into a character class
 *      Rank Results
 */

public class Main {
    public static void main(String[] args) {

        long start = System.currentTimeMillis();

        String[] matches = {"000 0000", "111 1111", "000.0000", "111.1111"};
        String[] negative = {"11  11", "aaa aaaa", "11..11", "aaa.aaaa"};

        List<Example> examples = Example.createExamples(matches, false);
        List<Example> negExamples = Example.createExamples(negative, true);

        Enumerator enumerator = new Enumerator(matches, negative);

        List<Graph> graphs = GenerateGraph.createGraphs(examples, enumerator);
        List<Graph> negGraphs = GenerateGraph.createGraphs(negExamples, enumerator);

        // clean up graphs for each example with the negative examples
        for (Graph graph : graphs) {
            for (Graph negGraph : negGraphs) {
                graph.subtract(negGraph);
            }

//            List<Operator> test = graph.listPossibleRegExpr();
//            System.out.println("Found " + test.size());
//            for (Operator t : test) {
//                System.out.println("\t" + t);
//            }
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

        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;

        System.out.println("Time elapsed: " + timeElapsed + " milliseconds");

        printRegularExpressions(res, examples, negExamples);
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

        for (Operator regEx : res) {
            String r = regEx.toString();
            System.out.println(r);

            for (Example ex : examples) {
                if (!ex.check(r)) {
                    System.out.println("\t" + r + " failed on example: " + ex);
                }
            }

            for (Example ex : negExamples) {
                if (ex.check(r)) {
                    System.out.println("\t" + r + " failed on negative example: " + ex);
                }
            }
        }
        System.out.println("-------------------------");
    }


}