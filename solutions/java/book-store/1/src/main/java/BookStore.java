import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BookStore {

    private static final double BOOK_PRICE = 8.0;
    private static final List<Double> DISCOUNTS = List.of(0.0, 0.0, 5.0, 10.0, 20.0, 25.0); // 0%, 0%, 5%, 10%, 20%, 25%
    
    // Type aliases for complex structures
    private static final class BookCounts extends HashMap<Integer, Integer> {}
    private static final class StrategyState {
        final BookCounts counts;
        final double cost;
        StrategyState(final BookCounts counts, final double cost) {
            this.counts = counts;
            this.cost = cost;
        }
    }

    public double calculateBasketCost(final List<Integer> books) {
        if (books.isEmpty()) {
            return 0.0;
        }

        // Count the frequency of each book (functional style)
        final Map<Integer, Integer> counts = books.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.summingInt(b -> 1)));
        final BookCounts bookCounts = new BookCounts();
        bookCounts.putAll(counts);

        // Try different grouping strategies and find the minimum cost
        return findOptimalCost(bookCounts);
    }

    private double findOptimalCost(final BookCounts bookCounts) {
        return IntStream.range(0, 3)
                .mapToDouble(strategy -> computeCostForStrategy(bookCounts, strategy))
                .min()
                .orElse(0.0);
    }

    private double computeCostForStrategy(final BookCounts originalCounts, final int strategy) {
        final List<Integer> groupSizes = getGroupSizes(strategy);

        final StrategyState finalState = groupSizes.stream().reduce(
                new StrategyState(copyCounts(originalCounts), 0.0),
                (state, groupSize) -> {
                    final List<BookCounts> sequence = java.util.stream.Stream.iterate(
                                    state.counts,
                                    counts -> canFormGroup(counts, groupSize),
                                    counts -> applyOneGroup(counts, groupSize)
                            )
                            .toList();

                    if (sequence.isEmpty()) {
                        return state;
                    }

                    final long groupsFormed = sequence.size();
                    final double addedCost = groupsFormed * calculateGroupCost(groupSize);
                    final BookCounts lastBeforeStop = sequence.get((int) groupsFormed - 1);
                    final BookCounts finalCounts = applyOneGroup(lastBeforeStop, groupSize);
                    return new StrategyState(finalCounts, state.cost + addedCost);
                },
                (a, b) -> {
                    throw new UnsupportedOperationException("Parallel reduction not supported");
                }
        );

        // Add cost for remaining books (no discount)
        final int remainingCount = finalState.counts.values().stream().mapToInt(Integer::intValue).sum();
        return finalState.cost + remainingCount * BOOK_PRICE;
    }

    private List<Integer> getGroupSizes(final int strategy) {
        switch (strategy) {
            case 0: return List.of(5, 4, 3, 2); // Prefer 5, then 4, then 3, then 2
            case 1: return List.of(4, 5, 3, 2); // Prefer 4, then 5, then 3, then 2
            case 2: return List.of(3, 4, 5, 2); // Prefer 3, then 4, then 5, then 2
            default: return List.of(5, 4, 3, 2);
        }
    }

    private boolean canFormGroup(final BookCounts bookCounts, final int groupSize) {
        // We can form a group if we have at least `groupSize` distinct titles
        return bookCounts.size() >= groupSize;
    }

    private BookCounts applyOneGroup(final BookCounts counts, final int groupSize) {
        final BookCounts next = copyCounts(counts);
        final List<Integer> keysToDecrement = next.entrySet().stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByValue(Comparator.reverseOrder()))
                .limit(groupSize)
                .map(Map.Entry::getKey)
                .toList();
        keysToDecrement.forEach(key -> {
            final int newCount = next.get(key) - 1;
            if (newCount == 0) {
                next.remove(key);
            } else {
                next.put(key, newCount);
            }
        });
        return next;
    }

    private BookCounts copyCounts(final BookCounts source) {
        final BookCounts copy = new BookCounts();
        copy.putAll(source);
        return copy;
    }

    private double calculateGroupCost(final int groupSize) {
        final double discount = DISCOUNTS.get(groupSize);
        return groupSize * BOOK_PRICE * (100 - discount) / 100.0;
    }
}