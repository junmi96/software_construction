package edu.hitsz.aircraft.enemy.enemy_product;

import edu.hitsz.game.GameConfig;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.shoot.RingShootStrategy;

import java.util.List;

public class BossEnemy extends AbstractEnemy {

    public BossEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        setShootStrategy(new RingShootStrategy(20, 6));
    }

    @Override
    public void forward() {
        this.locationX += this.speedX;
        if (this.locationX <= 0 || this.locationX >= GameConfig.GAME_WIDTH) {
            this.speedX = -this.speedX;
        }
    }

    @Override
    protected int getPower() {
        return 1;
    }

    @Override
    public List<BaseBullet> shoot() {
        return shootStrategy.shoot(this, getPower(), DIRECTION);
    }

    @Override
    public int getScore() {
        return 50;
    }

    @Override
    public void onBomb() {
    }

    @Override
    public void onFreeze() {
    }
}
