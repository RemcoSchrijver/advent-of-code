import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Grid;
import models.Pair;

public class Day07 {

    public static void main(String[] args) throws IOException {
        String partArg = args[0];
        List<String> inputStrings = Utils.readInput("day07.txt", args);
        List<List<Character>> inputGrid = new ArrayList<>();
        for (String string : inputStrings) {
            List<Character> row = new ArrayList<>();
            for (char character : string.toCharArray()) {
                row.add(character);
            }
            inputGrid.add(row);
        }

        Grid<Character> grid = new Grid<>(inputGrid);

        System.out.println(Utils.executeParts(partArg, grid, Day07::part1, Day07::part2));
    }

    public static long part1(Grid<Character> input) {
        Pair<Integer, Integer> start = findStart(input, 'S');
        Map<Pair<Integer, Integer>, Long> visited = new HashMap<>();
        visited = traverseGridAndCountTaychons(input, start, visited);
        long result = visited.size();
        return result;
    }

    public static long part2(Grid<Character> input) {
        Pair<Integer, Integer> start = findStart(input, 'S');
        Map<Pair<Integer, Integer>, Long> visited = new HashMap<>();
        long result = findPaths(input, start, visited);
        return result;
    }

    public static Pair<Integer, Integer> findStart(Grid<Character> grid, char startSymbol) {
        int y = 0;
        int x = 0;
        List<Character> row = grid.getRow(0);

        while (x < row.size() && row.get(x) != startSymbol) {
            x++;
        }

        return new Pair<>(x, y);
    }

    public static Map<Pair<Integer, Integer>, Long> traverseGridAndCountTaychons(Grid<Character> grid,
            Pair<Integer, Integer> start, Map<Pair<Integer, Integer>, Long> visited) {
        Pair<Integer, Integer> newPos = new Pair<>(start.first, start.second + 1);

        if (!grid.coordinatesOnGrid(newPos) || visited.containsKey(newPos)) {
            return visited;
        }

        char newChar = grid.get(newPos.first, newPos.second);
        if (newChar == '^') {
            visited.put(newPos, 1L);
            Pair<Integer, Integer> splitPos1 = new Pair<Integer, Integer>(newPos.first - 1, newPos.second);
            Pair<Integer, Integer> splitPos2 = new Pair<Integer, Integer>(newPos.first + 1, newPos.second);
            visited.putAll(traverseGridAndCountTaychons(grid, splitPos1, visited));
            visited.putAll(traverseGridAndCountTaychons(grid, splitPos2, visited));
            return visited;
        } else {
            return traverseGridAndCountTaychons(grid, newPos, visited);
        }
    }

    public static long findPaths(Grid<Character> grid,
            Pair<Integer, Integer> start, Map<Pair<Integer, Integer>, Long> visited) {
        Pair<Integer, Integer> newPos = new Pair<>(start.first, start.second + 1);

        if (!grid.coordinatesOnGrid(newPos) || visited.containsKey(newPos)) {
            if (visited.containsKey(newPos)) {
                return visited.get(newPos);
            }
            return 1;
        }

        char newChar = grid.get(newPos.first, newPos.second);
        if (newChar == '^') {
            Pair<Integer, Integer> splitPos1 = new Pair<Integer, Integer>(newPos.first - 1, newPos.second);
            Pair<Integer, Integer> splitPos2 = new Pair<Integer, Integer>(newPos.first + 1, newPos.second);
            Long result = findPaths(grid, splitPos1, visited) + findPaths(grid, splitPos2, visited);
            visited.put(newPos, result);
            return result;
        } else {
            return findPaths(grid, newPos, visited);
        }
    }
}
