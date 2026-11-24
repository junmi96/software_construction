package edu.hitsz.item;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.bullet.BaseBullet;

import java.util.List;

public class FireItem extends BaseItem {
    public FireItem(int x, int y, int sx, int sy) { super(x, y, sx, sy); }
    @Override
    public void activate(HeroAircraft hero, List<AbstractAircraft> enemyAircrafts, List<BaseBullet> enemyBullets) {
        hero.activateScatterShoot(10_000L);
    }
}
