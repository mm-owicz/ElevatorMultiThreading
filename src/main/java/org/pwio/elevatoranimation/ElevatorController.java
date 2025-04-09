package org.pwio.elevatoranimation;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class ElevatorController implements Runnable{
    private final Elevator elevator;
    private final ElevatorFloorQueue orderQueue;
    private final ConcurrentHashMap<FloorNumber, List<Person>> peopleInElevator
            = new ConcurrentHashMap<>(); // destination floor, people leaving there
    private final AtomicBoolean running = new AtomicBoolean(false);

    private static final Object floorsListLock = new Object();
    private final List<FloorNumber> floorsToVisit = new ArrayList<>();


    public ElevatorController(Elevator elevator,
                              ElevatorFloorQueue orderQueue) {
        this.elevator = elevator;
        this.orderQueue = orderQueue;
    }

    private void processPeopleEnteringElevator(Floor floor, ElevatorDirection direction) {
        List<Person> enteringPeople = floor.getPeopleGoingInDirection(direction);
        List<FloorNumber> clickedFloors = new ArrayList<>();
        for (Person person : enteringPeople) {
            System.out.println("Person: " + person.getId() + " is being processed entering elevator");
            System.out.println("   wants to go to " + person.getDestFloor());
            floor.removeWaitingPerson(person);
            FloorNumber destFloor = person.getDestFloor();
            clickedFloors.add(destFloor);
            System.out.println("Destination floor for order: " + destFloor);
            peopleInElevator.computeIfAbsent(destFloor, _ -> new ArrayList<>());
            peopleInElevator.get(destFloor).add(person);
            orderQueue.deleteOrderByID(person.getId());
        }


        synchronized (floorsListLock) {
            Set<FloorNumber> clickedFloorSet = new HashSet<>(clickedFloors);
            clickedFloorSet.forEach(f -> {
                if (!floorsToVisit.contains(f)) {
                    floorsToVisit.add(f);
                }
            });
            floorsToVisit.sort(direction.equals(ElevatorDirection.UP)
                    ? Comparator.naturalOrder() : Comparator.reverseOrder());
        }

    }

    public void stopRunning() {
        this.running.set(false);
    }

    private void waitForAnimation(){
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean floorsToVisitEmpty() {
        synchronized (floorsListLock) {
            return floorsToVisit.isEmpty();
        }
    }

    @Override
    public void run() {
        running.set(true);
        try {
            while (running.get()) {
                ElevatorFloorQueue.ElevatorOrder order = orderQueue.getNextOrder();
                if (order != null) {
                    System.out.println("Processing order: " + order);

                    ElevatorDirection direction = order.direction();
                    orderQueue.setCurrentDirection(direction);
                    FloorNumber floorNumber = order.floor();
                    Floor floor = Building.floors.get(floorNumber);

                    elevator.goToFloor(floorNumber);
                    floor.signalCondition();

                    waitForAnimation();
                    processPeopleEnteringElevator(floor, direction);

                    while(!floorsToVisitEmpty()) {

                        FloorNumber destFloorNumber;
                        synchronized (floorsListLock) {
                            System.out.println("Floors to visit: " + floorsToVisit.toString());
                            destFloorNumber = floorsToVisit.getFirst();
                            floorsToVisit.remove(destFloorNumber);
                        }

                        elevator.goToFloor(destFloorNumber);

                        // signal floor condition:
                        // - Person thread -> people that want to leave, are animated leaving
                        // - Person thread -> people that want to enter, are animated entering
                        floor = Building.floors.get(destFloorNumber);
                        floor.signalCondition();

                        waitForAnimation();

                        // enter people going in current direction
                        processPeopleEnteringElevator(floor, direction);

                        // delete orders
                        orderQueue.deleteOrdersFromFloor(floorNumber, direction);

                        peopleInElevator.remove(destFloorNumber);

                    }

                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
