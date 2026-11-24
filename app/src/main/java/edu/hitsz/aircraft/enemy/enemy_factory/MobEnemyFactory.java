package edu.hitsz.aircraft.enemy.enemy_factory;

import edu.hitsz.aircraft.enemy.enemy_product.AbstractEnemy;
import edu.hitsz.aircraft.enemy.enemy_product.MobEnemy;

public class MobEnemyFactory extends EnemyFactory {

    @Override
    public AbstractEnemy createEnemy() {
        // 普通敌机：直线下落，不射击
        int baseSpeedX = 0;
        int baseSpeedY = 10;
        int baseHp = 30;
        return new MobEnemy(
                randomX(defaultEnemyWidth()),
                randomY(),
                scaleSpeedX(baseSpeedX),
                scaleSpeedY(baseSpeedY),
                scaleHp(baseHp)
        );
    }

}
