package utils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.io.File;
import java.util.*;

@NoArgsConstructor
public class Specification {

    @Getter @Setter @NonNull
    private List<String> extraCharClasses;
    @Getter @Setter @NonNull
    private List<String> matching;
    @Getter @Setter @NonNull
    private List<String> negative;

    private static List<String> readInExtraCharClasses(Scanner sc) {
        String line = sc.nextLine();

        List<String> res = new ArrayList<>();

        while (!Objects.equals(line, "m")) {
            if (line.length() > 0) {
                res.add(line);
            }
            line = sc.nextLine();
        }

        // System.out.println("Extra classes: " + res);
        return res;
    }

    private static List<String> readInMatches(Scanner sc) {
        List<String> res = new ArrayList<>();
        String line;

        while (sc.hasNextLine() && !(line = sc.nextLine()).equals("n")) {
            if (line.length() > 0) {
                res.add(line);
            }
        }

        // System.out.println("Matching examples: " + res);
        return res;
    }


    private static List<String> readInNegative(Scanner sc) {
        List<String> res = new ArrayList<>();
        String line;

        while (sc.hasNextLine() && !(line = sc.nextLine()).equals("e") && !line.equals("m")) {
            if (line.length() > 0) {
                res.add(line);
            }
        }

        // System.out.println("Negative examples: " + res);
        return res;
    }



    public static List<Specification> readInSpec(String filename) {
        try {
            Scanner sc = new Scanner(new File(filename));

            List<Specification> res = new ArrayList<>();

            while (sc.hasNextLine()) {
                Specification spec = new Specification();
                String line = sc.nextLine();

                if (line.equals("e")) {
                    spec.setExtraCharClasses(readInExtraCharClasses(sc));
                } else {
                    spec.setExtraCharClasses(List.of());
                }

                spec.setMatching(readInMatches(sc));

                if (sc.hasNextLine()) {
                    spec.setNegative(readInNegative(sc));
                } else {
                    spec.setNegative(List.of());
                }

                res.add(spec);
            }

            return res;
        } catch (Exception e) {
            return null;
        }
    }



}
