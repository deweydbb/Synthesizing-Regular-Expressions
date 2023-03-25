package synthesize;

import regex.CharMatch;
import regex.Concat;
import regex.Operator;
import regex.Quantifier;
import utils.CharClass;

import java.util.*;
import java.util.stream.Collectors;

public class Graph {

    // map of edges, simpleKey num1 is source of edge, num2 is destination
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

    public int numEdges() {
        return map.size();
    }

    // inserts a character class along a specific edge. The character class matches the example between start and end
    public void insert(int start, int end, CharClass c) {
        SimpleKey key = new SimpleKey(start, end);

        Set<CharClass> set = map.computeIfAbsent(key, k -> new HashSet<>());
        set.add(c);
    }

    // returns a list of character classes that match the given substring (end is non-inclusive)
    public Set<CharClass> getEdge(int start, int end) {
        return map.get(new SimpleKey(start, end));
    }

    // helper method for listing all possible regular expressions
    private void listPossibleHelp(int start, List<Operator> partial, List<Operator> results) {
        // base case, reach last node, partial contains a complete regex.
        if (start == numNodes - 1) {
            Operator concat = new Concat(new ArrayList<>(partial));
            results.add(concat);
            return;
        }

        // loop through all possible edges from the current node
        for (int end = start + 1; end < numNodes; end++) {
            //start, end define an edge
            Set<CharClass> possible = getEdge(start, end);


            if (possible != null) {
                // loop through all different character classes that match the current edge
                for (CharClass c : possible) {
                    Operator match = new CharMatch(c.getCharSet(), c.getRepresentation());
                    if (c.getQuantifier() != null) {
                        // nest char match set in quantifier
                        match = new Quantifier(c.getQuantifier(), match);
                    }
                    partial.add(match);

                    listPossibleHelp(end, partial, results);

                    // back track, remove last operator so new one can be added
                    partial.remove(partial.size() - 1);
                }
            }
        }
    }

    // returns a list of all possible regular expressions in the version space
    public List<Operator> listPossibleRegExpr() {
        List<Operator> result = new ArrayList<>();

        listPossibleHelp(0, new ArrayList<>(), result);

        return result;
    }

    // returns an intersection of two edges
    private Set<CharClass> edgeIntersection(Set<CharClass> set1, Set<CharClass> set2) {
        return set1.stream().filter(set2::contains).collect(Collectors.toSet());
    }

    // inserts edges into the source major map and destination major map
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

    // generates an intersection of the calling graph and input graph
    public Graph intersection(Graph o) {
        Map<SimpleKey, Map<SimpleKey, Set<CharClass>>> startToEnd = new HashMap<>();
        Map<SimpleKey, Map<SimpleKey, Set<CharClass>>> endToStart = new HashMap<>();

        // produce a cartesian product of all edges
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

        // removes edges in the intermediate graph that do not exist along a path between the source and target
        Map<CombinedKey, Set<CharClass>> result = removeUnnecessaryEdges(startToEnd, endToStart, source, target);

        // converts the intermediate graph with combined keys into a regular graph with single numbers
        // for the source and destination of each edge
        return topologicalSort(result);
    }

    // performs a breadth first search traversal, returns a map that contains all the visited edges during the traversal
    // helper method for removing unnecessary edges from the graph
    private static Map<CombinedKey, Set<CharClass>> pathTraversal(Map<SimpleKey, Map<SimpleKey, Set<CharClass>>> startToEnd,
                                      SimpleKey source, boolean forward) {

        Map<CombinedKey, Set<CharClass>> result = new HashMap<>();

        Set<SimpleKey> visitedNodes = new HashSet<>();
        LinkedList<SimpleKey> queue = new LinkedList<>();
        queue.add(source); // kick off the traversal from the source

        while (queue.size() > 0) {
            SimpleKey node = queue.remove();

            // ignore nodes that have already been visited
            if (!visitedNodes.contains(node)) {
                visitedNodes.add(node); // mark as visited
                // get all edges from the current node
                Map<SimpleKey, Set<CharClass>> dests = startToEnd.get(node);
                if (dests != null) {
                    for (SimpleKey end : dests.keySet()) {
                        // start, end defines an edge
                        queue.add(end);

                        CombinedKey key = forward ? new CombinedKey(node, end, false) : new CombinedKey(end, node, false);
                        result.putIfAbsent(key, dests.get(end));
                    }
                }
            }
        }

        return result;
    }

    // removes edges not along a path from source to target by performing a breadth first search from source and a
    // backward breadth first search from the target. Only edges visited by both traversals exist along a path from the
    // source to the sink
    private static Map<CombinedKey, Set<CharClass>> removeUnnecessaryEdges(Map<SimpleKey, Map<SimpleKey, Set<CharClass>>> startToEnd,
                                                                           Map<SimpleKey, Map<SimpleKey, Set<CharClass>>> endToStart,
                                                                           SimpleKey source, SimpleKey target) {
        // perform the forward traversal from the source
        Map<CombinedKey, Set<CharClass>> forward = pathTraversal(startToEnd, source, true);
        // perform the backward traversal from the target
        Map<CombinedKey, Set<CharClass>> backward = pathTraversal(endToStart, target, false);

        // intersection of maps, only include edges in both traversals
        forward.keySet().retainAll(backward.keySet());

        return forward;
    }

    // converts an intermediate graph with combined keys into a graph with single integers for node representation
    // performs a topological sort of the nodes in O(E) time
    private static Graph topologicalSort(Map<CombinedKey, Set<CharClass>> result) {
        TreeSet<SimpleKey> ordering = new TreeSet<>();
        // adds all nodes in the intermediate graph into a set to be ordered
        for (CombinedKey key : result.keySet()) {
            ordering.add(key.start);
            ordering.add(key.end);
        }

        int numNodes = ordering.size();

        Map<SimpleKey, Integer> keyToNumMap = new HashMap<>();

        int num = 0;
        // creates a mapping from key to int based on order of the set
        for (SimpleKey key : ordering) {
            keyToNumMap.putIfAbsent(key, num++);
        }

        Map<SimpleKey, Set<CharClass>> input = new HashMap<>();

        // creates the new graph by renaming each node to its new single int name
        for (CombinedKey key : result.keySet()) {
            SimpleKey newKey = new SimpleKey(keyToNumMap.get(key.start), keyToNumMap.get(key.end));
            input.put(newKey, result.get(key));
        }

        return new Graph(input, numNodes);
    }

    // is a key of two number
    // used in a regular graph to represent and edge from num1 to num2
    // used in the intermediate graph to represent a single node
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
        // used to order the nodes in an intermediate graph for a topological sort
        public int compareTo(SimpleKey o) {
            // orders nodes on an earlier diagonal as "less", comes before other nodes on later diagonals closer to the target
            int sumDiff = (num1 + num2) - (o.num1 + o.num2);
            // arbitrarily break ties between nodes on the same diagonal
            if (sumDiff == 0) {
                return num1 - o.num1;
            }

            return sumDiff;
        }
    }

    // serves as the keys for the intermediate graph
    private static class CombinedKey {
        final SimpleKey start;
        final SimpleKey end;

        // creates a combined key where the simple keys represent an edge in an original graph
        public CombinedKey(SimpleKey key1, SimpleKey key2) {
            this.start = new SimpleKey(key1.num1, key2.num1);
            this.end = new SimpleKey(key1.num2, key2.num2);
        }

        // creates a combined key where key1 represents the start node of an edge in the intermediate graph
        // key2 represents the end nodes of an edge in the intermediate graph
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
    }
}
