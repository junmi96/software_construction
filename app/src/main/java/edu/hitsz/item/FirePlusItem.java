package edu.hitsz.item;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.bullet.BaseBullet;

import java.util.List;

public class FirePlusItem extends BaseItem {
    public FirePlusItem(int x, int y, int sx, int sy) { super(x, y, sx, sy); }
    @Override
    public void activate(HeroAircraft hero, List<AbstractAircraft> enemyAircrafts, List<BaseBullet> enemyBullets) {
        hero.activateRingShoot(8_000L);
    }
}
