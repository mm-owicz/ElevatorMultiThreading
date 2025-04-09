package org.pwio.elevatoranimation;

import javafx.application.Platform;
import javafx.scene.layout.Pane;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class PersonSpawner implements Runnable {
    private AtomicBoolean animationRunning = new AtomicBoolean(true);
    private final Pane root;
    private final ElevatorFloorQueue elevatorFloorQueue;

    private final ExecutorService executor = Executors.newCachedThreadPool();


    public void stopAnimation() {
        animationRunning.set(false);
    }

    public PersonSpawner(Pane root, ElevatorFloorQueue elevatorFloorQueue) {
        this.root = root;
        this.elevatorFloorQueue = elevatorFloorQueue;
    }

    @Override
    public void run() {
        while (animationRunning.get()) {
            Person person = new Person(elevatorFloorQueue, root);
            Floor startFloor = Building.floors.get(person.getStartFloor());

            person.setStartLockAndCondition(startFloor.getLock(), startFloor.getCondition());
            person.setDestLockAndCondition(Building.floors.get(person.getDestFloor()).getLock(), Building.floors.get(person.getDestFloor()).getCondition());

            Platform.runLater(() -> {
                root.getChildren().add(person.getPersonShape());
            });

            // ==========================================================
            // Thread + Runnable:
             new Thread(person).start();

            // Executor:
//            executor.execute(person);
            // ==========================================================

            try {
                Thread.sleep(12000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
