package edu.hitsz.shoot;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;

import java.util.LinkedList;
import java.util.List;

/**
 * 环射策略：围绕机体 360 度发射
 */
public class RingShootStrategy implements ShootStrategy {

    private final int bulletNum;
    private final int speedMagnitude;

    public RingShootStrategy(int bulletNum, int speedMagnitude) {
        this.bulletNum = bulletNum;
        this.speedMagnitude = speedMagnitude;
    }

    @Override
    public List<BaseBullet> shoot(AbstractAircraft aircraft, int power, int direction) {
        List<BaseBullet> res = new LinkedList<>();
        int x = aircraft.getLocationX();
        int y = aircraft.getLocationY() + direction * 2;

        for (int i = 0; i < bulletNum; i++) {
            double angle = 2 * Math.PI * i / bulletNum;
            int speedX = (int) Math.round(speedMagnitude * Math.cos(angle));
            int speedY = (int) Math.round(speedMagnitude * Math.sin(angle));
            if (speedX == 0 && speedY == 0) {
                speedY = direction;
            }
            res.add(createBullet(aircraft, x, y, speedX, speedY, power));
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
