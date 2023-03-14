package org.example;

import synthesize.GenerateGraph;
import synthesize.Graph;
import utils.Enumerator;
import utils.Quantifier;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        Enumerator enumerator = new Enumerator(List.of(Quantifier.STAR, Quantifier.PLUS));

        String example = "aab";
        Graph g = GenerateGraph.generateGraph(enumerator, example, 0, example.length());

        List<String> possible = g.listPossibleRegExpr();

        for (String regEx : possible) {
            System.out.println(regEx);
        }

    }
}