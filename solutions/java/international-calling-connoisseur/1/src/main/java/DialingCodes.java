import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;

public class DialingCodes {

    private final Map<Integer, String> codes = new HashMap<>();

    public Map<Integer, String> getCodes() {
        return unmodifiableMap(codes);
    }

    public void setDialingCode(Integer code, String country) {
        codes.put(code, country);
    }

    public String getCountry(Integer code) {
        return codes.get(code);
    }

    public void addNewDialingCode(Integer code, String country) {
        if (codes.containsKey(code) || codes.containsValue(country)) {
            return;
        }
        setDialingCode(code, country);
    }

    public Integer findDialingCode(String country) {
        return codes.entrySet().stream()
                .filter(entry -> entry.getValue().equals(country))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    public void updateCountryDialingCode(Integer code, String country) {
        final Integer dialingCode = findDialingCode(country);
        if (dialingCode != null) {
            codes.remove(dialingCode);
        }
        setDialingCode(code, country);
    }
}
