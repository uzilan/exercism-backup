public class SquareRoot {
    public int squareRoot(int radicand) {
        return sqrtRecursive(radicand, 1, radicand);
    }

    private int sqrtRecursive(int radicand, int low, int high) {
        int mid = low + (high - low) / 2;
        int div = radicand / mid;
        if (mid == div && mid * mid == radicand) {
            return mid;
        }
        if (mid <= div) {
            return sqrtRecursive(radicand, mid + 1, high);
        } else {
            return sqrtRecursive(radicand, low, mid - 1);
        }
    }
}
