package edu.hitsz.aircraft.enemy.enemy_factory;

import edu.hitsz.aircraft.enemy.enemy_product.AbstractEnemy;
import edu.hitsz.aircraft.enemy.enemy_product.BossEnemy;

public class BossEnemyFactory extends EnemyFactory {

    private int nextBossHp = 100;
    private int bossHpIncrement = 0;

    public void resetBossHpRule(int initialHp, int hpIncrement) {
        this.nextBossHp = Math.max(1, initialHp);
        this.bossHpIncrement = Math.max(0, hpIncrement);
    }

    @Override
    public AbstractEnemy createEnemy() {
        // Boss敌机：悬浮于上方，仅左右移动；扇形射击、可掉落道具，仅在分数达到阈值时生成
        int hp = scaleHp(nextBossHp);
        nextBossHp += bossHpIncrement;
        int baseSpeedX = 5;
        int baseSpeedY = 10;
        return new BossEnemy(
                randomX(defaultEnemyWidth()),
                randomY(),
                scaleSpeedX(baseSpeedX),
                scaleSpeedY(baseSpeedY),
                hp
        );
    }

}
