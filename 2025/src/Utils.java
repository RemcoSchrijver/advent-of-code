import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;

public class Utils {
    public static List<String> readInput(String input, String[] args) throws IOException {
        boolean test = false;
        if (args.length == 2) {
            if (args[1].toLowerCase().equals("--test")) {
                test = true;
            }
        }
        String directory = test ? "./tests/" : "./input/";
        try (FileReader fileReader = new FileReader(new File(directory + input))) {
            return fileReader.readAllLines();
        } catch (IOException e) {
            System.err.println("Problem with reading file: " + input);
            e.printStackTrace();
            throw e;
        }
    }

    public static <T, S> S executeParts(String partArg, T input, Function<T, S> part1, Function<T, S> part2) {
        switch (partArg.toLowerCase().replaceAll(" ", "")) {
            case "part1":
                return part1.apply(input);
            case "part2":
                return part2.apply(input);
            default:
                System.err.println("Something is wrong with your arguments: " + partArg);
                return null;
        }
    }
}
