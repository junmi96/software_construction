package edu.hitsz.aircraft;

import edu.hitsz.application.ImageManager;
import edu.hitsz.game.GameConfig;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.shoot.DirectShootStrategy;
import edu.hitsz.shoot.RingShootStrategy;
import edu.hitsz.shoot.ScatterShootStrategy;
import edu.hitsz.shoot.ShootStrategy;

import java.util.List;

/**
 * 英雄飞机，游戏玩家操控
 * @author hitsz
 */
public class HeroAircraft extends AbstractAircraft {

    //单例模式：持有唯一的英雄机实例
    private volatile static HeroAircraft heroAircraft;

    //每次射击发射子弹数量
    private final int power = 30;

    //子弹射击方向 (向上发射：-1，向下发射：1)
    private int direction = -1;

    // 火力道具状态
    private int fireModeLevel = 0;
    private long fireModeExpireTime = 0L;

    //私有化构造方法
    private HeroAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        setStraightShootStrategy();
    }

    //提供公共静态方法获取实例
    public static HeroAircraft getHeroAircraft(){
        if(heroAircraft==null){
            synchronized (HeroAircraft.class){
                if(heroAircraft==null){
                    heroAircraft=new HeroAircraft(GameConfig.GAME_WIDTH / 2,GameConfig.GAME_HEIGHT - ImageManager.HERO_IMAGE.getHeight(),0,0,100);
                }
            }
        }
        return heroAircraft;
    }
    @Override
    public void forward() {
        // 英雄机由鼠标控制，不通过forward函数移动
    }

    @Override
    /**
     * 通过射击产生子弹
     * @return 射击出的子弹List
     */
    public List<BaseBullet> shoot() {
        if (shootStrategy == null) {
            return List.of();
        }
        return shootStrategy.shoot(this, power, direction);
    }

    /**
     * 无道具时：直射
     */
    public void setStraightShootStrategy() {
        setShootStrategy(new DirectShootStrategy(1, 10, 5));
    }

    /**
     * 火力道具：散射
     */
    public void setScatterShootStrategy() {
        setShootStrategy(new ScatterShootStrategy(5, 12, -2, 0, 2));
    }

    /**
     * 超级火力道具：环射
     */
    public void setRingShootStrategy() {
        setShootStrategy(new RingShootStrategy(20, 6));
    }

    public void activateScatterShoot(long durationMillis) {
        activateFireMode(1, durationMillis, new ScatterShootStrategy(5, 12, -2, 0, 2));
    }

    public void activateRingShoot(long durationMillis) {
        activateFireMode(2, durationMillis, new RingShootStrategy(20, 6));
    }

    public void updateFireMode() {
        if (fireModeLevel != 0 && System.currentTimeMillis() >= fireModeExpireTime) {
            fireModeLevel = 0;
            fireModeExpireTime = 0L;
            setStraightShootStrategy();
        }
    }

    private void activateFireMode(int newLevel, long durationMillis, ShootStrategy strategy) {
        long now = System.currentTimeMillis();
        long newExpireTime = now + Math.max(0L, durationMillis);

        if (newLevel > fireModeLevel || now >= fireModeExpireTime) {
            fireModeLevel = newLevel;
            fireModeExpireTime = newExpireTime;
            setShootStrategy(strategy);
            return;
        }

        if (newLevel == fireModeLevel) {
            fireModeExpireTime = Math.max(fireModeExpireTime, newExpireTime);
        }
    }

    public void resetForNewGame(int hp) {
        this.maxHp = hp;
        this.hp = hp;
        this.isValid = true;
        this.fireModeLevel = 0;
        this.fireModeExpireTime = 0L;
        setStraightShootStrategy();
    }

}
