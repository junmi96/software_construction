package edu.hitsz.game;

import edu.hitsz.application.Game;
import edu.hitsz.aircraft.enemy.enemy_product.AbstractEnemy;

public class MediumGame extends Game {

    public MediumGame(String playerName) {
        super(playerName);
    }

    @Override
    protected void initializeDifficulty() {
        enemyMaxNumber = 6;
        enemySpawnCycle = 22;
        heroShootCycle = 20;
        enemyShootCycle = 20;
        bossScoreStep = 100;
        nextBossScoreThreshold = 100;
        enemyHpScale = 1.0;
        enemySpeedScale = 1.0;
        bossEnemyFactory.resetBossHpRule(120, 0);
    }

    @Override
    protected void increaseDifficulty(int stage) {
        enemyMaxNumber = 6;
        enemySpawnCycle = Math.max(12, 22 * Math.pow(0.95, stage));
        heroShootCycle = 20;
        enemyShootCycle = 20;
        enemyHpScale = 1.0 + 0.08 * stage;
        enemySpeedScale = 1.0 + 0.08 * stage;
        printDifficultyIncrease(String.format(
                "普通难度：敌机产生周期:%.2f，敌机速度倍率:%.2f，敌机血量倍率:%.2f。",
                enemySpawnCycle,
                enemySpeedScale,
                enemyHpScale
        ));
    }

    @Override
    protected AbstractEnemy createEnemy() {
        double rand = Math.random();
        if (rand < 0.58) {
            return mobEnemyFactory.createEnemy();
        }
        if (rand < 0.82) {
            return eliteEnemyFactory.createEnemy();
        }
        if (rand < 0.94) {
            return elitePlusEnemyFactory.createEnemy();
        }
        return eliteProEnemyFactory.createEnemy();
    }

    @Override
    protected boolean supportBoss() {
        return true;
    }

    @Override
    protected AbstractEnemy createBossEnemy() {
        nextBossScoreThreshold += bossScoreStep;
        return bossEnemyFactory.createEnemy();
    }
}
