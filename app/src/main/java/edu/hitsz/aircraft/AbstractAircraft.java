package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.shoot.ShootStrategy;
import java.util.List;

/**
 * 所有种类飞机的抽象父类
 * @author hitsz
 */
public abstract class AbstractAircraft extends AbstractFlyingObject {

    //最大生命值
    protected int maxHp;
    protected int hp;

    // 当前射击策略（策略模式）
    protected ShootStrategy shootStrategy;

    //获取当前生命值
    public int getHp() {
        return hp;
    }

    //获取最大生命值
    public int getMaxHp() {
        return maxHp;
    }

    //设置当前生命值
    public void setHp(int hp) {
        if (hp > maxHp) {
            this.hp = maxHp; // 强制兜底，不能超过上限
        } else if (hp < 0) {
            this.hp = 0;     // 强制兜底，不能为负
        } else {
            this.hp = hp;
        }
    }

    public AbstractAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY);
        this.hp = hp;
        this.maxHp = hp;
    }

    public void decreaseHp(int decrease) {
        hp -= decrease;
        if (hp <= 0) {
            hp = 0;
            vanish();
        }
    }

    public void setShootStrategy(ShootStrategy shootStrategy) {
        this.shootStrategy = shootStrategy;
    }


    /**
     * 飞机射击方法
     * @return
     *  可射击对象需实现，返回子弹列表
     *  非可射击对象空实现，返回空列表
     */
    public abstract List<BaseBullet> shoot();

}


