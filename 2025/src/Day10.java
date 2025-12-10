import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import models.Pair;

public class Day10 {

    public static void main(String[] args) throws IOException {
        String partArg = args[0];
        List<String> input = Utils.readInput("day10.txt", args);
        System.out.println(Utils.executeParts(partArg, input, Day10::part1, Day10::part2));
    }

    public static long part1(List<String> input) {
        List<Long> result = new ArrayList<>();
        List<String[]> splits = input.stream().map((s) -> s.split(" ")).toList();

        List<List<Boolean>> lights = parseLights(splits);
        List<Set<List<Boolean>>> buttonsList = parseButtons(splits, lights);

        for (int i = 0; i < lights.size(); i++) {
            List<Boolean> light = lights.get(i);
            Set<List<Boolean>> buttons = buttonsList.get(i);
            result.add(findLeastButtonPresses(light, buttons));
        }

        return result.stream().mapToLong(e -> e).sum();
    }

    public static long part2(List<String> input) {
        List<Long> result = new ArrayList<>();
        List<String[]> splits = input.stream().map((s) -> s.split(" ")).toList();

        List<List<Long>> joltages = parseJoltages(splits);
        List<Set<List<Long>>> buttonsList = parseButtonsForJoltages(splits, joltages);

        for (int i = 0; i < joltages.size(); i++) {
            System.out.println(100 * i / joltages.size() + "%");
            List<Long> light = joltages.get(i);
            Set<List<Long>> buttons = buttonsList.get(i);
            result.add(findLeastButtonPressesForJoltage(light, buttons));
        }

        return result.stream().mapToLong(e -> e).sum();
    }

    public static List<List<Boolean>> parseLights(List<String[]> splits) {
        List<List<Boolean>> result = new ArrayList<>();
        for (String[] splitLine : splits) {
            String lightString = splitLine[0];
            List<Boolean> wantedSequence = new ArrayList<>();
            for (Character chars : lightString.toCharArray()) {
                switch (chars) {
                    case '.':
                        wantedSequence.add(false);
                        break;
                    case '#':
                        wantedSequence.add(true);
                        break;
                    default:
                        break;
                }
            }
            result.add(wantedSequence);
        }
        return result;
    }

    public static List<Set<List<Boolean>>> parseButtons(List<String[]> splits, List<List<Boolean>> lights) {
        List<Set<List<Boolean>>> result = new ArrayList<>();
        for (int i = 0; i < splits.size(); i++) {
            String[] splitLine = splits.get(i);
            int gridLength = lights.get(i).size();

            Set<List<Boolean>> buttons = new HashSet<>();

            for (int j = 1; j < splitLine.length - 1; j++) {
                List<Boolean> button = new ArrayList<>(Collections.nCopies(gridLength, false));

                String buttonString = splitLine[j];
                String[] splitActivations = buttonString.substring(1, buttonString.length() - 1).split(",");

                for (String intString : splitActivations) {
                    button.set(Integer.parseInt(intString), true);
                }
                buttons.add(button);
            }
            result.add(buttons);
        }
        return result;
    }

    public static List<Set<List<Long>>> parseButtonsForJoltages(List<String[]> splits, List<List<Long>> joltages) {
        List<Set<List<Long>>> result = new ArrayList<>();
        for (int i = 0; i < splits.size(); i++) {
            String[] splitLine = splits.get(i);
            int gridLength = joltages.get(i).size();

            Set<List<Long>> buttons = new HashSet<>();

            for (int j = 1; j < splitLine.length - 1; j++) {
                List<Long> button = new ArrayList<>(Collections.nCopies(gridLength, 0L));

                String buttonString = splitLine[j];
                String[] splitActivations = buttonString.substring(1, buttonString.length() - 1).split(",");

                for (String intString : splitActivations) {
                    button.set(Integer.parseInt(intString), 1L);
                }
                buttons.add(button);
            }
            result.add(buttons);
        }
        return result;
    }

    public static List<List<Long>> parseJoltages(List<String[]> splits) {
        List<List<Long>> result = new ArrayList<>();
        for (String[] splitLine : splits) {
            String joltagesString = splitLine[splitLine.length - 1];
            List<Long> wantedJoltage = new ArrayList<>();
            for (String joltage : joltagesString.substring(1, joltagesString.length() - 1).split(",")) {
                wantedJoltage.add(Long.parseLong(joltage));
            }
            result.add(wantedJoltage);
        }
        return result;
    }

    public static long findLeastButtonPresses(List<Boolean> light, Set<List<Boolean>> buttons) {
        long iteration = 1;
        Map<Long, Set<List<Boolean>>> allButtonPresses = new HashMap<>();
        if (buttons.contains(light)) {
            return iteration;
        } else {
            allButtonPresses.put(iteration, buttons);
        }
        while (true) {
            iteration++;
            Set<List<Boolean>> resultSet = new HashSet<>();
            for (List<Boolean> buttonPress : allButtonPresses.get(iteration - 1)) {
                for (List<Boolean> button : buttons) {
                    List<Boolean> xorResult = xor(buttonPress, button);
                    if (xorResult.equals(light)) {
                        return iteration;
                    } else {
                        resultSet.add(xorResult);
                    }
                }
            }
            allButtonPresses.put(iteration, resultSet);
        }
    }

    public static List<Boolean> xor(List<Boolean> l1, List<Boolean> l2) {
        List<Boolean> result = new ArrayList<>();
        for (int i = 0; i < l1.size(); i++) {
            result.add(l1.get(i) ^ l2.get(i));
        }
        return result;
    }

    public static long findLeastButtonPressesForJoltage(List<Long> joltage, Set<List<Long>> buttons) {
        // Z3 solver huh
        return 0;
    }

    public static List<Long> add(List<Long> l1, List<Long> l2) {
        List<Long> result = new ArrayList<>();
        for (int i = 0; i < l1.size(); i++) {
            result.add(l1.get(i) + l2.get(i));
        }
        return result;
    }

    public static boolean joltageOverflow(List<Long> input, List<Long> target) {

        boolean result = false;
        for (int i = 0; i < input.size(); i++) {
            if (input.get(i) > target.get(i)) {
                return true;
            }
        }
        return result;
    }
}
