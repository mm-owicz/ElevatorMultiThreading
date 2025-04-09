package org.pwio.elevatoranimation;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.Group;
import javafx.util.Duration;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class Person implements Runnable{
    private final UUID personID;
    private final FloorNumber startFloor;
    private final FloorNumber destFloor;
    private final ElevatorDirection destinationDirection;

    private static final double WIDTH = 20;
    private static final double HEIGHT = 40;
    private static final double HEAD_RADIUS = 10;
    private final Group personShape;
    private final Random random = new Random();

    private final ElevatorFloorQueue elevatorFloorQueue;
    private Condition startfloorCondition;
    private Lock startlock;
    private Condition destFloorCondition;
    private Lock destLock;
    private boolean inElevator = false;
    private final Pane root;


    public Person(ElevatorFloorQueue elevatorFloorQueue, Pane root) {
        this.root = root;
        this.personID = UUID.randomUUID();
        this.elevatorFloorQueue = elevatorFloorQueue;

        this.startFloor = FloorNumber.getRandomFloorNumber();
        this.destFloor = FloorNumber.getRandomFloorNumber(this.startFloor);
        if(startFloor.getNumber() < destFloor.getNumber()) {
            this.destinationDirection = ElevatorDirection.UP;
        }else {
            this.destinationDirection = ElevatorDirection.DOWN;
        }


        Color randomColor = generateRandomColor();

        Rectangle body = new Rectangle(WIDTH, HEIGHT);
        body.setFill(randomColor);

        Circle head = new Circle(HEAD_RADIUS);
        head.setFill(randomColor);

        head.setCenterX(WIDTH / 2);
        head.setCenterY(-HEAD_RADIUS);

        this.personShape = new Group(body, head);


        double startY = Building.SCREEN_HEIGHT - (this.startFloor.getNumber()) * Building.FLOOR_HEIGHT - HEIGHT;
        personShape.setLayoutX(50 - WIDTH + 30);
        personShape.setLayoutY(startY);
    }

    public UUID getId() {
        return personID;
    }

    public Group getPersonShape() {
        return personShape;
    }

    public FloorNumber getDestFloor() {
        return destFloor;
    }

    public FloorNumber getStartFloor() {return startFloor;}

    public ElevatorDirection getDestinationDirection() {
        return destinationDirection;
    }

    public void setStartLockAndCondition(Lock lock, Condition floorCondition) {
        this.startlock = lock;
        this.startfloorCondition = floorCondition;
    }

    public void setDestLockAndCondition(Lock lock, Condition floorCondition) {
        this.destLock = lock;
        this.destFloorCondition = floorCondition;
    }

    private Color generateRandomColor() {
        return Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    private void orderElevator() {
        elevatorFloorQueue.addOrder(
                new ElevatorFloorQueue.ElevatorOrder(this.personID, this.startFloor, this.destinationDirection));
        Building.floors.get(getStartFloor()).addWaitingPerson(this);
    }

    public void moveIntoElevator() {
        TranslateTransition walk = new TranslateTransition(Duration.seconds(0.5), personShape);
        walk.setToX(Building.ELEVATOR_X - 40);
        walk.play();

        walk.setOnFinished(event -> {
            Platform.runLater(() -> {
                try {
                    root.getChildren().remove(personShape);
                } catch (Exception e) {
                    System.err.println("Error removing person shape: " + e.getMessage());
                }
            });
        });

    }

    private void leaveElevator(){
        int floorChange = startFloor.getNumber() - destFloor.getNumber();
        personShape.setTranslateY(Building.FLOOR_HEIGHT * floorChange);
        Platform.runLater(() -> root.getChildren().add(this.personShape));

        TranslateTransition walk = new TranslateTransition(Duration.seconds(3), personShape);
        walk.setByX(30 - personShape.getTranslateX());
        walk.play();

        walk.setOnFinished(e -> {
            removeFromScreen();
        });
    }

    public void moveUpInQueue() {
        TranslateTransition walk = new TranslateTransition(Duration.seconds(2), personShape);
        int randomDistance = random.nextInt(150-80+1) + 80;
        walk.setByX(Building.ELEVATOR_X - randomDistance);
        walk.play();
        waitForAnimation(2000);
    }

    private void removeFromScreen(){
        Platform.runLater(() -> root.getChildren().remove(personShape));
    }

    public boolean isInElevator() {
        return inElevator;
    }

    @Override
    public void run() {
        moveUpInQueue();
        orderElevator();

        while(!inElevator) {
            startlock.lock();
            try {
                startfloorCondition.await();
                System.out.println("Person " + personID + " has been notified");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                startlock.unlock();
            }

            if (elevatorFloorQueue.getCurrentDirection().equals(destinationDirection)) {
                // enter elevator animation
                System.out.println("Person " + personID + " is entering elevator");
                inElevator = true;
                moveIntoElevator();

            }
        }

        // wait for new lock (destination floor) to notify you
        destLock.lock();
        try {
            destFloorCondition.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            destLock.unlock();
        }
        // when it notifies you, leave visually
        leaveElevator();


    }

    private void waitForAnimation(int miliseconds) {
        try {
            Thread.sleep(miliseconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
