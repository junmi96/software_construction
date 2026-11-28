package edu.hitsz.item;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.bullet.BaseBullet;

import java.util.List;

public class FreezeItem extends BaseItem {

    private final ItemSubject itemSubject = new ItemSubject();

    public FreezeItem(int x, int y, int sx, int sy) {
        super(x, y, sx, sy);
    }

    @Override
    public void activate(HeroAircraft hero, List<AbstractAircraft> enemyAircrafts, List<BaseBullet> enemyBullets) {
        registerObservers(enemyAircrafts, enemyBullets);
        itemSubject.notifyFreezeObservers();
    }

    private void registerObservers(List<AbstractAircraft> enemyAircrafts, List<BaseBullet> enemyBullets) {
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            if (enemyAircraft instanceof ItemObserver && !enemyAircraft.notValid()) {
                itemSubject.addObserver((ItemObserver) enemyAircraft);
            }
        }
        for (BaseBullet enemyBullet : enemyBullets) {
            if (enemyBullet instanceof ItemObserver && !enemyBullet.notValid()) {
                itemSubject.addObserver((ItemObserver) enemyBullet);
            }
        }
    }
}
