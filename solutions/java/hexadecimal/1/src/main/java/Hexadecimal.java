public class Hexadecimal {

    public static int toDecimal(String number) {
        if (number == null || number.isEmpty() || !number.toLowerCase().matches("[0-9a-f]+")) {
            return 0;
        }
        
        return number.toLowerCase()
            .chars()
            .map(ch -> {
                if (ch >= '0' && ch <= '9') {
                    return ch - '0';
                } else {
                    return ch - 'a' + 10;
                }
            })
            .reduce(0, (result, digit) -> result * 16 + digit);
    }

}
