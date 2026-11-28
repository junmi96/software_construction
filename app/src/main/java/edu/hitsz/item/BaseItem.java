package edu.hitsz.item;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.game.GameConfig;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.bullet.BaseBullet;

import java.util.List;

public abstract class BaseItem extends AbstractFlyingObject {

    public BaseItem(int locationX, int locationY, int speedX, int speedY){
        super(locationX, locationY, speedX, speedY);
    }
    /**
     * 抽象方法：当英雄机撞到道具时触发的效果
     */
    public abstract void activate(HeroAircraft hero, List<AbstractAircraft> enemyAircrafts, List<BaseBullet> enemyBullets);
    /**
     * 道具的移动逻辑：以一定速度向屏幕下方移动
     */
    @Override
    public void forward(){
        locationY += speedY;
        if (locationY >= GameConfig.GAME_HEIGHT){
            vanish();
        }
    }
}
