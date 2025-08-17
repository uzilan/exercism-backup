import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class School {
    private final Map<Integer, List<String>> school = new HashMap<>();

    public List<String> roster() {
        return school.keySet().stream()
            .flatMap(key -> grade(key).stream())
            .toList();
    }

    public boolean add(String name, int grade) {
        if (school.values().stream().anyMatch(x -> x.contains(name))) {
            return false;
        }

        final List<String> names = school.containsKey(grade) ? school.get(grade) : new ArrayList<>();
        names.add(name);
        school.put(grade, names);
        return true;
    }

    public List<String> grade(int grade) {
        final List<String> students = school.get(grade);
        return students == null
            ? List.of()
            : students.stream().sorted().toList();
    }
}
