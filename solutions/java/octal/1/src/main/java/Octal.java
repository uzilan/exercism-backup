public class Octal {
    private final int decimal;

    public Octal(String number) {
        if (number.chars().anyMatch(ch -> ch < '0' || ch > '7')) {
            decimal = 0;
        } else {
            decimal = number.chars()
                .map(ch -> ch - '0')
                .reduce(0, (result, digit) -> result * 8 + digit);
        }
    }

    public int getDecimal() {
        return decimal;
    }
}
