package edu.hitsz.aircraft.enemy.enemy_product;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.item.BaseItem;
import edu.hitsz.shoot.EmptyShootStrategy;

import java.util.LinkedList;
import java.util.List;

public class MobEnemy extends AbstractEnemy {

    public MobEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        setShootStrategy(new EmptyShootStrategy());
    }

    @Override
    protected int getPower() {
        return 0;
    }

    @Override
    public List<BaseBullet> shoot() {
        return shootStrategy.shoot(this, getPower(), DIRECTION);
    }

    @Override
    public List<BaseItem> dropItems() {
        return new LinkedList<>();
    }

    @Override
    public int getScore() {
        return 10;
    }

    @Override
    public void onBomb() {
        vanish();
    }

    @Override
    public void onFreeze() {
        freezePermanently();
    }
}
