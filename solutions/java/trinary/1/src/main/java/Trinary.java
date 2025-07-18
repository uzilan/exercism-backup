public class Trinary {
    public static int toDecimal(String number) {
        if (number.chars().anyMatch(ch -> ch < '0' || ch > '2')) {
            return 0;
        }
        
        return number.chars()
                .map(ch -> ch - '0')
                .reduce(0, (result, digit) -> result * 3 + digit);
    }
}
