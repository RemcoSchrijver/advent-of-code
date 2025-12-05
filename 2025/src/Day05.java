import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import models.Pair;

public class Day05 {

    public static void main(String[] args) throws IOException {
        String partArg = args[0];
        List<String> input = Utils.readInput("day05.txt", args);
        System.out.println(Utils.executeParts(partArg, input, Day05::part1, Day05::part2));
    }

    public static long part1(List<String> input) {
        long result = 0;
        Pair<List<Pair<Long, Long>>, List<Long>> rangesAndIds = getRangesAndIds(input);
        for (Long id : rangesAndIds.second) {
            if (findIdInRanges(rangesAndIds.first, id)) {
                result++;
            }
        }
        return result;
    }

    public static long part2(List<String> input) {
        long result = 0;
        Pair<List<Pair<Long, Long>>, List<Long>> rangesAndIds = getRangesAndIds(input);
        List<Pair<Long, Long>> ranges = rangesAndIds.first;
        while (ranges.size() > 0) {
            Pair<Long, Long> range = ranges.get(0);
            result += range.second - range.first + 1;
            removeOverlappingRanges(ranges, range, 0);
        }
        return result;
    }

    public static Pair<List<Pair<Long, Long>>, List<Long>> getRangesAndIds(List<String> input) {
        boolean parsingRanges = true;
        List<Pair<Long, Long>> ranges = new ArrayList<>();
        List<Long> ids = new ArrayList<>();
        for (String line : input) {
            if (line.isBlank()) {
                parsingRanges = false;
                continue;
            }
            if (parsingRanges) {
                String[] split = line.split("-");
                ranges.add(new Pair<Long, Long>(Long.parseLong(split[0]), Long.parseLong(split[1])));
            } else {
                ids.add(Long.parseLong(line));
            }
        }

        return new Pair<>(ranges, ids);
    }

    public static boolean findIdInRanges(List<Pair<Long, Long>> ranges, Long id) {
        for (Pair<Long, Long> range : ranges) {
            boolean idInRange = findIdInRange(range, id);
            if (idInRange) {
                return idInRange;
            }
        }
        return false;
    }

    public static boolean findIdInRange(Pair<Long, Long> range, Long id) {
        return range.first <= id && range.second >= id;
    }

    public static List<Pair<Long, Long>> removeOverlappingRanges(List<Pair<Long, Long>> ranges, Pair<Long, Long> range,
            int index) {
        Optional<Integer> overlappingRangeIndex = Optional.empty();
        ranges.remove(index);
        do {
            overlappingRangeIndex = findIndexOverlappingRangeIfAny(ranges, range);
            if (overlappingRangeIndex.isPresent()) {
                List<Pair<Long, Long>> newRanges = shrinkRange(range, ranges.get(overlappingRangeIndex.get()));
                ranges.remove((int) overlappingRangeIndex.get());
                for (Pair<Long, Long> newRange : newRanges) {
                    ranges.addLast(newRange);
                }
            }
        } while (overlappingRangeIndex.isPresent());

        return ranges;
    }

    public static Optional<Integer> findIndexOverlappingRangeIfAny(List<Pair<Long, Long>> ranges,
            Pair<Long, Long> searchedRange) {
        for (int i = 0; i < ranges.size(); i++) {
            Pair<Long, Long> range = ranges.get(i);
            if (rangesOverlap(range, searchedRange)) {
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }

    public static boolean rangesOverlap(Pair<Long, Long> range1, Pair<Long, Long> range2) {
        return range1.first <= range2.second && range2.first <= range1.second;
    }

    public static List<Pair<Long, Long>> shrinkRange(Pair<Long, Long> inputRange, Pair<Long, Long> overlappedRange) {
        List<Pair<Long, Long>> result = new ArrayList<>();
        if (overlappedRange.first >= inputRange.first) {
            if (overlappedRange.second > inputRange.second) {
                result.add(new Pair<Long, Long>(inputRange.second + 1, overlappedRange.second));
            }
        } else if (overlappedRange.second <= inputRange.second) {
            if (overlappedRange.first < inputRange.first) {
                result.add(new Pair<Long, Long>(overlappedRange.first, inputRange.first - 1));
            }
        } else if (overlappedRange.first < inputRange.first && overlappedRange.second > inputRange.second) {
            result.add(new Pair<Long, Long>(inputRange.second + 1, overlappedRange.second));
            result.add(new Pair<Long, Long>(overlappedRange.first, inputRange.first - 1));
        }
        return result;
    }
}
