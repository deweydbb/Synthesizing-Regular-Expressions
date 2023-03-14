package synthesize;

import utils.CharClass;

import java.util.*;

public class Graph {

    public final Map<String, Set<CharClass>> map;
    private final int numNodes;

    public Graph(int matchLen) {
        this.map = new HashMap<>();
        this.numNodes = matchLen + 1;
    }

    public void insert(int start, int end, CharClass c) {
        String key = getKey(start, end);

        Set<CharClass> set = map.get(key);
        if (set == null) {
            set = new HashSet<>();
            set.add(c);
            map.put(key, set);
        } else {
            set.add(c);
        }
    }

    public List<CharClass> getEdge(int start, int end) {
        Set<CharClass> set = map.get(getKey(start, end));
        return set != null ? set.stream().toList() : null;
    }

    private String getKey(int start, int end) {
        return start + "-" + end;
    }

    private void listPossibleHelp(int start, StringBuilder partial, List<String> result) {
        if (start == numNodes - 1) {
            result.add(partial.toString());
        }

        for (int end = start + 1; end < numNodes; end++) {
            List<CharClass> possible = getEdge(start, end);

            if (possible != null) {
                for (CharClass c : possible) {
                    String cStr = c.toString();
                    partial.append(cStr);

                    listPossibleHelp(end, partial, result);

                    int partialSize = partial.length();
                    partial.delete(partialSize - cStr.length(), partialSize);
                }
            }
        }
    }

    public List<String> listPossibleRegExpr() {
        List<String> result = new ArrayList<>();

        listPossibleHelp(0, new StringBuilder(), result);

        return result;
    }



    private String combineKeys(String s1, String s2) {
        String[] split1 = s1.split("-");
        String[] split2 = s2.split("-");

        return String.format("%s,%s-%s,%s", split1[0], split2[0], split1[1], split2[1]);
    }

    private Set<CharClass> edgeIntersection(Set<CharClass> set1, Set<CharClass> set2) {
        Set<CharClass> intersection = new HashSet<>(set1);

        intersection.retainAll(set2);

        return intersection;
    }


    public Graph intersection(Graph o) {
        Map<String, Set<CharClass>> result = new HashMap<>();

        for (String key1 : map.keySet()) {

            for (String key2 : o.map.keySet()) {
                Set<CharClass> intersection = edgeIntersection(map.get(key1), o.map.get(key2));

                if (intersection.size() > 0) {
                    String newKey = combineKeys(key1, key2);
                    result.put(newKey, intersection);
                }

                System.out.println(key1 + ", " + key2 + "\tintersection: " + intersection + " key1: " + map.get(key1) + " key2: " + o.map.get(key2));
            }
        }

        System.out.println("---------------");
        for (String key : result.keySet()) {
            System.out.println(key + ":\t\t" + result.get(key));
        }

        return null;
    }




    /**
     * TODO:
     *  make key classes for single edge and intersection edge
     *      intersection edge should have validity check
     *
     * TODO: Graph intersection:
     *      charClass intersection needs to account for * and ? better
     *      remove nodes that have no incoming edges
     *      remove nodes that have no outgoing edges
     *      Complete topological sort and convert to graph object
     */


}
