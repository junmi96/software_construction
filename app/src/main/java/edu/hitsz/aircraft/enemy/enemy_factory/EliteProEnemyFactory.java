package edu.hitsz.aircraft.enemy.enemy_factory;

import edu.hitsz.aircraft.enemy.enemy_product.AbstractEnemy;
import edu.hitsz.aircraft.enemy.enemy_product.EliteProEnemy;

public class EliteProEnemyFactory extends EnemyFactory {

    @Override
    public AbstractEnemy createEnemy() {
        // 王牌敌机：三发射击、可掉落道具、可左右移动
        int baseSpeedX = 5;
        int baseSpeedY = 10;
        int baseHp = 80;
        return new EliteProEnemy(
                randomX(defaultEnemyWidth()),
                randomY(),
                scaleSpeedX(baseSpeedX),
                scaleSpeedY(baseSpeedY),
                scaleHp(baseHp)
        );
    }

}
