import java.util.Collections;
import java.util.List;

public record Poker(List<String> hands) {

    public List<String> getBestHands() {
        if (hands.size() == 1) {
            return hands;
        }

        final List<HandEvaluation> evaluations = hands.stream()
            .map(HandEvaluation::new)
            .sorted(Collections.reverseOrder())
            .toList();

        final HandEvaluation best = evaluations.get(0);

        return evaluations.stream()
            .filter(evaluation -> evaluation.compareTo(best) == 0)
            .map(HandEvaluation::getHand)
            .toList();
    }
}
