package org.pwio.elevatoranimation;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BuildingAnimation extends Application {
    ExecutorService controllerExecutor = Executors.newFixedThreadPool(2);


    private void drawLeftSideWall(Pane root){
        Line leftWall = new Line(50, 0, 50, Building.SCREEN_HEIGHT);
        leftWall.setStroke(Color.DARKGRAY);
        leftWall.setStrokeWidth(3);
        root.getChildren().add(leftWall);
    }

    private void drawFloors(Pane root){
        for (int i = 0; i <= 5; i++) {
            Line floor = new Line(50, i * Building.FLOOR_HEIGHT, Building.ELEVATOR_X + 60, i * Building.FLOOR_HEIGHT);
            floor.setStroke(Color.DARKGRAY);
            floor.setStrokeWidth(2);
            root.getChildren().add(floor);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();

        drawLeftSideWall(root);
        drawFloors(root);

        Elevator elevator = new Elevator(Building.ELEVATOR_X,
                Building.SCREEN_HEIGHT - Building.FLOOR_HEIGHT,
                Building.SCREEN_HEIGHT, root, FloorNumber.GROUND);

        ElevatorFloorQueue elevatorFloorQueue = new ElevatorFloorQueue();
        ElevatorController elevatorController = new ElevatorController(elevator, elevatorFloorQueue);

        PersonSpawner spawner = new PersonSpawner(root, elevatorFloorQueue);


        // ==========================================================
        // Thread + Runnable:
         new Thread(elevatorController).start();
         new Thread(spawner).start();

        // Executor:
//        controllerExecutor.execute(elevatorController);
//        controllerExecutor.execute(spawner);
        // ==========================================================


        Scene scene = new Scene(root, Building.SCREEN_WIDTH, Building.SCREEN_HEIGHT);
        primaryStage.setTitle("Building with Elevator");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}



