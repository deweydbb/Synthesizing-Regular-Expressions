package regex;

import synthesize.Graph;
import utils.QuantifierType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RegexMerge {

    // helper to combine range when one of the range has a size of zero
    // either return (operators)? or if operators is a plus quantifier
    // convert it to a plus
    private static Operator combineSingle(List<Operator> operators) {
        if (operators.size() == 1) {
            Operator op = operators.get(0);
            if (op instanceof Quantifier quantifier) {
                // do nothing if the quantifier is already optional
                if (QuantifierType.optional(quantifier.getType())) {
                    return quantifier;
                } else {
                    // quantifier must be a plus
                    return new Quantifier(QuantifierType.STAR, quantifier.getOp());
                }
            } else {
                // surround single operator with quantifier
                return new Quantifier(QuantifierType.QUESTION, op);
            }
        } else {
            return new Quantifier(
                    QuantifierType.QUESTION,
                    new Concat(operators));
        }
    }

    // given two regexs, combine the given sub ranges by the union operator is both regions are non-empty
    // or if one of the ranges is empty, surround the non-empty subrange in an optional quantifier.
    private static Operator combineRange(Concat r1, int start1, int end1, Concat r2, int start2, int end2) {
        try {
            List<Operator> op1 = new ArrayList<>(r1.getOperators().subList(start1, end1));
            List<Operator> op2 = new ArrayList<>(r2.getOperators().subList(start2, end2));

            int range1 = op1.size();
            int range2 = op2.size();

            if (range1 > 0 && range2 == 0) {
                return combineSingle(op1);
            } else if (range2 > 0 && range1 == 0) {
                return combineSingle(op2);
            } else {
                //TODO call createUnionOfRegexs here instead of simple union???
                return new Union(List.of(
                        new Concat(op1),
                        new Concat(op2)
                ));
            }
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("r1 = " + r1 + ", r2 = " + r2);
        }
    }

    // used to combine two regular expressions when one is a complete prefix or suffix of the other
    private static Operator combineFullMatch(Concat c1, Concat c2, int numShareStart, int numShareEnd) {
        int size1 = c1.getOperators().size();
        int size2 = c2.getOperators().size();

        if (numShareStart == size1 || numShareEnd == size1) {
            // full match of c1
            List<Operator> newOperators;
            if (numShareStart == size1) {
                // c1 is a prefix of c2
                newOperators = new ArrayList<>(c1.getOperators());
                Operator shared = combineRange(c1, 0, 0, c2, numShareStart, size2);
                newOperators.add(shared);
            } else {
                // c1 is a suffix of c2
                newOperators = new ArrayList<>();
                Operator shared = combineRange(c1, 0, 0, c2, 0, size2 - numShareEnd);
                newOperators.add(shared);
                newOperators.addAll(c1.getOperators());
            }

            return new Concat(newOperators);
        } else {
            // flip c1 and c2 in order to not have to duplicate the logic above
            return combineFullMatch(c2, c1, numShareStart, numShareEnd);
        }
    }

    // returns the number of elements the regex r1 and r2 have in common
    // starting from index 0 until they are no longer equal
    private static int shareStart(Concat c1, Concat c2) {
        int result = 0;

        Iterator<Operator> i1 = c1.getOperators().iterator();
        Iterator<Operator> i2 = c2.getOperators().iterator();

        boolean equal = true;

        while (i1.hasNext() && i2.hasNext() && equal) {
            if (i1.next().equals(i2.next())) {
                result++;
            } else {
                equal = false;
            }
        }

        return result;
    }

    // returns the number of elements the regex r1 and r2 have in common
    // starting from the end of both regexs until they are no longer equal
    private static int shareEnd(Concat c1, Concat c2) {
        int result = 0;

        List<Operator> l1 = c1.getOperators();
        List<Operator> l2 = c2.getOperators();

        int i1 = l1.size() - 1;
        int i2 = l2.size() - 1;

        for (; i1 >= 0 && i2 >= 0; i1--, i2--) {
            if (l1.get(i1).equals(l2.get(i2))) {
                result++;
            } else {
                return result;
            }
        }

        return result;
    }

    private static Operator createUnionOfRegexs(List<Operator> regexs) {
        for (int i = 0; i < regexs.size(); i++) {
            Operator r1 = regexs.get(i);
            for (int j = regexs.size() - 1; j > i; j--) {
                Operator r2 = regexs.get(j);

                if (!r1.toString().equals(r2.toString()) && r1 instanceof Concat c1 && r2 instanceof  Concat c2) {
                    int numShareStart = shareStart(c1, c2);
                    int numShareEnd = shareEnd(c1, c2);

                    if (numShareStart > 0 || numShareEnd > 0) {

                        int size1 = c1.getOperators().size();
                        int size2 = c2.getOperators().size();
                        int end1 = size1 - numShareEnd;
                        int end2 = size2 - numShareEnd;
                        // check to see if one is a complete prefix/suffix of the other regex
                        if (end1 == 0 || end2 == 0 || numShareStart == size1 || numShareStart == size2) {
                            r1 = combineFullMatch(c1, c2, numShareStart, numShareEnd);
                        } else {
                            assert end1 >= numShareStart && end2 >= numShareStart && (end1 > numShareStart || end2 > numShareStart);

                            Operator shared = combineRange(c1, numShareStart, end1,
                                    c2, numShareStart, end2);

                            List<Operator> newRegex = new ArrayList<>();
                            if (numShareStart > 0) {
                                newRegex.addAll(c1.getOperators().subList(0, numShareStart));
                            }
                            newRegex.add(shared);
                            if (numShareEnd > 0) {
                                newRegex.addAll(c1.getOperators().subList(end1, size1));
                            }

                            r1 = new Concat(newRegex);

                        }

                        regexs.set(i, r1);
                        regexs.remove(j);
                    }
                }
            }
        }

        return new Union(regexs);
    }




    private static void forceMergeHelp(int curr, int end, List<List<Operator>> graphRegExs, List<Operator> partial, List<Operator> result) {
        // base case
        if (curr == end) {
            if (partial.size() == 1) {
                result.add(partial.get(0));
            } else {
                result.add(createUnionOfRegexs(new ArrayList<>(partial)));
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

    // takes list of graphs that can no longer be combined via intersections and returns a list of regular expressions that have
    // been combined with the union operator and other custom rules
    public static List<Operator> forceMerge(List<Graph> graphs) {
        List<List<Operator>> graphRegExs = new ArrayList<>();

        for (Graph g : graphs) {
            graphRegExs.add(g.listPossibleRegExpr());
        }

        List<Operator> result = new ArrayList<>();

        forceMergeHelp(0, graphRegExs.size(), graphRegExs, new ArrayList<>(), result);

        return result;
    }

    private static Operator createConcat(List<Operator> partial) {
        Concat concat = new Concat(new ArrayList<>());

        for (Operator op : partial) {
            if (op instanceof Concat concatOp) {
                concat.getOperators().addAll(concatOp.getOperators());
            } else {
                concat.getOperators().add(op);
            }
        }

        return concat;
    }


    private static void regexConcatHelp(int index, List<List<Operator>> regexs, List<Operator> partial, List<Operator> result) {
        // base case
        if (index == regexs.size()) {
            result.add(createConcat(partial));
            return;
        }

        List<Operator> regexSet = regexs.get(index);

        for (Operator op : regexSet) {
            partial.add(op);

            regexConcatHelp(index + 1, regexs, partial, result);

            partial.remove(partial.size() - 1);
        }
    }

    public static List<Operator> regexConcat(List<List<Operator>> regexs) {
        List<Operator> result = new ArrayList<>();

        regexConcatHelp(0, regexs, new ArrayList<>(), result);

        return result;
    }
}
