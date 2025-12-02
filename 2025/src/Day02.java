import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import models.Pair;

public class Day02 {

    public static void main(String[] args) throws IOException {
        String partArg = args[0];
        List<String> input = Utils.readInput("day02.txt");
        System.out.println(Utils.executePartsLong(partArg, input, Day02::part1, Day02::part2));
    }

    public static long part1(List<String> input) {
        List<Pair<Long, Long>> ranges = parseRanges(input);
        long result = 0;
        for (Pair<Long, Long> range : ranges) {
            List<Long> resultFromRange = findRepeatingNumbersInRange(range);
            result += resultFromRange.stream().reduce(0L, Long::sum);
        }
        return result;
    }

    public static long part2(List<String> input) {
        List<Pair<Long, Long>> ranges = parseRanges(input);
        long result = 0;
        for (Pair<Long, Long> range : ranges) {
            Set<Long> resultFromRange = Set.copyOf(findAlLRepeatingSequenceNumbersInRange(range));
            result += resultFromRange.stream().reduce(0L, Long::sum);
        }
        return result;
    }

    public static List<Pair<Long, Long>> parseRanges(List<String> input) {
        if (input.size() != 1) {
            throw new IllegalArgumentException("This day's input only has one line");
        }
        List<String> rangeStrings = List.of(input.getFirst().split(","));

        List<Pair<Long, Long>> ranges = new ArrayList<>();
        for (String rangeString : rangeStrings) {
            String[] splitRange = rangeString.split("-");
            ranges.add(new Pair<>(Long.parseLong(splitRange[0]), Long.parseLong(splitRange[1])));
        }

        return ranges;
    }

    public static List<Long> findRepeatingNumbersInRange(Pair<Long, Long> range) {
        long start = range.first;
        long end = range.second;

        // End of induction
        if (start > end) {
            return new ArrayList<>();
        }

        // Impossible case, can't divide by two, jump up a significant digit.
        String startString = String.valueOf(start);
        if (startString.length() % 2 == 1) {
            Long newStart = Long.parseLong("1" + "0".repeat(startString.length()));
            return findRepeatingNumbersInRange(new Pair<>(newStart, end));
        }

        String firstHalf = startString.substring(0, startString.length() / 2);
        Long repeatingNum = Long.parseLong(firstHalf + firstHalf);
        if (repeatingNum <= end && repeatingNum >= start) {
            String nineHalf = "9".repeat(startString.length() / 2);
            List<Long> result = findRepeatingNumbersInRange(
                    new Pair<Long, Long>(Long.parseLong(firstHalf + nineHalf) + 1, end));
            result.add(repeatingNum);
            return result;
        } else if (repeatingNum < start) {
            Long nextDigit = Long.parseLong(String.valueOf(firstHalf.charAt(firstHalf.length() - 1))) + 1;
            Long nextNum = Long.parseLong(
                    firstHalf.substring(0, firstHalf.length() - 1) + nextDigit + "0".repeat(firstHalf.length()));
            return findRepeatingNumbersInRange(new Pair<>(nextNum, end));
        } else {
            return new ArrayList<>();
        }
    }

    public static List<Long> findAlLRepeatingSequenceNumbersInRange(Pair<Long, Long> range) {
        long start = range.first;
        long end = range.second;

        List<Long> result = new ArrayList<>();

        // End of induction
        if (start > end) {
            return result;
        }
        String startString = String.valueOf(start);
        List<Long> potentialSequences = findPotentialRepeatingSequences(start);
        for (Long potentialSequence : potentialSequences) {
            if (potentialSequence >= start && potentialSequence <= end) {
                result.add(potentialSequence);
            }
        }
        if (startString.length() % 2 == 0) {
            String firstHalf = startString.substring(0, startString.length() / 2);
            Long newStart = Long.parseLong(firstHalf + "9".repeat(firstHalf.length())) + 1;
            result.addAll(findAlLRepeatingSequenceNumbersInRange(new Pair<>(newStart, end)));
        } else {
            if (startString.length() == 1) {
                result.addAll(findAlLRepeatingSequenceNumbersInRange(new Pair<>(10L, end)));
            } else {
                String firstHalf = startString.substring(0, startString.length() / 2);
                Long newStart = Long.parseLong(
                        firstHalf + "9".repeat(firstHalf.length() + 1)) + 1;
                result.addAll(findAlLRepeatingSequenceNumbersInRange(new Pair<>(newStart, end)));
            }
        }
        return result;

    }

    public static List<Long> findPotentialRepeatingSequences(Long start) {
        String startString = String.valueOf(start);
        List<Long> potentialSequences = new ArrayList<>();
        List<Integer> divisors = findDivisorsForNumString(startString);
        int n = 0;
        while (divisors.size() > 0) {
            n = divisors.remove(0);
            String newRepeatSequence = startString.substring(0, startString.length() / n);
            potentialSequences.add(Long.valueOf(newRepeatSequence.repeat(n)));
        }
        return potentialSequences;
    }

    public static List<Integer> findDivisorsForNumString(String numString) {
        int length = numString.length();
        List<Integer> divisors = new ArrayList<>();
        int count = 2;
        while (count <= length) {
            if (length % count == 0) {
                divisors.add(count);
            }
            count++;
        }
        return divisors;
    }
}
