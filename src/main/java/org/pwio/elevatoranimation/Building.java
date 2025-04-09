package org.pwio.elevatoranimation;

import java.util.concurrent.ConcurrentHashMap;

public class Building {
    public static final double SCREEN_WIDTH = 600;
    public static final double SCREEN_HEIGHT = 600;
    public static final double FLOOR_HEIGHT = SCREEN_HEIGHT / 5;
    public static final double ELEVATOR_X = SCREEN_WIDTH - 100;

    public static final ConcurrentHashMap<FloorNumber, Floor> floors = new ConcurrentHashMap<>();
    static {
        floors.put(FloorNumber.GROUND, new Floor(FloorNumber.GROUND));
        floors.put(FloorNumber.FIRST, new Floor(FloorNumber.FIRST));
        floors.put(FloorNumber.SECOND, new Floor(FloorNumber.SECOND));
        floors.put(FloorNumber.THIRD, new Floor(FloorNumber.THIRD));
        floors.put(FloorNumber.FOURTH, new Floor(FloorNumber.FOURTH));
    }

}
