package edu.hitsz.bullet;

import edu.hitsz.item.ItemObserver;

public class EnemyBullet extends BaseBullet implements ItemObserver {

    private final int normalSpeedX;
    private final int normalSpeedY;
    private long freezeExpireTime = 0L;

    public EnemyBullet(int locationX, int locationY, int speedX, int speedY, int power) {
        super(locationX, locationY, speedX, speedY, power);
        this.normalSpeedX = speedX;
        this.normalSpeedY = speedY;
    }

    @Override
    public void forward() {
        updateFreezeStatus();
        super.forward();
    }

    @Override
    public void onBomb() {
        vanish();
    }

    @Override
    public void onFreeze() {
        freezeExpireTime = Math.max(freezeExpireTime, System.currentTimeMillis() + 5_000L);
        setSpeed(0, 0);
    }

    private void updateFreezeStatus() {
        if (freezeExpireTime == 0L) {
            return;
        }
        if (System.currentTimeMillis() < freezeExpireTime) {
            setSpeed(0, 0);
            return;
        }
        freezeExpireTime = 0L;
        setSpeed(normalSpeedX, normalSpeedY);
    }
}
