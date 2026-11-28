package edu.hitsz.aircraft.enemy.enemy_product;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.shoot.DirectShootStrategy;

import java.util.List;

public class ElitePlusEnemy extends AbstractEnemy {

    public ElitePlusEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        setShootStrategy(new DirectShootStrategy(2, 12, 5));
    }

    @Override
    protected int getPower() {
        return 5;
    }

    @Override
    public List<BaseBullet> shoot() {
        return shootStrategy.shoot(this, getPower(), DIRECTION);
    }

    @Override
    public int getScore() {
        return 30;
    }

    @Override
    public void onBomb() {
        vanish();
    }

    @Override
    public void onFreeze() {
        freezeFor(3_000L);
    }
}
