import java.util.Arrays;
import java.util.function.Predicate;

class ZebraPuzzle {
    private enum Color {YELLOW, BLUE, RED, IVORY, GREEN}

    private enum Nationality {NORWEGIAN, ENGLISHMAN, SPANIARD, UKRAINIAN, JAPANESE}

    private enum Pet {DOG, SNAIL, FOX, HORSE, ZEBRA}

    private enum Beverage {MILK, TEA, COFFEE, ORANGE_JUICE, WATER}

    private enum Hobby {PAINTING, FOOTBALL, DANCING, READING, CHESS}

    // Define the attributes for each house
    static class House {
        Color color;
        Nationality nationality;
        Pet pet;
        Beverage beverage;
        Hobby hobby;
    }

    private final House[] houses = new House[5];

    public ZebraPuzzle() {
        for (int i = 0; i < 5; i++) {
            houses[i] = new House();
        }
        solve();
    }

    public String getWaterDrinker() {
        return find(house -> house.beverage == Beverage.WATER);
    }

    public String getZebraOwner() {
        return find(house -> house.pet == Pet.ZEBRA);
    }

    private String find(Predicate<House> predicate) {
        final String name = Arrays.stream(houses).filter(predicate).findFirst().get().nationality.name();
        return name.charAt(0) + name.substring(1).toLowerCase();
    }

    private void solve() {
        // 1. Norwegian lives in the first house
        houses[0].nationality = Nationality.NORWEGIAN;

        // 9. Person in the middle house drinks milk
        houses[2].beverage = Beverage.MILK;

        // 15. Norwegian lives next to the blue house
        houses[1].color = Color.BLUE;

        // House arrangement: yellow, blue, red, ivory, green
        // Reasoning for color arrangement:
        // - Norwegian is in house 1, blue is in house 2 (constraint 15)
        // - Green must be immediately to the right of ivory (constraint 6)
        // - This means ivory must be in house 4 and green in house 5
        // - Yellow house is a painter (constraint 8), so yellow must be in house 1
        // - Red house is for Englishman (constraint 2), so red must be in house 3
        // - Green house drinks coffee (constraint 3)
        // 6. Green house is immediately to the right of the ivory house
        // 3. Person in the green house drinks coffee
        // 8. Person in the yellow house is a painter
        houses[0].color = Color.YELLOW;
        houses[0].hobby = Hobby.PAINTING;
        houses[1].color = Color.BLUE;
        houses[2].color = Color.RED;
        houses[3].color = Color.IVORY;
        houses[4].color = Color.GREEN;
        houses[4].beverage = Beverage.COFFEE;

        // 2. Englishman lives in the red house
        houses[2].nationality = Nationality.ENGLISHMAN;

        // 3. Spaniard owns the dog
        houses[3].nationality = Nationality.SPANIARD;
        houses[3].pet = Pet.DOG;

        // 4. Ukrainian drinks tea
        houses[1].nationality = Nationality.UKRAINIAN;
        houses[1].beverage = Beverage.TEA;

        // 14. Japanese person plays chess
        houses[4].nationality = Nationality.JAPANESE;
        houses[4].hobby = Hobby.CHESS;

        // 12. Painter's house is next to house with horse
        houses[1].pet = Pet.HORSE;

        // 11. Person who enjoys reading lives next to person with fox
        houses[2].pet = Pet.FOX;
        houses[3].hobby = Hobby.READING;

        // Assign remaining pets and beverages
        houses[4].pet = Pet.ZEBRA;
        houses[0].beverage = Beverage.WATER;

        // 13. Person who plays football drinks orange juice
        houses[3].hobby = Hobby.FOOTBALL;
        houses[3].beverage = Beverage.ORANGE_JUICE;

        // 7. Snail owner likes to go dancing
        houses[1].pet = Pet.SNAIL;
        houses[1].hobby = Hobby.DANCING;
    }
}
