import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import models.Pair;
import models.Vector3D;

public class Day08 {

    public static void main(String[] args) throws IOException {
        String partArg = args[0];
        List<String> input = Utils.readInput("day08.txt", args);
        List<Vector3D> points = parsePoints(input);
        System.out.println(Utils.executeParts(partArg, points, Day08::part1, Day08::part2));
    }

    public static long part1(List<Vector3D> input) {
        List<Pair<Vector3D, Vector3D>> connections = new ArrayList<>();
        int n = 1000;
        int counter = 0;
        Map<Vector3D, SortedSet<Pair<Double, Vector3D>>> distances = calculateAllDistances(input);
        while (counter < n) {
            connections.add(findShortestDistance(distances));
            counter++;
        }
        List<Set<Vector3D>> circuits = connectPoints(connections);
        circuits.sort((s1, s2) -> s1.size() - s2.size());
        circuits = circuits.reversed();

        return circuits.get(0).size() * circuits.get(1).size() * circuits.get(2).size();
    }

    public static long part2(List<Vector3D> input) {
        Set<Vector3D> fullConnectedSet = new HashSet<>(input);
        List<Set<Vector3D>> mergingSets = new ArrayList<>();
        Pair<Vector3D, Vector3D> lastConnection = null;
        Map<Vector3D, SortedSet<Pair<Double, Vector3D>>> distances = calculateAllDistances(input);

        while (mergingSets.size() == 0 || !mergingSets.getFirst().equals(fullConnectedSet)) {
            Pair<Vector3D, Vector3D> closestPair = findShortestDistance(distances);
            lastConnection = closestPair;

            Vector3D p1 = closestPair.first;
            Vector3D p2 = closestPair.second;

            Set<Vector3D> insertedSet = null;
            List<Set<Vector3D>> setsToDrop = new ArrayList<>();

            for (Set<Vector3D> set : mergingSets) {
                if (set.contains(p1) || set.contains(p2)) {
                    if (insertedSet == null) {

                        set.add(p1);
                        set.add(p2);
                        insertedSet = set;
                    } else {
                        insertedSet.addAll(set);
                        setsToDrop.add(set);
                    }
                }
            }
            if (insertedSet == null) {
                HashSet<Vector3D> set = new HashSet<>();
                set.add(p1);
                set.add(p2);
                mergingSets.add(set);
            }
            for (Set<Vector3D> set : setsToDrop) {
                mergingSets.remove(set);
            }
        }

        return lastConnection.first.x * lastConnection.second.x;
    }

    public static List<Vector3D> parsePoints(List<String> input) {
        List<Vector3D> result = new ArrayList<>();
        for (String line : input) {
            String[] splits = line.split(",");
            result.add(new Vector3D(Long.parseLong(splits[0]), Long.parseLong(splits[1]), Long.parseLong(splits[2])));
        }
        return result;
    }

    public static Map<Vector3D, SortedSet<Pair<Double, Vector3D>>> calculateAllDistances(List<Vector3D> points) {
        Map<Vector3D, SortedSet<Pair<Double, Vector3D>>> result = new HashMap<>();
        for (int i = 0; i < points.size(); i++) {
            SortedSet<Pair<Double, Vector3D>> distanceSet = new TreeSet<>(new Comparator<Pair<Double, Vector3D>>() {
                @Override
                public int compare(Pair<Double, Vector3D> p1, Pair<Double, Vector3D> p2) {
                    return p1.first.compareTo(p2.first);
                }
            });
            Vector3D point = points.get(i);
            for (int j = 0; j < points.size(); j++) {
                // Found ourselves
                if (j == i) {
                    continue;
                } else {
                    Vector3D other = points.get(j);
                    distanceSet.add(new Pair<Double, Vector3D>(point.calculateDistanceToPoint(other), other));
                }
            }
            result.put(point, distanceSet);
        }
        return result;
    }

    public static Pair<Vector3D, Vector3D> findShortestDistance(
            Map<Vector3D, SortedSet<Pair<Double, Vector3D>>> distances) {
        Vector3D minPoint = null;
        double minDistance = Double.MAX_VALUE;
        for (Vector3D point : distances.keySet()) {
            SortedSet<Pair<Double, Vector3D>> distSet = distances.get(point);
            Pair<Double, Vector3D> closest = distSet.getFirst();
            if (closest.first < minDistance) {
                minPoint = point;
                minDistance = closest.first;
            }
        }
        Pair<Double, Vector3D> minPair = distances.get(minPoint).removeFirst();
        distances.get(minPair.second).remove(new Pair<>(minPair.first, minPoint));
        return new Pair<Vector3D, Vector3D>(minPoint, minPair.second);
    }

    public static List<Set<Vector3D>> connectPoints(List<Pair<Vector3D, Vector3D>> pairs) {
        List<Set<Vector3D>> result = new ArrayList<>();

        for (Pair<Vector3D, Vector3D> pair : pairs) {
            Vector3D p1 = pair.first;
            Vector3D p2 = pair.second;
            Set<Vector3D> insertedSet = null;
            List<Set<Vector3D>> setsToDrop = new ArrayList<>();

            for (Set<Vector3D> set : result) {
                if (set.contains(p1) || set.contains(p2)) {
                    if (insertedSet == null) {

                        set.add(p1);
                        set.add(p2);
                        insertedSet = set;
                    } else {
                        insertedSet.addAll(set);
                        setsToDrop.add(set);
                    }
                }
            }
            if (insertedSet == null) {
                HashSet<Vector3D> set = new HashSet<>();
                set.add(p1);
                set.add(p2);
                result.add(set);
            }
            for (Set<Vector3D> set : setsToDrop) {
                result.remove(set);
            }
        }
        return result;
    }

}
