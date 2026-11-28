package edu.hitsz.item;

import java.util.List;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.bullet.BaseBullet;

public class BombItem extends BaseItem {

    private final ItemSubject itemSubject = new ItemSubject();

    public BombItem(int x, int y, int sx, int sy) {
        super(x, y, sx, sy);
    }

    @Override
    public void activate(HeroAircraft hero,
                         List<AbstractAircraft> enemyAircrafts,
                         List<BaseBullet> enemyBullets) {
        registerObservers(enemyAircrafts, enemyBullets);
        itemSubject.notifyBombObservers();
    }

    private void registerObservers(List<AbstractAircraft> enemyAircrafts,
                                   List<BaseBullet> enemyBullets) {
        for (AbstractAircraft enemy : enemyAircrafts) {
            if (enemy instanceof ItemObserver && !enemy.notValid()) {
                itemSubject.addObserver((ItemObserver) enemy);
            }
        }

        for (BaseBullet bullet : enemyBullets) {
            if (bullet instanceof ItemObserver && !bullet.notValid()) {
                itemSubject.addObserver((ItemObserver) bullet);
            }
        }
    }
}