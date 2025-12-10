import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import models.Pair;

public class Day09 {

    public static void main(String[] args) throws IOException {
        String partArg = args[0];
        List<String> input = Utils.readInput("day09.txt", args);
        List<Pair<Long, Long>> coordinates = parseCoordinates(input);
        System.out.println(Utils.executeParts(partArg, coordinates, Day09::part1, Day09::part2));
    }

    public static long part1(List<Pair<Long, Long>> input) {
        Map<Pair<Pair<Long, Long>, Pair<Long, Long>>, Long> areas = findAllAreas(input);
        return areas.values().stream().mapToLong(l -> l).max().orElseThrow(NoSuchElementException::new);
    }

    public static long part2(List<Pair<Long, Long>> input) {
        Set<Pair<Long, Long>> borders = paintBorder(input);
        Map<Pair<Pair<Long, Long>, Pair<Long, Long>>, Long> areas = findAllAreasWithBorder(input, borders);
        return areas.values().stream().mapToLong(l -> l).max().orElseThrow(NoSuchElementException::new);
    }

    public static List<Pair<Long, Long>> parseCoordinates(List<String> input) {
        List<Pair<Long, Long>> result = new ArrayList<>();
        for (String line : input) {
            String[] splits = line.split(",");
            result.add(new Pair<>(Long.valueOf(splits[0]), Long.valueOf(splits[1])));
        }
        return result;
    }

    public static Map<Pair<Pair<Long, Long>, Pair<Long, Long>>, Long> findAllAreas(List<Pair<Long, Long>> coordinates) {
        Map<Pair<Pair<Long, Long>, Pair<Long, Long>>, Long> result = new HashMap<>();
        for (int i = 0; i < coordinates.size(); i++) {
            for (int j = 0; j < coordinates.size(); j++) {
                if (j == i)
                    continue;
                Pair<Pair<Long, Long>, Pair<Long, Long>> combination = new Pair<>(coordinates.get(i),
                        coordinates.get(j));
                Pair<Pair<Long, Long>, Pair<Long, Long>> reverseCombination = new Pair<>(coordinates.get(j),
                        coordinates.get(i));
                if (result.containsKey(combination) || result.containsKey(reverseCombination)) {
                    continue;
                }
                result.put(combination, calculateArea(combination.first, combination.second));
            }
        }
        return result;
    }

    public static long calculateArea(Pair<Long, Long> p1, Pair<Long, Long> p2) {
        return (Math.abs(p1.first - p2.first) + 1) * (Math.abs(p1.second - p2.second) + 1);
    }

    public static Map<Pair<Pair<Long, Long>, Pair<Long, Long>>, Long> findAllAreasWithBorder(
            List<Pair<Long, Long>> coordinates, Set<Pair<Long, Long>> borders) {
        Map<Pair<Pair<Long, Long>, Pair<Long, Long>>, Long> result = new HashMap<>();

        for (int i = 0; i < coordinates.size(); i++) {
            System.out.println("%" + (100 * i / coordinates.size()));
            for (int j = 0; j < coordinates.size(); j++) {
                if (j == i)
                    continue;

                Pair<Long, Long> p1 = coordinates.get(i);
                Pair<Long, Long> p2 = coordinates.get(j);
                Pair<Pair<Long, Long>, Pair<Long, Long>> combination = new Pair<>(p1, p2);
                Pair<Pair<Long, Long>, Pair<Long, Long>> reverseCombination = new Pair<>(p2, p1);
                if (result.containsKey(combination) || result.containsKey(reverseCombination)) {
                    continue;
                }

                Pair<Long, Long> opposingCorner1 = new Pair<>(coordinates.get(i).first, coordinates.get(j).second);
                Pair<Long, Long> opposingCorner2 = new Pair<>(coordinates.get(j).first, coordinates.get(i).second);

                if (lineDoesNotCrossBorder(borders, p1, opposingCorner1)
                        && lineDoesNotCrossBorder(borders, p1, opposingCorner2)
                        && lineDoesNotCrossBorder(borders, p2, opposingCorner1)
                        && lineDoesNotCrossBorder(borders, p2, opposingCorner2)) {
                    result.put(combination, calculateArea(p1, p2));
                }
            }
        }
        return result;
    }

    public static Set<Pair<Long, Long>> paintBorder(List<Pair<Long, Long>> coordinates) {
        Set<Pair<Long, Long>> result = new HashSet<>();
        for (int i = 0; i < coordinates.size(); i++) {
            int next = (i + 1) % coordinates.size();
            Pair<Long, Long> coordinate = coordinates.get(i);
            Pair<Long, Long> nextCoordinate = coordinates.get(next);

            long x1 = coordinate.first;
            long x2 = nextCoordinate.first;
            long y1 = coordinate.second;
            long y2 = nextCoordinate.second;

            if (x1 == x2) {
                long start = y1 < y2 ? y1 : y2;
                long end = y1 > y2 ? y1 : y2;
                for (long k = start; k <= end; k++) {
                    result.add(new Pair<>(x1, k));
                }
            } else {
                long start = x1 < x2 ? x1 : x2;
                long end = x1 > x2 ? x1 : x2;
                for (long k = start; k <= end; k++) {
                    result.add(new Pair<>(k, y1));
                }
            }
        }
        return result;
    }

    public static boolean lineDoesNotCrossBorder(Set<Pair<Long, Long>> border, Pair<Long, Long> c1,
            Pair<Long, Long> c2) {

        Pair<Long, Long> moveVector = null;
        if (c1.first == c2.first) {
            if (c1.second < c2.second) {
                moveVector = new Pair<>(0L, 1L);
            } else {
                moveVector = new Pair<>(0L, -1L);
            }
        } else {
            if (c1.first < c2.first) {
                moveVector = new Pair<>(1L, 0L);
            } else {
                moveVector = new Pair<>(-1L, 0L);
            }
        }

        boolean inBorder = false;

        Pair<Long, Long> currVector = c1;
        while (!currVector.equals(c2)) {
            currVector = add(currVector, moveVector);
            if (border.contains(currVector)) {
                inBorder = true;
            } else {
                // We crossed the border
                if (inBorder)
                    return false;
            }
        }

        return true;
    }

    public static Pair<Long, Long> add(Pair<Long, Long> pair1, Pair<Long, Long> pair2) {
        return new Pair<>(pair1.first + pair2.first, pair1.second + pair2.second);
    }

    public static Pair<Long, Long> mul(Pair<Long, Long> pair, Long mul) {
        return new Pair<>(pair.first * mul, pair.second * mul);
    }

}
