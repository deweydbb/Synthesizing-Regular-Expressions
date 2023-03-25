package org.example;

import regex.Operator;
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

        String[] matches = {"a", "aa", "aab", "aaabb", "abbbaaa"};

        List<Example> examples = new ArrayList<>();

        for (String s : matches) {
            examples.add(new Example(s));
        }

        Enumerator enumerator = new Enumerator(List.of(QuantifierType.PLUS));

        List<Graph> graphs = new ArrayList<>();

        for (Example ex : examples) {
            Graph g = GenerateGraph.generateGraph(enumerator, ex);
            graphs.add(g);

            enumerator.reset();
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
}