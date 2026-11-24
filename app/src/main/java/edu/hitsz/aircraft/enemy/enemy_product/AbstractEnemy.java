package edu.hitsz.aircraft.enemy.enemy_product;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.application.Main;
import edu.hitsz.item.BaseItem;
import edu.hitsz.item.ItemFactory;
import edu.hitsz.item.ItemObserver;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractEnemy extends AbstractAircraft implements ItemObserver {
    protected static final int DIRECTION = 1;

    private final int normalSpeedX;
    private final int normalSpeedY;
    private long freezeExpireTime = 0L;
    private long slowExpireTime = 0L;
    private double slowRate = 1.0;

    public AbstractEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.normalSpeedX = speedX;
        this.normalSpeedY = speedY;
    }

    @Override
    public void forward() {
        updateMoveState();
        if (speedX == 0 && speedY == 0) {
            return;
        }
        super.forward();
        if (locationY >= Main.WINDOW_HEIGHT) {
            vanish();
        }
    }

    protected void freezePermanently() {
        freezeExpireTime = Long.MAX_VALUE;
        setSpeed(0, 0);
    }

    protected void freezeFor(long durationMillis) {
        freezeExpireTime = Math.max(freezeExpireTime, System.currentTimeMillis() + durationMillis);
        setSpeed(0, 0);
    }

    protected void slowFor(long durationMillis, double rate) {
        slowExpireTime = Math.max(slowExpireTime, System.currentTimeMillis() + durationMillis);
        slowRate = rate;
        applySlowSpeed();
    }

    private void updateMoveState() {
        long now = System.currentTimeMillis();
        if (freezeExpireTime == Long.MAX_VALUE) {
            setSpeed(0, 0);
            return;
        }
        if (freezeExpireTime > now) {
            setSpeed(0, 0);
            return;
        }
        freezeExpireTime = 0L;

        if (slowExpireTime > now) {
            applySlowSpeed();
            return;
        }
        slowExpireTime = 0L;
        slowRate = 1.0;
        setSpeed(normalSpeedX, normalSpeedY);
    }

    private void applySlowSpeed() {
        int slowedSpeedX = (int) Math.round(normalSpeedX * slowRate);
        int slowedSpeedY = (int) Math.round(normalSpeedY * slowRate);
        if (normalSpeedX != 0 && slowedSpeedX == 0) {
            slowedSpeedX = normalSpeedX > 0 ? 1 : -1;
        }
        if (normalSpeedY != 0 && slowedSpeedY == 0) {
            slowedSpeedY = normalSpeedY > 0 ? 1 : -1;
        }
        setSpeed(slowedSpeedX, slowedSpeedY);
    }

    protected abstract int getPower();

    public List<BaseItem> dropItems() {
        List<BaseItem> res = new LinkedList<>();
        BaseItem item = ItemFactory.createItem(this.getLocationX(), this.getLocationY());
        if (item != null) {
            res.add(item);
        }
        return res;
    }

    public abstract int getScore();
}
