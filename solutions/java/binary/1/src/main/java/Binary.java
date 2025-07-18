public class Binary {
    private final int decimal;

    public Binary(String number) {
        if (number.chars().anyMatch(ch -> ch < '0' || ch > '1')) {
            decimal = 0;
        } else {
            decimal = number.chars()
                .map(ch -> ch - '0')
                .reduce(0, (result, digit) -> result * 2 + digit);
        }
    }

    public int getDecimal() {
        return decimal;
    }
}
