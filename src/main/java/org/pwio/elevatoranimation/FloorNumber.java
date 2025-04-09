package org.pwio.elevatoranimation;

import java.util.Arrays;
import java.util.Random;

public enum FloorNumber {
    GROUND(0), FIRST(1), SECOND(2), THIRD(3), FOURTH(4);

    private static final FloorNumber[] VALUES = values();
    private static final int SIZE = VALUES.length;
    private static final Random RANDOM = new Random();

    private final int number;

    FloorNumber(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public static FloorNumber getRandomFloorNumber() {
        return VALUES[RANDOM.nextInt(SIZE)];
    }

    public static FloorNumber getRandomFloorNumber(FloorNumber withoutNumber) {
        FloorNumber[] newValues = Arrays.stream(VALUES)
                .filter(floorNumber -> floorNumber != withoutNumber)
                .toArray(FloorNumber[]::new);
        return newValues[RANDOM.nextInt(SIZE-1)];
    }
}
