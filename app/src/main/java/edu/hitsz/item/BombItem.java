package edu.hitsz.item;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.application.AudioManager;
import edu.hitsz.bullet.BaseBullet;

import java.util.List;

public class BombItem extends BaseItem {

    private final ItemSubject itemSubject = new ItemSubject();

    public BombItem(int x, int y, int sx, int sy) {
        super(x, y, sx, sy);
    }

    @Override
    public void activate(HeroAircraft hero, List<AbstractAircraft> enemyAircrafts, List<BaseBullet> enemyBullets) {
        AudioManager.getInstance().playBomb();
        registerObservers(enemyAircrafts, enemyBullets);
        itemSubject.notifyBombObservers();
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
