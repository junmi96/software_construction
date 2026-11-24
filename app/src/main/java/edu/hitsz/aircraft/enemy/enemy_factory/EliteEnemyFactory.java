package edu.hitsz.aircraft.enemy.enemy_factory;

import edu.hitsz.aircraft.enemy.enemy_product.AbstractEnemy;
import edu.hitsz.aircraft.enemy.enemy_product.EliteEnemy;

public class EliteEnemyFactory extends EnemyFactory {

    @Override
    public AbstractEnemy createEnemy() {
        // 精英敌机：单发射击、可掉落道具
        int baseSpeedX = 0;
        int baseSpeedY = 10;
        int baseHp = 50;
        return new EliteEnemy(
                randomX(defaultEnemyWidth()),
                randomY(),
                scaleSpeedX(baseSpeedX),
                scaleSpeedY(baseSpeedY),
                scaleHp(baseHp)
        );
    }

}
