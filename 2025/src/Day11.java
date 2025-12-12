import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Day11 {

    public static void main(String[] args) throws IOException {
        String partArg = args[0];
        List<String> input = Utils.readInput("day11.txt", args);
        Map<String, String[]> graph = parseGraph(input);
        System.out.println(Utils.executeParts(partArg, graph, Day11::part1, Day11::part2));
    }

    public static long part1(Map<String, String[]> input) {
        return findPathsToOut("you", input, Set.of());
    }

    public static long part2(Map<String, String[]> input) {
        return findPathsToOutThroughDacAndFft("svr", input, Set.of(), new HashMap<>());
    }

    public static Map<String, String[]> parseGraph(List<String> input) {
        Map<String, String[]> result = new HashMap<>();
        for (String line : input) {
            String[] kvSplit = line.split(": ");
            String key = kvSplit[0];
            String[] values = kvSplit[1].split(" ");

            result.put(key, values);
        }
        return result;
    }

    public static long findPathsToOut(String current, Map<String, String[]> graph, Set<String> visited) {
        if (current.equals("out")) {
            return 1;
        }
        if (visited.contains(current)) {
            return 0;
        } else {
            long collector = 0;
            String[] neighbors = graph.get(current);
            Set<String> copyVisited = visited.stream().collect(Collectors.toSet());
            copyVisited.add(current);
            for (String next : neighbors) {
                collector += findPathsToOut(next, graph, copyVisited);
            }
            return collector;
        }
    }

    public static long findPathsToOutThroughDacAndFft(String current, Map<String, String[]> graph, Set<String> visited, Map<Set<String>, Long> knownAnswers) {
        if (current.equals("out")) {
            if (visited.contains("dac") && visited.contains("fft")) {
                knownAnswers.put(visited, 1L);
                return 1;
            }
                knownAnswers.put(visited, 0L);
            return 0;
        }
        if (visited.contains(current)) {
            return 0;
        } else {
            long collector = 0;
            String[] neighbors = graph.get(current);
            Set<String> copyVisited = visited.stream().collect(Collectors.toSet());
            copyVisited.add(current);
            if (knownAnswers.containsKey(copyVisited)) {
                return knownAnswers.get(copyVisited);
            }
            for (String next : neighbors) {
                collector += findPathsToOutThroughDacAndFft(next, graph, copyVisited, knownAnswers);
            }
            knownAnswers.put(copyVisited, collector);
            return collector;
        }
    }
}
