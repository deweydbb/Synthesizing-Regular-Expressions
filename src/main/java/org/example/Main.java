package org.example;

import synthesize.GenerateGraph;
import synthesize.Graph;
import utils.Enumerator;
import utils.Quantifier;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        long start = System.currentTimeMillis();


        Enumerator enumerator = new Enumerator(List.of(Quantifier.STAR, Quantifier.PLUS));

        String example1 = "aba";
        Graph g1 = GenerateGraph.generateGraph(enumerator, example1, 0, example1.length());


        enumerator.reset();
        String example2 = "aa";
        Graph g2 = GenerateGraph.generateGraph(enumerator, example2, 0, example2.length());

        Graph result = g1.intersection(g2);

        List<String> list = result.listPossibleRegExpr();

        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;

        System.out.println("Time elapsed: " + timeElapsed + " milliseconds");

        for (String regEx : list) {
            System.out.println(regEx);
        }

    }
}