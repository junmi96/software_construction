package edu.hitsz.aircraft.enemy.enemy_product;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.shoot.DirectShootStrategy;

import java.util.List;

public class EliteEnemy extends AbstractEnemy {

    public EliteEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        setShootStrategy(new DirectShootStrategy(1, 10, 5));
    }

    @Override
    protected int getPower() {
        return 10;
    }

    @Override
    public List<BaseBullet> shoot() {
        return shootStrategy.shoot(this, getPower(), DIRECTION);
    }

    @Override
    public int getScore() {
        return 20;
    }

    @Override
    public void onBomb() {
        vanish();
    }

    @Override
    public void onFreeze() {
        freezeFor(4_000L);
    }
}
