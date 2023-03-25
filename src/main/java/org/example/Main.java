package org.example;

import regex.Operator;
import regex.Union;
import synthesize.Example;
import synthesize.GenerateGraph;
import synthesize.Graph;
import utils.Enumerator;
import utils.QuantifierType;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO:
 *      Have listPossibleRegExpr return an actual regular expression object
 *      Enumeration:
 *          Have enumeration include negation of classes
 *          Have enumeration combine classes
 *          Have enumeration perform observed equivalency
 *      Create class for partitioning
 *      Create class to merge expressions that cannot be combined
 */

public class Main {
    public static void main(String[] args) {

        long start = System.currentTimeMillis();

        String[] matches = {"aaabb", "abb"};
        String[] negative = {"aab"};

        List<Example> examples = Example.createExamples(matches, false);
        List<Example> negExamples = Example.createExamples(negative, true);

        Enumerator enumerator = new Enumerator(List.of(QuantifierType.PLUS));

        List<Graph> graphs = createGraphs(examples, enumerator);
        List<Graph> negGraphs = createGraphs(negExamples, enumerator);

        // clean up graphs for each example with the negative examples
        for (Graph graph : graphs) {
            System.out.println(graph.listPossibleRegExpr());
            for (Graph negGraph : negGraphs) {
                graph.subtract(negGraph);
            }
            System.out.println("new: " + graph.listPossibleRegExpr());
        }

        graphs = combineGraphs(graphs);

        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;

        System.out.println("Time elapsed: " + timeElapsed + " milliseconds");


        for (Graph graph : graphs) {
            List<Operator> res = graph.listPossibleRegExpr();
            System.out.println(res.size() + " regular expressions found");

            for (Operator regEx : res) {

                System.out.println(regEx.toString());
            }
            System.out.println("-------------------------");
        }

        List<Operator> res = forceMerge(graphs);
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


    private static void forceMergeHelp(int curr, int end, List<List<Operator>> graphRegExs, List<Operator> partial, List<Operator> result) {
        // base case
        if (curr == end) {
            if (partial.size() == 1) {
                result.add(partial.get(0));
            } else {
                result.add(new Union(new ArrayList<>(partial)));
            }
        } else {
            List<Operator> currentRegExs = graphRegExs.get(curr);

            for (Operator currentRegEx : currentRegExs) {
                partial.add(currentRegEx);

                forceMergeHelp(curr + 1, end, graphRegExs, partial, result);

                partial.remove(partial.size() - 1);
            }
        }
    }

    public static List<Operator> forceMerge(List<Graph> graphs) {
        List<List<Operator>> graphRegExs = new ArrayList<>();

        for (Graph g : graphs) {
            graphRegExs.add(g.listPossibleRegExpr());
        }

        List<Operator> result = new ArrayList<>();

        forceMergeHelp(0, graphRegExs.size(), graphRegExs, new ArrayList<>(), result);

        return result;
    }

    private static List<Graph> createGraphs(List<Example> examples, Enumerator enumerator) {
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