package edu.hitsz.aircraft.enemy.enemy_factory;

import edu.hitsz.aircraft.enemy.enemy_product.AbstractEnemy;
import edu.hitsz.aircraft.enemy.enemy_product.ElitePlusEnemy;
import edu.hitsz.application.ImageManager;

public class ElitePlusEnemyFactory extends EnemyFactory {

    @Override
    public AbstractEnemy createEnemy() {
        // 精锐敌机：双发射击、可掉落道具、可左右移动
        int baseSpeedX = 5;
        int baseSpeedY = 10;
        int baseHp = 60;
        return new ElitePlusEnemy(
                randomX(ImageManager.ELITE_PLUS_ENEMY_IMAGE.getWidth()),
                randomY(),
                scaleSpeedX(baseSpeedX),
                scaleSpeedY(baseSpeedY),
                scaleHp(baseHp)
        );
    }

}
