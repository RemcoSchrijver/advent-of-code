import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Day03 {
    public static void main(String[] args) throws IOException {
        String partArg = args[0];
        List<String> input = Utils.readInput("day03.txt", args);
        System.out.println(Utils.executeParts(partArg, input, Day03::part1, Day03::part2));
    }

    public static long part1(List<String> input) {
        long result = 0;
        for (String string : input) {
            long joltage = findLargestJoltage(string, 2);
            result += joltage;
        }
        return result;
    }

    public static long part2(List<String> input) {
        long result = 0;
        for (String string : input) {
            long joltage = findLargestJoltage(string, 12);
            result += joltage;
        }
        return result;
    }

    public static long findLargestJoltage(String input, int bankSize) {
        String cleanInput = input.trim();
        List<Integer> digitIndices = new ArrayList<>();
        int startIndex = -1;
        for (int i = bankSize - 1; i >= 0; i--) {
            if (!digitIndices.isEmpty()) {
                startIndex = digitIndices.getLast();
            }
            String searchString = cleanInput.substring(startIndex + 1,
                    Math.min(cleanInput.length() - i, cleanInput.length()));
            int relativeIndex = findIndexFirstLargestDigit(searchString);
            digitIndices.add(relativeIndex + startIndex + 1);
        }

        String digits = digitIndices.stream().map((index) -> String.valueOf(cleanInput.charAt(index)))
                .collect(Collectors.joining());
        return Long.parseLong(digits);
    }

    public static int findIndexFirstLargestDigit(String input) {
        for (int digitEval = 9; digitEval >= 0; digitEval--) {
            String digitString = String.valueOf(digitEval);
            if (input.contains(digitString)) {
                return input.indexOf(digitString);
            }
        }
        return 0;
    }
}
