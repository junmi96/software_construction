package edu.hitsz.shoot;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;

import java.util.LinkedList;
import java.util.List;

/**
 * 散射策略：根据 speedXOffsets 形成扇形弹道
 */
public class ScatterShootStrategy implements ShootStrategy {

    private final int speedYDelta;
    private final int spacing;
    private final int[] speedXOffsets;

    public ScatterShootStrategy(int speedYDelta, int spacing, int... speedXOffsets) {
        this.speedYDelta = speedYDelta;
        this.spacing = spacing;
        this.speedXOffsets = speedXOffsets;
    }

    @Override
    public List<BaseBullet> shoot(AbstractAircraft aircraft, int power, int direction) {
        List<BaseBullet> res = new LinkedList<>();
        int x = aircraft.getLocationX();
        int y = aircraft.getLocationY() + direction * 2;
        int speedY = aircraft.getSpeedY() + direction * speedYDelta;

        int bulletNum = speedXOffsets.length;
        for (int i = 0; i < bulletNum; i++) {
            int offset = (i * 2 - bulletNum + 1) * spacing;
            int speedX = speedXOffsets[i];
            res.add(createBullet(aircraft, x + offset, y, speedX, speedY, power));
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
