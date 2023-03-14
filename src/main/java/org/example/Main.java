package org.example;

import utils.Enumerator;
import utils.Quantifier;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        Enumerator enumerator = new Enumerator(List.of(Quantifier.STAR, Quantifier.PLUS));

        while (enumerator.hasNext()) {
            System.out.println(enumerator.next());
        }

    }
}