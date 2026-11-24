package edu.hitsz.aircraft.enemy.enemy_product;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.shoot.ScatterShootStrategy;

import java.util.List;

public class EliteProEnemy extends AbstractEnemy {

    public EliteProEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        setShootStrategy(new ScatterShootStrategy(5, 12, -2, 0, 2));
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
        return 40;
    }

    @Override
    public void onBomb() {
        decreaseHp(30);
    }

    @Override
    public void onFreeze() {
        slowFor(5_000L, 0.5);
    }
}
