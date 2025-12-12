import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Pair;

public class Day12 {

    public static void main(String[] args) throws IOException {
        String partArg = args[0];
        List<String> input = Utils.readInput("day12.txt", args);
        System.out.println(Utils.executeParts(partArg, input, Day12::part1, Day12::part2));
    }

    public static long part1(List<String> input) {
        long result = 0;
        Map<Integer, Integer> presentAreaMap = createPresentAreaMap(input);
        List<Pair<Integer, List<Integer>>> treeAreaList = getTreeAreas(input);

        for (Pair<Integer, List<Integer>> pair : treeAreaList) {
            int area = pair.first;
            List<Integer> giftNumbers = pair.second;
            int usedArea = 0;
            for (int i = 0; i < giftNumbers.size(); i++) {
                usedArea += giftNumbers.get(i) * presentAreaMap.get(i); 
            }
            if (usedArea < area) {
                result++;
            }
        }
        return result;
    }

    public static long part2(List<String> input) {
        return 0;
    }

    public static Map<Integer, Integer> createPresentAreaMap(List<String> input) {
        Map<Integer, Integer> result = new HashMap<>();
        for (int i = 0; i < input.size(); i++) {
            String line = input.get(i);
            if (line.contains("x"))
                break;
            int key = Integer.parseInt(line.replace(":", ""));

            String nextLine = "";
            int area = 0;
            do {
                i++;
                nextLine = input.get(i);
                area += nextLine.length() - nextLine.replace("#", "").length();
            } while (!nextLine.isBlank());
            result.put(key, area);
        }
        return result;
    }

    public static List<Pair<Integer, List<Integer>>> getTreeAreas(List<String> input) {
        int offset = 0;
        for (int i = 0; i < input.size(); i++) {
            if (input.get(i).contains("x")) {
                offset = i;
                break;
            }
        }

        List<Pair<Integer, List<Integer>>> result = new ArrayList<>();

        for (int i = offset; i < input.size(); i++) {
            String line = input.get(i);
            String[] colonSplit = line.split(":");
            String[] areaSplit = colonSplit[0].split("x");
            String[] valuesSplit = colonSplit[1].trim().split(" ");
            int area = Integer.parseInt(areaSplit[0]) * Integer.parseInt(areaSplit[1]);
            List<Integer> values = new ArrayList<>();
            for (String integerString : valuesSplit) {
                values.add(Integer.parseInt(integerString));
            }
            result.add(new Pair<>(area, values));
        }

        return result;
    }

}
