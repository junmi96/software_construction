package edu.hitsz.item;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.bullet.BaseBullet;

import java.util.List;

public class BloodItem extends BaseItem {
    public BloodItem(int locationX, int locationY, int speedX, int speedY){
        super(locationX, locationY, speedX, speedY);
    }
    @Override
    public void activate(HeroAircraft hero, List<AbstractAircraft> enemyAircrafts, List<BaseBullet> enemyBullets){
        //定义每次回血量
        int recoverHp = 10;
        //获取当前血量和最大血量
        int currentHp = hero.getHp();
        int maxHp = hero.getMaxHp();
        //计算回血后的血量
        int finalHp = Math.min(currentHp + recoverHp, maxHp);
        //写回英雄机
        hero.setHp(finalHp);
        System.out.println("Health Restored!");
    }
}
