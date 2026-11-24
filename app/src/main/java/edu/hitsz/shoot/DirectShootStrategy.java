package edu.hitsz.shoot;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;

import java.util.LinkedList;
import java.util.List;

/**
 * 直射策略：可配置子弹数量，默认平行直射
 */
public class DirectShootStrategy implements ShootStrategy {

    private final int bulletNum;
    private final int spacing;
    private final int speedYDelta;

    public DirectShootStrategy(int bulletNum, int spacing, int speedYDelta) {
        this.bulletNum = bulletNum;
        this.spacing = spacing;
        this.speedYDelta = speedYDelta;
    }

    @Override
    public List<BaseBullet> shoot(AbstractAircraft aircraft, int power, int direction) {
        List<BaseBullet> res = new LinkedList<>();
        int x = aircraft.getLocationX();
        int y = aircraft.getLocationY() + direction * 2;
        int speedY = aircraft.getSpeedY() + direction * speedYDelta;

        for (int i = 0; i < bulletNum; i++) {
            int offset = (i * 2 - bulletNum + 1) * spacing;
            res.add(createBullet(aircraft, x + offset, y, 0, speedY, power));
        }
        return res;
    }

    private BaseBullet createBullet(AbstractAircraft aircraft, int x, int y, int speedX, int speedY, int power) {
        if (aircraft instanceof HeroAircraft) {
            return new HeroBullet(x, y, speedX, speedY, power);
        }
        return new EnemyBullet(x, y, speedX, speedY, power);
    }
}
