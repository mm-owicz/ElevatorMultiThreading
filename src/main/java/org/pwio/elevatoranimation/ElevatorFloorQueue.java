package org.pwio.elevatoranimation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ElevatorFloorQueue {
    private final List<ElevatorOrder> orders = Collections.synchronizedList(new ArrayList<>());
    private ElevatorDirection currentDirection;

    public ElevatorFloorQueue() {}

    public synchronized void addOrder(ElevatorOrder order) {
        orders.add(order);
        notify();
    }

    public ElevatorDirection getCurrentDirection() {
        return currentDirection;
    }

    public void setCurrentDirection(ElevatorDirection currentDirection) {
        this.currentDirection = currentDirection;
    }

    public synchronized ElevatorOrder getNextOrder() throws InterruptedException {
        while (orders.isEmpty()) {
            wait();
        }
        return orders.removeFirst();
    }

    public void deleteOrdersFromFloor(FloorNumber fromFloor, ElevatorDirection direction) {
        orders.removeIf(order -> order.floor() == fromFloor && order.direction() == direction);
    }

    public synchronized void deleteOrderByID(UUID id) {
        orders.removeIf(order -> order.personID.equals(id));
    }


    public record ElevatorOrder(UUID personID, FloorNumber floor, ElevatorDirection direction) {}
}
