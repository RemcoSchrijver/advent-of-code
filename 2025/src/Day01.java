import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day01 {
    public static void main(String[] args) throws IOException {
        String partArg = args[0];
        List<String> input = Utils.readInput("day01.txt");
        System.out.println(Utils.executeParts(partArg, input, Day01::part1, Day01::part2));
    }

    public static int part1(List<String> input) {
        List<Integer> codeSequence = createCodeSequence(input);

        List<Integer> zeroes = codeSequence.stream().filter(number -> number == 0).toList();
        return zeroes.size();
    }

    public static int part2(List<String> input) {
        return countZeroPasses(input);
    }

    public static List<Integer> createCodeSequence(List<String> input) {
        List<Integer> codeSequence = new ArrayList<Integer>(List.of(50));
        for (String line : input) {
            int previous = codeSequence.getLast();
            Direction direction = line.substring(0, 1).equals("L") ? Direction.LEFT : Direction.RIGHT;
            int movement = Integer.parseInt(line.substring(1));

            if (direction == Direction.LEFT) {
                int newNum = previous - (movement % 100);
                codeSequence.addLast(newNum < 0 ? 100 + newNum : newNum);
            } else {
                codeSequence.addLast((previous + movement) % 100);
            }
        }
        return codeSequence;
    }

    public static int countZeroPasses(List<String> input) {
        List<Integer> codeSequence = new ArrayList<Integer>(List.of(50));
        int zeroPasses = 0;
        for (String line : input) {
            System.out.println("Line: " + line);
            int previous = codeSequence.getLast();
            Direction direction = line.substring(0, 1).equals("L") ? Direction.LEFT : Direction.RIGHT;
            int movement = Integer.parseInt(line.substring(1));
            zeroPasses += Math.floorDiv(movement, 100);
            System.out.println("Zero Passes " + Math.floorDiv(movement, 100));

            if (direction == Direction.LEFT) {
                int newNum = previous - (movement % 100);
                if (newNum < 0) {
                    System.out.println("Passed zero left");
                    if (previous != 0) {
                        zeroPasses += 1;
                    }
                    codeSequence.addLast(100 + newNum);
                } else {
                    codeSequence.addLast(newNum);
                }
            } else {
                int newNum = previous + (movement % 100);
                if (newNum > 100) {
                    System.out.println("Passed zero right");
                    zeroPasses += 1;
                }
                codeSequence.addLast((previous + movement) % 100);
            }
            System.out.println("Current num: " + codeSequence.getLast());
        }
        List<Integer> zeroes = codeSequence.stream().filter(number -> number == 0).toList();
        System.out.println(zeroes.size());
        zeroPasses += zeroes.size();

        return zeroPasses;
    }
}

public enum Direction {
    LEFT,
    RIGHT
}
