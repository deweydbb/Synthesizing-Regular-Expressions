package org.example;

import synthesize.GenerateGraph;
import synthesize.Graph;
import utils.Enumerator;
import utils.Quantifier;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        Enumerator enumerator = new Enumerator(List.of(Quantifier.STAR, Quantifier.PLUS));

        String example1 = "aab";
        Graph g1 = GenerateGraph.generateGraph(enumerator, example1, 0, example1.length());


        enumerator.reset();
        String example2 = "ab";
        Graph g2 = GenerateGraph.generateGraph(enumerator, example2, 0, example2.length());

        Graph result = g1.intersection(g2);

        List<String> list = result.listPossibleRegExpr();

        for (String regEx : list) {
            System.out.println(regEx);
        }

    }
}