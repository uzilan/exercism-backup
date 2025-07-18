import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ParallelLetterFrequency {

    private final Map<Integer, Integer> map;

    public ParallelLetterFrequency(String input) {
        map = input.chars()
            .parallel()
            .filter(Character::isLetter)
            .map(Character::toLowerCase)
            .boxed()
            .collect(Collectors.groupingByConcurrent(
                Function.identity(),
                Collectors.collectingAndThen(
                    Collectors.counting(),
                    Long::intValue
                )
            ));
    }

    public Map<Integer, Integer> letterCounts() {
        return map;
    }
}
