import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import models.Grid;

public class Day06 {

    public static void main(String[] args) throws IOException {
        String partArg = args[0];
        List<String> input = Utils.readInput("day06.txt", args);
        System.out.println(Utils.executeParts(partArg, input, Day06::part1, Day06::part2));
    }

    public static long part1(List<String> input) {
        List<List<Long>> inputGrid = new ArrayList<>();
        for (String line : input.subList(0, input.size() - 1)) {
            List<Long> row = new ArrayList<>();
            for (String item : line.split(" ")) {
                item = item.trim();
                if (!item.isBlank()) {
                    row.add(Long.parseLong(item));
                }
            }
            inputGrid.add(row);
        }

        char[] ops = (input.getLast().replaceAll(" ", "").toCharArray());
        Grid<Long> grid = new Grid<>(inputGrid);

        List<Long> results = new ArrayList<>();

        for (int x = 0; x < grid.width(0); x++) {
            List<Long> column = grid.getColumn(x);
            switch (ops[x]) {
                case '+':
                    results.add(column.stream().reduce(0L, (a, b) -> a + b));
                    break;
                case '*':
                    results.add(column.stream().reduce(1L, (a, b) -> a * b));
                    break;
                default:
                    break;
            }
        }

        return results.stream().reduce(0L, (a, b) -> a + b);
    }

    public static long part2(List<String> input) {
        char[] ops = (input.getLast().replaceAll(" ", "").toCharArray());

        Grid<Character> grid = new Grid<>(input.size() - 1, input.getFirst().length());
        for (int lineNum = 0; lineNum < input.size() - 1; lineNum++) {
            String line = input.get(lineNum);
            char[] chars = line.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                grid.getRow(lineNum).add(chars[i]);
            }
        }

        List<Long> results = new ArrayList<>();
        int opsCounter = 0;
        List<Long> accumalator = new ArrayList<>();

        for (int x = 0; x < grid.width(0); x++) {
            String column = String
                    .valueOf(grid.getColumn(x).stream().map(String::valueOf).collect(Collectors.joining()));
            if (column.isBlank() || x == grid.width(0) - 1) {
                if (x == grid.width(0) - 1) {
                    accumalator.add(Long.valueOf(column.trim()));
                }
                switch (ops[opsCounter]) {
                    case '+':
                        results.add(accumalator.stream().reduce(0L, (a, b) -> a + b));
                        break;
                    case '*':
                        results.add(accumalator.stream().reduce(1L, (a, b) -> a * b));
                        break;
                    default:
                        break;
                }
                opsCounter++;
                accumalator = new ArrayList<>();
            } else {
                accumalator.add(Long.valueOf(column.trim()));
            }
        }

        return results.stream().reduce(0L, (a, b) -> a + b);
    }

    public static List<List<Long>> transformColumnsToRightNumbers(Grid<String> grid) {

        List<List<Long>> calculationColumns = new ArrayList<>();
        for (int x = 0; x < grid.width(0); x++) {
            Map<Integer, String> columnNumCollector = new HashMap<>();
            List<String> column = grid.getColumn(x);
            for (String numString : column) {
                char[] chars = numString.toCharArray();
                for (int i = 0; i < chars.length; i++) {
                    if (columnNumCollector.containsKey(i)) {
                        columnNumCollector.put(i, columnNumCollector.get(i) + chars[i]);
                    } else {
                        columnNumCollector.put(i, String.valueOf(chars[i]));
                    }
                }
            }
            calculationColumns.add(columnNumCollector.values().stream().map((e) -> Long.parseLong(e)).toList());
        }

        return calculationColumns;
    }
}
