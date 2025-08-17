import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    public static record Card(String rank, String suit) {
        private static final Map<String, Integer> RANK_VALUES = Map.ofEntries(
            Map.entry("2", 2),
            Map.entry("3", 3),
            Map.entry("4", 4),
            Map.entry("5", 5),
            Map.entry("6", 6),
            Map.entry("7", 7),
            Map.entry("8", 8),
            Map.entry("9", 9),
            Map.entry("10", 10),
            Map.entry("J", 11),
            Map.entry("Q", 12),
            Map.entry("K", 13),
            Map.entry("A", 14)
        );

        public int rankValue() {
            return RANK_VALUES.getOrDefault(rank, 0);
        }
        
        @Override
        public String toString() {
            return rank + suit;
        }
    }

    public static class CardParser {
        
        public static Card parse(String cardString) {
            if (cardString == null || cardString.length() < 2) {
                throw new IllegalArgumentException("Invalid card format: " + cardString);
            }
            
            if (cardString.length() == 2) {
                return new Card(cardString.substring(0, 1), cardString.substring(1, 2));
            } else if (cardString.length() == 3) {
                return new Card(cardString.substring(0, 2), cardString.substring(2, 3));
            } else {
                throw new IllegalArgumentException("Invalid card format: " + cardString);
            }
        }
        
        public static Card[] parseHand(String handString) {
            if (handString == null || handString.trim().isEmpty()) {
                throw new IllegalArgumentException("Hand string cannot be null or empty");
            }
            
            return Arrays.stream(handString.split("\\s+"))
                    .map(CardParser::parse)
                    .toArray(Card[]::new);
        }
    }

    public static class PokerHand implements Comparable<PokerHand> {
        private final String hand;
        private final List<Card> cards;
        private final Rank rank;
        private final List<Integer> tiebreakers;

        public PokerHand(String hand) {
            this.hand = hand;
            this.cards = parseHand(hand);
            this.rank = evaluateHandRank();
            this.tiebreakers = calculateTiebreakers();
        }

        public String getHand() {
            return hand;
        }

        private List<Card> parseHand(String hand) {
            return Arrays.stream(CardParser.parseHand(hand))
                    .sorted(Comparator.comparing(Card::rankValue).reversed())
                    .collect(Collectors.toList());
        }

        private Rank evaluateHandRank() {
            if (isStraightFlush()) return Rank.STRAIGHT_FLUSH;
            if (isFourOfAKind()) return Rank.FOUR_OF_A_KIND;
            if (isFullHouse()) return Rank.FULL_HOUSE;
            if (isFlush()) return Rank.FLUSH;
            if (isStraight()) return Rank.STRAIGHT;
            if (isThreeOfAKind()) return Rank.THREE_OF_A_KIND;
            if (isTwoPair()) return Rank.TWO_PAIR;
            if (isOnePair()) return Rank.ONE_PAIR;
            return Rank.HIGH_CARD;
        }

        private boolean isStraightFlush() {
            return isFlush() && isStraight();
        }

        private boolean isFourOfAKind() {
            return getRankCounts().containsValue(4);
        }

        private boolean isFullHouse() {
            final Map<Integer, Integer> rankCounts = getRankCounts();
            return rankCounts.containsValue(3) && rankCounts.containsValue(2);
        }

        private boolean isFlush() {
            return cards.stream()
                    .map(Card::suit)
                    .distinct()
                    .count() == 1;
        }

        private boolean isStraight() {
            final List<Integer> values = getSortedValues();

            // Check for regular straight
            final boolean isRegularStraight = IntStream.range(1, values.size())
                    .allMatch(i -> values.get(i) == values.get(i-1) + 1);

            // Check for A-2-3-4-5 straight (Ace low)
            final boolean isAceLowStraight = values.equals(List.of(2, 3, 4, 5, 14));

            return isRegularStraight || isAceLowStraight;
        }

        private boolean isThreeOfAKind() {
            return getRankCounts().containsValue(3);
        }

        private boolean isTwoPair() {
            return getRankCounts().values().stream()
                    .filter(count -> count == 2)
                    .count() == 2;
        }

        private boolean isOnePair() {
            return getRankCounts().containsValue(2);
        }

        private Map<Integer, Integer> getRankCounts() {
            return cards.stream()
                    .collect(Collectors.groupingBy(
                        Card::rankValue,
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                    ));
        }

        private List<Integer> getSortedValues() {
            return cards.stream()
                    .map(Card::rankValue)
                    .sorted()
                    .collect(Collectors.toList());
        }

        private List<Integer> calculateTiebreakers() {
            final Map<Integer, Integer> rankCounts = getRankCounts();

            return switch (rank) {
                case STRAIGHT_FLUSH, STRAIGHT -> List.of(isAceLowStraight() ? 5 : cards.get(0).rankValue());

                case FOUR_OF_A_KIND -> {
                    final List<Integer> result = new ArrayList<>();
                    result.add(getRankByCount(rankCounts, 4));
                    result.add(getRankByCount(rankCounts, 1));
                    yield result;
                }

                case FULL_HOUSE -> {
                    final List<Integer> result = new ArrayList<>();
                    result.add(getRankByCount(rankCounts, 3));
                    result.add(getRankByCount(rankCounts, 2));
                    yield result;
                }

                case FLUSH, HIGH_CARD -> cards.stream()
                        .map(Card::rankValue)
                        .collect(Collectors.toList());

                case THREE_OF_A_KIND -> {
                    final List<Integer> result = new ArrayList<>();
                    result.add(getRankByCount(rankCounts, 3));
                    result.addAll(getRanksByCount(rankCounts, 1));
                    yield result;
                }

                case TWO_PAIR -> {
                    final List<Integer> result = new ArrayList<>();
                    result.addAll(getRanksByCount(rankCounts, 2).stream()
                            .sorted(Collections.reverseOrder())
                            .collect(Collectors.toList()));
                    result.addAll(getRanksByCount(rankCounts, 1));
                    yield result;
                }

                case ONE_PAIR -> {
                    final List<Integer> result = new ArrayList<>();
                    result.add(getRankByCount(rankCounts, 2));
                    result.addAll(getRanksByCount(rankCounts, 1));
                    yield result;
                }
            };
        }

        private Integer getRankByCount(Map<Integer, Integer> rankCounts, int count) {
            return rankCounts.entrySet().stream()
                    .filter(entry -> entry.getValue() == count)
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(0);
        }

        private List<Integer> getRanksByCount(Map<Integer, Integer> rankCounts, int count) {
            return rankCounts.entrySet().stream()
                    .filter(entry -> entry.getValue() == count)
                    .map(Map.Entry::getKey)
                    .sorted(Collections.reverseOrder())
                    .collect(Collectors.toList());
        }

        private boolean isAceLowStraight() {
            return getSortedValues().equals(List.of(2, 3, 4, 5, 14));
        }

        @Override
        public int compareTo(PokerHand other) {
            final int rankComparison = Integer.compare(this.rank.ordinal(), other.rank.ordinal());
            if (rankComparison != 0) {
                return rankComparison;
            }

            // Compare tiebreakers
            return IntStream.range(0, Math.min(this.tiebreakers.size(), other.tiebreakers.size()))
                    .map(i -> Integer.compare(this.tiebreakers.get(i), other.tiebreakers.get(i)))
                    .filter(comparison -> comparison != 0)
                    .findFirst()
                    .orElse(0);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            final PokerHand that = (PokerHand) obj;
            return compareTo(that) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(hand, rank, tiebreakers);
        }

        public enum Rank {
            HIGH_CARD,
            ONE_PAIR,
            TWO_PAIR,
            THREE_OF_A_KIND,
            STRAIGHT,
            FLUSH,
            FULL_HOUSE,
            FOUR_OF_A_KIND,
            STRAIGHT_FLUSH
        }
    }
}
