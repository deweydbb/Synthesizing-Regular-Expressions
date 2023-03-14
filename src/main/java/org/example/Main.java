package org.example;

import synthesize.Graph;
import utils.CharClass;
import utils.Enumerator;
import utils.Quantifier;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        Enumerator enumerator = new Enumerator(List.of(Quantifier.STAR, Quantifier.PLUS));

        int matchLen = 4;
        Graph g = new Graph(matchLen);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                CharClass c = enumerator.next();
                System.out.print(c);

                int end = i + j + 1;
                if (end > matchLen) {
                    end = matchLen;
                }

                System.out.printf(" \tstart = %d, end = %d\n", i, end);
                g.insert(i, end, c);
            }
        }

        System.out.println("----------");

        List<String> possible = g.listPossibleRegExpr();

        for (String regEx : possible) {
            System.out.println(regEx);
        }
    }
}