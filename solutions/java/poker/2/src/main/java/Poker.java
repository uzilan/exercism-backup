import java.util.Collections;
import java.util.List;

public record Poker(List<String> hands) {

    public List<String> getBestHands() {
        if (hands.size() == 1) {
            return hands;
        }

        final List<PokerHand> evaluations = hands.stream()
            .map(PokerHand::new)
            .sorted(Collections.reverseOrder())
            .toList();

        final PokerHand best = evaluations.get(0);

        return evaluations.stream()
            .filter(evaluation -> evaluation.compareTo(best) == 0)
            .map(PokerHand::getHand)
            .toList();
    }
}
