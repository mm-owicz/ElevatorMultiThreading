package org.pwio.elevatoranimation;

import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.concurrent.atomic.AtomicBoolean;

public class Elevator {
    private static final double ELEVATOR_WIDTH = 60;
    private final int DOOR_ANIMATION_SEC = 1;
    private final int FLOOR_ANIMATION_SEC = 2;
    private final int FLOOR_SHIFT_Y = 120;
    private FloorNumber currentFloor;

    private final Rectangle cabin;
    private final Rectangle leftDoor;
    private final Rectangle rightDoor;
    private final AtomicBoolean opened = new AtomicBoolean(false);

    private final Group elevatorGroup;


    public Elevator(double x, double y, double buildingHeight, Pane root, FloorNumber currentFloor) {
        this.currentFloor = currentFloor;

        // Elevator shaft
        Rectangle shaft = new Rectangle(x, 0, ELEVATOR_WIDTH, buildingHeight);
        shaft.setFill(Color.LIGHTGRAY);
        root.getChildren().add(shaft);

        // Elevator cabin
        cabin = new Rectangle(x, y, ELEVATOR_WIDTH, Building.FLOOR_HEIGHT);
        cabin.setFill(Color.GRAY);

        // Elevator doors
        leftDoor = new Rectangle(x, y, ELEVATOR_WIDTH / 2, Building.FLOOR_HEIGHT);
        rightDoor = new Rectangle(x + ELEVATOR_WIDTH / 2, y, ELEVATOR_WIDTH / 2, Building.FLOOR_HEIGHT);
        leftDoor.setFill(Color.DARKGRAY);
        rightDoor.setFill(Color.DARKGRAY);

        elevatorGroup = new Group(cabin, leftDoor, rightDoor);
        root.getChildren().add(elevatorGroup);

    }

    public void goToFloor(FloorNumber floor)  {
        // Animation
        waitForAnimation(1);
        closeDoors();
        int floorShift = currentFloor.getNumber() - floor.getNumber();
        double yShift = floorShift * FLOOR_SHIFT_Y;
        this.currentFloor = floor;

        TranslateTransition elevatorTransition = new TranslateTransition(Duration.seconds(FLOOR_ANIMATION_SEC), elevatorGroup);
        elevatorTransition.setByY(yShift);
        elevatorTransition.play();

        waitForAnimation(FLOOR_ANIMATION_SEC);

        openDoors();

    }

    public void openDoors() {
        if(opened.get()){
            return;
        }
        TranslateTransition openLeftDoor = new TranslateTransition(Duration.seconds(DOOR_ANIMATION_SEC), leftDoor);
        openLeftDoor.setByX(-ELEVATOR_WIDTH / 2);

        TranslateTransition openRightDoor = new TranslateTransition(Duration.seconds(DOOR_ANIMATION_SEC), rightDoor);
        openRightDoor.setByX(ELEVATOR_WIDTH / 2);

        ParallelTransition openAnimation = new ParallelTransition(openLeftDoor, openRightDoor);
        openAnimation.play();

        waitForAnimation(DOOR_ANIMATION_SEC);
        opened.set(true);
    }

    public void closeDoors() {
        if(!opened.get()){
            return;
        }
        TranslateTransition closeLeftDoor = new TranslateTransition(Duration.seconds(DOOR_ANIMATION_SEC), leftDoor);
        closeLeftDoor.setByX(ELEVATOR_WIDTH / 2);

        TranslateTransition closeRightDoor = new TranslateTransition(Duration.seconds(DOOR_ANIMATION_SEC), rightDoor);
        closeRightDoor.setByX(-ELEVATOR_WIDTH / 2);

        ParallelTransition closeAnimation = new ParallelTransition(closeLeftDoor, closeRightDoor);
        closeAnimation.play();

        waitForAnimation(DOOR_ANIMATION_SEC);
        opened.set(false);
    }

    private void waitForAnimation(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

