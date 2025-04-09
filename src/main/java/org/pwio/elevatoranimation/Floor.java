package org.pwio.elevatoranimation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Floor {
    private final Condition condition;
    private final Lock lock;
    private final FloorNumber floorNumber;
    private final ConcurrentMap<UUID, Person> waitingPeople = new ConcurrentHashMap<>();
    private final List<Person> waitingOrder
            = Collections.synchronizedList(new ArrayList<>()); // for animation

    public Floor(FloorNumber floorNumber) {
        this.lock = new ReentrantLock();
        this.condition = lock.newCondition();
        this.floorNumber = floorNumber;
    }

    public Condition getCondition() {
        return condition;
    }

    public Lock getLock() {return lock;}

    public synchronized void addWaitingPerson(Person person) {
        System.out.println("putting person " + person.getId() + " into waiting list");
        this.waitingPeople.putIfAbsent(person.getId(), person);
        this.waitingOrder.add(person);
    }

    public synchronized void removeWaitingPerson(Person person) {
        this.waitingPeople.remove(person.getId());
        this.waitingOrder.remove(person);
    }

    public void signalCondition() {
        lock.lock();
        try{
            System.out.println("Signaling all waiting at floor " + floorNumber);
            condition.signalAll();
        }finally {
            lock.unlock();
        }
    }

    public FloorNumber getFloorNumber() {
        return floorNumber;
    }

    public List<Person> getPeopleGoingInDirection(ElevatorDirection direction) {
        List<Person> peopleGoingInDirection = new ArrayList<>();
        for(Person person : waitingPeople.values()) {
            if(person.getDestinationDirection().equals(direction) && person.isInElevator()) {
                peopleGoingInDirection.add(person);
            }
        }
        return peopleGoingInDirection;
    }


}
