package synthesize;

import utils.CharClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {

    public final Map<String, List<CharClass>> map;
    private final int numNodes;

    public Graph(int matchLen) {
        this.map = new HashMap<>();
        this.numNodes = matchLen + 1;
    }

    public void insert(int start, int end, CharClass c) {
        String key = getKey(start, end);

        List<CharClass> list = map.get(key);
        if (list == null) {
            list = new ArrayList<>();
            list.add(c);
            map.put(key, list);
        } else {
            list.add(c);
        }
    }

    public List<CharClass> getPossibleClasses(int start, int end) {
        return map.get(getKey(start, end));
    }

    private String getKey(int start, int end) {
        return start + "-" + end;
    }


    private void listPossibleHelp(int start, StringBuilder partial, List<String> result) {
        if (start == numNodes - 1) {
            result.add(partial.toString());
        }

        for (int end = start + 1; end < numNodes; end++) {
            List<CharClass> possible = getPossibleClasses(start, end);

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

}
