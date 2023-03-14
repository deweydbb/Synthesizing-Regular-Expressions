package synthesize;

import utils.CharClass;

import java.util.*;
import java.util.stream.Collectors;

public class Graph {

    public final Map<SimpleKey, Set<CharClass>> map;
    private final int numNodes;

    public Graph(int matchLen) {
        this.map = new HashMap<>();
        this.numNodes = matchLen + 1;
    }

    private Graph(Map<SimpleKey, Set<CharClass>> map, int numNodes) {
        this.map = map;
        this.numNodes = numNodes;
    }


    public void insert(int start, int end, CharClass c) {
        SimpleKey key = getKey(start, end);

        Set<CharClass> set = map.computeIfAbsent(key, k -> new HashSet<>());
        set.add(c);
    }

    public List<CharClass> getEdge(int start, int end) {
        Set<CharClass> set = map.get(getKey(start, end));
        return set != null ? set.stream().toList() : null;
    }

    private SimpleKey getKey(int start, int end) {
        return new SimpleKey(start, end);
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

    private Set<CharClass> edgeIntersection(Set<CharClass> set1, Set<CharClass> set2) {
        return set1.stream().filter(set2::contains).collect(Collectors.toSet());
    }

    private void insertEdges(Map<SimpleKey, Map<SimpleKey, Set<CharClass>>> startToEnd,
                             Map<SimpleKey, Map<SimpleKey, Set<CharClass>>> endToStart,
                             CombinedKey combinedKey, Set<CharClass> intersection) {

        if (intersection.size() > 0) {
            SimpleKey startKey = combinedKey.start;
            SimpleKey endKey = combinedKey.end;

            Map<SimpleKey, Set<CharClass>> map = startToEnd.computeIfAbsent(startKey, k -> new HashMap<>());
            map.put(endKey, intersection);

            map = endToStart.computeIfAbsent(endKey, k -> new HashMap<>());
            map.put(startKey, intersection);
        }
    }

    public Graph intersection(Graph o) {
        Map<SimpleKey, Map<SimpleKey, Set<CharClass>>> startToEnd = new HashMap<>();
        Map<SimpleKey, Map<SimpleKey, Set<CharClass>>> endToStart = new HashMap<>();

        for (SimpleKey key1 : map.keySet()) {
            for (SimpleKey key2 : o.map.keySet()) {
                CombinedKey combinedKey = new CombinedKey(key1, key2);
                if (combinedKey.isValid()) {
                    Set<CharClass> intersection = edgeIntersection(map.get(key1), o.map.get(key2));

                    insertEdges(startToEnd, endToStart, combinedKey, intersection);
                }
            }
        }

        SimpleKey source = new SimpleKey(0, 0);
        SimpleKey target = new SimpleKey(numNodes - 1, o.numNodes - 1);

        Map<CombinedKey, Set<CharClass>> result = removeUnnecessaryEdges(startToEnd, endToStart, source, target);

        System.out.println("---------------");
        for (CombinedKey key : result.keySet()) {
            System.out.println(key + ":\t\t" + result.get(key));
        }

        return topologicalSort(result);
    }

    private static Map<CombinedKey, Set<CharClass>> pathTraversal(Map<SimpleKey, Map<SimpleKey, Set<CharClass>>> startToEnd,
                                      SimpleKey source, boolean forward) {

        Map<CombinedKey, Set<CharClass>> result = new HashMap<>();

        Set<SimpleKey> visitedNodes = new HashSet<>();
        LinkedList<SimpleKey> queue = new LinkedList<>();
        queue.add(source);

        while (queue.size() > 0) {
            SimpleKey node = queue.remove();
            if (!visitedNodes.contains(node)) {
                visitedNodes.add(node);
                Map<SimpleKey, Set<CharClass>> dests = startToEnd.get(node);
                if (dests != null) {
                    for (SimpleKey end : dests.keySet()) {
                        queue.add(end);

                        CombinedKey key = forward ? new CombinedKey(node, end, false) : new CombinedKey(end, node, false);
                        result.putIfAbsent(key, dests.get(end));
                    }
                }
            }
        }

        return result;
    }

    private static Map<CombinedKey, Set<CharClass>> removeUnnecessaryEdges(Map<SimpleKey, Map<SimpleKey, Set<CharClass>>> startToEnd,
                                                                           Map<SimpleKey, Map<SimpleKey, Set<CharClass>>> endToStart,
                                                                           SimpleKey source, SimpleKey target) {
        Map<CombinedKey, Set<CharClass>> forward = pathTraversal(startToEnd, source, true);
        Map<CombinedKey, Set<CharClass>> backward = pathTraversal(endToStart, target, false);

        // intersection of maps, only include edges in both traversals
        forward.keySet().retainAll(backward.keySet());

        return forward;
    }

    private static Graph topologicalSort(Map<CombinedKey, Set<CharClass>> result) {
        TreeSet<SimpleKey> ordering = new TreeSet<>();
        for (CombinedKey key : result.keySet()) {
            ordering.add(key.start);
            ordering.add(key.end);
        }

        int numNodes = ordering.size();

        Map<SimpleKey, Integer> keyToNumMap = new HashMap<>();

        int num = 0;
        for (SimpleKey key : ordering) {
            System.out.println(key + "-> " + num);
            keyToNumMap.putIfAbsent(key, num++);
        }

        Map<SimpleKey, Set<CharClass>> input = new HashMap<>();
        for (CombinedKey key : result.keySet()) {
            SimpleKey newKey = new SimpleKey(keyToNumMap.get(key.start), keyToNumMap.get(key.end));
            input.put(newKey, result.get(key));
        }

        return new Graph(input, numNodes);
    }

    /**
     * TODO: Graph intersection:
     *      charClass intersection needs to account for * and ? better
     */


    private static class SimpleKey implements Comparable<SimpleKey> {
        final int num1;
        final int num2;

        public SimpleKey(int start, int end) {
            this.num1 = start;
            this.num2 = end;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof SimpleKey other) {
                return num1 == other.num1 && num2 == other.num2;
            }

            return false;
        }

        @Override
        public int hashCode() {
            return num1 ^ num2;
        }

        @Override
        public String toString() {
            return num1 + "-" + num2;
        }

        @Override
        public int compareTo(SimpleKey o) {
            if (this.equals(o)) {
                return 0;
            }

            int sumDiff = (num1 + num2) - (o.num1 - o.num2);
            if (sumDiff == 0) {
                return num1 - o.num1;
            }

            return sumDiff;
        }
    }

    private static class CombinedKey implements Comparable<CombinedKey> {
        final SimpleKey start;
        final SimpleKey end;

        public CombinedKey(SimpleKey key1, SimpleKey key2) {
            this.start = new SimpleKey(key1.num1, key2.num1);
            this.end = new SimpleKey(key1.num2, key2.num2);
        }

        public CombinedKey(SimpleKey key1, SimpleKey key2, boolean t) {
            this.start = key1;
            this.end = key2;
        }

        public boolean isValid() {
            return true;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof CombinedKey other) {
                return start.equals(other.start) && end.equals(other.end);
            }

            return false;
        }

        @Override
        public int hashCode() {
            return start.hashCode() ^ end.hashCode();
        }

        @Override
        public String toString() {
            return String.format("%d,%d-%d,%d", start.num1, start.num2, end.num1, end.num2);
        }

        @Override
        public int compareTo(CombinedKey o) {
            return start.compareTo(o.start);
        }
    }
}
