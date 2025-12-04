import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import models.Grid;

public class Day04 {

    public static void main(String[] args) throws IOException {
        String partArg = args[0];
        List<String> inputStrings = Utils.readInput("day04.txt", args);
        List<List<Character>> inputGrid = new ArrayList<>();
        for (String string : inputStrings) {
            List<Character> row = new ArrayList<>();
            for (char character : string.toCharArray()) {
                row.add(character);
            }
            inputGrid.add(row);
        }

        Grid<Character> grid = new Grid<>(inputGrid);

        System.out.println(Utils.executeParts(partArg, grid, Day04::part1, Day04::part2));
    }

    public static long part1(Grid<Character> input) {
        long result = 0;
        for (int y = 0; y < input.length(); y++) {
            for (int x = 0; x < input.width(y); x++) {
                if (input.get(x, y) == '@') {
                    List<Character> neighbors = input.returnNeighbors(x, y);
                    if (nodeCanBeRemoved(neighbors)) {
                        result++;
                    }
                }
            }
        }
        return result;
    }

    public static long part2(Grid<Character> input) {
        long old_result = -1;
        long result = 0;
        while(old_result != result) {
            old_result = result;
            for (int y = 0; y < input.length(); y++) {
                for (int x = 0; x < input.width(y); x++) {
                    if (input.get(x, y) == '@') {
                        List<Character> neighbors = input.returnNeighbors(x, y);
                        if (nodeCanBeRemoved(neighbors)) {
                            result++;
                            input.set(x, y, '.');
                        }
                    }
                }
            }
        }
        return result;
    }

    public static boolean nodeCanBeRemoved(List<Character> input) {
        return input.stream().filter(c -> c == '@').toList().size() < 4;
    }

}
