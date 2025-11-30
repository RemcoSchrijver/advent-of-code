import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;

public class Utils {
    public static List<String> readInput(String input) throws IOException {
        try (FileReader fileReader = new FileReader(new File("../input/" + input))) {
            return fileReader.readAllLines();
		} catch (IOException e) {
            System.err.println("Problem with reading file: " + input);
			e.printStackTrace();
            throw e;
		}
    }

    public static int executeParts(String partArg, List<String> input, Function<List<String>, Integer> part1, Function<List<String>, Integer> part2) {
        switch (partArg.toLowerCase().replaceAll(" ", "")) {
            case "part1":
                return part1.apply(input);
            case "part2":
                return part2.apply(input);
            default:
                System.err.println("Something is wrong with your arguments: " + partArg);
                return -1;
        }
    }
}
