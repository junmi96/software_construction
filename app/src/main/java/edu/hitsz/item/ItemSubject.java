package edu.hitsz.item;

import java.util.ArrayList;
import java.util.List;

public class ItemSubject {

    private final List<ItemObserver> observers = new ArrayList<>();

    public void addObserver(ItemObserver observer) {
        if (observer != null) {
            observers.add(observer);
        }
    }

    public void removeObserver(ItemObserver observer) {
        observers.remove(observer);
    }

    public void notifyBombObservers() {
        for (ItemObserver observer : observers) {
            observer.onBomb();
        }
    }

    public void notifyFreezeObservers() {
        for (ItemObserver observer : observers) {
            observer.onFreeze();
        }
    }
}
